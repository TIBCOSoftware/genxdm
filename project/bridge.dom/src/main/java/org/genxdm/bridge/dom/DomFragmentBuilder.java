/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.genxdm.NodeKind;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.io.FragmentBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DomFragmentBuilder
    implements FragmentBuilder<Node>
{

    public DomFragmentBuilder(DocumentBuilderFactory dbf) {
        try
        {
			m_db = ( dbf == null ? DomProcessingContext.sm_db : dbf.newDocumentBuilder() );
		}
        catch (ParserConfigurationException pce)
        {
            throw new RuntimeException(pce);
        }
    }
    
    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GenXDMException
    {
        DtdAttributeKind kind = (type == null) ? DtdAttributeKind.CDATA : type;
        if (m_depth > 0)
        {
            final Attr attribute = DomSupport.setAttributeUntyped(m_current, namespaceURI, localName, prefix, value);
            if ( (type == DtdAttributeKind.ID) ||
                    (namespaceURI.equals(XMLConstants.XML_NS_URI) &&
                     localName.equals("id")) )
                ((Element)m_current).setIdAttributeNode(attribute, true);
            setAnnotationType(attribute, new QName("http://www.w3.org/TR/REC-xml", kind.toString()));
        }
        else
        {
            startNodeProcessing();
            m_current = DomSupport.createAttributeUntyped(getOwner(), namespaceURI, localName, prefix, value);
            setAnnotationType(m_current, new QName("http://www.w3.org/TR/REC-xml", kind.toString()));
            endNodeProcessing();
        }
    }

    public void comment(final String value) throws GenXDMException
    {
        push(value, NodeKind.COMMENT);
    }

    public void endDocument() throws GenXDMException
    {
        flush();
        endNodeProcessing();
        while (m_depth > 0)
            endNodeProcessing();
    }

    public void endElement() throws GenXDMException
    {
        flush();
        endNodeProcessing();
    }

    public void flush()
    {
        if (null != m_chNodeKind)
        {
            switch (m_chNodeKind)
            {
                case TEXT:
                {
                    if (m_depth > 0)
                    {
                        appendText(m_current, m_chBuffer.toString());
                    }
                    else
                    {
                        startNodeProcessing();
                        m_current = getOwner().createTextNode(m_chBuffer.toString());
                        endNodeProcessing();
                    }
                }
                break;
                case COMMENT:
                {
                    if (m_depth > 0)
                    {
                        final Node comment = getOwner().createComment(m_chBuffer.toString());
                        m_current.appendChild(comment);
                    }
                    else
                    {
                        startNodeProcessing();
                        m_current = getOwner().createComment(m_chBuffer.toString());
                        endNodeProcessing();
                    }
                }
                break;
                default:
                {
                    throw new AssertionError(m_chNodeKind);
                }
            }
            m_chBuffer.setLength(0);
            m_chNodeKind = null;
        }
    }

    public List<Node> getNodes()
    {
        flush();
        return m_nodes;
    }
    
    public Node getNode()
    {
        if (m_nodes.size() > 0)
            return getNodes().get(0);
        return null;
    }

    public void namespace(final String prefix, final String namespaceURI) 
        throws GenXDMException
    {
        if (m_depth > 0)
        {
            DomSupport.setNamespace(m_current, prefix, namespaceURI);
        }
        else
        {
            startNodeProcessing();
            m_current = DomSupport.createNamespace(getOwner(), prefix, namespaceURI);
            endNodeProcessing();
        }
    }

    public void processingInstruction(final String target, final String data) 
        throws GenXDMException
    {
        flush();
        if (m_depth > 0)
        {
            final Node pi = getOwner().createProcessingInstruction(target, data);
            m_current.appendChild(pi);
        }
        else
        {
            startNodeProcessing();
            m_current = getOwner().createProcessingInstruction(target, data);
            endNodeProcessing();
        }
    }

    public void startDocument(final URI baseURI, final String docTypeDecl) 
        throws GenXDMException
    {
        if (m_current == null)
        {
            startNodeProcessing();
            m_current = newDocument(baseURI);
        }
        else
        {
            throw new IllegalStateException("A document cannot be contained by a document or element.");
        }
    }

    public void startElement(final String namespaceURI, final String localName, final String prefix) 
        throws GenXDMException
    {
        flush();
        startNodeProcessing();
        final Element element = DomSupport.createElement(getOwner(), namespaceURI, localName, prefix);
        if (m_current != null)
        {
            try
            {
                m_current.appendChild(element);
            }
            catch (final DOMException e)
            {
                throw new UnsupportedOperationException(e);
            }
        }
        m_current = element;
    }

    public void text(final String strval) 
        throws GenXDMException
    {
        push(strval, NodeKind.TEXT);
    }

    public void close()
        throws IOException
    {
    }

    public void reset()
    {
        m_depth = 0;
        m_nodes.clear();
        m_chBuffer.setLength(0);
        m_current = null;
        m_chNodeKind = null;
    }


    protected void startNodeProcessing()
    {
        m_depth += 1;
    }

    protected void endNodeProcessing()
    {
        m_depth -= 1;

        if (m_depth < 0)
            throw new IllegalStateException("Closed a container that was never opened.");
        if (m_depth > 0)
        {
            m_current = DomSupport.getParentNode(m_current);
        }
        else
        {
            m_nodes.add(m_current);
            m_current = null;
        }
    }

    protected Document getOwner()
    {
        if (null != m_current)
        {
            return DomSupport.getOwner(m_current);
        }
        return newDocument(null);
    }
    
    private void push(final String strval, final NodeKind nodeKind)
    {
        if (null != m_chNodeKind && m_chNodeKind != nodeKind)
        {
            flush();
        }
        m_chNodeKind = nodeKind;
        m_chBuffer.append(strval);
    }

    private Document newDocument(final URI documentURI)
    {
        final Document document = m_db.newDocument();

        if (null != documentURI)
        {
            try
            {
                document.setDocumentURI(documentURI.toString());
            }
            catch (final AbstractMethodError e)
            {
                // Thrown by org.apache.xerces.dom.DocumentImpl
            }
        }

        return document;
    }

    private void setAnnotationType(final Node node, final QName type)
    {
        // TODO: we could, potentially, store DTD types even in untyped API
        // to do so, though, we have to figure out how to define a QName for the DtdAttributeKind enumeration.
        if (DomSupport.supportsCoreLevel3(node))
        {
            try
            {
                node.setUserData(DomConstants.UD_ANNOTATION_TYPE, type, null);
            }
            catch (final AbstractMethodError e)
            {
                // LOG.warn("setAnnotationType", e);
            }
        }
        // TODO: Log something for DOM w/o Level 3 support?
        // LOG.warn("DOM does not support DOM CORE version 3.0: setUserData");
    }

    private Node appendText(final Node parent, final String strval)
    {
        final Node lastChild = parent.getLastChild();
        if (null != lastChild && (lastChild.getNodeType() == Node.TEXT_NODE || lastChild.getNodeType() == Node.CDATA_SECTION_NODE))
        {
            final String existing = lastChild.getNodeValue();
            lastChild.setNodeValue(existing.concat(strval));
            return lastChild;
        }
        else
        {
            Document doc = DomSupport.getOwner(parent);
            final Node text = doc.createTextNode(strval);
            try
            {
                // Here's a crazy work-around - we want to be able to insert white-space text nodes
                // at the root of the Document, before/after the first element. So we check for that
                // here, and turn off error checking on the DOM, because the built-in JDK DOM will
                // throw chunks.
                if (parent == doc && strval.trim().length() == 0) 
                {
                    boolean strict = doc.getStrictErrorChecking();
                    doc.setStrictErrorChecking(false);
                    parent.appendChild(text);
                    doc.setStrictErrorChecking(strict);
                }
                else 
                {
                    parent.appendChild(text);
                }
            }
            catch (final DOMException e)
            {
                throw new UnsupportedOperationException(e);
            }
            return text;
        }
    }

    private final DocumentBuilder m_db;
    
    protected int m_depth;
    protected Node m_current;
    private final ArrayList<Node> m_nodes = new ArrayList<Node>();
    private final StringBuilder m_chBuffer = new StringBuilder();
    private NodeKind m_chNodeKind = null;
}
