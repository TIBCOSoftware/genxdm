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

import org.genxdm.processor.xpath.v10.expressions.DynamicContextBuilderBaseImpl;
import org.genxdm.xpath.v10.TraverserDynamicContext;
import org.genxdm.xpath.v10.TraverserDynamicContextBuilder;
import org.genxdm.xpath.v10.TraverserVariant;

public class TraverserDynamicContextBuilderImpl extends DynamicContextBuilderBaseImpl
    implements TraverserDynamicContextBuilder {

    private final Map<QName, TraverserVariant> variables = new HashMap<QName, TraverserVariant>();
    
    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void bindVariableValue(QName name, TraverserVariant value) {
        variables.put(name, value);
    }

    @Override
    public TraverserDynamicContext build() {
        return new TraverserDynamicContextImpl(position, size, variables, m_inheritAttributes, m_inheritNamespaces);
    }

}
