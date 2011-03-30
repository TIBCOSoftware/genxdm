/**
 * Copyright (c) 2010 TIBCO Software Inc.
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

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.nodes.Informer;
import org.genxdm.nodes.TypeInformer;

public abstract class XmlNode
    implements Informer, TypeInformer<XmlAtom>, XmlNodeNavigator
{
    protected XmlNode(final NodeKind nodeKind)
    {
        this.nodeKind = PreCondition.assertNotNull(nodeKind, "nodeKind");
    }

    public Iterable<QName> getAttributeNames(boolean orderCanonical)
    {
        return null;
    }

    public URI getBaseURI()
    {
        if (parent != null)
            return parent.getBaseURI();
        return null;
    }

    public URI getDocumentURI()
    {
        return null;
    }

    public XmlElementNode getElementById(final String id)
    {
        if (getRoot().getNodeKind() == NodeKind.DOCUMENT)
            return ((XmlRootNode)getRoot()).getElementById(id);
        return null;
    }

    public int getLineNumber()
    {
        return lineNo;
    }
    
    //TODO: not public.  should only be called from within package?
    void setLineNumber(int number)
    {
        lineNo = number;
    }

    public String getLocalName()
    {
        return localName;
    }

    public String getNamespaceURI()
    {
        return namespaceURI;
    }

    public XmlNode getNextSibling()
    {
        return nextSibling;
    }

    public XmlElementNode getNextSiblingElement()
    {
        return getNextSiblingElementByName(null, null);
    }

    public XmlElementNode getNextSiblingElementByName(final String nsURI, final String lName)
    {
        if (nextSibling == null)
            return null;
        if (nextSibling.isElement())
        {
            if (nsURI == null)
            {
                if ( (lName == null) || lName.equals(nextSibling.localName) )
                    return (XmlElementNode)nextSibling;
            }
            else if (nsURI.equals(nextSibling.namespaceURI) )
            {
                if ( (lName == null) || lName.equals(nextSibling.localName) )
                    return (XmlElementNode)nextSibling;
            }
        }
        return nextSibling.getNextSiblingElementByName(nsURI, lName);
    }
    
    public XmlNode getNodeId()
    {
        return this;
    }

    public NodeKind getNodeKind()
    {
        return nodeKind;
    }

    public XmlContainerNode getParent()
    {
        return parent;
    }

    public String getPrefix()
    {
        return prefixHint;
    }

    public XmlNode getPreviousSibling()
    {
        return prevSibling;
    }

    public XmlNode getRoot()
    {
        if (parent != null)
            return parent.getRoot();
        return this;
    }

    public boolean hasNextSibling()
    {
        return (nextSibling != null);
    }

    public boolean hasParent()
    {
        return (parent != null);
    }

    public boolean hasPreviousSibling()
    {
        return (prevSibling != null);
    }

    public boolean isSameNode(final XmlNode other)
    {
        return (this == other);
    }

    public boolean matches(final NodeKind nodeKind, final String namespaceURI, final String localName)
    {
        // null nodekind means "match anything"
        if ( (nodeKind == null) || ((nodeKind != null) && nodeKind.equals(this.nodeKind)) )
            return matches(namespaceURI, localName);
        return false;
    }

    public boolean matches(final String namespaceURI, final String localName)
    {
        if (namespaceURI == null)
        {
            // matches any namespaceURI
            if (localName != null)
                return localName.equals(this.localName);
            return true; // null, null.
        }
        else if (namespaceURI.equals(this.namespaceURI))
        {
            if (localName == null)
                return true;
            else
                return localName.equals(this.localName);
        }
        return false;
    }
    
    void setParent(final XmlContainerNode container)
    {
        this.parent = container;
    }
    
    protected String localName;
    protected String namespaceURI;
    protected String prefixHint;

    protected XmlNode nextSibling;
    protected XmlNode prevSibling;

    protected XmlContainerNode parent;

    private final NodeKind nodeKind;
    
    private int lineNo = -1;
}
