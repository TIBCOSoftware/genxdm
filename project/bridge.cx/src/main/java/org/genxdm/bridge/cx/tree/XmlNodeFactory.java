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
import java.util.List;

import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.mutable.NodeFactory;
import org.genxdm.xs.types.Type;

public class XmlNodeFactory
    implements NodeFactory<XmlNode>
{
    // TODO: maybe provide alternate constructors instead of spewing new
    // factories from models from factories from models from factories?

    public XmlAttributeNode createAttribute(String namespaceURI, String localName, String prefix, String value)
    {
        return new XmlAttributeNode(namespaceURI, localName, prefix, DtdAttributeKind.CDATA, value);
    }
    
    public XmlAttributeNode createAttribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
    {
        PreCondition.assertNotNull(prefix, "prefix");
        return new XmlAttributeNode(namespaceURI, localName, prefix, type, value);
    }
    
    public XmlAttributeNode createAttribute(String namespaceURI, String localName, String prefix, List<? extends XmlAtom> data, Type type)
    {
        PreCondition.assertNotNull(prefix, "prefix");
        return new XmlAttributeNode(namespaceURI, localName, prefix, (type == null ? null : type.getName()), makeList(data));
    }
    
    public XmlCommentNode createComment(String data)
    {
        return new XmlCommentNode(data);
    }

    public XmlRootNode createDocument(final URI documentURI, final String docTypeDecl)
    {
        return new XmlRootNode(documentURI, docTypeDecl);
    }

    public XmlElementNode createElement(String namespaceURI, String localName, String prefix)
    {
        PreCondition.assertNotNull(prefix, "prefix");
        return new XmlElementNode(namespaceURI, localName, prefix, null);
    }
    
    public XmlElementNode createElement(String namespaceURI, String localName, String prefix, Type type)
    {
        PreCondition.assertNotNull(prefix, "prefix");
        if (type == null)
            return createElement(namespaceURI, localName, prefix);
        return new XmlElementNode(namespaceURI, localName, prefix, type.getName());
    }

    public XmlNamespaceNode createNamespace(String prefix, String namespaceURI)
    {
        return new XmlNamespaceNode(prefix, namespaceURI);
    }

    public XmlPINode createProcessingInstruction(String target, String data)
    {
        return new XmlPINode(target, data);
    }

    public XmlTextNode createText(String value)
    {
        return new XmlTextNode(value);
    }
    
    public XmlTextNode createText(List<? extends XmlAtom> data)
    {
        return new XmlTextNode(makeList(data));
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
