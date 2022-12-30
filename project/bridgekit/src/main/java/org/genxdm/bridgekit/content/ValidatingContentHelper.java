package org.genxdm.bridgekit.content;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.creation.Attrib;
import org.genxdm.creation.BinaryAttrib;
import org.genxdm.creation.BinaryContentHelper;
import org.genxdm.creation.TypeAwareBranchCopier;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentGenerator;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.io.SequenceGenerator;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.exceptions.SchemaExceptionThrower;

public class ValidatingContentHelper<N, A>
    implements BinaryContentHelper, TypeAwareBranchCopier<A>, SequenceHandlerSource<A>
{
    // this is the tree-oriented validation version. really, this just means that the
    // validator exposes a standard sequencehandler interface.
    // maybe allow a default namespace prefix to be passed?
    public ValidatingContentHelper(ValidationHandler<A> validator, TypedContext<N, A> context)
    {
        //
        m_xdmValidator = PreCondition.assertNotNull(validator, "validator");
        m_context = PreCondition.assertNotNull(context, "context");
        m_bridge = m_context.getAtomBridge();
        // now, basically do the same thing that we usually do in the "validate()" method in typedcontext
        m_builder = m_context.newSequenceBuilder();
        m_xdmValidator.setSchema(m_context.getSchema());
        m_xdmValidator.setSequenceHandler(m_builder);
        // folks who want to use a catcher (or something else need to set it before passing the validator in
        if (m_xdmValidator.getSchemaExceptionHandler() == null)
            m_xdmValidator.setSchemaExceptionHandler(SchemaExceptionThrower.SINGLETON);
    }
    
    // this is a hack. i don't want to have to figure out whether a byte [] is base64Binary or
    // hexBinary. we're gonna assume base64 unless we're given names of elements/attributes that
    // have binary content and prefer hexbinary encoding. the validator won't accept the identity of
    // both encoding styles, unfortunately
    // note that you don't have to use the binary methods, if your binary bits are small enough that you
    // don't have to care about the cost of encoding. just let them go through as encoded text values,
    // and the validator will fix it.
    public ValidatingContentHelper(ValidationHandler<A> validator, TypedContext<N, A> context, Set<QName> hexElements, Set<QName> hexAtts)
    {
        this(validator, context);
        m_hexElements = hexElements;
        // doing this for attributes is obviously fragile, but so is the whole hack
        m_hexAttributes = hexAtts;
    }
    // an alternate constructor could accept a SAXValidator impl instead, or a single
    // constructor could accept Validator and introspect to determine which it is,
    // then setting a flag to say "sax" or "xdm" interface (or each method checks
    // nullity of xdm and if null use the saxval, like that).
    
    @Override
    public SequenceHandler<A> getSequenceHandler()
    {
        return m_builder;
    }

    @Override // simple start-document invocation
    public void start()
    {
        if (depth >= 0)
            throw new GenXDMException("Illegal start-document invocation: nesting depth >= 0 ("+depth+")");
        m_xdmValidator.startDocument(null, null);
        depth++;
    }

    @Override
    public void start(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        start();
        if (name != null)
            startComplex(ns, name, bindings, attributes);
        // however, if the name is null or empty, *and* ...
        else if ( (ns != null) || (bindings != null) || (attributes != null) )
            throw new GenXDMException("Illegal start-document invocation: unnamed element has namespace(s) and/or attributes");
        // but null or empty name with everything else null is just ignorable, right?
    }

    @Override
    public void startComplex(String ns, String name)
    {
        startComplex(ns, name, null, null);
    }

    @Override
    public void startComplex(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes)
    {
        // this is the only place that we increment depth (well, here and in document start())
        if ( (name == null) || name.trim().isEmpty() )
            throw new IllegalArgumentException("Illegal start-complex invocation: unnamed element");
        // do namespace munging if needed
        if (ns == null)
            ns = NIT;
        if (m_nsStack.getPrefix(ns, bindings) == null)
        {
            // add a binding for the namespace, and initialize bindings.
            if (bindings == null)
                bindings = new HashMap<String, String>();
            if (ns.isEmpty())
                bindings.put(NIT, NIT);
            else
                bindings.put(m_nsStack.newPrefix(), ns);
        }
        // manage attributes
        if (attributes != null)
            for (Attrib att : attributes)
            {
                if (!att.getNamespace().isEmpty())
                    bindings = m_nsStack.checkAttributePrefix(att, bindings);
            }
        // by the time we get here, the local namespace context should be consistent.
        // in most cases, we shouldn't have had to do anything to achieve that.
        // this is radically simpler than other versions, because we make the validator do the heavy lifting.
        // pass null as type instead of doing the lookup.
        m_xdmValidator.startElement(ns, name, m_nsStack.getPrefix(ns, bindings), null);

        // okay, here we go back to tested code for namespaces (basecontenthelper example)
        if (bindings != null)
            for (Map.Entry<String, String> binding : bindings.entrySet())
            {
                m_xdmValidator.namespace(binding.getKey(), binding.getValue());
            }

        if (attributes != null)
        {
            for (Attrib attribute : attributes)
            {
                String ans = NIT; // attribute namespace = "", usually true
                String prefix = NIT; 
                // empty namespace for an attribute *cannot be bound* to a prefix
                if (!attribute.getNamespace().isEmpty())
                {
                    ans = attribute.getNamespace();
                    prefix = m_nsStack.getAttributePrefix(attribute.getNamespace(), bindings);
                }
                if (attribute instanceof BinaryAttr)
                {
                    BinaryAttr battery = (BinaryAttr)attribute;
                    boolean hexed = false;
                    if (m_hexAttributes != null)
                    {
                        final QName attrQ = new QName(ans, attribute.getName());
                        if (m_hexAttributes.contains(attrQ))
                        {
                            m_xdmValidator.attribute(ans, attribute.getName(), prefix, m_bridge.wrapAtom(m_bridge.createHexBinary(battery.getData())), null);
                            hexed = true;
                        }
                    }
                    if (!hexed)
                        m_xdmValidator.attribute(ans, attribute.getName(), prefix, m_bridge.wrapAtom(m_bridge.createBase64Binary(battery.getData())), null);
                }
                else
                    m_xdmValidator.attribute(ans, attribute.getName(), prefix, attribute.getValue(), null);
            }
        }
        m_nsStack.push(bindings);
        depth++;
    }

    @Override
    public void simpleElement(String ns, String name, String value)
    {
        simplexElement(ns, name, null, null, value);
    }

    @Override
    public void binaryElement(String ns, String name, byte[] data)
    {
        binaryExElement(ns, name, null, null, data);
    }
    
    @Override
    public void simplexElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, String value)
    {
        startComplex(ns, name, bindings, attributes);
        text(value);
        endComplex();
    }
    
    @Override
    public void binaryExElement(String ns, String name, Map<String, String> bindings, Iterable<Attrib> attributes, byte [] data)
    {
        startComplex(ns, name, bindings, attributes);
        boolean hexed = false;
        if (m_hexElements != null)
        {
            final QName eQ = new QName(ns, name);
            if (m_hexElements.contains(eQ))
            {
                data(m_bridge.createHexBinary(data));
                hexed = true;
            }
        }
        if (!hexed)
            data(m_bridge.createBase64Binary(data));
        endComplex();
    }

    @Override
    public Attrib newAttribute(final String name, final String value)
    {
        return new Attr(name, value);
    }

    @Override
    public Attrib newAttribute(final String ns, final String name, final String value)
    {
        return new Attr(ns, name, value);
    }

    @Override
    public BinaryAttrib newBinaryAttribute(final String name, final byte [] data)
    {
        return new BinaryAttr(name, data);
    }
    
    @Override
    public BinaryAttrib newBinaryAttribute(final String ns, final String name, final byte[] data)
    {
        return new BinaryAttr(ns, name, data);
    }
    
    // the validator is going to discard these.
    @Override 
    public void comment(final String text)
    {
        m_xdmValidator.comment(text);
    }

    // validator also discards these
    @Override
    public void pi(final String target, final String data)
    {
        m_xdmValidator.processingInstruction(target, data);
    }

    @Override
    public void endComplex()
    {
        m_xdmValidator.endElement();
        m_nsStack.pop();
        depth--;
    }

    @Override
    public void end()
    {
        while (depth > 0)
            endComplex();
        m_xdmValidator.endDocument();
        depth = -1;
    }

    // TODO: I'm skeptical of having this resettable. I'm not convinced this cleans up state properly,
    // and it may need some mutators to let it change more of the member variables.
    @Override
    public void reset()
    {
        depth = -1;
        try
        {
            m_builder.flush();
            m_builder.close();
        }
        catch (IOException ioe)
        {
            throw new GenXDMException("Exception in reset of TypedContentHelper, while flushing attached handler", ioe);
        }
        m_nsStack.reset();
        m_xdmValidator.reset();
        m_builder = m_context.newSequenceBuilder();
        m_xdmValidator.setSequenceHandler(m_builder);
        // do something with the schema exception handler?
    }

    @Override
    public void copyTreeAt(ContentGenerator generator)
    {
        PreCondition.assertNotNull(generator, "generator");
        PreCondition.assertTrue(generator.isElement(), "ContentGenerator must be positioned on an element");
        generator.write(m_xdmValidator);
    }

    @Override
    public void copyTypedTreeAt(SequenceGenerator<A> generator)
    {
        PreCondition.assertNotNull(generator, "generator");
        PreCondition.assertTrue(generator.isElement(), "SequenceGenerator must be positioned on an element");
        generator.write(m_xdmValidator, false);
    }

    // used only for base64Binary and hexBinary
    private void data(final A data)
    {
        m_xdmValidator.text(m_bridge.wrapAtom(data));
    }

    private void text(final String value)
    {
        // we could wrap it up as untypedAtomic, but xdm validator does that for us
        m_xdmValidator.text(value);
    }

    private final TypedContext<N, A> m_context;
    private final AtomBridge<A> m_bridge;
    private final ValidationHandler<A> m_xdmValidator;
    // not implemented, but this assumes the easy path: pass a Validator and only one these two will be non-null
    //private final SAXValidator<A> m_saxValidator;
    private final NamespaceContextStack m_nsStack = new NamespaceContextStack("vchns");
    
    private SequenceBuilder<N, A> m_builder;
    private Set<QName> m_hexElements;
    private Set<QName> m_hexAttributes;
    
    private int depth = -1;
    private static final String NIT = "";
}
