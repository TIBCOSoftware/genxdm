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

import org.genxdm.base.mutable.NodeFactory;
import org.genxdm.exceptions.PreCondition;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DomNodeFactory
    implements NodeFactory<Node, Document>
{

	public DomNodeFactory(DocumentBuilderFactory dbf) {
		m_dbf = dbf;
	}
	
	public DomNodeFactory(DocumentBuilderFactory dbf, Document doc) {
		this(dbf);
		if (doc != null)
			m_doc = doc;
	}
	
    public DocumentBuilderFactory getCachedDocumentBuilderFactory()
    {
        return m_dbf;
    }
    
    public void setApiFactory(Document doc) {
    	if (doc != null)
    		m_doc = doc;
    }
    
    public Node createAttribute(String namespaceURI, String localName, String prefix, String value)
    {
        return DomSupport.createAttributeUntyped(insureDocument(), namespaceURI, localName, prefix, value);
    }

    public Node createComment(String data)
    {
        PreCondition.assertArgumentNotNull(data, "data");
        return DomSupport.createComment(insureDocument(), data);
    }

    public Node createDocument(final URI uri, final String docTypeDecl)
    {
        // TODO: set document uri and use the doc type declaration
        try
        {
            return sm_dbf.newDocumentBuilder().newDocument();
        }
        catch (ParserConfigurationException pce)
        {
            throw new RuntimeException(pce);
        }
    }

    public Node createElement(String namespaceURI, String localName, String prefix)
    {
        return DomSupport.createElement(insureDocument(), namespaceURI, localName, prefix);
    }

    public Node createNamespace(String prefix, String namespaceURI)
    {
        return DomSupport.createNamespace(insureDocument(), prefix, namespaceURI);
    }

    public Node createProcessingInstruction(String target, String data)
    {
        PreCondition.assertArgumentNotNull(data, "data");
        return DomSupport.createProcessingInstruction(insureDocument(), target, data);
    }

    public Node createText(String value)
    {
        PreCondition.assertArgumentNotNull(value, "value");
        return DomSupport.createText(insureDocument(), value);
    }
    
    private Document insureDocument() {
    	if (m_doc == null)
    		m_doc = (Document)createDocument(null, null);
    	return m_doc;
    }

    private final DocumentBuilderFactory m_dbf;
    private Document m_doc;
    
    /**
     * Initializing the {@link DocumentBuilderFactory} is expensive so we do it once statically.
     */
    private static final DocumentBuilderFactory sm_dbf = DocumentBuilderFactory.newInstance();
    
    static
    {
        sm_dbf.setNamespaceAware(true);
    }

}
