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
package org.genxdm.processor.xpath.v10.variants;

import org.genxdm.nodes.Traverser;
import org.genxdm.processor.xpath.v10.iterators.CloneableTraverser;
import org.genxdm.processor.xpath.v10.iterators.CloneableTraverserImpl;
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.TraverserVariant;
import org.genxdm.xpath.v10.ExprException;

public class PermanentTraverserVariant extends VariantCoreBase implements TraverserVariant {

    private final CloneableTraverser iter;

    PermanentTraverserVariant(Traverser iter) throws ExprException
    {
        if (iter instanceof CloneableTraverser)
        {
            this.iter = (CloneableTraverser)iter;
        }
        else
        {
            this.iter = new CloneableTraverserImpl(iter);
        }
        this.iter.bind();
    }

    @Override
    public Traverser convertToTraverser()
    {
        return (CloneableTraverser)iter.clone();
    }

    public String convertToString()
    {
        return Converter.toString(convertToTraverser());
    }

    public boolean convertToBoolean()
    {
        return Converter.toBooleanFromTraverser(convertToTraverser());
    }

    @Override
    public double convertToNumber()
    {
        return Converter.toNumber(convertToTraverser());
    }

    @Override
    public boolean isNodeSet()
    {
        return true;
    }

    @Override
    public boolean convertToPredicate(TraverserDynamicContext context) {
        return convertToBoolean();
    }

    @Override
    public TraverserVariant makePermanentCursor() throws ExprException {
        return this;
    }
}
