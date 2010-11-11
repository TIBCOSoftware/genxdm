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
package org.genxdm.bridge.cx.base;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genxdm.DtdAttributeKind;
import org.genxdm.base.io.FragmentBuilder;
import org.genxdm.bridge.cx.tree.XmlAttributeNode;
import org.genxdm.bridge.cx.tree.XmlCommentNode;
import org.genxdm.bridge.cx.tree.XmlElementNode;
import org.genxdm.bridge.cx.tree.XmlNamespaceNode;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridge.cx.tree.XmlNodeFactory;
import org.genxdm.bridge.cx.tree.XmlNodeMutator;
import org.genxdm.bridge.cx.tree.XmlPINode;
import org.genxdm.bridge.cx.tree.XmlRootNode;
import org.genxdm.bridge.cx.tree.XmlTextNode;
import org.genxdm.exceptions.GxmlException;

public class XmlNodeBuilder
    implements FragmentBuilder<XmlNode>
{
	
	public XmlNodeBuilder() {
		this(null);
	}
	
	/**
	 * This constructor exists for the purposes of cloning/importing into a new document,
	 * rather than the same one.
	 * 
	 * @param document	The root ("document") node to set on the results.
	 */
	public XmlNodeBuilder(XmlRootNode document) {
		currentDoc = document;
	}

    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GxmlException
    {
        flushCatch();
        depth++;
        if (current != null)
        {
            final XmlAttributeNode attribute = factory.createAttribute(currentDoc, namespaceURI, localName, prefix, value, type);
            mutator.setAttribute(current, attribute);
            current = attribute;
        }
        else
        {
            current = factory.createAttribute(currentDoc, namespaceURI, localName, prefix, value, type);
        }
        endNodeProcessing();
    }

    public void close()
        throws IOException
    {
        // TODO Auto-generated method stub
        flush();
    }

    public void comment(String value)
        throws GxmlException
    {
        flushCatch();
        depth++;
        if (current != null)
        {
            final XmlCommentNode comment = factory.createComment(currentDoc, value);
            mutator.appendChild(current, comment);
            current = comment;
        }
        else
        {
            current = factory.createComment(currentDoc, value);
        }
        endNodeProcessing();
    }

    public void endDocument()
        throws GxmlException
    {
        flushCatch();
        endNodeProcessing();
        currentDoc = null;
    }

    public void endElement()
        throws GxmlException
    {
        flushCatch();
        endNodeProcessing();
    }

    public void flush()
        throws IOException
    {
        // TODO Auto-generated method stub

    }

    public XmlNode getNode()
    {
        return getNodes().get(0);
    }

    public List<XmlNode> getNodes()
    {
        flushCatch();
        return Collections.unmodifiableList(nodes);
    }

    public void namespace(String prefix, String namespaceURI)
        throws GxmlException
    {
        flushCatch();
        depth++;
        if (current != null)
        {
            final XmlNamespaceNode namespace = factory.createNamespace(currentDoc, prefix, namespaceURI);
            mutator.setNamespace(current, namespace);
            current = namespace;
        }
        else
        {
            current = factory.createNamespace(currentDoc, prefix, namespaceURI);
        }
        endNodeProcessing();
    }

    public void processingInstruction(String target, String data)
        throws GxmlException
    {
        flushCatch();
        depth++;
        if (current != null)
        {
            final XmlPINode pi = factory.createProcessingInstruction(currentDoc, target, data);
            mutator.appendChild(current, pi);
            current = pi;
        }
        else
        {
            current = factory.createProcessingInstruction(currentDoc, target, data);
        }
        endNodeProcessing();
    }

    public void reset()
    {
        current = null;
        depth = 0;
        nodes.clear();
    }

    public void startDocument(URI documentURI, String docTypeDecl)
        throws GxmlException
    {
        depth++;
        current = factory.createDocument(documentURI, docTypeDecl);
        currentDoc = (XmlRootNode)current;
    }

    public void startElement(String namespaceURI, String localName, String prefix)
        throws GxmlException
    {
        flushCatch();
        depth++;
        if (current != null)
        {
            final XmlElementNode element = factory.createElement(currentDoc, namespaceURI, localName, prefix);
            mutator.appendChild(current, element);
            current = element;
        }
        else
        {
            current = factory.createElement(currentDoc, namespaceURI, localName, prefix);
        }
    }

    public void text(String data)
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
    
    protected void endNodeProcessing()
    {
        depth--;
        
        if (depth > 0)
        {
            current = current.getParent();
        }
        else
        {
            nodes.add(current);
            current = null;
        }
    }
    
    protected void flushCatch()
    {
        try
        {
            flush();
        }
        catch (IOException ioe)
        {
            throw new GxmlException(ioe);
        }
    }

    protected final ArrayList<XmlNode> nodes = new ArrayList<XmlNode>();
    protected int depth;
    protected XmlNode current;
    protected XmlRootNode currentDoc;
    protected final XmlNodeFactory factory = new XmlNodeFactory();
    protected final XmlNodeMutator mutator = new XmlNodeMutator();
}
