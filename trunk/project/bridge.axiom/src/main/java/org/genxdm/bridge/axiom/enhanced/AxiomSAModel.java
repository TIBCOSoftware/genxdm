/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.bridge.axiom.enhanced;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.genxdm.NodeKind;
import org.genxdm.bridge.axiom.AxiomModel;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.ContentHandler;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;

/**
 * Note that some implementations of Axiom implement {@link OMNode} on their 
 * document node but don't support the OMNode interface methods. This means 
 * that we have to check for a document first in order not to throw 
 * unchecked exceptions.
 * 
 */
final class AxiomSAModel implements TypedModel<Object, XmlAtom>
{
    public AxiomSAModel(final AxiomModel delegate, final AtomBridge<XmlAtom> atomBridge)
    {
        this.delegate = delegate;
        this.atomBridge = atomBridge;
    }

    public int compare(final Object one, final Object two)
    {
        return delegate.compare(one, two);
    }

    public Iterable<Object> getAncestorAxis(Object origin)
    {
        return delegate.getAncestorAxis(origin);
    }

    public Iterable<Object> getAncestorOrSelfAxis(Object origin)
    {
        return delegate.getAncestorOrSelfAxis(origin);
    }

    public OMAttribute getAttribute(final Object parent, final String namespaceURI, final String localName)
    {
        return delegate.getAttribute(parent, namespaceURI, localName);
    }

    public Iterable<Object> getAttributeAxis(final Object node, final boolean inherit)
    {
        return delegate.getAttributeAxis(node, inherit);
    }

    public Iterable<QName> getAttributeNames(final Object node, final boolean orderCanonical)
    {
        return delegate.getAttributeNames(node, orderCanonical);
    }

    public String getAttributeStringValue(final Object parent, final String namespaceURI, final String localName)
    {
        return delegate.getAttributeStringValue(parent, namespaceURI, localName);
    }

    public List<XmlAtom> getAttributeValue(final Object parent, final String namespaceURI, final String localName)
    {
        // TODO: implement
        throw new UnsupportedOperationException();
    }

    public QName getAttributeTypeName(final Object parent, final String namespaceURI, final String localName)
    {
        // TODO: implement
        throw new UnsupportedOperationException();
    }
    
    public URI getBaseURI(final Object node)
    {
        return delegate.getBaseURI(node);
    }

    public Iterable<Object> getChildAxis(final Object node)
    {
        return delegate.getChildAxis(node);
    }

    public Iterable<Object> getChildElements(final Object node)
    {
        return delegate.getChildElements(node);
    }

    public Iterable<Object> getChildElementsByName(final Object node, final String namespaceURI, final String localName)
    {
        return delegate.getChildElementsByName(node, namespaceURI, localName);
    }

    public Iterable<Object> getDescendantAxis(Object origin)
    {
        return delegate.getDescendantAxis(origin);
    }

    public Iterable<Object> getDescendantOrSelfAxis(Object origin)
    {
        return delegate.getDescendantOrSelfAxis(origin);
    }

    public URI getDocumentURI(final Object node)
    {
        return delegate.getDocumentURI(node);
    }
    
    public OMElement getElementById(final Object context, final String id)
    {
        return delegate.getElementById(context, id);
    }

    public OMNode getFirstChild(final Object origin)
    {
        return delegate.getFirstChild(origin);
    }

    public Object getFirstChildElement(final Object origin)
    {
        return delegate.getFirstChildElement(origin);
    }

    public Object getFirstChildElementByName(Object origin, String namespaceURI, String localName)
    {
        return delegate.getFirstChildElementByName(origin, namespaceURI, localName);
    }

    public Iterable<Object> getFollowingAxis(Object origin)
    {
        return delegate.getFollowingAxis(origin);
    }

    public Iterable<Object> getFollowingSiblingAxis(Object origin)
    {
        return delegate.getFollowingSiblingAxis(origin);
    }

    public Object getLastChild(final Object origin)
    {
        return delegate.getLastChild(origin);
    }

