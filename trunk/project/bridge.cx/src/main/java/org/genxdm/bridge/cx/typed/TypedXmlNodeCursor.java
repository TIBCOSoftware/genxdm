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

import org.genxdm.bridge.cx.base.XmlNodeCursor;
import org.genxdm.bridge.cx.tree.Walker;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;

public class TypedXmlNodeCursor
    extends XmlNodeCursor
    implements TypedCursor<XmlNode, XmlAtom>
{
    TypedXmlNodeCursor(final TypedXmlNodeContext context, final XmlNode node)
    {
        super(node);
        this.context = PreCondition.assertNotNull(context);
    }

    @Override
    public QName getAttributeTypeName(String namespaceURI, String localName)
    {
        return node.getAttributeTypeName(namespaceURI, localName);
    }

    @Override
    public Iterable<? extends XmlAtom> getAttributeValue(String namespaceURI, String localName)
    {
        return node.getAttributeValue(namespaceURI, localName);
    }

    @Override
    public QName getTypeName()
    {
        return node.getTypeName();
    }

    @Override
    public Iterable<? extends XmlAtom> getValue()
    {
        AtomBridge<XmlAtom> bridge = context.getAtomBridge();
        switch (node.getNodeKind())
        {
            case ATTRIBUTE :
            {
                return node.getValue();
            }
            case TEXT :
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
    
    @Override
    public void write(SequenceHandler<XmlAtom> handler, boolean bogus)
        throws GenXDMException
    {
        Walker.walk(node, handler, bogus);
    }

    private final TypedXmlNodeContext context;
}
