package org.genxdm.bridgekit.names;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.names.NamespaceBinding;

public class DefaultNamespaceContext
    implements NamespaceContext
{
    public DefaultNamespaceContext(Iterable<NamespaceBinding> bindingsInScope)
    {
        inScope = bindingsInScope;
    }
    
    // this is a much more useful constructor, you know
    public <N> DefaultNamespaceContext(Model<N> model, N context)
    {
        // iterate from the starting point to the root via ancestor axis,
        // at each element getting local namespace bindings and adding any that
        // are not already bound in descendant context.
        List<NamespaceBinding> bindings = new ArrayList<NamespaceBinding>();
        Set<String> prefixes = new HashSet<String>();
        do {
            // set context to null if we're at the root
            if (model.getNodeKind(context) == NodeKind.DOCUMENT)
                context = null;
            // if we have an element enhance the binding list
            if (model.isElement(context))
            {
                // check locally declared bindings
                for (NamespaceBinding binding : model.getNamespaceBindings(context))
                {
                    // if the prefix hasn't already (in a descendant) been bound, bind
                    String prefix = binding.getPrefix();
                    if (!prefixes.contains(prefix))
                    {
                        prefixes.add(prefix);
                        bindings.add(binding);
                    }
                }
            }
            // finished with child, move to parent
            context = model.getParent(context);
        } while (context != null); // stop when there's no more ancestors
        // set the inScope to the list
        inScope = bindings;
    }

    @Override
    public String getNamespaceURI(String prefix)
    {
        if (prefix == null)
            throw new IllegalArgumentException();
        if (prefix.startsWith(XMLConstants.XML_NS_PREFIX))
        {
            if (prefix.equals(XMLConstants.XML_NS_PREFIX))
                return XMLConstants.XML_NS_URI;
            if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE))
                return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }
        for (NamespaceBinding binding : inScope)
        {
            if (binding.getPrefix().equals(prefix))
                return binding.getNamespaceURI();
        }
        return XMLConstants.NULL_NS_URI;
    }

    @Override
    public String getPrefix(String namespaceURI)
    {
        if (namespaceURI == null)
            throw new IllegalArgumentException();
        if (namespaceURI.equals(XMLConstants.NULL_NS_URI))
            return XMLConstants.DEFAULT_NS_PREFIX;
        if (namespaceURI.equals(XMLConstants.XML_NS_URI))
            return XMLConstants.XML_NS_PREFIX;
        if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
            return XMLConstants.XMLNS_ATTRIBUTE;
        for (NamespaceBinding binding : inScope)
        {
            if (binding.getNamespaceURI().equals(namespaceURI))
                return binding.getPrefix();
        }
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI)
    {
        if (namespaceURI == null)
            throw new IllegalArgumentException();
        if (namespaceURI.equals(XMLConstants.XML_NS_URI))
            return new UnaryIterable<String>(XMLConstants.XML_NS_PREFIX).iterator();
        if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
            return new UnaryIterable<String>(XMLConstants.XMLNS_ATTRIBUTE).iterator();
        List<String> prefixesList = new ArrayList<String>();
        for (NamespaceBinding binding : inScope)
        {
            if (binding.getNamespaceURI().equals(namespaceURI))
                prefixesList.add(binding.getPrefix());
        }
        if (prefixesList.isEmpty() && namespaceURI.equals(XMLConstants.NULL_NS_URI))
            return new UnaryIterable<String>(XMLConstants.DEFAULT_NS_PREFIX).iterator();
        return new UnaryIterable<String>(null).iterator(); // empty iterator
    }

    private final Iterable<NamespaceBinding> inScope;
}
