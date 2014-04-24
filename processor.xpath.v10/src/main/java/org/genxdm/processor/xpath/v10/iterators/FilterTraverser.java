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

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.tree.TraversingInformerDelegate;
import org.genxdm.nodes.Traverser;
import org.genxdm.xpath.v10.BooleanExpr;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.TraverserVariant;
import org.genxdm.xpath.v10.ExtensionContext;

public class FilterTraverser
    extends TraversingInformerDelegate
    implements Traverser, TraverserDynamicContext
{

    protected final TraverserDynamicContext origContext;
    private int pos = 0;
    private int lastPos = 0;
    private Traverser iter;
    private final BooleanExpr predicate;
    
    public FilterTraverser(final Traverser iter, final TraverserDynamicContext dynEnv, final BooleanExpr predicate)
    {
        super(null);
        origContext = dynEnv;
        this.iter = iter;
        this.predicate = predicate;
    }
    
    @Override
    public boolean moveToNext()
    {
        for (;iter.moveToNext();)
        {
            ++pos;
            if (predicate.booleanFunction(iter, this))
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isFinished() {
        return iter.isFinished();
    }
    
    @Override
    public int getContextPosition()
    {
        return pos;
    }
    
    @Override
    public int getContextSize()
    {
        if (lastPos == 0)
        {
            CloneableTraverser cloneIter;
            if (iter instanceof CloneableTraverser)
            {
                cloneIter = (CloneableTraverser)iter;
            }
            else
            {
                cloneIter = new CloneableTraverserImpl(iter);
            }
            iter = (Traverser)cloneIter.clone();
            int savePosition = pos;
            try
            {
                while (moveToNext())
                    ;
                lastPos = pos;
            }
            finally
            {
                pos = savePosition;
                iter = cloneIter;
            }
        }
        return lastPos;
    }
    
    public ExtensionContext getExtensionContext(final String namespace)
    {
        return origContext.getExtensionContext(namespace);
    }
    
    public TraverserVariant getVariableValue(final QName name)
    {
        return origContext.getVariableValue(name);
    }
    
    @Override
    public boolean getInheritAttributes() {
        return origContext.getInheritAttributes();
    }
    
    @Override
    public boolean getInheritNamespaces() {
        return origContext.getInheritNamespaces();
    }

}
