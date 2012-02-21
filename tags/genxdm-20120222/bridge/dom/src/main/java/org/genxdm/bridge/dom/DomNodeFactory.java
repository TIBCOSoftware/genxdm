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
    public DomNodeFactory(DocumentBuilderFactory dbf) {
        PreCondition.assertNotNull(dbf, "dbf");
        m_dbf = dbf;
    }
    
    // use for context instantiation
    public DomNodeFactory(Document doc) {
        PreCondition.assertNotNull(doc, "doc");
        m_doc = doc;
        m_dbf = DomProcessingContext.sm_dbf;
    }
    
    public DocumentBuilderFactory getCachedDocumentBuilderFactory()
    {
        return m_dbf;
    }
    
    public Node createAttribute(String namespaceURI, String localName, String prefix, String value)
    {
        insureDocumentExists();
        return DomSupport.createAttributeUntyped(m_doc, namespaceURI, localName, prefix, value);
    }

    public Node createComment(String data)
    {
        insureDocumentExists();
        return m_doc.createComment((data == null) ? "" : data);
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
        insureDocumentExists();
        return DomSupport.createElement(m_doc, namespaceURI, localName, prefix);
    }

    public Node createProcessingInstruction(String target, String data)
    {
        insureDocumentExists();
        return m_doc.createProcessingInstruction(target, (data == null) ? "" : data);
    }

    public Node createText(String value)
    {
        insureDocumentExists();
        return m_doc.createTextNode((value == null) ? "" : value);
    }
    
    private void insureDocumentExists()
    {
        if (m_doc == null)
            createDocument(null, null);
    }
    
    private DocumentBuilderFactory m_dbf;
    private Document m_doc;
}