    public String getLocalName(final Object node)
    {
        return delegate.getLocalName(node);
    }

    public Iterable<Object> getNamespaceAxis(final Object node, final boolean inherit)
    {
        return delegate.getNamespaceAxis(node, inherit);
    }

    public Iterable<NamespaceBinding> getNamespaceBindings(final Object node)
    {
        return delegate.getNamespaceBindings(node);
    }
    
    public String getNamespaceForPrefix(final Object node, final String prefix)
    {
        return delegate.getNamespaceForPrefix(node, prefix);
    }

    public Iterable<String> getNamespaceNames(final Object node, final boolean orderCanonical)
    {
        return delegate.getNamespaceNames(node, orderCanonical);
    }

    public String getNamespaceURI(final Object node)
    {
        return delegate.getNamespaceURI(node);
    }

    public OMNode getNextSibling(final Object origin)
    {
        return delegate.getNextSibling(origin);
    }

    public Object getNextSiblingElement(Object node)
    {
        return delegate.getNextSiblingElement(node);
    }

    public Object getNextSiblingElementByName(Object node, String namespaceURI, String localName)
    {
        return delegate.getNextSiblingElementByName(node, namespaceURI, localName);
    }
    
    public Object getNodeId(final Object node)
    {
        return delegate.getNodeId(node);
    }

    public NodeKind getNodeKind(final Object origin)
    {
        return delegate.getNodeKind(origin);
    }

    public OMContainer getParent(final Object origin)
    {
        return delegate.getParent(origin);
    }

    public Iterable<Object> getPrecedingAxis(Object origin)
    {
        return delegate.getPrecedingAxis(origin);
    }

    public Iterable<Object> getPrecedingSiblingAxis(Object origin)
    {
        return delegate.getPrecedingSiblingAxis(origin);
    }

    public String getPrefix(final Object node)
    {
        return delegate.getPrefix(node);
    }

    public Object getPreviousSibling(final Object origin)
    {
        return delegate.getPreviousSibling(origin);
    }

    public Object getRoot(final Object origin)
    {
        return delegate.getRoot(origin);
    }

    public String getStringValue(final Object node)
    {
        return delegate.getStringValue(node);
    }

    public List<XmlAtom> getValue(Object node)
    {
        // TODO Auto-generated method stub
        throw new AssertionError("TODO");
    }

    public QName getTypeName(Object node)
    {
        // TODO: implement
        return null;
    }

    public boolean hasAttributes(final Object node)
    {
        return delegate.hasAttributes(node);
    }

    public boolean hasChildren(final Object node)
    {
        return delegate.hasChildren(node);
    }

    public boolean hasNamespaces(final Object node)
    {
        return delegate.hasNamespaces(node);
    }

    public boolean hasNextSibling(final Object node)
    {
        return delegate.hasNextSibling(node);
    }

    public boolean hasParent(final Object node)
    {
        return delegate.hasParent(node);
    }

    public boolean hasPreviousSibling(final Object node)
    {
        return delegate.hasPreviousSibling(node);
    }

    public boolean isAttribute(final Object node)
    {
        return delegate.isAttribute(node);
    }

    public boolean isElement(final Object node)
    {
        return delegate.isElement(node);
    }
    
    public boolean isId(final Object node)
    {
        return delegate.isId(node);
    }
    
    public boolean isIdRefs(final Object node)
    {
        return delegate.isIdRefs(node);
    }

    public boolean isNamespace(final Object node)
    {
        return delegate.isNamespace(node);
    }

    public boolean isText(final Object node)
    {
        return delegate.isText(node);
    }

    public boolean matches(final Object node, final NodeKind kind, String namespaceURI, String localName)
    {
        return delegate.matches(node, kind, namespaceURI, localName);
    }

    public boolean matches(final Object node, final String namespaceArg, final String localNameArg)
    {
        return delegate.matches(node, namespaceArg, localNameArg);
    }

    @Override
    public void stream(Object node, ContentHandler handler) throws GenXDMException
    {
        delegate.stream(node, handler); 
    }
    
