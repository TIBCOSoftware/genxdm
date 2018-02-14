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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLReporter;

import org.genxdm.io.DocumentHandler;
import org.genxdm.io.DocumentHandlerFactory;
import org.genxdm.io.Resolver;
import org.genxdm.io.SerializationParams;
import org.w3c.dom.Node;

/**
 * DOM-specific factory class for instantiating DocumentHandler<Node> instances.
 */
public class DomDocumentHandlerFactory implements DocumentHandlerFactory<Node> {

    public DomDocumentHandlerFactory(DocumentBuilderFactory dbf) {
        m_dbf = ( dbf == null ? DomProcessingContext.sm_dbf : dbf );
    }
    
    public DocumentBuilderFactory getDocumentBuilderFactory() {
        return m_dbf;
    }
    
    public DocumentHandler<Node> newDocumentHandler() {
        return new DomDocumentHandler(m_dbf);
    }

    public DocumentHandler<Node> newDocumentHandler(XMLReporter reporter, Resolver resolver)
    {
        // TODO: implement
        return newDocumentHandler();
    }

    public void setDefaultReporter(XMLReporter reporter)
    {
        this.reporter = reporter;
    }
    
    public void setDefaultResolver(Resolver resolver)
    {
        this.resolver = resolver;
    }
    
    public XMLReporter getDefaultReporter()
    {
        return reporter;
    }
    
    public Resolver getDefaultResolver()
    {
        return resolver;
    }

    @Override
    public SerializationParams getDefaultParameters()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDefaultParameters(SerializationParams config)
    {
        // TODO Auto-generated method stub
        
    }
   final DocumentBuilderFactory m_dbf;
    private XMLReporter reporter;
    private Resolver resolver;
}
