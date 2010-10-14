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
package org.gxml.processor.w3c.xs;

import java.util.ArrayList;
import java.util.List;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.SmEnumeration;

final class FacetEnumerationImpl<A> implements SmEnumeration<A>
{
	private final List<A> m_value;
	private final AtomBridge<A> atomBridge;

	public FacetEnumerationImpl(final List<? extends A> value, final AtomBridge<A> atomBridge)
	{
		this.m_value = new ArrayList<A>();
		this.m_value.addAll(PreCondition.assertArgumentNotNull(value, "value"));
		this.atomBridge = atomBridge;
	}

	public List<A> getValue()
	{
		return m_value;
	}

	@Override
	public String toString()
	{
		return atomBridge.getC14NString(m_value);
	}
}
