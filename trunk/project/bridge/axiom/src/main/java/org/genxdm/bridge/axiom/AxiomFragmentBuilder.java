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
package org.genxdm.bridge.axiom;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.genxdm.DtdAttributeKind;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.exceptions.IllegalNullArgumentException;
import org.genxdm.exceptions.PreCondition;

public class AxiomFragmentBuilder
    implements FragmentBuilder<Object>
{
    
    public AxiomFragmentBuilder(final OMFactory factory, boolean ignoreComments)
    {
        this.factory = PreCondition.assertNotNull(factory, "factory");
        this.ignoreComments = ignoreComments;
    }

    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GxmlException
    {
        PreCondition.assertNotNull(namespaceURI, "namespaceURI");
        PreCondition.assertNotNull(localName, "localName");
        PreCondition.assertNotNull(prefix, "prefix");
        PreCondition.assertNotNull(value, "value");

        if (null != currentNode)
        {
            final OMElement element = AxiomSupport.dynamicDowncastElement(currentNode);
            OMNamespace namespace = element.findNamespace(namespaceURI, prefix);
            if (namespace == null)
                namespace = factory.createOMNamespace(namespaceURI, prefix);
            final OMAttribute attribute = factory.createOMAttribute(localName, namespace, value);
            element.addAttribute(attribute);
        }
        else
        {
            final OMNamespace namespace = factory.createOMNamespace(namespaceURI, prefix);
            nodes.add(factory.createOMAttribute(localName, namespace, value));
        }
    }

    public void comment(String value)
        throws GxmlException
    {
        prolog();
        if (!ignoreComments)
        {
            if (null != currentNode)
            {
                final OMContainer container = AxiomSupport.dynamicDowncastContainer(currentNode);
                if (null != container)
                {
                    final OMComment comment = factory.createOMComment(container, value);
                    container.addChild(comment);
                    currentNode = comment;
                }
                else
                {
                    throw new IllegalStateException("comment");
                }
            }
            else
            {
                // Axiom won't let use create a comment without a parent
                // node so we have to put it inside a document.
                final OMDocument document = factory.createOMDocument();
                currentNode = factory.createOMComment(document, value);
            }
        }
        epilog();
    }

    public void endDocument()
        throws GxmlException
    {
        epilog();
        if (level > 0)
            throw new IllegalStateException("Document ended with unclosed elements.");
    }

    public void endElement()
        throws GxmlException
    {
        epilog();
    }

    public void namespace(String prefix, String namespaceURI)
        throws GxmlException
    {
        if (null != currentNode)
        {
            final OMElement parent = (OMElement)currentNode;
            if (namespaceURI == null) {
            	namespaceURI = "";
            }
            parent.declareNamespace(namespaceURI, prefix);
        }
        else
        {
            nodes.add(factory.createOMNamespace(namespaceURI.toString(), prefix));
        }
    }

    public void processingInstruction(String target, String data)
        throws GxmlException
    {
        prolog();
        if (null != currentNode)
        {
            final OMContainer container = AxiomSupport.dynamicDowncastContainer(currentNode);
            if (null != container)
            {
                final OMProcessingInstruction pi = factory.createOMProcessingInstruction(container, target, data);
                container.addChild(pi);
                currentNode = pi;
            }
            else
            {
                throw new IllegalStateException("processingInstruction");
            }
        }
        else
        {
            currentNode = factory.createOMProcessingInstruction(null, target, data);
        }
        epilog();
    }

    public void startDocument(final URI documentURI, final String docTypeDecl)
        throws GxmlException
    {
        prolog();
        if (null == currentNode)
        {
            currentNode = factory.createOMDocument();
        }
        else
        {
            throw new IllegalStateException("A document cannot be contained by a document or element.");
        }
    }

    public void startElement(String namespaceURI, String localName, String prefix)
        throws GxmlException
    {
        prolog();
        IllegalNullArgumentException.check(namespaceURI, "namespaceURI");
        IllegalNullArgumentException.check(localName, "localName");
        IllegalNullArgumentException.check(prefix, "prefix");
        final QName name = (prefix != null) ? new QName(namespaceURI, localName, prefix) : new QName(namespaceURI, localName, XMLConstants.DEFAULT_NS_PREFIX);
        if (null != currentNode)
        {
            final OMContainer container = AxiomSupport.dynamicDowncastContainer(currentNode);
            if (null != container)
            {
                final OMElement element = factory.createOMElement(name, container);
                currentNode = element;
            }
            else
            {
                throw new IllegalStateException("startElement");
            }
        }
        else
        {
            final OMElement element = factory.createOMElement(name);
            
            currentNode = element;
        }
    }

    public void text(String data)
        throws GxmlException
    {
        prolog();
        if (currentNode != null)
        {
            final OMContainer container = AxiomSupport.dynamicDowncastContainer(currentNode);
            if (container != null)
            {
                if ( (container instanceof OMDocument) && (data.trim().length() > 0) )
                    throw new IllegalStateException("Non-whitespace text is not permitted in prolog or epilog.");
                final OMText text = factory.createOMText(data);
                container.addChild(text);
                currentNode = text;
            }
            else
            {
                throw new IllegalStateException();
            }
        }
        else
        {
            currentNode = factory.createOMText(data);
        }
        epilog();
    }

    public void close()
    {
        // TODO: implement?
    }

    public void flush()
    {
        // TODO: implement?
    }

    public List<Object> getNodes()
    {
        flush();
        return nodes;
    }
    
    public Object getNode()
    {
        if (nodes.size() > 0)
            return getNodes().get(0);
        return null;
    }

    public void reset()
    {
        nodes.clear();
        currentNode = null;
        level = 0;
    }
    
    public OMFactory getFactory()
    {
        return factory;
    }

    private void epilog()
    {
        level--;
        if (level < 0)
            throw new IllegalStateException("Closed a container that was never opened.");
        if (level == 0)
        {
            nodes.add(currentNode);
            currentNode = null;
        }
        else
        {
            final OMContainer parentNode = AxiomSupport.getParent(currentNode);
            if (null != parentNode)
            {
                currentNode = parentNode;
            }
        }
    }

    private void prolog()
    {
        level++;
    }

    protected int level;
    protected final OMFactory factory;
    protected ArrayList<Object> nodes = new ArrayList<Object>();
    protected Object currentNode;
    protected boolean ignoreComments;
}
