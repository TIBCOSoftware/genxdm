package org.genxdm.bridgekit.content;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

public class TypedContentHelper<A>
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
        handler.startDocument(null, null);
    }

    @Override
    public void start(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        start();
        if (name != null)
            startComplex(ns, name, bindings, attributes);
        //else what? just drop it? TODO
    }

    @Override
    public void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        if ( (name == null) || name.trim().isEmpty() )
            throw new IllegalArgumentException("Element with null or empty name");
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
        // retrieve the type. quick and stupid: use the element name
        Type type = typeStack.peek();
        if (type == null)
        {
            ElementDefinition element = provider.getElementDeclaration(new QName(ns, name, nsStack.getPrefix(ns, bindings)));
            if (element == null)
                throw new IllegalStateException("Element without a declaration");
            type = element.getType();
        }
        else
        {
            // this is where we go sideways, because the way to ensure
            // the order of things is correct is to walk over the schema
            // model at the same time that we receive our events via the message interface
            // we aren't doing it that way, because it takes a week just to understand the schema model
            // this is so going to break for so many things. ugh.
            if (type instanceof ComplexType) // ought to be
            {
                // TODO: do you know how much of a mess it is to find things? ugh
                //((ComplexType)type).getContentType().
                ElementDefinition element = provider.getElementDeclaration(new QName(ns, name, nsStack.getPrefix(ns, bindings)));
                // errors, because we're being sleazy as hell here
                type = element.getType();
            }
            else
                throw new IllegalStateException("Element content inside an element of non-complex type");
        }
            
        handler.startElement(ns, name, nsStack.getPrefix(ns, bindings), type.getName());
        
        // okay, here we go back to tested code for namespaces 
        if (bindings != null)
            for (Map.Entry<String, String> binding : bindings.entrySet())
            {
                handler.namespace(binding.getKey(), binding.getValue());
            }
        
        // and *now*, we have to handle the attributes, which is another set of
        // painful bits. *note*: this means that all the complexity, pretty much,
        // is in this single method. yay, design.
        // TODO: *also* handle the problem of missing attributes? or defaulted ones?
        if (attributes != null)
        {
            if ( !(type instanceof ComplexType) )
                throw new IllegalStateException("Attributes on instance of element not of complex type");
            Map<QName, AttributeUse> uses = ((ComplexType)type).getAttributeUses();
            for (Attrib attribute : attributes)
            {
                String ans = NIT;
                String prefix = NIT;
                if (!attribute.getNamespace().isEmpty())
                {
                    ans = attribute.getNamespace();
                    prefix = nsStack.getAttributePrefix(attribute.getNamespace(), bindings);
                }
                AttributeUse use = uses.get(new QName(ans, attribute.getName(), prefix));
                if (use != null)
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
                        throw new GenXDMException("Invalid attribute value", dte);
                    }
                    // the value constraint is fixed or default values.
                    // TODO: handle value constraints, after getting the value

                    handler.attribute(ans, attribute.getName(), prefix, data, aType.getName());
                }
                else
                    throw new IllegalStateException("Unrecognized attribute");
            }
        }
        nsStack.push(bindings);
        typeStack.push(type);
    }

    @Override
    public void simpleElement(String ns, String name, String value)
    {
        simplexElement(ns, name, null, null, value);
    }

    @Override
    public void simplexElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, String value)
    {
        startComplex(ns, name, bindings, attributes);
        // TODO: typed form
        // TODO: if it's empty, leave it alone, okay? null or value.isEmpty().
        // we need to handle that special case, right?
        Type type = typeStack.peek();
        List<A> content = null;
        if (type instanceof ComplexType) // allowed, if simple content
        {
        }
        else if (type instanceof SimpleType)
        {
            try
            {
                content = ((SimpleType)type).validate(value, bridge);
            }
            catch (DatatypeException dte)
            {
                throw new GenXDMException("invalid content");
            }
        }
        // TODO: if we don't have simple (or empty) content, blow up
        //((SimpleType)type).validate(value, bridge);
        handler.text(content);
        endComplex();
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
            throw new IllegalArgumentException("Missing data");
        startComplex(ns, name, bindings, attributes);
        Type type = typeStack.peek();
        // TODO: if the type isn't base64Binary (or, yeah, okay, hexBinary, all twice
        // that we've seen a customer actually do it), blow up and barf on someone's shoes
        List<A> content = bridge.wrapAtom(bridge.createBase64Binary(data));
        handler.text(content);
        endComplex();
    }

    @Override
    public Attrib newAttribute(String name, String value)
    {
        return new Attrib(name, value);
    }

    @Override
    public Attrib newAttribute(String ns, String name, String value)
    {
        return new Attrib(ns, name, value);
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
    }

    @Override
    public void end()
    {
        // TODO : implement user-friendly finishing?
        handler.endDocument();
    }

    @Override
    public void reset()
    {
        // TODO : prolly mostly stack management?
        nsStack.reset();
        typeStack.clear();
        try
        {
            handler.flush();
            handler.close();
        }
        catch (IOException ioe)
        {
            // TODO : something that isn't this
            throw new GenXDMException(ioe);
        }
    }

    private final SequenceHandler<A> handler;
    private final AtomBridge<A> bridge;
    private final ComponentProvider provider;
    private final NamespaceContextStack nsStack = new NamespaceContextStack("cns");
    private final Deque<Type> typeStack = new ArrayDeque<Type>();
    
    private static final String NIT = "";
}
