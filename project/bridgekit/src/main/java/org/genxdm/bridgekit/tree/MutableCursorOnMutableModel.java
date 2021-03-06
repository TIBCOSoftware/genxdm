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
package org.genxdm.bridgekit.tree;

import org.genxdm.mutable.MutableCursor;
import org.genxdm.mutable.MutableModel;
import org.genxdm.mutable.NodeFactory;

public class MutableCursorOnMutableModel<N>
    extends CursorOnModel<N>
    implements MutableCursor<N>
{
    
    public MutableCursorOnMutableModel(N node, MutableModel<N> model)
    {
        super(node, model);
        this.tmodel = model;
    }
    
    public NodeFactory<N> getFactory()
    {
        return tmodel.getFactory(node);
    }

    public void appendChild(final N newChild)
    {
        tmodel.appendChild(node, newChild);
    }
    
    public void appendChildren(final Iterable<N> content)
    {
        tmodel.appendChildren(node, content);
    }
    
    public void prependChild(final N newChild)
    {
        tmodel.prependChild(node, newChild);
    }
    
    public void prependChildren(final Iterable<N> content)
    {
        tmodel.prependChildren(node, content);
    }

    public void insertBefore(final N previous)
    {
        tmodel.insertBefore(node, previous);
    }
    
    public void insertBefore(final Iterable<N> content)
    {
        tmodel.insertBefore(node, content);
    }
    
    public void insertAfter(final N next)
    {
        tmodel.insertAfter(node, next);
    }
    
    public void insertAfter(final Iterable<N> content)
    {
        tmodel.insertAfter(node, content);
    }

    public N delete()
    {
        N old = node;
        if (!moveToPreviousSibling())
            moveToParent();
        return tmodel.delete(old);
    }
    
    public Iterable<N> deleteChildren()
    {
        return tmodel.deleteChildren(node);
    }

    public N replace(final N newNode)
    {
        N old = tmodel.replace(node, newNode);
        if (old != null)
            setCurrentNode(newNode);
        return old;
    }
    
    public String replaceValue(final String value)
    {
        return tmodel.replaceValue(node, value);
    }

    public void insertAttribute(final N attribute)
    {
        tmodel.insertAttribute(node, attribute);
    }
    
    public void insertAttributes(final Iterable<N> attributes)
    {
        tmodel.insertAttributes(node, attributes);
    }

    public void insertNamespace(String prefix, String uri)
    {
        tmodel.insertNamespace(node, prefix, uri);
    }

    private final MutableModel<N> tmodel;
}
