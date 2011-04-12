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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genxdm.Cursor;
import org.genxdm.Model;
import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.nodes.Bookmark;

public class BookmarkOnModel<N>
    extends InformerOnModel<N>
    implements Bookmark<N>
{
    
    public BookmarkOnModel(final N node, final Model<N> model)
    {
        super(node, model);
    }

    public Model<N> getModel()
    {
        return model;
    }

    public Object getState(String key)
    {
        return state.get(key);
    }

    public Cursor<N> newCursor()
    {
        return new CursorOnModel<N>(node, model);
    }

    public void putState(String key, Object data)
    {
        state.put(key, data);
    }
    
    public N getNode()
    {
        return node;
    }

    public List<N> getNodes()
    {
        return new UnaryIterable<N>(node);
    }

    private final Map<String, Object> state = new HashMap<String, Object>();
}
