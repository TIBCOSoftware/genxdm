/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
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
package org.genxdm.bridge.cx.tree;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.bridgekit.names.DefaultNamespaceBinding;
import org.genxdm.bridgekit.names.QNameComparator;
import org.genxdm.bridgekit.xs.BuiltInSchema;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NamespaceBinding;

public final class XmlElementNode
    extends XmlContainerNode
{
    XmlElementNode(final String namespace, final String localName, final String prefix, final QName type)
    {
        super(NodeKind.ELEMENT);
        this.typeName = type;
        this.localName = localName;
        this.namespaceURI = namespace;
        this.prefixHint = prefix;
    }

    public XmlAttributeNode getAttribute(String nsURI, final String name)
    {
        PreCondition.assertNotNull(name, "name");
        if (nsURI == null)
            nsURI = "";
        if (firstAttribute == null)
            return null;
        XmlAttributeNode attribute = firstAttribute;
        while (attribute != null)
        {
            if (attribute.localName.equals(name) &&
                attribute.namespaceURI.equals(nsURI) )
                return attribute;
            attribute = (XmlAttributeNode)attribute.nextSibling;
        }
        return null;
    }

    public Iterable<QName> getAttributeNames(boolean orderCanonical)
    {
        if (firstAttribute == null)
            return new UnaryIterable<QName>(null);
        XmlAttributeNode attribute = firstAttribute;
        List<QName> names = new ArrayList<QName>();
        while (attribute != null)
        {
            names.add(new QName(attribute.namespaceURI, attribute.localName, attribute.prefixHint));
            attribute = (XmlAttributeNode)attribute.nextSibling;
        }
        if (orderCanonical)
            Collections.sort(names, new QNameComparator());
        return names;
    }

    public String getAttributeStringValue(String namespaceURI, String localName)
    {
        XmlAttributeNode att = getAttribute(namespaceURI, localName);
        if (att != null)
            return att.getStringValue();
        return null;
    }

    public QName getAttributeTypeName(String namespaceURI, String localName)
    {
        XmlAttributeNode att = getAttribute(namespaceURI, localName);
        if (att != null)
            return att.getTypeName();
        return null;
    }

    public Iterable<? extends XmlAtom> getAttributeValue(String namespaceURI, String localName)
    {
        XmlAttributeNode att = getAttribute(namespaceURI, localName);
        if (att != null)
            return att.getValue();
        return null;
    }

    public URI getBaseURI()
    {
        // TODO: modify this, if there is an "xml:base" attribute.
        if (baseURI != null)
            return baseURI;
        return super.getBaseURI();
    }
    
    public Iterable<NamespaceBinding> getNamespaceBindings()
    {
        if (firstNamespace == null)
            return new UnaryIterable<NamespaceBinding>(null);
        XmlNamespaceNode namespace = firstNamespace;
        List<NamespaceBinding> bindings = new ArrayList<NamespaceBinding>();
        while (namespace != null)
        {
            bindings.add(new DefaultNamespaceBinding(namespace.getLocalName(), namespace.getStringValue()));
            namespace = (XmlNamespaceNode)namespace.getNextSibling();
        }
        return bindings;
    }

    public String getNamespaceForPrefix(String prefix)
    {
        PreCondition.assertNotNull(prefix, "prefix");
        if (firstNamespace == null)
            return null;
        XmlNamespaceNode namespace = firstNamespace;
        while (namespace != null)
        {
            if (namespace.getLocalName().equals(prefix))
                return namespace.getStringValue();
            namespace = (XmlNamespaceNode)namespace.nextSibling;
        }
        return null;
    }
    
    public Iterable<String> getNamespaceNames(boolean orderCanonical)
    {
        if (firstNamespace == null)
            return new UnaryIterable<String>(null);
        XmlNamespaceNode namespace = firstNamespace;
        List<String> names = new ArrayList<String>();
        while (namespace != null)
        {
            names.add(namespace.getLocalName());
            namespace = (XmlNamespaceNode)namespace.getNextSibling();
        }
        if (orderCanonical)
            Collections.sort(names);
        return names;
    }

    public QName getTypeName()
    {
        if (typeName == null)
            return UNTYPED_NAME;
        return typeName;
    }

    public boolean hasAttributes()
    {
        return (firstAttribute != null);
    }

    public boolean hasNamespaces()
    {
        return (firstNamespace != null);
    }

    public boolean isElement()
    {
        return true;
    }

    public boolean isId()
    {
        if (firstAttribute != null)
        {
            XmlAttributeNode attribute = firstAttribute;
            while (attribute != null)
            {
                if (attribute.isId())
                    return true;
                attribute = (XmlAttributeNode)attribute.nextSibling;
            }
        }
        return false;
    }

    public boolean isIdRefs()
    {
        if (firstAttribute != null)
        {
            XmlAttributeNode attribute = firstAttribute;
            while (attribute != null)
            {
                if (attribute.isIdRefs())
                    return true;
                attribute = (XmlAttributeNode)attribute.nextSibling;
            }
        }
        return false;
    }
    
    public void setTypeName(QName name)
    {
        typeName = name;
    }
    
    void setAttribute(XmlAttributeNode attribute)
    {
        if (firstAttribute == null)
        {
            firstAttribute = attribute;
            attribute.parent = this;
            attribute.checkId();
        }
        else
        {
            XmlAttributeNode lastAttribute = firstAttribute;
            while (lastAttribute != null)
            {
                if (lastAttribute.namespaceURI.equals(attribute.namespaceURI) &&
                    lastAttribute.localName.equals(attribute.localName) )
                {
                    // replace
                    if (lastAttribute.prevSibling != null)
                        lastAttribute.prevSibling.nextSibling = attribute;
                    attribute.prevSibling = lastAttribute.prevSibling;
                    if (lastAttribute.nextSibling != null)
                        lastAttribute.nextSibling.prevSibling = attribute;
                    attribute.nextSibling = lastAttribute.nextSibling;
                    attribute.parent = this;
                    attribute.checkId();
                    if (firstAttribute == lastAttribute)
                        firstAttribute = attribute;
                    lastAttribute.nextSibling = lastAttribute.prevSibling = lastAttribute.parent = null;
                    break;
                }
                if (lastAttribute.nextSibling == null)
                {
                    // append
                    lastAttribute.nextSibling = attribute;
                    attribute.prevSibling = lastAttribute;
                    attribute.parent = this;
                    attribute.checkId();
                    break;
                }
                lastAttribute = (XmlAttributeNode)lastAttribute.nextSibling;
            }
        }
    }
    
    void removeAttribute(XmlAttributeNode attribute)
    {
        XmlAttributeNode lastAttribute = firstAttribute;
        while (lastAttribute != null)
        {
            if (lastAttribute.namespaceURI.equals(attribute.namespaceURI) &&
                lastAttribute.localName.equals(attribute.localName) )
            {
                // connect up prevsibling and nextsibling
                if (lastAttribute.prevSibling != null) // not firstAttribute
                    lastAttribute.prevSibling.nextSibling = lastAttribute.nextSibling;
                else
                {
                    firstAttribute = (XmlAttributeNode)lastAttribute.nextSibling;
                    if (firstAttribute != null)
                        firstAttribute.prevSibling = null;
                }
                if (lastAttribute.nextSibling != null)
                    lastAttribute.nextSibling.prevSibling = lastAttribute.prevSibling;
                // disconnect the found attribute from siblings and parent
                lastAttribute.nextSibling = lastAttribute.prevSibling = lastAttribute.parent = null;
                // for safety, do the same with supplied attribute
                attribute.nextSibling = attribute.prevSibling = attribute.parent = null;
                // and stop iterating
                break;
            }
            lastAttribute = (XmlAttributeNode)lastAttribute.nextSibling;
        }
    }

    void setNamespace(XmlNamespaceNode namespace)
    {
        if (firstNamespace == null)
        {
            firstNamespace = namespace;
            namespace.parent = this;
        }
        else
        {
            XmlNamespaceNode lastNamespace = firstNamespace;
            while (lastNamespace != null)
            {
                if (lastNamespace.localName.equals(namespace.localName) )
                    // TODO: replace?
                    break;
                if (lastNamespace.nextSibling == null)
                {
                    lastNamespace.nextSibling = namespace;
                    namespace.prevSibling = lastNamespace;
                    namespace.parent = this;
                }
                lastNamespace = (XmlNamespaceNode)lastNamespace.nextSibling;
                
            }
        }
    }
    
    void removeNamespace(XmlNamespaceNode namespace)
    {
        XmlNamespaceNode lastNamespace = firstNamespace;
        while (lastNamespace != null)
        {
            if (lastNamespace.localName.equals(namespace.localName) )
            {
                // connect up prevsibling and nextsibling
                if (lastNamespace.prevSibling != null)
                    lastNamespace.prevSibling.nextSibling = lastNamespace.nextSibling;
                else
                    firstNamespace = (XmlNamespaceNode)lastNamespace.nextSibling;
                if (lastNamespace.nextSibling != null)
                    lastNamespace.nextSibling.prevSibling = lastNamespace.prevSibling;
                // disconnect the found attribute from siblings and parent
                lastNamespace.nextSibling = lastNamespace.prevSibling = lastNamespace.parent = null;
                // for safety, do the same with supplied attribute
                namespace.nextSibling = namespace.prevSibling = namespace.parent = null;
                // and stop iterating
                break;
            }
            lastNamespace = (XmlNamespaceNode)lastNamespace.nextSibling;
        }
    }
    
    /**
     * Useful for debugging:
     */
    @Override
    public String toString() {
        return "Element: {" + namespaceURI + "}" + localName;
    }

    private URI baseURI;
    
    protected XmlAttributeNode firstAttribute;
    protected XmlNamespaceNode firstNamespace;
    
    private QName typeName;
    private static final QName UNTYPED_NAME = BuiltInSchema.SINGLETON.UNTYPED.getName();
}
