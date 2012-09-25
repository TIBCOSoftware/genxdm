/*
 * Copyright (c) 2012 TIBCO Software Inc.
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
package org.genxdm.processor.xpath.v10.iterators;

import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;
import org.genxdm.nodes.TraversingInformerDelegate;

public class SingleTraverser extends TraversingInformerDelegate implements Traverser {

    private TraversingInformer node;
    
    public SingleTraverser(final TraversingInformer node)
    {
        super(null);
        this.node = node;
    }

    @Override
    public boolean moveToNext() {
        if (node != null) {
            setInformer(node);
            node = null;
        }
        else {
            setInformer(null);
        }
        return getInformer() != null;
    }

    @Override
    public boolean isFinished() {
        return this.node == null && getInformer() == null;
    }
}
