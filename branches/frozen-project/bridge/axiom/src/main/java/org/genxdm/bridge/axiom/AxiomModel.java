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
package org.genxdm.bridge.axiom;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.bridgekit.axes.IterableAncestorAxis;
import org.genxdm.bridgekit.axes.IterableAncestorOrSelfAxis;
import org.genxdm.bridgekit.axes.IterableChildAxis;
import org.genxdm.bridgekit.axes.IterableChildAxisElements;
import org.genxdm.bridgekit.axes.IterableChildAxisElementsByName;
import org.genxdm.bridgekit.axes.IterableDescendantAxis;
import org.genxdm.bridgekit.axes.IterableDescendantOrSelfAxis;
import org.genxdm.bridgekit.axes.IterableFollowingAxis;
import org.genxdm.bridgekit.axes.IterableFollowingSiblingAxis;
import org.genxdm.bridgekit.axes.IterablePrecedingAxis;
import org.genxdm.bridgekit.axes.IterablePrecedingSiblingAxis;
import org.genxdm.bridgekit.names.QNameComparator;
import org.genxdm.bridgekit.tree.Ordering;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.names.NamespaceBinding;

public class AxiomModel
    implements Model<Object>
{

    public int compare(Object one, Object two)
    {
        return Ordering.compareNodes(one, two, this);
    }

    public Iterable<Object> getAncestorAxis(Object node)
    {
        PreCondition.assertNotNull(node);
        return new IterableAncestorAxis<Object>(node, this);
    }

    public Iterable<Object> getAncestorOrSelfAxis(Object node)
    {
        PreCondition.assertNotNull(node);
        return new IterableAncestorOrSelfAxis<Object>(node, this);
    }

    public OMAttribute getAttribute(final Object parent, final String namespaceURI, final String localName)
    {
        final OMElement element = AxiomSupport.dynamicDowncastElement(parent);
        if (null != element)
        {
            return element.getAttribute(new QName(namespaceURI.toString(), localName.toString()));
        }
        else
        {
            return null;
        }
    }

    public Iterable<Object> getAttributeAxis(final Object node, final boolean inherit)
    {
        PreCondition.assertNotNull(node);
        final OMElement element = AxiomSupport.dynamicDowncastElement(node);
        if (element != null)
        {
            boolean hasLang = false;
            boolean hasSpace = false;
            boolean hasBase = false;
            final ArrayList<Object> attributes = new ArrayList<Object>();
            @SuppressWarnings("unchecked")
            final Iterator<OMAttribute> it = element.getAllAttributes();
            while (it.hasNext())
            {
                OMAttribute a = it.next();
                attributes.add(a);
                if (inherit)
                    {
                    QName n = a.getQName();
                    if (n.getNamespaceURI().equals(XMLConstants.XML_NS_URI))
                    {
                        String l = n.getLocalPart();
                        if (l.equals("lang"))
                            hasLang = true;
                        else if (l.equals("space"))
                            hasSpace = true;
                        else if (l.equals("base"))
                            hasBase = true;
                    }
                }
            }
            if (inherit)
            {
                OMContainer parent = element;
                do {
                    parent = getParent(parent);
                    if ( (parent != null) && (parent instanceof OMElement) )
                    {
                        Iterable<Object> parentAtts = getAttributeAxis(parent, false);
                        for (Object o : parentAtts)
                        {
                            OMAttribute a = AxiomSupport.dynamicDowncastAttribute(o);
                            QName aName = a.getQName();
                            if (aName.getNamespaceURI().equals(XMLConstants.XML_NS_URI))
                            {
                                // TODO: should these be new faux attributes?
                                String n = aName.getLocalPart();
                                if (n.equals("lang") && !hasLang)
                                {
                                    attributes.add(a);
                                    hasLang = true;
                                }
                                else if (n.equals("space") && !hasSpace)
                                {
                                    attributes.add(a);
                                    hasSpace = true;
                                }
                                else if (n.equals("base") && !hasBase)
                                {
                                    attributes.add(a);
                                    hasBase = true;
                                }
                            }
                        }
                    }
                } while (parent != null);
            }
            return attributes; // enhanced with scoped atts if inherit == true, otherwise not
        }
        return Collections.emptyList();
    }

    public Iterable<QName> getAttributeNames(final Object node, final boolean orderCanonical)
    {
        final OMElement element = AxiomSupport.dynamicDowncastElement(node);
        if (element != null)
        {
            final ArrayList<QName> names = new ArrayList<QName>();
            @SuppressWarnings("unchecked")
            final Iterator<OMAttribute> it = element.getAllAttributes();
            while (it.hasNext())
            {
                names.add(it.next().getQName());
            }
            if (orderCanonical)
            {
                Collections.sort(names, new QNameComparator());
            }
            return names;
        }
        return null;
    }

    public String getAttributeStringValue(Object parent, String namespaceURI, String localName)
    {
        OMAttribute attribute = getAttribute(parent, namespaceURI, localName);
        if (attribute != null)
            return attribute.getAttributeValue();
        return null;
    }
    
    public URI getBaseURI(final Object node)
    {
        // TODO: resolve this problem.
        // axiom doesn't support XML:Base, it appears.
        return null;
    }

    public Iterable<Object> getChildAxis(Object node)
    {
        if (node != null)
            return new IterableChildAxis<Object>(node, this);
        return null;
    }

    public Iterable<Object> getChildElements(Object node)
    {
        if (node != null)
            return new IterableChildAxisElements<Object>(node, this);
        return null;
    }

    public Iterable<Object> getChildElementsByName(final Object node, final String namespaceURI, final String localName)
    {
        if (null != node)
        {
            return new IterableChildAxisElementsByName<Object>(node, namespaceURI, localName, this);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Iterable<Object> getDescendantAxis(Object node)
    {
        if (node != null)
            return new IterableDescendantAxis<Object>(node, this);
        return null;
    }

    public Iterable<Object> getDescendantOrSelfAxis(Object node)
    {
        if (node != null)
            return new IterableDescendantOrSelfAxis<Object>(node, this);
        return null;
    }

    public URI getDocumentURI(final Object node)
    {
        // TODO: resolve missing axiom functionality
        return null;
    }
    
    public OMElement getElementById(final Object context, final String id)
    {
        // note: this depends upon the map having been initialized.
        // TODO: check the size of the map? if it's empty, we might want
        // to try to look through the document for ids.  barf-puke, but eh.
        Map<String, OMElement> idMap = AxiomSupport.getIdMap(AxiomSupport.dynamicDowncastDocument(getRoot(context)));
        return idMap.get(id);
    }

    public OMNode getFirstChild(final Object origin)
    {
        final OMContainer container = AxiomSupport.dynamicDowncastContainer(origin);
        if (null != container)
        {
            return container.getFirstOMChild();
        }
        else
        {
            return null;
        }
    }

    public OMElement getFirstChildElement(final Object origin)
    {
        // it's probably an element container
        final OMElement element = AxiomSupport.dynamicDowncastElement(origin);
        if (element != null)
        {
            return element.getFirstElement();
        }
        else
        {
            // but it might be a document (nothing but documents and elements contain elements)
            final OMDocument document = AxiomSupport.dynamicDowncastDocument(origin);
            if (document != null)
            {
                return document.getOMDocumentElement();
            }
        }
        return null;
    }

    public OMElement getFirstChildElementByName(Object node, String namespaceURI, String localName)
    {
        final OMContainer container = AxiomSupport.dynamicDowncastContainer(node);
        if (container != null)
        {
            final QName name = new QName(namespaceURI, localName);
            return container.getFirstChildWithName(name);
        }
        return null;
    }

    public Iterable<Object> getFollowingAxis(Object node)
    {
        if (node != null)
            return new IterableFollowingAxis<Object>(node, this);
        return null;
    }

    public Iterable<Object> getFollowingSiblingAxis(Object node)
    {
        if (node != null)
            return new IterableFollowingSiblingAxis<Object>(node, this);
        return null;
    }

    public OMNode getLastChild(final Object origin)
    {
        final OMContainer container = AxiomSupport.dynamicDowncastContainer(origin);
        if (null != container)
        {
            Object lastChild = null;
            final Iterator<?> children = container.getChildren();
            while (children.hasNext())
            {
                lastChild = children.next();
            }
            return ((OMNode)lastChild);
        }
        else
        {
            return null;
        }
    }

    public String getLocalName(Object node)
    {
        {
            final OMElement element = AxiomSupport.dynamicDowncastElement(node);
            if (null != element)
            {
                return element.getLocalName();
            }
        }
        {
            final OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(node);
            if (null != attribute)
            {
                return attribute.getLocalName();
            }
        }
        {
            final OMNamespace namespace = AxiomSupport.dynamicDowncastNamespace(node);
            if (null != namespace)
            {
                return namespace.getPrefix();
            }
        }
        {
            final OMProcessingInstruction pi = AxiomSupport.dynamicDowncastProcessingInstruction(node);
            if (null != pi)
            {
                return pi.getTarget();
            }
        }
        switch (AxiomSupport.getNodeKind(node))
        {
            case DOCUMENT:
            case COMMENT:
            case TEXT:
            {
                return null;
            }
            default:
            {
                throw new AssertionError(AxiomSupport.getNodeKind(node));
            }
        }
    }

    public Iterable<Object> getNamespaceAxis(final Object node, final boolean inherit)
    {
        PreCondition.assertNotNull(node);
        final OMElement origin = AxiomSupport.dynamicDowncastElement(node);
        if (origin != null)
        {
            if (inherit)
            {
                return getNamespacesInScope(origin);
            }
            else
            {
                return getNamespaces(origin);
            }
        }
        return Collections.emptyList();
    }

    public Iterable<NamespaceBinding> getNamespaceBindings(final Object node)
    {
        // TODO: review this for correctness?
        final OMElement element = AxiomSupport.dynamicDowncastElement(node);
        if (null != element)
        {
            /**
             * Axiom's getAllDeclaredNamespaces method returns only those prefix mappings that are declared locally. This is what we want for this method. However, it also returns a prefix mapping, ""=>"", even if this has not been explicitly declared. There is no way
             * of telling whether it is explicitly declared or implicitly added by the Axiom implementation. We can remove it but should also check first to see if it is being added as a cancellation. The alternative may be to have all implementations return this
             * mapping if the element is in the global namespace.
             */
            @SuppressWarnings("unchecked")
            final Iterator<OMNamespace> namespaces = element.getAllDeclaredNamespaces();
            if (namespaces.hasNext())
            {
                final ArrayList<NamespaceBinding> names = new ArrayList<NamespaceBinding>();
                while (namespaces.hasNext())
                {
                    final OMNamespace namespace = namespaces.next();
                    final String prefix = namespace.getPrefix();
                    final String uri = namespace.getNamespaceURI();
                    if (uri.length() == 0 && prefix.length() == 0)
                    {
                        if (isNamespaceCancellationRequired(element))
                        {
                            names.add(new NamespaceBinding()
                            {

                                public String getNamespaceURI()
                                {
                                    return uri;
                                }

                                public String getPrefix()
                                {
                                    return XMLConstants.DEFAULT_NS_PREFIX;
                                }
                            });
                        }
                    }
                    else
                    {
                        names.add(new NamespaceBinding()
                        {

                            public String getNamespaceURI()
                            {
                                return uri;
                            }

                            public String getPrefix()
                            {
                                return prefix;
                            }
                        });
                    }
                }
                return names;
            }
            else
            {
                return Collections.emptyList();
            }
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public String getNamespaceForPrefix(final Object node, final String prefix)
    {
        for (NamespaceBinding binding : getNamespaceBindings(node))
        {
            if (binding.getPrefix().equals(prefix))
                return binding.getNamespaceURI();
        }
        return null;
    }

    public Iterable<String> getNamespaceNames(final Object node, final boolean orderCanonical)
    {
        final OMElement element = AxiomSupport.dynamicDowncastElement(node);
        if (null != element)
        {
            final ArrayList<String> names = new ArrayList<String>();
            for (Object ns : getNamespaces(element))
            {
                OMNamespace namespace = AxiomSupport.dynamicDowncastFauxNamespace(ns);
                final String prefix = namespace.getPrefix();
                final String uri = namespace.getNamespaceURI();
                if (uri.length() == 0 && prefix.length() == 0)
                {
                    if (isNamespaceCancellationRequired(element))
                    {
                        names.add(XMLConstants.DEFAULT_NS_PREFIX);
                    }
                }
                else
                {
                    if (isNamespaceDeclarationRequired(prefix, uri, element))
                    {
                        names.add(prefix);
                    }
                }
            }
            if (orderCanonical)
            {
                Collections.sort(names);
            }
            if (names.size() > 0)
                return names;
        }
        return Collections.emptyList();
    }

    public Iterable<Object> getNamespaces(final OMElement element)
    {
        @SuppressWarnings("unchecked")
        final Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
        if (it.hasNext())
        {
            final ArrayList<Object> namespaces = new ArrayList<Object>();
            while (it.hasNext())
            {
                final OMNamespace namespace = it.next();
                final String prefix = namespace.getPrefix();
                final String uri = namespace.getNamespaceURI();
                if (uri.length() == 0 && prefix.length() == 0)
                {
                    if (isNamespaceCancellationRequired(element))
                    {
                        namespaces.add(new FauxNamespace(namespace, element));
                    }
                }
                else
                {
                    // ignore the xml namespace
                    if (!prefix.equals("xml"))
                        namespaces.add(new FauxNamespace(namespace, element));
                }
            }
            return namespaces;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Iterable<Object> getNamespacesInScope(final OMElement element)
    {
        final LinkedList<OMElement> chain = new LinkedList<OMElement>();
        OMElement ancestorOrSelf = element;
        while (null != ancestorOrSelf)
        {
            chain.addFirst(ancestorOrSelf);
            ancestorOrSelf = AxiomSupport.dynamicDowncastElement(ancestorOrSelf.getParent());
        }
        final Map<String, Object> namespaces = new HashMap<String, Object>();
        for (final OMElement link : chain)
        {
            @SuppressWarnings("unchecked")
            final Iterator<OMNamespace> it = link.getAllDeclaredNamespaces();
            while (it.hasNext())
            {
                final OMNamespace namespace = it.next();
                final String prefix = namespace.getPrefix();
                final String uri = namespace.getNamespaceURI();
                if (uri.length() == 0 && prefix.length() == 0)
                {
                    if (namespaces.containsKey(prefix))
                    {
                        namespaces.remove(prefix);
                    }
                }
                else
                {
                    namespaces.put(prefix, new FauxNamespace(namespace, element));
                }
            }
        }
        namespaces.put(XMLConstants.XML_NS_PREFIX, new FauxNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI, (OMContainer)getRoot(element)));
        return namespaces.values();
    }

    public String getNamespaceURI(Object node)
    {
        {
            final OMElement element = AxiomSupport.dynamicDowncastElement(node);
            if (null != element)
            {
                // namespace may be null.
                if (element.getNamespace() == null)
                {
                    return XMLConstants.NULL_NS_URI;
                }
                return element.getNamespace().getNamespaceURI();
            }
        }
        {
            final OMNamespace namespace = AxiomSupport.dynamicDowncastNamespace(node);
            if (null != namespace)
            {
                return XMLConstants.NULL_NS_URI;
            }
        }
        {
            final OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(node);
            if (null != attribute)
            {
                OMNamespace ns = attribute.getNamespace();
                if (ns == null)
                    return "";
                return ns.getNamespaceURI();
            }
        }
        NodeKind kind = AxiomSupport.getNodeKind(node);
        if (kind != null) {
            switch (kind)
            {
                case DOCUMENT:
                case COMMENT:
                case TEXT:
                {
                    return null;
                }
                case PROCESSING_INSTRUCTION:
                {
                    return XMLConstants.NULL_NS_URI;
                }
                default:
                {
                    throw new AssertionError(AxiomSupport.getNodeKind(node));
                }
            }
        }
        return null;
    }

    public OMNode getNextSibling(final Object origin)
    {
        final OMNode node = AxiomSupport.dynamicDowncastNode(origin);
        if (null != node)
        {
            return node.getNextOMSibling();
        }
        else
        {
            // It could be document, attribute or namespace which aren't OMNode
            // and also don't have the concept of siblings.
            return null;
        }
    }

    public OMElement getNextSiblingElement(Object node)
    {
        return getNextSiblingElementByName(node, null, null);
    }

    public OMElement getNextSiblingElementByName(Object node, String namespaceURI, String localName)
    {
        OMNode nodely = AxiomSupport.dynamicDowncastNode(node);
        if (nodely != null)
        {
            OMNode next = nodely.getNextOMSibling();
            while (next != null) {
                if (matches(next, NodeKind.ELEMENT, namespaceURI, localName))
                {
                        return (OMElement)next;
                }
                next = next.getNextOMSibling();
            }
        }
        // TODO: just guessing that if you want a sibling element, you've got an element.
        // however, this may not be true, so we may need to enhance the else, here.
        return null;
    }

    public NodeKind getNodeKind(final Object origin)
    {
        return AxiomSupport.getNodeKind(origin);
    }

    public OMContainer getParent(final Object origin)
    {
        return AxiomSupport.getParent(origin);
    }

    public Iterable<Object> getPrecedingAxis(Object node)
    {
        if (node != null)
            return new IterablePrecedingAxis<Object>(node, this);
        return null;
    }

    public Iterable<Object> getPrecedingSiblingAxis(Object node)
    {
        if (node != null)
            return new IterablePrecedingSiblingAxis<Object>(node, this);
        return null;
    }

    public String getPrefix(final Object node)
    {
        {
            final OMElement element = AxiomSupport.dynamicDowncastElement(node);
            if (null != element)
            {
                // namespace may be null
                if (element.getNamespace() == null)
                {
                    return XMLConstants.DEFAULT_NS_PREFIX;
                }
                return element.getNamespace().getPrefix();
            }
        }
        {
            final OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(node);
            if (null != attribute)
            {
                OMNamespace ns = attribute.getNamespace();
                if (ns == null)
                    return "";
                return ns.getPrefix();
            }
        }
        {
            final OMNamespace namespace = AxiomSupport.dynamicDowncastNamespace(node);
            if (null != namespace)
            {
                // "getPrefix()" is absolutely *wrong*.
                return XMLConstants.DEFAULT_NS_PREFIX;
            }
        }
        switch (AxiomSupport.getNodeKind(node))
        {
            case DOCUMENT:
            case COMMENT:
            case TEXT:
            {
                return null;
            }
            case PROCESSING_INSTRUCTION:
            {
                return XMLConstants.DEFAULT_NS_PREFIX;
            }
            default:
            {
                throw new AssertionError(AxiomSupport.getNodeKind(node));
            }
        }
    }

    public OMNode getPreviousSibling(final Object origin)
    {
        final OMDocument document = AxiomSupport.dynamicDowncastDocument(origin);
        if (null != document)
        {
            return null;
        }
        else
        {
            final OMNode node = AxiomSupport.dynamicDowncastNode(origin);
            if (null != node)
            {
                final OMNode previous = node.getPreviousOMSibling();
                if (node == previous)
                {
                    // This is clearly a bug
                    return null;
                }
                else
                {
                    return previous;
                }
            }
            else
            {
                // It could be attribute or namespace which aren't OMNode
                // and also don't have the concept of siblings.
                return null;
            }
        }
    }

    public Object getRoot(final Object origin)
    {
        final OMContainer x = AxiomSupport.getParent(origin);
        if (x == null)
        {
            return origin;
        }
        else
        {
            return getRoot(x);
        }
    }

    public String getStringValue(final Object node)
    {
        {
            final OMContainer container = AxiomSupport.dynamicDowncastContainer(node);
            if (null != container)
            {
                @SuppressWarnings("unchecked")
                final Iterator<OMNode> children = container.getChildren();
                String first = null;
                if (children.hasNext())
                {
                    final OMNode child = children.next();
                    switch (getNodeKind(child))
                    {
                        case ELEMENT:
                        {
                            first = getStringValue(child);
                        }
                        break;
                        case TEXT:
                        {
                            first = ((OMText)child).getText();
                        }
                        break;
                        case COMMENT:
                        {
                            // Ignore
                        }
                        break;
                        default:
                        {
                            throw new AssertionError(getNodeKind(child));
                        }
                    }
                }
                if (children.hasNext())
                {
                    final StringBuilder sb = new StringBuilder();
                    if (null != first)
                    {
                        sb.append(first);
                    }
                    while (children.hasNext())
                    {
                        final OMNode child = children.next();
                        switch (getNodeKind(child))
                        {
                            case ELEMENT:
                            {
                                sb.append(getStringValue(child));
                            }
                            break;
                            case TEXT:
                            {
                                sb.append(((OMText)child).getText());
                            }
                            case COMMENT:
                            case PROCESSING_INSTRUCTION:
                            {
                                // Ignore
                            }
                            break;
                            default:
                            {
                                throw new AssertionError(getNodeKind(child));
                            }
                        }
                    }
                    return sb.toString();
                }
                else
                {
                    return (null != first) ? first : "";
                }
            }
        }
        {
            final OMText text = AxiomSupport.dynamicDowncastText(node);
            if (null != text)
            {
                return text.getText();
            }
        }
        {
            final OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(node);
            if (null != attribute)
            {
                return attribute.getAttributeValue();
            }
        }
        {
            final OMNamespace namespace = AxiomSupport.dynamicDowncastNamespace(node);
            if (null != namespace)
            {
                return namespace.getNamespaceURI();
            }
        }
        {
            final OMProcessingInstruction pi = AxiomSupport.dynamicDowncastProcessingInstruction(node);
            if (null != pi)
            {
                return pi.getValue();
            }
        }
        {
            final OMComment comment = AxiomSupport.dynamicDowncastComment(node);
            if (null != comment)
            {
                return comment.getValue();
            }
        }
        throw new AssertionError("getStringValue(" + node + ")");
    }

    public boolean hasAttributes(final Object node)
    {
        final OMElement element = AxiomSupport.dynamicDowncastElement(node);
        if (null != element)
        {
            return element.getAllAttributes().hasNext();
        }
        else
        {
            return false;
        }
    }

    public boolean hasChildren(final Object node)
    {
        final OMContainer container = AxiomSupport.dynamicDowncastContainer(node);
        if (null != container)
        {
            return container.getChildren().hasNext();
        }
        else
        {
            return false;
        }
    }

    public boolean hasNamespaces(final Object node)
    {
        final OMElement element = AxiomSupport.dynamicDowncastElement(node);
        if (null != element)
        {
            @SuppressWarnings("unchecked")
            final Iterator<OMNamespace> namespaces = element.getAllDeclaredNamespaces();
            while (namespaces.hasNext())
            {
                // oh, axiom, shame on you!
                // the default namespace is reported as "declared"
                OMNamespace ns = (OMNamespace)namespaces.next();
                if (! (ns.getPrefix().equals("") && ns.getNamespaceURI().equals("")) )
                    return true;
            }
            return false;
        }
        else
        {
            return false;
        }
    }

    public boolean hasNextSibling(final Object node)
    {
        {
            final OMElement element = AxiomSupport.dynamicDowncastElement(node);
            {
                if (null != element)
                {
                    return element.getNextOMSibling() != null;
                }
            }
        }
        {
            final OMText text = AxiomSupport.dynamicDowncastText(node);
            {
                if (null != text)
                {
                    return text.getNextOMSibling() != null;
                }
            }
        }
        {
            final OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(node);
            {
                if (null != attribute)
                {
                    return false;
                }
            }
        }
        {
            final OMNamespace namespace = AxiomSupport.dynamicDowncastNamespace(node);
            {
                if (null != namespace)
                {
                    return false;
                }
            }
        }
        {
            final OMDocument document = AxiomSupport.dynamicDowncastDocument(node);
            {
                if (null != document)
                {
                    return false;
                }
            }
        }
        {
            final OMComment comment = AxiomSupport.dynamicDowncastComment(node);
            {
                if (null != comment)
                {
                    return comment.getNextOMSibling() != null;
                }
            }
        }
        {
            final OMProcessingInstruction pi = AxiomSupport.dynamicDowncastProcessingInstruction(node);
            {
                if (null != pi)
                {
                    return pi.getNextOMSibling() != null;
                }
            }
        }
        throw new AssertionError("hasNextSibling(" + node + ")");
    }

    public boolean hasParent(final Object node)
    {
        {
            final OMElement element = AxiomSupport.dynamicDowncastElement(node);
            if (null != element)
            {
                return (null != element.getParent());
            }
        }
        {
            final OMText text = AxiomSupport.dynamicDowncastText(node);
            if (null != text)
            {
                return (null != text.getParent());
            }
        }
        {
            final OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(node);
            {
                if (null != attribute)
                {
                    return attribute.getOwner() != null;
                }
            }
        }
        {
            final FauxNamespace namespace = AxiomSupport.dynamicDowncastFauxNamespace(node);
            if (null != namespace)
            {
                return namespace.getParent() != null;
            }
        }
        {
            final OMNamespace namespace = AxiomSupport.dynamicDowncastNamespace(node);
            if (null != namespace)
            {
                return false;
            }
        }
        if (null != AxiomSupport.dynamicDowncastDocument(node))
        {
            return false;
        }
        {
            final OMProcessingInstruction pi = AxiomSupport.dynamicDowncastProcessingInstruction(node);
            if (null != pi)
            {
                return (null != pi.getParent());
            }
        }
        {
            final OMComment comment = AxiomSupport.dynamicDowncastComment(node);
            if (null != comment)
            {
                return (null != comment.getParent());
            }
        }
        throw new AssertionError("hasParent(" + node + ")");
    }

    public boolean hasPreviousSibling(final Object node)
    {
        {
            final OMElement element = AxiomSupport.dynamicDowncastElement(node);
            {
                if (null != element)
                {
                    return element.getPreviousOMSibling() != null;
                }
            }
        }
        {
            final OMText text = AxiomSupport.dynamicDowncastText(node);
            {
                if (null != text)
                {
                    return text.getPreviousOMSibling() != null;
                }
            }
        }
        {
            final OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(node);
            {
                if (null != attribute)
                {
                    return false;
                }
            }
        }
        {
            final OMNamespace namespace = AxiomSupport.dynamicDowncastNamespace(node);
            {
                if (null != namespace)
                {
                    return false;
                }
            }
        }
        {
            final OMDocument document = AxiomSupport.dynamicDowncastDocument(node);
            {
                if (null != document)
                {
                    return false;
                }
            }
        }
        {
            final OMComment comment = AxiomSupport.dynamicDowncastComment(node);
            {
                if (null != comment)
                {
                    return comment.getPreviousOMSibling() != null;
                }
            }
        }
        {
            final OMProcessingInstruction pi = AxiomSupport.dynamicDowncastProcessingInstruction(node);
            {
                if (null != pi)
                {
                    return pi.getPreviousOMSibling() != null;
                }
            }
        }
        throw new AssertionError("hasPreviousSibling(" + node + ")");
    }

    public boolean isAttribute(final Object node)
    {
        if (null != AxiomSupport.dynamicDowncastAttribute(node))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isElement(final Object node)
    {
        if (null != AxiomSupport.dynamicDowncastElement(node))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isNamespace(final Object node)
    {
        if (null != AxiomSupport.dynamicDowncastNamespace(node))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean isId(final Object node)
    {
        if (isAttribute(node))
        {
            OMAttribute att = AxiomSupport.dynamicDowncastAttribute(node);
            if (att.getAttributeType().equals("ID")) return true;
            OMNamespace omNs = att.getNamespace();
            if (omNs != null && omNs.getNamespaceURI().equals(XMLConstants.XML_NS_URI) &&
                att.getLocalName().equals("id"))
                return true;
        }
        if (isElement(node))
        {
            for (Object o : getAttributeAxis(node, false))
            {
                if (isId(o)) return true;
            }
        }
        // falls through, if you weren't paying attention.
        return false;
    }
    
    public boolean isIdRefs(final Object node)
    {
        if (isAttribute(node))
        {
            OMAttribute att = AxiomSupport.dynamicDowncastAttribute(node);
            return att.getAttributeType().startsWith("IDREF");
        }
        if (isElement(node))
        {
            for (Object o : getAttributeAxis(node, false))
            {
                if (isIdRefs(o)) return true;
            }
        }
        return false;
    }

    public Object getNodeId(final Object node)
    {
        if (node instanceof OMAttribute)
            return new AttributeIdentity((OMAttribute)node);
        if (node instanceof OMNamespace)
            return new NamespaceIdentity((OMNamespace)node);
        return node;
    }

    public boolean isText(final Object node)
    {
        if (null != AxiomSupport.dynamicDowncastText(node))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean matches(Object node, NodeKind nodeKind, String namespaceURI, String localName)
    {
        if (nodeKind != null)
        {
            if (getNodeKind(node) != nodeKind)
            {
                return false;
            }
        }
        return matches(node, namespaceURI, localName);
    }

    public boolean matches(final Object node, final String namespaceArg, final String localNameArg)
    {
        if (namespaceArg != null)
        {
            final String namespace = getNamespaceURI(node);
            if (null != namespace)
            {
                if (!namespaceArg.equals(namespace))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        if (localNameArg != null)
        {
            final String localName = getLocalName(node);
            if (null != localName)
            {
                if (!localNameArg.equals(localName))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void stream(Object node, ContentHandler handler)
        throws GenXDMException
    {
        switch (getNodeKind(node))
        {
            case ELEMENT:
                {
                    OMElement element = AxiomSupport.dynamicDowncastElement(node);
                    handler.startElement(element.getQName().getNamespaceURI(), element.getQName().getLocalPart(), element.getQName().getPrefix());
                    if (hasNamespaces(node))
                    {
                        Iterator it = element.getAllDeclaredNamespaces();
                        while (it.hasNext())
                        {
                            stream(it.next(), handler);
                        }
                    }
                    if (hasAttributes(node))
                    {
                        Iterator it = element.getAllAttributes();
                        while (it.hasNext())
                        {
                            stream(it.next(), handler);
                        }
                    }
                    if (hasChildren(node))
                    {
                        Iterator it = element.getChildren();
                        while (it.hasNext())
                        {
                            stream(it.next(), handler);
                        }
                    }
                    handler.endElement();
                }
                break;
            case ATTRIBUTE:
                {
                    OMAttribute attribute = AxiomSupport.dynamicDowncastAttribute(node);
                    final String prefix = attribute.getQName().getPrefix();
                    handler.attribute(attribute.getQName().getNamespaceURI(), attribute.getQName().getLocalPart(), prefix, attribute.getAttributeValue(), DtdAttributeKind.get(attribute.getAttributeType()));
                }
                break;
                case TEXT:
                {
                    OMText text = AxiomSupport.dynamicDowncastText(node);
                    handler.text(text.getText());
                }
                break;
            case DOCUMENT:
                {
                    OMDocument doc = AxiomSupport.dynamicDowncastDocument(node);
                    // TODO: i don't think that this is quite right.
                    handler.startDocument(getDocumentURI(node), "");
                    Iterator it = doc.getChildren();
                    while (it.hasNext())
                    {
                        stream(it.next(), handler);
                    }
                    handler.endDocument();
                }
                break;
            case NAMESPACE:
                {
                    OMNamespace ns = AxiomSupport.dynamicDowncastNamespace(node);
                    handler.namespace(ns.getPrefix(), ns.getNamespaceURI());
                }
                break;
            case COMMENT:
                {
                    OMComment comment = AxiomSupport.dynamicDowncastComment(node);
                    handler.comment(comment.getValue());
                }
                break;
            case PROCESSING_INSTRUCTION:
                {
                    OMProcessingInstruction pi = AxiomSupport.dynamicDowncastProcessingInstruction(node);
                    handler.processingInstruction(pi.getTarget(), pi.getValue());
                }
                break;
            default:
                {
                    throw new AssertionError(getNodeKind(node));
                }
        }
    }

    /**
     * Determines whether the cancellation, xmlns="", is required to ensure correct semantics.
     * 
     * @param element
     *            The element that would be the parent of the cancellation.
     * @return <code>true</code> if the cancellation is required.
     */
    private static boolean isNamespaceCancellationRequired(final OMElement element)
    {
        final OMContainer parent = element.getParent();
        if (null != parent)
        {
            final OMElement scope = AxiomSupport.dynamicDowncastElement(parent);
            if (null != scope)
            {
                final OMNamespace scopeDefaultNS = scope.findNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX);
                if (null != scopeDefaultNS)
                {
                    if (scopeDefaultNS.getNamespaceURI().length() > 0)
                    {
                        return true;
                    }
                    else
                    {
                        // The scope is the global namespace so the cancellation
                        // can be ignored.
                    }
                }
                else
                {
                    // There does not seem to be any conflict so ignore the
                    // mapping.
                }
            }
            else
            {
                // The parent must be a document, so the mapping must be
                // spurious.
            }
        }
        else
        {
            // If there is no parent then the mapping is ambiguous. Ignore it.
        }
        return false;
    }

    private static boolean isNamespaceDeclarationRequired(final String prefix, final String uri, final OMElement element)
    {
        final OMContainer parent = element.getParent();
        if (null != parent)
        {
            OMElement scope = AxiomSupport.dynamicDowncastElement(parent);
            while (null != scope)
            {
                final OMNamespace namespace = scope.findNamespaceURI(prefix);
                if (null != namespace)
                {
                    // The prefix is mapped to something in a higher scope.
                    if (!namespace.getNamespaceURI().equals(uri))
                    {
                        // The mapping must be overridden.
                        return true;
                    }
                    else
                    {
                        // The mapping already exists.
                        return false;
                    }
                }
                scope = AxiomSupport.dynamicDowncastElement(scope.getParent());
            }
        }
        else
        {
            // If there is no parent then the mapping is required.
        }
        return true;
    }

}
