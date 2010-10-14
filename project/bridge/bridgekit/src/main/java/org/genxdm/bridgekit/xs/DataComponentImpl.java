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
package org.genxdm.bridgekit.xs;

import javax.xml.namespace.QName;

import org.genxdm.xs.components.SmDataComponent;
import org.genxdm.xs.constraints.SmHasValueConstraint;
import org.genxdm.xs.constraints.SmValueConstraint;
import org.genxdm.xs.enums.SmScopeExtent;

public abstract class DataComponentImpl<A> extends NamedComponentImpl<A> implements SmDataComponent<A>, SmHasValueConstraint<A>
{
	// The {value constraint} is mutable and optional.
	private SmValueConstraint<A> m_valueConstraint = null;

	public DataComponentImpl(final QName name, final SmScopeExtent scope)
	{
		super(name, false, scope);
	}

	public final SmValueConstraint<A> getValueConstraint()
	{
		return m_valueConstraint;
	}

	public final void setValueConstraint(final SmValueConstraint<A> valueConstraint)
	{
		m_valueConstraint = valueConstraint;
	}
}
