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

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridge.cx.base.XmlNodeBuilder;
import org.genxdm.bridge.cx.tree.XmlAttributeNode;
import org.genxdm.bridge.cx.tree.XmlElementNode;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridge.cx.tree.XmlTextNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.types.Type;

public class TypedXmlNodeBuilder
    extends XmlNodeBuilder
    implements SequenceBuilder<XmlNode, XmlAtom>
{
    
    TypedXmlNodeBuilder(TypedXmlNodeContext context)
    {
        this.cache = PreCondition.assertNotNull(context.getSchema(), "schema");
    }

    public void attribute(String namespaceURI, String localName, String prefix, List<? extends XmlAtom> data, QName type)
        throws GenXDMException
    {
//System.out.println("got an attribute " + localName);
        flushCatch();
        depth++;
        Type stype = (type == null) ? null : cache.getComponentProvider().getTypeDefinition(type);
        if (current != null)
        {
            final XmlAttributeNode attribute = factory.createAttribute(namespaceURI, localName, prefix, data, stype);
            mutator.insertAttribute(current, attribute);
            current = attribute;
        }
        else
        {
            current = factory.createAttribute(namespaceURI, localName, prefix, data, stype);
        }
        endNodeProcessing();
    }

    public void startElement(String namespaceURI, String localName, String prefix, QName type)
        throws GenXDMException
    {
//System.out.println("got an element " + localName);
        flushCatch();
        depth++;
        Type stype = (type == null) ? null : cache.getComponentProvider().getTypeDefinition(type);
        if (current != null)
        {
            final XmlElementNode element = factory.createElement(namespaceURI, localName, prefix, stype);
            mutator.appendChild(current, element);
            current = element;
        }
        else
        {
            current = factory.createElement(namespaceURI, localName, prefix, stype);
        }
    }

    public void text(List<? extends XmlAtom> data)
        throws GenXDMException
    {
//System.out.println("got a text node");
        flushCatch();
        depth++;
        if (current != null)
        {
            final XmlTextNode text = factory.createText(data);
            mutator.appendChild(current, text);
            current = text;
        }
        else
        {
            current = factory.createText(data);
        }
        endNodeProcessing();
    }
    
    private final SchemaComponentCache cache;
}