    public void stream(Object node, SequenceHandler<XmlAtom> handler) throws GenXDMException
    {
    }

//    public Iterable<Object> getNamespaces(final Object node)
//    {
//        final OMElement element = AxiomSupport.dynamicDowncastElement(node);
//        if (null != element)
//        {
//            @SuppressWarnings("unchecked")
//            final Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
//            if (it.hasNext())
//            {
//                final ArrayList<Object> namespaces = new ArrayList<Object>();
//                while (it.hasNext())
//                {
//                    final OMNamespace namespace = it.next();
//                    final String prefix = namespace.getPrefix();
//                    final String uri = namespace.getNamespaceURI();
//                    if (uri.length() == 0 && prefix.length() == 0)
//                    {
//                        if (isNamespaceCancellationRequired(element))
//                        {
//                            namespaces.add(new FauxNamespace(prefix, uri, element));
//                        }
//                    }
//                    else
//                    {
//                        namespaces.add(new FauxNamespace(prefix, uri, element));
//                    }
//                }
//                return namespaces;
//            }
//            else
//            {
//                return Collections.emptyList();
//            }
//        }
//        else
//        {
//            return Collections.emptyList();
//        }
//    }
//
//    public Iterable<Object> getNamespacesInScope(final Object node)
//    {
//        final OMElement element = AxiomSupport.dynamicDowncastElement(node);
//        if (null != element)
//        {
//            final LinkedList<OMElement> chain = new LinkedList<OMElement>();
//            OMElement ancestorOrSelf = element;
//            while (null != ancestorOrSelf)
//            {
//                chain.addFirst(ancestorOrSelf);
//                ancestorOrSelf = AxiomSupport.dynamicDowncastElement(ancestorOrSelf.getParent());
//            }
//            final Map<String, Object> namespaces = new HashMap<String, Object>();
//            for (final OMElement link : chain)
//            {
//                @SuppressWarnings("unchecked")
//                final Iterator<OMNamespace> it = link.getAllDeclaredNamespaces();
//                while (it.hasNext())
//                {
//                    final OMNamespace namespace = it.next();
//                    final String prefix = namespace.getPrefix();
//                    final String uri = namespace.getNamespaceURI();
//                    if (uri.length() == 0 && prefix.length() == 0)
//                    {
//                        if (namespaces.containsKey(prefix))
//                        {
//                            namespaces.remove(prefix);
//                        }
//                    }
//                    else
//                    {
//                        namespaces.put(prefix, new FauxNamespace(prefix, uri, element));
//                    }
//                }
//            }
//            namespaces.put(XMLConstants.XML_NS_PREFIX, new FauxNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI, element));
//            return namespaces.values();
//        }
//        else
//        {
//            return Collections.emptyList();
//        }
//    }
//
//    public String getNamespaceURIInScope(final Object node, final String prefix)
//    {
//        if (XMLConstants.XML_NS_PREFIX.equals(prefix))
//        {
//            return XMLConstants.XML_NS_URI;
//        }
//        OMElement element = AxiomSupport.dynamicDowncastElement(node);
//        if (null != element)
//        {
//            while (null != element)
//            {
//                final OMNamespace namespace = element.findNamespaceURI(prefix);
//                if (null != namespace)
//                {
//                    return namespace.getNamespaceURI();
//                }
//                element = AxiomSupport.dynamicDowncastElement(element.getParent());
//            }
//            return null;
//        }
//        final OMDocument document = AxiomSupport.dynamicDowncastDocument(node);
//        if (null != document)
//        {
//            if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix))
//            {
//                return XMLConstants.NULL_NS_URI;
//            }
//        }
//        return null;
//    }
//
//    @SuppressWarnings("unused")
//    private Iterable<Object> getAttributes(final Object node)
//    {
//        final OMElement element = AxiomSupport.dynamicDowncastElement(node);
//        if (null != element)
//        {
//            final ArrayList<Object> attributes = new ArrayList<Object>();
//            @SuppressWarnings("unchecked")
//            final Iterator<OMAttribute> it = element.getAllAttributes();
//            while (it.hasNext())
//            {
//                attributes.add(it.next());
//            }
//            return attributes;
//        }
//        else
//        {
//            return Collections.emptyList();
//        }
//    }
//
//    @SuppressWarnings("unused")
//    private Iterable<Object> getAttributesInScope(final Object node)
//    {
//        final OMElement element = AxiomSupport.dynamicDowncastElement(node);
//        if (null != element)
//        {
//            final LinkedList<OMElement> chain = new LinkedList<OMElement>();
//            OMElement ancestorElement = AxiomSupport.dynamicDowncastElement(element.getParent());
//            while (null != ancestorElement)
//            {
//                chain.addFirst(ancestorElement);
//                ancestorElement = AxiomSupport.dynamicDowncastElement(ancestorElement.getParent());
//            }
//            final Map<QName, Object> attributes = new HashMap<QName, Object>();
//            for (final OMElement link : chain)
//            {
//                @SuppressWarnings("unchecked")
//                final Iterator<OMAttribute> it = link.getAllAttributes();
//                while (it.hasNext())
//                {
//                    final OMAttribute attribute = it.next();
//                    if (attribute.getNamespace().getNamespaceURI().equals(XMLConstants.XML_NS_URI))
//                    {
//                        attributes.put(attribute.getQName(), new FauxAttribute(attribute, element));
//                    }
//                }
//            }
//            @SuppressWarnings("unchecked")
//            final Iterator<OMAttribute> it = element.getAllAttributes();
//            while (it.hasNext())
//            {
//                final OMAttribute attribute = it.next();
//                attributes.put(attribute.getQName(), attribute);
//            }
//            return attributes.values();
//        }
//        else
//        {
//            return Collections.emptyList();
//        }
//    }
//
//    /**
//     * Determines whether the cancellation, xmlns="", is required to ensure correct semantics.
//     * 
//     * @param element
//     *            The element that would be the parent of the cancellation.
//     * @return <code>true</code> if the cancellation is required.
//     */
//    private static boolean isNamespaceCancellationRequired(final OMElement element)
//    {
//        final OMContainer parent = element.getParent();
//        if (null != parent)
//        {
//            final OMElement scope = AxiomSupport.dynamicDowncastElement(parent);
//            if (null != scope)
//            {
//                final OMNamespace scopeDefaultNS = scope.findNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX);
//                if (null != scopeDefaultNS)
//                {
//                    if (scopeDefaultNS.getNamespaceURI().length() > 0)
//                    {
//                        return true;
//                    }
//                    else
//                    {
//                        // The scope is the global namespace so the cancellation
//                        // can be ignored.
//                    }
//                }
//                else
//                {
//                    // There does not seem to be any conflict so ignore the
//                    // mapping.
//                }
//            }
//            else
//            {
//                // The parent must be a document, so the mapping must be
//                // spurious.
//            }
//        }
//        else
//        {
//            // If there is no parent then the mapping is ambiguous. Ignore it.
//        }
//        return false;
//    }
//
//    private static boolean isNamespaceDeclarationRequired(final String prefix, final String uri, final OMElement element)
//    {
//        final OMContainer parent = element.getParent();
//        if (null != parent)
//        {
//            OMElement scope = AxiomSupport.dynamicDowncastElement(parent);
//            while (null != scope)
//            {
//                final OMNamespace namespace = scope.findNamespaceURI(prefix);
//                if (null != namespace)
//                {
//                    // The prefix is mapped to something in a higher scope.
//                    if (!namespace.getNamespaceURI().equals(uri))
//                    {
//                        // The mapping must be overridden.
//                        return true;
//                    }
//                    else
//                    {
//                        // The mapping already exists.
//                        return false;
//                    }
//                }
//                scope = AxiomSupport.dynamicDowncastElement(scope.getParent());
//            }
//        }
//        else
//        {
//            // If there is no parent then the mapping is required.
//        }
//        return true;
//    }

    @SuppressWarnings("unused")
    private final AtomBridge<XmlAtom> atomBridge;
    private final AxiomModel delegate;
}
