package org.genxdm.bridgekit.filters;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

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
        NamespaceBinding pns = handleAttributeNS(namespaceURI, localName, prefix);
        attributes.add(new Attr(pns.getNamespaceURI(), localName, pns.getPrefix(), value, type));
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
        reconcile();
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
        // make sure that the prefix isn't already declared in this scope,
        // bound to some other namespace.
        String boundTo = getDeclaredURI(prefix);
        if ( (boundTo != null) && !boundTo.equals(namespaceURI) )
            throw new GenXDMException("In the element '" + elementName + "', the prefix '" + prefix + "' is already bound to '" + boundTo + "' and cannot also be bound to '" + namespaceURI + "'.");
        // namespace redeclaration minimization: we know some users have grotty xml
        // with the same namespace bound to the same prefix over and over in a descendant
        // tree. let's clean it up some, since it's not useful and does obscure things.
        if (!inScope(prefix, namespaceURI))
        {
            // queue the namespaces
            namespaces.add(new DefaultNamespaceBinding(prefix, namespaceURI));
            // and modify the current scope
            scopeDeque.peekFirst().put(prefix, namespaceURI);
        }
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
        reconcile(); // this should be a no-op. it's here
        // because otherwise we can fail the document-in-element test.
        getOutputHandler().startDocument(documentURI, docTypeDecl);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
        throws GenXDMException
    {
        PreCondition.assertNotNull(getOutputHandler());
        // customers are already using elements with this prefix, and have
        // apparently done so for over a decade. don't try to wrestle pigs.
//        PreCondition.assertFalse(localName.toLowerCase().startsWith("xml"), "Invalid element name: " + localName);
        reconcile();
        newScope();
        elementPrefix = prefix;
        elementNs = namespaceURI;
        elementName = localName;
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

    protected abstract ContentHandler getOutputHandler();
    
    protected abstract void outputAttribute(Attr a);
    
    protected void outputCurrentElement()
    {
        PreCondition.assertNotNull(getOutputHandler());
        getOutputHandler().startElement(elementNs, elementName, elementPrefix);
        elementNs = null;
        elementName = null;
        elementPrefix = null;
    }
    
    protected NamespaceBinding handleAttributeNS(String namespace, String name, String prefix)
    {
        // customers are already using elements with this prefix, and have
        // apparently done so for over a decade. since they've gotten away
        // with violating the specification (section 2.3), they want to do
        // it some more. sooner or later some other xml api will bite them
        // and they'll cry about it, but not our business. don't try to wrestle pigs.
        //if (name.toLowerCase().startsWith("xml"))
        //    throw new GenXDMException("Invalid attribute name: " + name);
        NamespaceBinding nsb = null;
        // first, make sure that we're not going to try to
        // generate an attribute with default prefix in non-default namespace
        String ns = namespace == null ? "" : namespace;
        String p = prefix == null ? "" : prefix;
        if (ns.trim().length() > 0)
        {
            if (p.trim().length() == 0)
            {
                p = getPrefixForURI(ns);
                if (p == null)
                    p = uniquePrefix(ns);
            }
            nsb = new DefaultNamespaceBinding(p, ns);
            requiredAttNsBindings.add(new DefaultNamespaceBinding(p, ns));
        }
        if (nsb == null)
            nsb = new DefaultNamespaceBinding(p, ns);
        return nsb;
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
    	// Check attribute ns bindings.
        if(!requiredAttNsBindings.isEmpty()) {
            for (NamespaceBinding want : requiredAttNsBindings)
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
            requiredAttNsBindings.clear();
        }
        // if we have namespace declarations and an open start tag, check
        // the elementPrefix for consistency. change it if needed.
        if (!namespaces.isEmpty() && (elementName != null)) {
            for (NamespaceBinding namespace : namespaces)
                if (namespace.getPrefix().equals(elementPrefix) && !namespace.getNamespaceURI().equals(elementNs))
                    elementPrefix = uniquePrefix(elementNs);
        }
        // Check element ns binding. it should be reconciled, now; there should be
        // no conflict from the existing namespaces, all of which are in namespaces
        if (elementPrefix != null && !inScope(elementPrefix, elementNs)) // needed, not present
            namespace(elementPrefix, elementNs); // declare it as is
        
        // uses elementNs, elementName, elementPrefix in base impl, plus elementType in sequence filter impl
        if (elementName != null) // only if called with open start tag
            outputCurrentElement();
        // now emit all the namespace events at once
        if (!namespaces.isEmpty()) 
        {
            for (NamespaceBinding namespace : namespaces)
                getOutputHandler().namespace(namespace.getPrefix(), namespace.getNamespaceURI());
            // clear the bindings; we're done with them.
            namespaces.clear();
        }
        
        if (!attributes.isEmpty()) 
        {
            // emit all the attribute events.  we've already insured that all the
            // attributes in namespaces have prefixes, and that the bindings are in scope
            for (Attr a : attributes)
                outputAttribute(a);
            // done, clear that set
            attributes.clear();
        }
    }
    
    private boolean inScope(String prefix, String uri)
    {
        for(Map<String, String> scope : scopeDeque) {
            String bound = scope.get(prefix);
            if (bound != null)
            {
                if (uri.equals(bound))
                    return true;
                // This prefix is not bound to this namespace in scope; even if
                // a more distant ancestor has bound it, we need to return false 
                // here and stop looking.
                break;
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
        // look through the scopes, from most recent
        for (Map<String, String> scope : scopeDeque) 
        {
            for (Map.Entry<String, String> binding : scope.entrySet())
            {
                // does this scope have a binding of this namespace?
                if (binding.getValue().equals(ns))
                    return binding.getKey(); // return it
                // note that the return may not be inScope(prefix, ns)!
            }
        }
        // didn't find anyone binding this namespace
        return null;
    }
    
    private String getURIforPrefix(String prefix)
    {
        // look through the scopes, from most recent
        for (Map<String, String> scope : scopeDeque)
        {
            for (Map.Entry<String, String> binding : scope.entrySet())
            {
                // if this scope has a binding of this prefix
                if (binding.getKey().equals(prefix))
                    return binding.getValue(); // return it
                // note that we never return out-of-scope
            }
        }
        // no previous binding for this prefix
        return null;
    }
    
    private String uniquePrefix(String uri)
    {
        // if we get here, we know that the original prefix for the supplied
        // uri isn't acceptable. first, check for a bound prefix:
        String prefix = getPrefixForURI(uri);
        if ((prefix == null) || !inScope(prefix, uri))
        {
            // either there is no binding for the URI in scope (which is weird,
            // because we should only be called when there's a conflict), or
            // the requested prefix is already bound to some other namespace.
            // so, ask for a new one.
            prefix = nextPrefix();
            while (getURIforPrefix(prefix) != null) // is the generated prefix already bound?
                prefix = nextPrefix(); // generate another one.
        }
        return prefix; // either existing was in scope, or we created a new one
    }
    
    private String nextPrefix() { // a *very* simpleminded unique prefix generator
        return PREFIX_PREFIX + counter++;
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
    }
    
    protected ArrayList<NamespaceBinding> namespaces = new ArrayList<NamespaceBinding>();
    protected ArrayList<NamespaceBinding> requiredAttNsBindings = new ArrayList<NamespaceBinding>();
    protected String elementNs;
    protected String elementPrefix;
    protected String elementName;
    protected ArrayList<Attr> attributes = new ArrayList<Attr>();
    protected Deque<Map<String, String>> scopeDeque = new ArrayDeque<Map<String, String>>();
    protected int counter = 0;
    
    private static final String PREFIX_PREFIX = "ns";
}
