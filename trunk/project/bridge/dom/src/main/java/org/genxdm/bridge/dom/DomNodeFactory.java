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
package org.genxdm.bridge.dom;

import java.net.URI;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.mutable.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DomNodeFactory
    implements NodeFactory<Node>
{

    // use for creating new documents.
    public DomNodeFactory(DocumentBuilderFactory dbf, DomModelMutable model) {
        PreCondition.assertNotNull(dbf, "dbf");
        PreCondition.assertNotNull(model, "model");
        m_dbf = dbf;
        m_model = model;
    }
    
    // use for context instantiation
    public DomNodeFactory(Document doc, DomModelMutable model) {
        PreCondition.assertNotNull(doc, "doc");
        PreCondition.assertNotNull(model, "model");
        m_doc = doc;
        m_model = model;
    }
    
    public DomModelMutable getMutableModel() {
        return m_model;
    }
    
    public DocumentBuilderFactory getCachedDocumentBuilderFactory()
    {
        return m_dbf;
    }
    
    public Node createAttribute(String namespaceURI, String localName, String prefix, String value)
    {
        PreCondition.assertNotNull(m_doc, "m_doc");
        return DomSupport.createAttributeUntyped(m_doc, namespaceURI, localName, prefix, value);
    }

    public Node createComment(String data)
    {
        PreCondition.assertArgumentNotNull(data, "data");
        PreCondition.assertNotNull(m_doc, "m_doc");
        return DomSupport.createComment(m_doc, data);
    }

    public Node createDocument(final URI uri, final String docTypeDecl)
    {
        // TODO: set document uri and use the doc type declaration
        try
        {
            m_doc = m_dbf.newDocumentBuilder().newDocument();
            return m_doc;
        }
        catch (ParserConfigurationException pce)
        {
            throw new RuntimeException(pce);
        }
    }

    public Node createElement(String namespaceURI, String localName, String prefix)
    {
        PreCondition.assertNotNull(m_doc, "m_doc");
        return DomSupport.createElement(m_doc, namespaceURI, localName, prefix);
    }

    public Node createNamespace(String prefix, String namespaceURI)
    {
        PreCondition.assertNotNull(m_doc, "m_doc");
        return DomSupport.createNamespace(m_doc, prefix, namespaceURI);
    }

    public Node createProcessingInstruction(String target, String data)
    {
        PreCondition.assertArgumentNotNull(data, "data");
        PreCondition.assertNotNull(m_doc, "m_doc");
        return DomSupport.createProcessingInstruction(m_doc, target, data);
    }

    public Node createText(String value)
    {
        PreCondition.assertArgumentNotNull(value, "value");
        PreCondition.assertNotNull(m_doc, "m_doc");
        return DomSupport.createText(m_doc, value);
    }
    
    private DocumentBuilderFactory m_dbf;
    private Document m_doc;
    private final DomModelMutable m_model;
}
