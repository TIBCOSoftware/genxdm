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
import org.genxdm.nodes.TraversingInformerDelegate;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.NodeSetExpr;

public class SequenceComposeTraverser extends TraversingInformerDelegate implements Traverser
{
    private final Traverser iter1;
    private Traverser iter2;
    private final NodeSetExpr expr;
    private final TraverserDynamicContext context;

    public SequenceComposeTraverser(final Traverser iter, final NodeSetExpr expr, final TraverserDynamicContext context)
    {
        super(null);
        this.iter1 = iter;
        this.expr = expr;
        this.context = context;
        this.iter2 = NullNodeTraverser.DEFAULT;
    }

    @Override
    public boolean moveToNext() {
        for (;;)
        {
            if (iter2.moveToNext()) {
                setInformer(iter2);
                return true;
            }
            
            setInformer(null);
            if (!iter1.moveToNext())
                return false;
            
            iter2 = expr.traverseNodes(iter1, context);
        }
    }

    @Override
    public boolean isFinished() {
        return iter1.isFinished() && iter2.isFinished();
    }
    
}
