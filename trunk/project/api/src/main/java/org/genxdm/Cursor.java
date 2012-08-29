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
package org.genxdm;

import org.genxdm.axes.Repositioner;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.ContentHandler;
import org.genxdm.nodes.Bookmark;
import org.genxdm.nodes.Informer;

/**
 * A cursor provides the XQuery Data Model interface with the addition of the
 * notion of positional state.
 * 
 * <p>A cursor may be moved over the underlying data model and provides access 
 * to the properties of its underlying node.</p>
 */
public interface Cursor<N>
    extends Precursor, Repositioner<N>, Comparable<Cursor<N>>
{
    /**
     * Obtain a fixed-location marker initialized with the current node.
     */
    Bookmark<N> bookmark();
    
}
