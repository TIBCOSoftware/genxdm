/*
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
package org.genxdm.bridge.cx.typed;

import javax.xml.namespace.QName;

import org.genxdm.bridge.cx.base.XmlNodeModel;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;

public class TypedXmlNodeModel
    extends XmlNodeModel
    implements TypedModel<XmlNode, XmlAtom>
{
    TypedXmlNodeModel(final AtomBridge<XmlAtom> bridge)
    {
        this.bridge = PreCondition.assertNotNull(bridge, "atomBridge");
    }
    
    public QName getAttributeTypeName(XmlNode parent, String namespaceURI, String localName)
    {
        return parent.getAttributeTypeName(namespaceURI, localName);
    }

    public Iterable<? extends XmlAtom> getAttributeValue(XmlNode parent, String namespaceURI, String localName)
    {
        return parent.getAttributeValue(namespaceURI, localName);
    }

    public QName getTypeName(XmlNode node)
    {
        return node.getTypeName();
    }

    public Iterable<? extends XmlAtom> getValue(XmlNode node)
    {
        switch (node.getNodeKind())
        {
            case TEXT :
            case ATTRIBUTE :
            {
                return node.getValue();
            }
            case NAMESPACE :
            case COMMENT :
            case PROCESSING_INSTRUCTION :
            case DOCUMENT :
            {
                return bridge.wrapAtom(bridge.createString(node.getStringValue()));
            }
            case ELEMENT :
            {
                if (node.hasChildren())
                {
                    XmlNode firstChild = node.getFirstChild();
                    if ( (firstChild == node.getLastChild()) && firstChild.isText() ) // simple content
                        return firstChild.getValue();
                }
                return bridge.wrapAtom(bridge.createString(node.getStringValue())); // complex or empty content
            }
            default :
            {
                throw new AssertionError(node.getNodeKind());
            }
        }
    }

    public void stream(XmlNode node, boolean copyNamespaces, boolean copyTypeAnnotations, SequenceHandler<XmlAtom> handler)
        throws GxmlException
    {
        // TODO Auto-generated method stub
        
    }
    
    private final AtomBridge<XmlAtom> bridge;
}
