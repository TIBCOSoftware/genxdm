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
package org.genxdm.io;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLReporter;


/**
 * Interface for manufacturing {@link DocumentHandler} instances
 * 
 * <p>The DocumentHandler interface is designed to be used with a single thread
 * as it allows the client to set the error handler for parsing problems.  Since
 * a particular processor may handle errors differently in different scenarios,
 * this interface allows the client to manufacture multiple <code>DocumentHandler</code>
 * instances depending on needs.</p>
 * 
 * <p>This is roughly analogous to the distinction between a {@link DocumentBuilderFactory}
 * and a {@link DocumentBuilder}.
 * </p>
 */
public interface DocumentHandlerFactory<N> {

    XMLReporter getDefaultReporter();
    
    Resolver getDefaultResolver();
    
	/**
     * Returns a new builder for parsing and writing data models.
     */
    DocumentHandler<N> newDocumentHandler();
    
    DocumentHandler<N> newDocumentHandler(XMLReporter reporter, Resolver resolver);

    void setDefaultReporter(XMLReporter reporter);
    
    void setDefaultResolver(Resolver resolver);
}
