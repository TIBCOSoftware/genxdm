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
package org.genxdm.bridgekit.tree;

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

    public void appendChild(N newChild)
    {
        tmodel.appendChild(node, newChild);
    }

    public N insertBefore(N newChild, N refChild)
    {
        return tmodel.insertBefore(refChild, newChild);
    }

    public void removeAttribute(String namespaceURI, String localName)
    {
    	tmodel.delete(tmodel.getAttribute(node, namespaceURI, localName));
//        tmodel.removeAttribute(node, namespaceURI, localName);
    }

    public N removeChild(N oldChild)
    {
        return tmodel.delete(oldChild);
    }

    public void removeNamespace(String prefix)
    {
//        tmodel.removeNamespace(node, prefix);
    }

    public N replaceChild(N newChild, N oldChild)
    {
        return tmodel.replace(oldChild, newChild);
    }

    public void setAttribute(N attribute)
    {
        tmodel.insertAttribute(node, attribute);
    }

    public N setAttribute(String namespaceURI, String localName, String prefix, String value)
    {
        return null;//tmodel.setAttribute(node, namespaceURI, localName, prefix, value);
    }

    public void setNamespace(N namespace)
    {
//        tmodel.setNamespace(node, namespace);
    }

    public void setNamespace(String prefix, String uri)
    {
        tmodel.propagateNamespace(node, prefix, uri);
    }

    private final MutableModel<N> tmodel;
}
