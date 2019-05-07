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

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.nodes.NodeIndex;

// there are only two possible containers: the "root" (typically a document, but xquery allows
// it to be a little strange) or an element.
public class XmlContainerNode
    extends XmlNode
{
    protected XmlContainerNode(final NodeKind nodeKind)
    {
        super(nodeKind);
    }

    public XmlAttributeNode getAttribute(String namespaceURI, String localName)
    {
        return null;
    }

    public String getAttributeStringValue(String namespaceURI, String localName)
    {
        return null;
    }

    public QName getAttributeTypeName(String namespaceURI, String localName)
    {
        return null; 
    }

    public Iterable<? extends XmlAtom> getAttributeValue(String namespaceURI, String localName)
    {
        return null;
    }

    public XmlNode getFirstChild()
    {
        return firstChild;
    }

    public XmlElementNode getFirstChildElement()
    {
        return getFirstChildElementByName(null, null);
    }

    public XmlElementNode getFirstChildElementByName(String nsURI, String lName)
    {
        if (firstChild == null)
            return null;
        if (firstChild.isElement())
        {
            if (nsURI == null)
            {
                if ( (lName == null) || lName.equals(firstChild.localName) )
                    return (XmlElementNode)firstChild;
            }
            else if (nsURI.equals(firstChild.namespaceURI) )
            {
                if ( (lName == null) || lName.equals(firstChild.localName) )
                    return (XmlElementNode)firstChild;
            }
        }
        return firstChild.getNextSiblingElementByName(nsURI, lName);
    }
    
    public NodeIndex getIndex()
    {
        return null;
    }

    public XmlNode getLastChild()
    {
        return lastChild;
    }

    public Iterable<NamespaceBinding> getNamespaceBindings()
    {
        return new UnaryIterable<NamespaceBinding>(null);
    }

    public String getNamespaceForPrefix(String prefix)
    {
        return null;
    }

    public Iterable<String> getNamespaceNames(boolean orderCanonical)
    {
        return new UnaryIterable<String>(null);
    }

    public String getStringValue()
    {
        if (firstChild == null)
            return "";
        StringBuilder sb = new StringBuilder();
        XmlNode child = firstChild;
        while (child != null)
        {
            if (child.isText() || child.isElement())
                sb.append(child.getStringValue());
            child = child.getNextSibling();
        }
        return sb.toString();
    }

    public QName getTypeName()
    {
        return null;
    }

    public Iterable<? extends XmlAtom> getValue()
    {
        // TODO: we need an atom bridge to do this successfully.  defer to typed model/typed cursor
        return null;
    }

    public boolean hasAttributes()
    {
        return false;
    }

    public boolean hasChildren()
    {
        return (firstChild != null);
    }

    public boolean hasNamespaces()
    {
        return false;
    }

    public boolean isAttribute()
    {
        return false;
    }

    public boolean isElement()
    {
        return false;
    }

    public boolean isId()
    {
        return false;
    }

    public boolean isIdRefs()
    {
        return false;
    }

    public boolean isNamespace()
    {
        return false;
    }

    public boolean isText()
    {
        return false;
    }
    
    void appendChild(XmlNode child)
    {
        PreCondition.assertNotNull(child, "child");
        if (lastChild == null)
        {
            firstChild = child;
            lastChild = child;
            child.parent = this;
            child.prevSibling = null;
            child.nextSibling = null;
        }
        else
        {
            lastChild.nextSibling = child;
            child.prevSibling = lastChild;
            child.nextSibling = null;
            lastChild = child;
            child.parent = this;
        }
    }
    
    void removeChild(XmlNode child)
    {
        PreCondition.assertNotNull(child, "child");
        if (child.prevSibling != null)
            child.prevSibling.nextSibling = child.nextSibling;
        else
            firstChild = child.nextSibling;
        if (child.nextSibling != null)
            child.nextSibling.prevSibling = child.prevSibling;
        else
            lastChild = child.prevSibling;
        child.nextSibling = child.prevSibling = child.parent = null;
    }
    
    void insertChild(XmlNode child, XmlNode nextChild)
    {
        // can never change lastChild
        PreCondition.assertNotNull(child, "child");
        PreCondition.assertNotNull(nextChild, "nextChild");
        if (nextChild.prevSibling != null)
        {
            nextChild.prevSibling.nextSibling = child;
            child.prevSibling = nextChild.prevSibling;
        }
        else
        {
            firstChild = child;
            child.prevSibling = null;
        }
        nextChild.prevSibling = child;
        child.nextSibling = nextChild;
        child.parent = this;
    }

    protected XmlNode firstChild;
    protected XmlNode lastChild;
}
