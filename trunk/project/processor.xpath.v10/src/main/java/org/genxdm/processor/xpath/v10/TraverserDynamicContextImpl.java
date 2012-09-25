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
package org.genxdm.processor.xpath.v10;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.processor.xpath.v10.expressions.DynamicContextBaseImpl;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.TraverserVariant;

public class TraverserDynamicContextImpl extends DynamicContextBaseImpl implements
        TraverserDynamicContext {

    private final Map<QName, TraverserVariant> variables;

    public TraverserDynamicContextImpl(final int position, final int size,
            final Map<? extends QName, ? extends TraverserVariant> variables,
                    boolean inheritAttributes, boolean inheritNamespaces)
    {
        super(position, size, inheritAttributes, inheritNamespaces);
        this.variables = new HashMap<QName, TraverserVariant>(variables);
    }

    @Override
    public TraverserVariant getVariableValue(QName name) {
        return variables.get(name);
    }

}
