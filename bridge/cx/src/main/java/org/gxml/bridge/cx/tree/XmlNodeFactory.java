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
package org.gxml.bridge.cx.tree;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.gxml.bridgekit.atoms.XmlAtom;
import org.gxml.DtdAttributeKind;
import org.gxml.base.mutable.NodeFactory;
import org.gxml.xs.types.SmType;

public class XmlNodeFactory
    implements NodeFactory<XmlNode>
{

    public XmlAttributeNode createAttribute(XmlNode owner, String namespaceURI, String localName, String prefix, String value)
    {
        return new XmlAttributeNode(getRoot(owner), namespaceURI, localName, prefix, DtdAttributeKind.CDATA, value);
    }
    
    public XmlAttributeNode createAttribute(XmlNode owner, String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
    {
        return new XmlAttributeNode(getRoot(owner), namespaceURI, localName, prefix, type, value);
    }
    
    public XmlAttributeNode createAttribute(XmlNode owner, String namespaceURI, String localName, String prefix, List<? extends XmlAtom> data, SmType<XmlAtom> type)
    {
        return new XmlAttributeNode(getRoot(owner), namespaceURI, localName, prefix, type, makeList(data));
    }
    
    public XmlCommentNode createComment(XmlNode owner, String data)
    {
        return new XmlCommentNode(getRoot(owner), data);
    }

    public XmlRootNode createDocument(final URI documentURI, final String docTypeDecl)
    {
        return new XmlRootNode(documentURI, docTypeDecl);
    }

    public XmlElementNode createElement(XmlNode owner, String namespaceURI, String localName, String prefix)
    {
        return new XmlElementNode(getRoot(owner), namespaceURI, localName, prefix, null);
    }
    
    public XmlElementNode createElement(XmlNode owner, String namespaceURI, String localName, String prefix, SmType<XmlAtom> type)
    {
        return new XmlElementNode(getRoot(owner), namespaceURI, localName, prefix, type);
    }

    public XmlNamespaceNode createNamespace(XmlNode owner, String prefix, String namespaceURI)
    {
        return new XmlNamespaceNode(getRoot(owner), prefix, namespaceURI);
    }

    public XmlPINode createProcessingInstruction(XmlNode owner, String target, String data)
    {
        return new XmlPINode(getRoot(owner), target, data);
    }

    public XmlTextNode createText(XmlNode owner, String value)
    {
        return new XmlTextNode(getRoot(owner), value);
    }
    
    public XmlTextNode createText(XmlNode owner, List<? extends XmlAtom> data)
    {
        return new XmlTextNode(getRoot(owner), makeList(data));
    }
    
    private XmlRootNode getRoot(XmlNode node)
    {
        if (node == null)
            return null;
        return node.getRoot();
    }

    List<XmlAtom> makeList(Iterable<? extends XmlAtom> it)
    {
        List<XmlAtom> list = new ArrayList<XmlAtom>();
        for (XmlAtom atom : it)
        {
            list.add(atom);
        }
        return list;
    }
}
