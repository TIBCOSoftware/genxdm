package org.genxdm.bridgekit.filters;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;

import org.genxdm.bridgekit.names.DefaultNamespaceBinding;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.names.NamespaceBinding;

public abstract class AbstractNamespaceFixupHandler
    implements ContentHandler
{

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GenXDMException
    {
        PreCondition.assertNotNull(getOutputHandler());
        if (localName.equalsIgnoreCase("xmlns") || ((prefix != null) && prefix.equalsIgnoreCase("xmlns")) )
        {
            // treat it as a mistaken attempt to declare a namespace using the wrong method.
            namespace(localName.equalsIgnoreCase("xmlns") ? "" : localName, value);
            return;
        }
        PNS pns = handleAttributeNS(namespaceURI, localName, prefix);
        attributes.add(new Attr(pns.namespace, localName, pns.prefix, value, type));
    }

    @Override
    public void comment(String value)
        throws GenXDMException
    {
        PreCondition.assertNotNull(getOutputHandler());
        reconcile();
        getOutputHandler().comment(value);
    }

    @Override
    public void endDocument()
        throws GenXDMException
    {
        PreCondition.assertNotNull(getOutputHandler());
        getOutputHandler().endDocument();
    }

    @Override
    public void endElement()
        throws GenXDMException
    {
        PreCondition.assertNotNull(getOutputHandler());
        reconcile();
        getOutputHandler().endElement();
        scopeDeque.removeFirst();
    }

    @Override
    public void namespace(String prefix, String namespaceURI)
        throws GenXDMException
    {
        PreCondition.assertNotNull(getOutputHandler());
        if (prefix == null)
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
        if (namespaceURI == null)
            namespaceURI = XMLConstants.NULL_NS_URI;
        // check reserved namespaces
        if (prefix.equals(XMLConstants.XML_NS_PREFIX) &&
            !namespaceURI.equals(XMLConstants.XML_NS_URI) )
            return; // silently drop it.
        if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE) &&
            !namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI) )
            return; // silently drop it.
        if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
            return;
        // make sure that the prefix isn't already declared in this scope
        String boundTo = getDeclaredURI(prefix);
        if ( (boundTo != null) && !boundTo.equals(namespaceURI) )
        {
            throw new GenXDMException("The prefix '" + prefix + "' is already bound to " + boundTo + "and cannot also be bound to " + namespaceURI + ".");
        }
        // queue the namespaces
        namespaces.add(new DefaultNamespaceBinding(prefix, namespaceURI));
        // and modify the current scope
        scopeDeque.peekFirst().put(prefix, namespaceURI);
    }

    @Override
    public void processingInstruction(String target, String data)
        throws GenXDMException
    {
        PreCondition.assertNotNull(getOutputHandler());
        reconcile();
        getOutputHandler().processingInstruction(target, data);
    }

    @Override
    public void startDocument(URI documentURI, String docTypeDecl)
        throws GenXDMException
    {
        PreCondition.assertNotNull(getOutputHandler());
        getOutputHandler().startDocument(documentURI, docTypeDecl);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
        throws GenXDMException
    {
        PreCondition.assertNotNull(getOutputHandler());
        reconcile();
        getOutputHandler().startElement(namespaceURI, localName, prefix);
        newScope();
        required.add(new DefaultNamespaceBinding(prefix, namespaceURI));
    }

    @Override
    public void text(String data)
        throws GenXDMException
    {
        PreCondition.assertNotNull(getOutputHandler());
        reconcile();
        getOutputHandler().text(data);
    }

    @Override
    public void close()
        throws IOException
    {
        PreCondition.assertNotNull(getOutputHandler());
        getOutputHandler().close();
    }

    @Override
    public void flush()
        throws IOException
    {
        PreCondition.assertNotNull(getOutputHandler());
        getOutputHandler().flush();
    }

    protected abstract <H extends ContentHandler> H getOutputHandler();
    
    protected abstract void outputAttribute(Attr a);
    
    protected PNS handleAttributeNS(String namespace, String name, String prefix)
    {
        if (name.toLowerCase().startsWith("xml"))
            throw new GenXDMException("Invalid attribute name: " + name);
        // first, make sure that we're not sending going to try to
        // generate an attribute with default prefix in non-default namespace
        String ns = namespace == null ? "" : namespace;
        String p = prefix == null ? "" : prefix;
        if (ns.trim().length() > 0)
        {
            if (p.trim().length() == 0)
            {
                p = getPrefixForURI(ns);
                if (p == null)
                    p = randomPrefix(ns);
            }
            required.add(new DefaultNamespaceBinding(p, ns));
        }
        return new PNS(p, ns);
    }
    
    protected void newScope()
    {
        Map<String, String> scope = new HashMap<String, String>();
        if (scopeDeque.isEmpty()) // initialize
        {
            scope.put(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
            scope.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
            scope.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        }
        scopeDeque.addFirst(scope);
    }
    
    protected void reconcile()
    {
        for (NamespaceBinding want : required)
        {
            if (!inScope(want.getPrefix(), want.getNamespaceURI())) // needed, not present
            {
                // note: instead of checking here, we're just going to declare
                // it, by calling the namespace() method.  That method is going
                // to throw an exception if this prefix is already declared in this
                // scope.  We can't recover from multiple desired bindings for
                // one prefix in a scope.
                namespace(want.getPrefix(), want.getNamespaceURI()); // declare it as is
            }
            // if it's in scope, we're good
        }
        // all of the desired bindings are either already in scope, or have
        // been added.
        required.clear();
        // now emit all the namespace events at once
        for (NamespaceBinding namespace : namespaces)
        {
            getOutputHandler().namespace(namespace.getPrefix(), namespace.getNamespaceURI());
        }
        // clear the bindings; we're done with them.
        namespaces.clear();
        // emit all the attribute events.  we've already insured that all the
        // attributes in namespaces have prefixes, and that the bindings are in scope
        for (Attr a : attributes)
        {
            outputAttribute(a);
        }
        // done, clear that set
        attributes.clear();
    }
    
    private boolean inScope(String prefix, String uri)
    {
        for(Map<String, String> scope : scopeDeque) {
            String bound = scope.get(prefix);
            if (bound != null)
            {
                if (uri.equals(bound))
                    return true;
            }
        }
        return false;
    }
    
    private String getDeclaredURI(String prefix)
    {
        for (NamespaceBinding namespace : namespaces)
        {
            if (namespace.getPrefix().equals(prefix))
                return namespace.getNamespaceURI();
        }
        return null;
    }
    
    private String getPrefixForURI(String ns)
    {
        for(Map<String, String> scope : scopeDeque) {
            for (Map.Entry<String, String> binding : scope.entrySet())
            {
                if (binding.getValue().equals(ns))
                {
                    return binding.getKey();
                }
            }
        }
        return null;
    }
    
    private String randomPrefix(String uri)
    {
        // we can think about extracting something from the uri that isn't in scope
        return "ns" + counter++;
    }
    
    protected class PNS
    {
        PNS(String p, String ns) { prefix = p; namespace = ns; }
        final String prefix;
        final String namespace;
    }
    
    protected class Attr
    {
        protected Attr() {}
        Attr(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        {
            this.namespace = namespaceURI;
            this.name = localName;
            this.prefix = prefix;
            this.value = value;
            this.type = type;
        }
        String namespace;
        String name;
        String prefix;
        String value;
        DtdAttributeKind type;
        @Override
        public int hashCode()
        {
            return ("{" + namespace + "}" + name).hashCode();
        }
        @Override
        public boolean equals(Object other)
        {
            if (other instanceof Attr)
                return hashCode() == other.hashCode();
            return false;
        }
    }

    protected Set<NamespaceBinding> namespaces = new HashSet<NamespaceBinding>();
    protected Set<NamespaceBinding> required = new HashSet<NamespaceBinding>();
    protected Set<Attr> attributes = new HashSet<Attr>();
    protected Deque<Map<String, String>> scopeDeque = new ArrayDeque<Map<String, String>>();
    protected int counter = 0;
}
