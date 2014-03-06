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
import org.genxdm.xpath.v10.Converter;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.TraverserVariant;
import org.genxdm.xpath.v10.ExprException;

public class TraverserVariantImpl extends VariantCoreBase implements TraverserVariant {

    private final Traverser iter;

    public TraverserVariantImpl(final Traverser iter)
    {
        this.iter = iter;
    }

    @Override
    public boolean convertToBoolean() {
        return Converter.toBooleanFromTraverser(iter);
    }

    @Override
    public double convertToNumber() {
        return Converter.toNumber(iter);
    }

    @Override
    public String convertToString() {
        return Converter.toStringFromTraverser(iter);
    }

    @Override
    public boolean isNodeSet() {
        return true;
    }

    @Override
    public Traverser convertToTraverser() {
        return iter;
    }

    @Override
    public boolean convertToPredicate(TraverserDynamicContext context) {
        return convertToBoolean();
    }

    @Override
    public TraverserVariant makePermanentCursor() throws ExprException {
        return new PermanentTraverserVariant(iter);
    }

}
