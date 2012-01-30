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
package org.genxdm.processor.output;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;

public class ContentHandlerOnXmlStreamWriter
    implements ContentHandler
{
    public ContentHandlerOnXmlStreamWriter(XMLStreamWriter output)
    {
        this.output = PreCondition.assertNotNull(output, "output");
    }

    public void flush()
        throws IOException
    {
        try
        {
            output.flush();
        }
        catch (XMLStreamException xse)
        {
            throw new IOException(xse);
        }
    }

    public void close()
        throws IOException
    {
        try
        {
            output.close();
        }
        catch (XMLStreamException xse)
        {
            throw new IOException(xse);
        }
    }

    public void attribute(final String namespaceURI, final String localName, final String prefix, final String value, DtdAttributeKind type)
        throws GenXDMException
    {
        PreCondition.assertNotNull(namespaceURI, "namespaceURI");
        PreCondition.assertNotNull(localName, "localName");
        PreCondition.assertNotNull(value, "value");
        
        attributes.add(new Attrib(namespaceURI, localName, prefix, value));
    }

    public void comment(String value)
        throws GenXDMException
    {
        try
        {
            flushPending();
            output.writeComment(value);
        }
        catch (XMLStreamException xse)
        {
            throw new GenXDMException(xse);
        }
    }

    public void endDocument()
        throws GenXDMException
    {
        try
        {
            flushPending();
            output.writeEndDocument();
        }
        catch (XMLStreamException xse)
        {
            throw new GenXDMException(xse);
        }
    }

    public void endElement()
        throws GenXDMException
    {
        try
        {
            flushPending();
            output.writeEndElement();
        }
        catch (XMLStreamException xse)
        {
            throw new GenXDMException(xse);
        }
        finally
        {
            contexts.pop();
        }
    }

    public void namespace(final String prefix, final String namespaceURI)
        throws GenXDMException
    {
        PreCondition.assertNotNull(prefix, "prefix");
        PreCondition.assertNotNull(namespaceURI, "namespaceURI");
        nsDeclarations.add(new PrefixMap(prefix, namespaceURI));
    }

    public void processingInstruction(final String target, final String data)
        throws GenXDMException
    {
        try
        {
            flushPending();
            output.writeProcessingInstruction(target, data);
        }
        catch (XMLStreamException xse)
        {
            throw new GenXDMException(xse);
        }
    }

    public void startDocument(final URI documentURI, final String docTypeDecl)
        throws GenXDMException
    {
        try
        {
            currentContext = output.getNamespaceContext();
            if (currentContext == null)
                currentContext = new NSContext();
            contexts.push(currentContext);
            output.writeStartDocument();
        }
        catch (XMLStreamException xse)
        {
            throw new GenXDMException(xse);
        }
    }

    public void startElement(final String namespaceURI, final String localName, final String prefix)
        throws GenXDMException
    {
        PreCondition.assertNotNull(namespaceURI, "namespaceURI");
        PreCondition.assertNotNull(localName, "localName");
        
        // since this may be a fragment, verify that we have a root context
        if (currentContext == null)
        {
            currentContext = output.getNamespaceContext();
            if (currentContext == null)
                currentContext = new NSContext();
            contexts.push(currentContext);
        }

        flushPending();
        
        currentContext = new NSContext();
        contexts.push(currentContext);
        elementNamespace = namespaceURI;
        elementLocalName = localName;
        elementPrefix = prefix;
        elementPending = true;
    }

    public void text(final String data)
        throws GenXDMException
    {
        try
        {
            flushPending();
            output.writeCharacters(data);
        }
        catch (XMLStreamException xse)
        {
            throw new GenXDMException(xse);
        }
    }
    
    private void flushPending()
        throws GenXDMException
    {
        // TODO: we might consider variations on a theme, with writeEmptyElement
        // when that's appropriate.  Or not.
        if (elementPending)
        {
            try
            {
                fixupNamespaces();
                output.writeStartElement(elementPrefix, elementLocalName, elementNamespace);
                
                for (PrefixMap decl : nsDeclarations)
                {
                    output.writeNamespace(decl.prefix, decl.uri);
                }
                
                for (Attrib attribute : attributes)
                {
                    output.writeAttribute(attribute.prefix, attribute.namespace, attribute.localName, attribute.value);
                }
            }
            catch (XMLStreamException xse)
            {
                throw new GenXDMException(xse);
            }
            finally
            {
                nsDeclarations.clear();
                attributes.clear();
                
                elementPending = false;
            }
        }
    }
    
    private void fixupNamespaces()
        throws GenXDMException
    {
        try
        {
            // first, declare all the namespaces we've accumulated.
            // this may throw an exception, if the prefix is already declared
            // in this context.
            for (PrefixMap decl : nsDeclarations)
            {
                // if the current context is one of our scopes,
                // declare the namespace.
                if (currentContext instanceof NSContext)
                    ((NSContext)currentContext).declare(decl.prefix, decl.uri);
                else // otherwise, declare it on the root context
                    output.setPrefix(decl.prefix, decl.uri);
            }
            // for the element namespace, and each attribute namespace, check that
            // there is a declaration in scope.  Add one if necessary.  Invent prefixes if necessary.
            elementPrefix = verifyNamespace(elementNamespace, elementPrefix, false);
            for (Attrib attribute : attributes)
            {
                String prefix = ( attribute.prefix == null || attribute.prefix.length() == 0) ? null : attribute.prefix;
                attribute.prefix = verifyNamespace(attribute.namespace, prefix, true);
            }
        }
        catch (XMLStreamException xse)
        {
            throw new GenXDMException(xse);
        }
    }
    
    // *never* return null
    private String verifyNamespace(final String uri, final String prefixSuggestion, final boolean isAttribute)
        throws XMLStreamException
    {
        String retVal;
        if (isAttribute)
        {
            // attributes in the default namespace are not in global scope (requiring a namespace declaration),
            // but in the scope of their parent element, and the default prefix, for attributes, is *never*
            // bound (or bindable) to anything other than the default/global/null namespace
            if (uri.equals(XMLConstants.NULL_NS_URI) || (uri == null))
                return XMLConstants.DEFAULT_NS_PREFIX;
        }
        if (prefixSuggestion == null) // no prefix.
        {
            String suggestion = null;
            suggestion = currentContext.getPrefix(uri);
            if (suggestion == null)
            {
                Stack<NamespaceContext> pusher = new Stack<NamespaceContext>();
                do {
                    NamespaceContext context = contexts.pop();
                    pusher.push(context);
                    suggestion = context.getPrefix(uri);
                    if (suggestion != null)
                        break;
                } while (contexts.size() > 0);
                do {
                    contexts.push(pusher.pop());
                } while (pusher.size() > 0);
                if (suggestion == null)
                {
                    suggestion = "ns"+nsCounter++;
                    if (currentContext instanceof NSContext) // isn't this a given?
                        ((NSContext)currentContext).declare(suggestion, uri);
                    else // it's the root context
                        output.setPrefix(suggestion, uri);
                    nsDeclarations.add(new PrefixMap(suggestion, uri));
                }
            }
            retVal = suggestion;
        }
        else // there's a suggestion
        {
            String ns = currentContext.getNamespaceURI(prefixSuggestion);
            if ((ns != null) && ns.equals(uri)) // declared in this context.
            {
                retVal = prefixSuggestion;
            }
            else // not declared in this context.  how about the scopes?
            {
                boolean suggestionOk = false;
                String alternative = null;
                if (ns == null)
                {
                    suggestionOk = true;
                    Stack<NamespaceContext> pusher = new Stack<NamespaceContext>();
                    do {
                        NamespaceContext context = contexts.pop();
                        pusher.push(context);
                        ns = context.getNamespaceURI(prefixSuggestion);
                        if ((ns != null) && ns.equals(uri))
                        {
                            alternative = prefixSuggestion;
                            break;
                        }
                    } while (contexts.size() > 0);
                    do {
                        contexts.push(pusher.pop());
                    } while (pusher.size() > 0);
                }
                if (alternative == null)
                {
                    String prefix = (suggestionOk ? prefixSuggestion : "ns"+nsCounter++);
                    ((NSContext)currentContext).declare(prefix, uri);
                    nsDeclarations.add(new PrefixMap(prefix, uri));
                    retVal = prefix;
                }
                else
                    retVal = alternative; // declared, different prefix.
            }
        }
        return retVal;
    }
    
    // simple struct for attribute collection
    private class Attrib
    {
        Attrib(final String namespace, final String localName, final String prefix, final String value)
        {
            this.namespace = PreCondition.assertNotNull(namespace);
            this.localName = PreCondition.assertNotNull(localName);
            this.prefix = prefix;
            this.value = PreCondition.assertNotNull(value);
        }
        final String namespace;
        final String localName;
        String prefix;
        final String value;
    }
    
    // another simple struct, for namespace/prefix mappings
    private class PrefixMap
    {
        PrefixMap(final String prefix, final String uri)
        {
            this.prefix = PreCondition.assertNotNull(prefix);
            this.uri = PreCondition.assertNotNull(uri);
        }
        final String prefix;
        final String uri;
    }
    
    private class NSContext implements NamespaceContext
    {

        public String getNamespaceURI(String prefix)
        {
            return content.get(prefix);
        }

        public String getPrefix(String namespaceURI)
        {
            Iterator<String> it = getPrefixes(namespaceURI);
            if (it.hasNext())
                return it.next();
            return null;
        }

        public Iterator<String> getPrefixes(String namespaceURI)
        {
            PreCondition.assertNotNull(namespaceURI);
            Set<String> prefixes = new HashSet<String>();
            for (Map.Entry<String, String> entry : content.entrySet())
            {
                if (entry.getValue().equals(namespaceURI))
                    prefixes.add(entry.getKey());
            }
            return prefixes.iterator();
        }
        
        void declare(final String prefix, final String uri)
            throws GenXDMException
        {
            if (content.containsKey(prefix))
                throw new GenXDMException("Prefix bound to multiple URIs in a single scope.");
            content.put(prefix, uri);
        }
        
//        Iterable<String> getDeclarations()
//        {
//            return content.keySet();
//        }
        private Map<String, String> content = new HashMap<String, String>();
    }

    private final XMLStreamWriter output;

    /**
     * State variables for the cached element, its namespaces and attributes.
     */
    private boolean elementPending = false; // the element has not been flushed.
    private String elementNamespace;
    private String elementLocalName;
    private String elementPrefix;
    private NamespaceContext currentContext;
    private final ArrayList<Attrib> attributes = new ArrayList<Attrib>();
    private final ArrayList<PrefixMap> nsDeclarations = new ArrayList<PrefixMap>();
    private final Stack<NamespaceContext> contexts = new Stack<NamespaceContext>();
    
    private int nsCounter = 0;
}