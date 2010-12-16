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

import java.util.Comparator;

import org.genxdm.axes.AxisNodeNavigator;
import org.genxdm.axes.NodeNavigator;
import org.genxdm.base.io.ContentHandler;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.nodes.NodeInformer;

/**
 * The model provides the tree API.
 *
 * <p/>
 * 
 * End users will normally consume, rather than implement, this interface.
 * 
 * @param <N> Corresponds to the base type for all members of the underlying tree API.
 */
public interface Model<N>
    extends Comparator<N>, NodeInformer<N>, NodeNavigator<N>, AxisNodeNavigator<N>
{
    /**
     * Support for streaming a node to a {@link ContentHandler}.
     * 
     * @param node
     *            The node to be streamed.
     * @param copyNamespaces
     *            Determines whether namespaces nodes are streamed.
     * @param handler
     *            The handler for events generated.
     */
    void stream(N node, boolean copyNamespaces, ContentHandler handler) throws GxmlException;
}
