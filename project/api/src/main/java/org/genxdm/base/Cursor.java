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
package org.genxdm.base;

import org.genxdm.axes.Repositioner;
import org.genxdm.base.io.ContentHandler;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.nodes.Bookmark;
import org.genxdm.nodes.Informer;

/**
 * A cursor is a random-access representation of an XML tree with the unique property that it moves over the underlying
 * data model without forcing the implementation to provide object handles for each node visited.
 * <p>
 * A cursor may be moved over the underlying data model and provides access to the properties of its underlying node.
 * </p>
 */
public interface Cursor<N>
    extends Informer, Repositioner<N>, Comparable<Cursor<N>>
{
    /**
     * Obtain a fixed-location marker initialized with the current node.
     */
    Bookmark<N> bookmark();
    
    /**
     * Support for streaming the current node to a {@link ContentHandler}.
     * 
     * @param writer
     *            The handler for events generated.
     * 
     * @throws GxmlException
     */
    void write(ContentHandler writer) throws GxmlException;
}
