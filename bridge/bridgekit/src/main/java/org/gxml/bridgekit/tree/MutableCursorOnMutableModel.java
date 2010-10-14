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
package org.gxml.bridgekit.tree;

import org.genxdm.base.mutable.MutableCursor;
import org.genxdm.base.mutable.MutableModel;

public class MutableCursorOnMutableModel<N>
    extends CursorOnModel<N>
    implements MutableCursor<N>
{

    public MutableCursorOnMutableModel(N node, MutableModel<N> model)
    {
        super(node, model);
//        PreCondition.assertTrue(context.isMutable());
        this.tmodel = model;
    }

    public N adoptNode(N source)
    {
        return tmodel.adoptNode(node, source);
    }

    public void appendChild(N newChild)
    {
        tmodel.appendChild(node, newChild);
    }

    public N clone(boolean deep)
    {
        return tmodel.cloneNode(node, deep);
    }

    public N getOwner()
    {
        return tmodel.getOwner(node);
    }

    public N importNode(N source, boolean deep)
    {
        return tmodel.importNode(node, source, deep);
    }

    public N insertBefore(N newChild, N refChild)
    {
        return tmodel.insertBefore(node, newChild, refChild);
    }

    public void normalize()
    {
        tmodel.normalize(node);
    }

    public void removeAttribute(String namespaceURI, String localName)
    {
        tmodel.removeAttribute(node, namespaceURI, localName);
    }

    public N removeChild(N oldChild)
    {
        return tmodel.removeChild(node, oldChild);
    }

    public void removeNamespace(String prefix)
    {
        tmodel.removeNamespace(node, prefix);
    }

    public N replaceChild(N newChild, N oldChild)
    {
        return tmodel.replaceChild(node, newChild, oldChild);
    }

    public void setAttribute(N attribute)
    {
        tmodel.setAttribute(node, attribute);
    }

    public N setAttribute(String namespaceURI, String localName, String prefix, String value)
    {
        return tmodel.setAttribute(node, namespaceURI, localName, prefix, value);
    }

    public void setNamespace(N namespace)
    {
        tmodel.setNamespace(node, namespace);
    }

    public void setNamespace(String prefix, String uri)
    {
        tmodel.setNamespace(node, prefix, uri);
    }

    private final MutableModel<N> tmodel;
}
