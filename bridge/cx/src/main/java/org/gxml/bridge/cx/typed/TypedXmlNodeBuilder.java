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
package org.gxml.bridge.cx.typed;

import java.util.List;

import javax.xml.namespace.QName;

import org.gxml.bridgekit.atoms.XmlAtom;
import org.gxml.bridge.cx.base.XmlNodeBuilder;
import org.gxml.bridge.cx.tree.XmlAttributeNode;
import org.gxml.bridge.cx.tree.XmlElementNode;
import org.gxml.bridge.cx.tree.XmlNode;
import org.gxml.bridge.cx.tree.XmlTextNode;
import org.gxml.exceptions.GxmlException;
import org.gxml.exceptions.PreCondition;
import org.gxml.typed.io.SequenceBuilder;
import org.gxml.xs.types.SmType;

public class TypedXmlNodeBuilder
    extends XmlNodeBuilder
    implements SequenceBuilder<XmlNode, XmlAtom>
{
    
    TypedXmlNodeBuilder(TypedXmlNodeContext context)
    {
        this.context = PreCondition.assertNotNull(context, "context");
    }

    public void atom(XmlAtom atom)
        throws GxmlException
    {
        // TODO Auto-generated method stub

    }

    public void attribute(String namespaceURI, String localName, String prefix, List<? extends XmlAtom> data, QName type)
        throws GxmlException
    {
        flushCatch();
        depth++;
        SmType<XmlAtom> stype = context.getTypeDefinition(type);
        if (current != null)
        {
            final XmlAttributeNode attribute = factory.createAttribute(currentDoc, namespaceURI, localName, prefix, data, stype);
            mutator.setAttribute(current, attribute);
            current = attribute;
        }
        else
        {
            current = factory.createAttribute(currentDoc, namespaceURI, localName, prefix, data, stype);
        }
        endNodeProcessing();
    }

    public void endSequence()
        throws GxmlException
    {
        // TODO Auto-generated method stub

    }

    public void startElement(String namespaceURI, String localName, String prefix, QName type)
        throws GxmlException
    {
        flushCatch();
        depth++;
        SmType<XmlAtom> stype = context.getTypeDefinition(type);
        if (current != null)
        {
            final XmlElementNode element = factory.createElement(currentDoc, namespaceURI, localName, prefix, stype);
            mutator.appendChild(current, element);
            current = element;
        }
        else
        {
            current = factory.createElement(currentDoc, namespaceURI, localName, prefix, stype);
        }
    }

    public void startSequence()
        throws GxmlException
    {
        // TODO Auto-generated method stub

    }

    public void text(List<? extends XmlAtom> data)
        throws GxmlException
    {
        flushCatch();
        depth++;
        if (current != null)
        {
            final XmlTextNode text = factory.createText(currentDoc, data);
            mutator.appendChild(current, text);
            current = text;
        }
        else
        {
            current = factory.createText(currentDoc, data);
        }
        endNodeProcessing();
    }
    
    private final TypedXmlNodeContext context;
}
