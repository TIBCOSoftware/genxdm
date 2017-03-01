package org.genxdm.bridgekit.content;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.creation.Attrib;
import org.genxdm.creation.BinaryAttrib;
import org.genxdm.creation.BinaryContentHelper;
import org.genxdm.creation.EventKind;
import org.genxdm.creation.TypedContentEvent;
import org.genxdm.creation.TypedEventQueue;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
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

// this has a metric fuckton of copypasta from typedcontenthelper,
// with some very minor tweaks. think about how to refactor cleanly.
// same is true for the untyped contenthelpertoeventqueue vis-a-vis basecontenthelper
public class BinaryContentHelperToEventQueue<A>
    extends AbstractContentHelper
    implements BinaryContentHelper, TypedEventQueue<A>
{
    public BinaryContentHelperToEventQueue(AtomBridge<A> atoms, ComponentProvider components, Map<String, String> bindings) 
    { 
        bridge = PreCondition.assertNotNull(atoms);
        provider = PreCondition.assertNotNull(components);
        if (bindings == null)
            bindings = new HashMap<String, String>();
        nsStack.push(bindings);
    }

    public List<TypedContentEvent<A>> getQueue()
    {
        if (depth != 0)
            throw new GenXDMException("Unbalanced queue! Missing 'end' event for 'start' event");
        return queue;
    }
    
    @Override
    public void start()
    {
        // TODO: this should *probably* throw an exception, because how stupid is this?
        // it's an event queue. we shouldn't have a document in it.
        // *alternately*, just discard the event. do that for now.
        //queue.add(new TypedContentEventImpl<A>((URI)null, null));
    }
    
    // can only be called after construction or after reset.
    public void setContainerType(ComplexType containerType)
    {
        PreCondition.assertNotNull(containerType, "container type");
        PreCondition.assertTrue(typeStack.isEmpty(), "type stack must be empty");
        parentType = containerType;
    }

    @Override
    public void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
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
        if ( (parentType != null) && (type == null) )
            type = parentType;
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
         
        queue.add(new TypedContentEventImpl<A>(ns, name, nsStack.getPrefix(ns, bindings), type.getName()));
        
        // okay, here we go back to tested code for namespaces (basecontenthelper example)
        if (bindings != null)
            for (Map.Entry<String, String> binding : bindings.entrySet())
            {
                queue.add(new TypedContentEventImpl<A>(EventKind.NAMESPACE, binding.getKey(), binding.getValue()));
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

                    queue.add(new TypedContentEventImpl<A>(ans, attribute.getName(), prefix, data, aType.getName()));
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
                        queue.add(new TypedContentEventImpl<A>(entry.getKey().getNamespaceURI(), entry.getKey().getLocalPart(), entry.getKey().getPrefix(), entry.getValue().getValueConstraint().getValue(bridge), entry.getValue().getAttribute().getType().getName()));
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
                        queue.add(new TypedContentEventImpl<A>(entry.getKey().getNamespaceURI(), entry.getKey().getLocalPart(), entry.getKey().getPrefix(), entry.getValue().getValueConstraint().getValue(bridge), entry.getValue().getAttribute().getType().getName()));
                }
            }
            // not a complex type, nothing required.
        }
        nsStack.push(bindings);
        typeStack.push(type);
        depth++;
    }

    @Override
    public void binaryElement(String ns, String name, byte [] data)
    {
        binaryExElement(ns, name, null, null, data);
    }
    
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
        queue.add(new TypedContentEventImpl<A>(content));
        endComplex();
    }

    @Override
    public void comment(String text)
    {
        queue.add(new TypedContentEventImpl<A>(EventKind.COMMENT, text));
    }

    @Override
    public void pi(String target, String data)
    {
        queue.add(new TypedContentEventImpl<A>(EventKind.PROCESSING_INSTRUCTION, target, data));
    }

    @Override
    public void endComplex()
    {
        queue.add(new TypedContentEventImpl<A>(EventKind.END_ELEMENT));
        nsStack.pop();
        typeStack.pop();
        depth--;
    }

    @Override
    public void end()
    {
        //TODO: see above, for start(). we should *not* have documents inside
        // an event queue, so we either throw an exception or we ignore it.
        // ignore for now.
        //queue.add(new TypedContentEventImpl<A>(EventKind.END_DOCUMENT));
    }

    @Override
    public void reset()
    {
        parentType = null;
        nsStack.reset();
        typeStack.clear();
        depth = -1;
        queue.clear();
    }
    // the next two are not ideal, but neither is returning attribs for
    // newattribute() in abstracthelper, for our case.
    @Override
    public BinaryAttrib newBinaryAttribute(String name, byte [] data)
    {
        return new BinaryAttr(name, data);
    }
    
    @Override
    public BinaryAttrib newBinaryAttribute(String ns, String name, byte[] data)
    {
        return new BinaryAttr(ns, name, data);
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
            queue.add(new TypedContentEventImpl<A>(content));
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
    
    private List<TypedContentEvent<A>> queue = new ArrayList<TypedContentEvent<A>>();
    private final AtomBridge<A> bridge;
    private final ComponentProvider provider;
    private final NamespaceContextStack nsStack = new NamespaceContextStack("qns");
    
    private final Deque<Type> typeStack = new ArrayDeque<Type>();
    private ComplexType parentType = null;
    
    private int depth = 0;
    
    private static final String NIT = "";

}
