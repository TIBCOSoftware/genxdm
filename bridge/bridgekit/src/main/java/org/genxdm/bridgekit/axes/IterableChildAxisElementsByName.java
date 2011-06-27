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
package org.genxdm.bridgekit.axes;

import java.util.Iterator;

import org.genxdm.Model;

public final class IterableChildAxisElementsByName<N> implements Iterable<N>
{
    private final N m_origin;
    private final String m_namespaceURI;
    private final String m_localName;
    private final Model<N> m_model;

    public IterableChildAxisElementsByName(final N origin, final String namespaceURI, final String localName, final Model<N> model)
    {
        this.m_model = model;
        this.m_origin = origin;
        this.m_namespaceURI = namespaceURI;
        this.m_localName = localName;
    }

    public Iterator<N> iterator()
    {
        return new IteratorChildAxisElementsByName<N>(m_origin, m_namespaceURI, m_localName, m_model);
    }
}
