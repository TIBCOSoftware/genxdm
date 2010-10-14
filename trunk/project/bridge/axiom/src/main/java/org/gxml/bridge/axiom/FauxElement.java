/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.gxml.bridge.axiom;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.genxdm.exceptions.PreCondition;

public class FauxElement
    extends Object
    implements OMElement
{
    
    public FauxElement(OMContainer root)
    {
        PreCondition.assertNotNull(root, "root");
        this.root = root;
    }

    public OMAttribute addAttribute(OMAttribute attr)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMAttribute addAttribute(String attributeName, String value, OMNamespace ns)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMElement cloneOMElement()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMNamespace declareDefaultNamespace(String uri)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMNamespace declareNamespace(OMNamespace namespace)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMNamespace declareNamespace(String uri, String prefix)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMNamespace findNamespace(String uri, String prefix)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMNamespace findNamespaceURI(String prefix)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator getAllAttributes()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator getAllDeclaredNamespaces()
        throws OMException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMAttribute getAttribute(QName qname)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAttributeValue(QName qname)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMXMLParserWrapper getBuilder()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator getChildElements()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMNamespace getDefaultNamespace()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMElement getFirstElement()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int getLineNumber()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getLocalName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMNamespace getNamespace()
        throws OMException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public QName getQName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getText()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public QName getTextAsQName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public XMLStreamReader getXMLStreamReader()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public XMLStreamReader getXMLStreamReaderWithoutCaching()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeAttribute(OMAttribute attr)
    {
        // TODO Auto-generated method stub

    }

    public QName resolveQName(String qname)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setBuilder(OMXMLParserWrapper wrapper)
    {
        // TODO Auto-generated method stub

    }

    public void setFirstChild(OMNode node)
    {
        // TODO Auto-generated method stub

    }

    public void setLineNumber(int lineNumber)
    {
        // TODO Auto-generated method stub

    }

    public void setLocalName(String localName)
    {
        // TODO Auto-generated method stub

    }

    public void setNamespace(OMNamespace namespace)
    {
        // TODO Auto-generated method stub

    }

    public void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace)
    {
        // TODO Auto-generated method stub

    }

    public void setText(String text)
    {
        // TODO Auto-generated method stub

    }

    public void setText(QName text)
    {
        // TODO Auto-generated method stub

    }

    public String toStringWithConsume()
        throws XMLStreamException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void build()
    {
        // TODO Auto-generated method stub

    }

    public void buildWithAttachments()
    {
        // TODO Auto-generated method stub

    }

    public void close(boolean build)
    {
        // TODO Auto-generated method stub

    }

    public OMNode detach()
        throws OMException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void discard()
        throws OMException
    {
        // TODO Auto-generated method stub

    }

    public OMNode getNextOMSibling()
        throws OMException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMFactory getOMFactory()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMContainer getParent()
    {
        return root;
    }

    public OMNode getPreviousOMSibling()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int getType()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public void insertSiblingAfter(OMNode sibling)
        throws OMException
    {
        // TODO Auto-generated method stub

    }

    public void insertSiblingBefore(OMNode sibling)
        throws OMException
    {
        // TODO Auto-generated method stub

    }

    public boolean isComplete()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void serialize(XMLStreamWriter xmlWriter)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub

    }

    public void serialize(OutputStream output)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub

    }

    public void serialize(Writer writer)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub

    }

    public void serialize(OutputStream output, OMOutputFormat format)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub

    }

    public void serialize(Writer writer, OMOutputFormat format)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub

    }

    public void serializeAndConsume(XMLStreamWriter xmlWriter)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub

    }

    public void serializeAndConsume(OutputStream output)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub

    }

    public void serializeAndConsume(Writer writer)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub

    }

    public void serializeAndConsume(OutputStream output, OMOutputFormat format)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub

    }

    public void serializeAndConsume(Writer writer, OMOutputFormat format)
        throws XMLStreamException
    {
        // TODO Auto-generated method stub

    }

    public void addChild(OMNode omNode)
    {
        // TODO Auto-generated method stub

    }

    public void buildNext()
    {
        // TODO Auto-generated method stub

    }

    public Iterator getChildren()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator getChildrenWithLocalName(String localName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator getChildrenWithName(QName elementQName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator getChildrenWithNamespaceURI(String uri)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMElement getFirstChildWithName(QName elementQName)
        throws OMException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OMNode getFirstOMChild()
    {
        // TODO Auto-generated method stub
        return null;
    }

    private final OMContainer root;

	public XMLStreamReader getXMLStreamReader(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void serialize(XMLStreamWriter arg0, boolean arg1)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
}
