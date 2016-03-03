package org.genxdm.bridgekit.content;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.ParticleTerm;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.ContentTypeKind;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

public class TypedContentHelper<A>
    extends AbstractContentHelper
    implements BinaryContentHelper
{
    public TypedContentHelper(SequenceHandler<A> output, ComponentProvider components, AtomBridge<A> atoms)
    {
        handler = PreCondition.assertNotNull(output, "sequence handler");
        provider = PreCondition.assertNotNull(components, "component provider");
        bridge = PreCondition.assertNotNull(atoms, "atom bridge");
    }
    
    @Override
    public void start()
    {
        if (depth >= 0)
            throw new GenXDMException("Illegal start-document invocation: nesting depth >= 0 ("+depth+")");
        handler.startDocument(null, null);
        depth++;
    }

    @Override
    public void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        // this is the only place that we increment depth (well, and document start())
        if ( (name == null) || name.trim().isEmpty() )
            throw new IllegalArgumentException("Illegal start-complex invocation: unnamed element");
        if (ns == null)
            ns = NIT;
        if (nsStack.getPrefix(ns, bindings) == null)
        {
            // add a binding for the namespace, and initialize bindings.
            if (bindings == null)
                bindings = new HashMap<String, String>();
            if (ns.isEmpty())
                bindings.put(NIT, NIT);
            else
                bindings.put(nsStack.newPrefix(), ns);
        }
        if (attributes != null)
            for (Attrib att : attributes)
            {
                if (!att.getNamespace().isEmpty())
                    bindings = nsStack.checkAttributePrefix(att, bindings);
            }
        // by the time we get here, the local namespace context should be consistent.
        // in most cases, we shouldn't have had to do anything to achieve that.

        // this is where we do the promotion for the element.
        // we have to retrieve the correct type name. we should then have a stack
        // of types that we are tracking.
        // TODO: we do not handle xsi:type overrides. leave it for later.
        // retrieve the type. quick and stupid: use the element name
        Type type = typeStack.peek(); // type of the parent of this element!
        QName elementQName = new QName(ns, name, nsStack.getPrefix(ns, bindings));
        if (type == null) // true for the root element, hopefully *only*
        {
            // this is more or less correct.
            ElementDefinition element = provider.getElementDeclaration(elementQName);
            if (element == null) // nowhere else to look. die in flames
                throw new GenXDMException("Illegal start-complex invocation: element {"+ns+"}"+name+" has no element declaration");
            type = element.getType();
        }
        else
        {
            ElementDefinition element = provider.getElementDeclaration(elementQName);
            if (type instanceof ComplexType)// ought to be; it contains this element
            {
                // we don't mind the check above, because it catches
                // attempts to put things in the wrong place. however,
                // if we already have a global element, don't look for
                // a local.
                if (element == null)
                {
                    ComplexType cType = (ComplexType)type;
                    if (!cType.getContentType().getKind().isSimple())
                    {
                        ModelGroup group = cType.getContentType().getContentModel().getTerm();
                        element = locateElementDefinition(group, elementQName);
                        if (element == null) // nowhere else to look. die in flames
                            throw new GenXDMException("Illegal start-complex invocation: element {"+ns+"}"+name+" has no global element declaration or local element declaration in the scope of its parent");
                    } // TODO: actually throw on this one, too?
                    // else throw new ExceptionThisIsAnElementWhereAnAttributeShouldBe
                }
                type = element.getType();
            }
            else
                throw new IllegalStateException("Illegal element content {"+ns+"}"+name+" inside an element of non-complex type {"+type.getName().getNamespaceURI()+"}"+type.getName().getLocalPart());
        }
            
        handler.startElement(ns, name, nsStack.getPrefix(ns, bindings), type.getName());
        
        // okay, here we go back to tested code for namespaces (basecontenthelper example)
        if (bindings != null)
            for (Map.Entry<String, String> binding : bindings.entrySet())
            {
                handler.namespace(binding.getKey(), binding.getValue());
            }
        
        // and *now*, we have to handle the attributes, which is another set of
        // painful bits. *note*: this means that all the complexity, pretty much,
        // is in this single method. yay, design.
        if (attributes != null)
        {
            if ( !(type instanceof ComplexType) )
                throw new GenXDMException("Illegal content: element {"+ns+"}"+name+" is not of complex type, but the instance contains attributes");
            Map<QName, AttributeUse> uses = ((ComplexType)type).getAttributeUses();
            Set<QName> used = new HashSet<QName>();
            for (Attrib attribute : attributes)
            {
                String ans = NIT; // attribute namespace = "", usually true
                String prefix = NIT; 
                // empty namespace for an attribute *cannot be bound* to a prefix
                if (!attribute.getNamespace().isEmpty())
                {
                    ans = attribute.getNamespace();
                    prefix = nsStack.getAttributePrefix(attribute.getNamespace(), bindings);
                }
                // schema uses qnames. we *might* have to canonicalize, but hopefully
                // the schema api/impl does it for us.
                QName key = new QName(ans, attribute.getName(), prefix);
                AttributeUse use = uses.get(key);
                if (use == null)
                    throw new GenXDMException("Illegal content: element {"+ns+"}"+name+" contains an undeclared attribute "+(ans.isEmpty()?ans:"{"+ans+"}")+attribute.getName()+"='"+attribute.getValue()+"'");
                else // found the matching attribute decl
                {
                    Type aType = use.getAttribute().getType();
                    ValueConstraint constraint = use.getValueConstraint();
                    List<A> data = null;
                    try
                    {
                        data = ((SimpleType)aType).validate(attribute.getValue(), bridge);
                    }
                    catch (DatatypeException dte)
                    {
                        throw new GenXDMException("Invalid attribute value '"+attribute.getValue()+"' + for attribute "+(ans.isEmpty()?ans:"{"+ans+"}")+attribute.getName()+"in element {"+ns+"}"+name, dte);
                    }
                    if (constraint != null)
                    {
                        if (constraint.getVariety().isFixed())
                        {
                            // TODO: compare the values
                            // for a fixed attribute, a different value is an error
                        }
                        // otherwise it's default, so we can ignore it.
                    }

                    handler.attribute(ans, attribute.getName(), prefix, data, aType.getName());
                    used.add(key);
                }
            }
            // this is actually not safe. how interesting.
            // it modifies the type, by removing its attribute uses,
            // because the complex type impl returns the modifiable map.
            //for (QName key : used)
            //    uses.remove(key);
            //if (!uses.isEmpty())
            Map<QName, AttributeUse> unused = new HashMap<QName, AttributeUse>();
            for (Map.Entry<QName, AttributeUse> entry : uses.entrySet())
                if (!used.contains(entry.getKey()))
                    unused.put(entry.getKey(), entry.getValue());
            if (!unused.isEmpty())
            {
                // handle missing attributes. if any of them are required
                // and missing, that's an error. if any are defaulted, this is where
                // we add them (unless they've already been overridden, in which case they
                // won't be found in the map)
                for (Map.Entry<QName, AttributeUse> entry : unused.entrySet())
                {
                    if (entry.getValue().isRequired())
                        throw new GenXDMException("Illegal start-complex invocation: element {"+ns+"}"+name+" is missing required attribute "+(entry.getKey().getNamespaceURI()==null?"":"{"+entry.getKey().getNamespaceURI()+"}")+entry.getKey().getLocalPart());
                    // just a note: the prefix we use here is almost certainly wrong. Do. Not. Care. worry later.
                    else if (entry.getValue().getValueConstraint().getVariety().isDefault())
                        handler.attribute(entry.getKey().getNamespaceURI(), entry.getKey().getLocalPart(), entry.getKey().getPrefix(), entry.getValue().getValueConstraint().getValue(bridge), entry.getValue().getAttribute().getType().getName());
                }
            }
        }
        else // attributes is null
        {
            // are there any attributes required? exception.
            // any defaulted? add them
            // otherwise, ignore.
            if (type instanceof ComplexType)
            {
                Map<QName, AttributeUse> uses = ((ComplexType)type).getAttributeUses();
                for (Map.Entry<QName, AttributeUse> entry : uses.entrySet())
                {
                    if (entry.getValue().isRequired())
                        throw new GenXDMException("Illegal start-complex invocation: element {"+ns+"}"+name+" is missing required attribute "+(entry.getKey().getNamespaceURI()==null?"":"{"+entry.getKey().getNamespaceURI()+"}")+entry.getKey().getLocalPart());
                    // just a note: the prefix we use here is almost certainly wrong. Do. Not. Care. worry later.
                    else if (entry.getValue().getValueConstraint().getVariety().isDefault())
                        handler.attribute(entry.getKey().getNamespaceURI(), entry.getKey().getLocalPart(), entry.getKey().getPrefix(), entry.getValue().getValueConstraint().getValue(bridge), entry.getValue().getAttribute().getType().getName());
                }
            }
            // not a complex type, nothing required.
        }
        nsStack.push(bindings);
        typeStack.push(type);
        depth++;
    }

    @Override
    public void binaryElement(String ns, String name, byte[] data)
    {
        binaryExElement(ns, name, null, null, data);
    }
    
    @Override
    public void binaryExElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, byte [] data)
    {
        if (data == null)
            throw new GenXDMException("Illegal content in invocation of binary-element for element {"+ns+"}"+name+": missing data");
        startComplex(ns, name, bindings, attributes);
        Type type = typeStack.peek();
        SimpleType bType = (type instanceof SimpleType) ? (SimpleType)type : null;
        if (bType == null)
        {
            // type must be a ComplexType, since it isn't simple
            ContentType cType = ((ComplexType)type).getContentType();
            if (cType.getKind() == ContentTypeKind.Simple)
                bType = cType.getSimpleType();
        }
        if (bType == null)
            throw new GenXDMException("Illegal invocation of binary-element for element {"+ns+"}"+name+" : type is not simple");
        NativeType nType = bType.getNativeType();
        if ( (nType != NativeType.BASE64_BINARY) && (nType != NativeType.HEX_BINARY) )
            throw new GenXDMException("Illegal invocation of binary-element for element {"+ns+"}"+name+" : type is simple but not binary");
        // if we get here, then we have either base64Binary or hexBinary.
        final List<A> content;
        if (nType == NativeType.BASE64_BINARY) // usual case
            content = bridge.wrapAtom(bridge.createBase64Binary(data));
        else
            content = bridge.wrapAtom(bridge.createHexBinary(data));
        handler.text(content);
        endComplex();
    }

    @Override
    public void comment(String text)
    {
        handler.comment(text);
    }

    @Override
    public void pi(String target, String data)
    {
        handler.processingInstruction(target, data);
    }

    @Override
    public void endComplex()
    {
        handler.endElement();
        nsStack.pop();
        typeStack.pop();
        depth--;
    }

    @Override
    public void end()
    {
        while (depth > 0)
            endComplex();
        handler.endDocument();
        depth = -1;
    }

    @Override
    public void reset()
    {
        nsStack.reset();
        typeStack.clear();
        depth = -1;
        try
        {
            handler.flush();
            handler.close();
        }
        catch (IOException ioe)
        {
            throw new GenXDMException("Exception in reset of TypedContentHelper, while flushing attached handler", ioe);
        }
    }
    
    protected void text(String ns, String name, String value)
    {
        // now handle the content of the text node. it *may* be empty or null,
        // in which case, bypass all of this and don't even supply a text node.
        if ( (value != null) && !value.trim().isEmpty() )
        {
            Type type = typeStack.peek();
            List<A> content = null;
            if (type instanceof ComplexType) // allowed, if simple content
            {
                ContentType contentType = ((ComplexType)type).getContentType();
                if (contentType.getKind() == ContentTypeKind.Simple)
                {
                    SimpleType simp = contentType.getSimpleType();
                    try
                    {
                        content = simp.validate(value, bridge);
                    }
                    catch (DatatypeException dte)
                    {
                        throw new GenXDMException("Invalid content for element {"+ns+"}"+name+" '"+value+"'", dte);
                    }
                }
                // we can't do this: we would end up calling handler.text() twice. no.
                // and I'm pretty sure this code won't work for mixed content anyway.
//                else if (contentType.getKind() == ContentTypeKind.Mixed)
//                {
//                    // NOTA BENE: this is so going to fail. I'm pretty sure
//                    // we don't support mixed content in startComplex().
//                    handler.text(value);
//                }
                else // empty or element-only, with a text node
                    throw new GenXDMException("Invalid content for element {"+ns+"}"+name+" '"+value+"'"+" with content type '"+contentType+"'");
            }
            else if (type instanceof SimpleType)
            {
                try
                {
                    content = ((SimpleType)type).validate(value, bridge);
                }
                catch (DatatypeException dte)
                {
                    throw new GenXDMException("Invalid content for element {"+ns+"}"+name+" '"+value+"'", dte);
                }
            }
            // we should have actual content, either from the simple type branch
            // or the complex type simple content branch.
            handler.text(content);
        }
        else
        {
            // TODO: we should check that empty content is allowed for this type.
            // leave it for later, though.
        }
    }
    
    private ElementDefinition locateElementDefinition(ModelGroup mg, QName target)
    {
        for (SchemaParticle particle : mg.getParticles())
        {
            ParticleTerm term = particle.getTerm();
            if (term instanceof ModelGroup)
            {
                ElementDefinition candidate = locateElementDefinition((ModelGroup)term, target);
                if (candidate != null)
                    return candidate;
            }
            else if (term instanceof ElementDefinition) // better be!
            {
                if ( ((ElementDefinition)term).getName().equals(target) )
                    return (ElementDefinition)term;
            }
            // only two possibilities covered; if we haven't returned a match,
            // fall through and return null.
        }
        return null;
    }

    private final SequenceHandler<A> handler;
    private final AtomBridge<A> bridge;
    private final ComponentProvider provider;
    private final NamespaceContextStack nsStack = new NamespaceContextStack("cns");
    private final Deque<Type> typeStack = new ArrayDeque<Type>();
    
    private int depth = -1;
    
    private static final String NIT = "";
}
