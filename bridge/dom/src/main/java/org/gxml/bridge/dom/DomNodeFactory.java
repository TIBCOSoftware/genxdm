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
package org.gxml.bridge.dom;

import java.net.URI;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.genxdm.base.mutable.NodeFactory;
import org.genxdm.exceptions.PreCondition;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DomNodeFactory
    implements NodeFactory<Node>
{

	public DomNodeFactory(DocumentBuilderFactory dbf) {
		m_dbf = dbf;
	}
	
    public DocumentBuilderFactory getCachedDocumentBuilderFactory()
    {
        return m_dbf;
    }
    
    public Node createAttribute(Node owner, String namespaceURI, String localName, String prefix, String value)
    {
        return DomSupport.createAttributeUntyped(DomSupport.getOwner(owner), namespaceURI, localName, prefix, value);
    }

    public Node createComment(Node owner, String data)
    {
        PreCondition.assertArgumentNotNull(owner, "owner");
        PreCondition.assertArgumentNotNull(data, "data");
        final Document document = DomSupport.getOwner(owner);
        return DomSupport.createComment(document, data);
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

    public Node createElement(Node owner, String namespaceURI, String localName, String prefix)
    {
        PreCondition.assertArgumentNotNull(owner, "owner");
        final Document document = DomSupport.getOwner(owner);
        return DomSupport.createElement(document, namespaceURI, localName, prefix);
    }

    public Node createNamespace(Node owner, String prefix, String namespaceURI)
    {
        return DomSupport.createNamespace(DomSupport.getOwner(owner), prefix, namespaceURI);
    }

    public Node createProcessingInstruction(Node owner, String target, String data)
    {
        PreCondition.assertArgumentNotNull(owner, "owner");
        PreCondition.assertArgumentNotNull(data, "data");
        final Document document = DomSupport.getOwner(owner);
        return DomSupport.createProcessingInstruction(document, target, data);
    }

    public Node createText(Node owner, String value)
    {
        PreCondition.assertArgumentNotNull(owner, "owner");
        PreCondition.assertArgumentNotNull(value, "value");
        final Document document = DomSupport.getOwner(owner);
        return DomSupport.createText(document, value);
    }

    private final DocumentBuilderFactory m_dbf;
    
    /**
     * Initializing the {@link DocumentBuilderFactory} is expensive so we do it once statically.
     */
    private static final DocumentBuilderFactory sm_dbf = DocumentBuilderFactory.newInstance();
    
    static
    {
        sm_dbf.setNamespaceAware(true);
    }

}
