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
package org.genxdm.processor.w3c.xs.impl;

import java.util.ArrayList;
import java.util.List;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.EnumerationDefinition;

final class FacetEnumerationImpl implements EnumerationDefinition
{
    private final List<String> m_value;

    public FacetEnumerationImpl(final List<String> value)
    {
        this.m_value = new ArrayList<String>();
        this.m_value.addAll(PreCondition.assertArgumentNotNull(value, "value"));
    }

    public <A> List<A> getValue(AtomBridge<A> bridge)
    {
        // TODO: this is no longer the way it works; fix it so it converts on the fly.
//      return m_value;
        return null;
    }

    // TODO: still need the override, but not like this.
//  @Override
//  public String toString()
//  {
//      return atomBridge.getC14NString(m_value);
//  }
}
