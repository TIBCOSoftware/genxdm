package org.genxdm.bridgekit.content;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.creation.Attrib;
import org.genxdm.creation.BinaryAttrib;
import org.genxdm.creation.BinaryContentHelper;
import org.genxdm.creation.TypeAwareBranchCopier;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentGenerator;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.typed.io.SequenceGenerator;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.ParticleTerm;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.ContentTypeKind;
import org.genxdm.xs.types.Type;

public class TypedContentHelper<A>
    extends AbstractContentHelper
    implements BinaryContentHelper, TypeAwareBranchCopier<A>, SequenceHandlerSource<A>
{
    public TypedContentHelper(SequenceHandler<A> output, ComponentProvider components, AtomBridge<A> atoms)
    {
        handler = PreCondition.assertNotNull(output, "sequence handler");
        provider = PreCondition.assertNotNull(components, "component provider");
        bridge = PreCondition.assertNotNull(atoms, "atom bridge");
        promoter = new TypePromoter(handler, bridge, provider);
    }
    
    @Override
    public SequenceHandler<A> getSequenceHandler()
    {
        return handler;
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

        final String ePfx = nsStack.getPrefix(ns, bindings);
        // here we handle xsi:type overrides (q.v. impl)
        QName xsiTypeOverride = hasTypeOverride(attributes);
        if (xsiTypeOverride != null)
            promoter.startElement(ns, name, ePfx, xsiTypeOverride);
        else
            promoter.startElement(ns, name, ePfx);
        // here we go back to tested code for namespaces (basecontenthelper example)
        if (bindings != null)
            for (Map.Entry<String, String> binding : bindings.entrySet())
            {
                promoter.namespace(binding.getKey(), binding.getValue());
            }
        
        // all we do with attributes now is to fire the proper sort of attribute events.
        if (attributes != null)
        {
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
                if (!(attribute instanceof BinaryAttrib))
                    promoter.attribute(attribute.getNamespace(), attribute.getName(), prefix, attribute.getValue(), DtdAttributeKind.CDATA);
                else
                    promoter.binaryAttribute(attribute.getNamespace(), attribute.getName(), prefix, ((BinaryAttrib)attribute).getData());
            }
        }
        //else // attributes is null ; promoter is going to handle this because it knows from types; we don't.
        nsStack.push(bindings);
        depth++;
    }

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
        binaryText(ns, name, data);
        endComplex();
    }

    @Override
    public void comment(String text)
    {
        promoter.comment(text);
    }

    @Override
    public void pi(String target, String data)
    {
        promoter.processingInstruction(target, data);
    }

    @Override
    public void endComplex()
    {
        promoter.endElement();
        nsStack.pop();
        depth--;
    }

    @Override
    public void end()
    {
        while (depth > 0)
            endComplex();
        promoter.endDocument();
        depth = -1;
    }

// check this one
    @Override
    public void reset()
    {
        nsStack.reset();
        depth = -1;
        promoter.reset();
    }
    
    @Override
    public void copyTreeAt(ContentGenerator generator)
    {
        // it is actually possible that this can work, btw. we only really have a difference
        // between binary-content and string-content trees, prior to stuffing things into a promoter
        PreCondition.assertNotNull(generator);
        PreCondition.assertTrue(generator.isElement(), "ContentGenerator must be positioned on an element");
        // note: we may not be positioned correctly. let the output handler fail
        // note: the target (us! right here!) has to handle namespace issues
        generator.write(promoter);
    }

    @Override
    public void copyTypedTreeAt(SequenceGenerator<A> generator)
    {
        PreCondition.assertNotNull(generator);
        PreCondition.assertTrue(generator.isElement(), "SequenceGenerator must be positioned on an element");
        // note: we may not be positioned correctly. let the output handler fail
        // note: the target handler has to handle namespace fixup
        generator.write(promoter, false);
    }

    protected void text(String ns, String name, String value)
    {
        promoter.text(value);
    }
    
    protected void binaryText(String ns, String name, byte [] data)
    {
        promoter.binaryText(data);
    }
    
    private QName hasTypeOverride(Iterable<Attrib> attributes)
    {
        if (attributes != null)
        {
            for (Attrib attr : attributes)
            {
                String ns = attr.getNamespace();
                if (ns != null)
                {
                    if (ns.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI) && attr.getName().equals("type") )
                        // attr.getValue() gives you pfx:name; resolve it then get the type.
                        return resolveQualifiedNameString(attr.getValue());
                }
            }
        }
        return null;
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
    
    private QName resolveQualifiedNameString(final String name)
    {
        if (!name.contains(":"))
            return new QName(name);
        else
        {
            final String pfx = name.substring(0, name.indexOf(":"));
            final String local = name.substring(name.indexOf(":")+1);
            final String ns = nsStack.getNamespace(pfx);
            if (ns != null)
                return new QName(ns, local, pfx);
        }
        return null;
    }

    // no longer used directly. We give the handler to promoter, with the atom bridge and component provider,
    // and after that, we mostly forget about it unless we need to ask for the resolved type information to
    // do some extra processing here.
    private final SequenceHandler<A> handler;
    private final AtomBridge<A> bridge;
    private final ComponentProvider provider;
    private final TypePromoter<A> promoter;
    private final NamespaceContextStack nsStack = new NamespaceContextStack("cns");
    
    private int depth = -1;
    
    private static final String NIT = "";
}
