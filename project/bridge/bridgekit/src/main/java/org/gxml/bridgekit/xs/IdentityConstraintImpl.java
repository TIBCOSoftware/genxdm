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
package org.gxml.bridgekit.xs;

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.constraints.SmIdentityConstraintKind;
import org.genxdm.xs.constraints.SmRestrictedXPath;
import org.genxdm.xs.enums.SmScopeExtent;

public final class IdentityConstraintImpl<A> extends NamedComponentImpl<A> implements SmIdentityConstraint<A>
{
	private final SmIdentityConstraintKind m_category;
	private final List<SmRestrictedXPath> m_fields;
	private final SmIdentityConstraint<A> m_keyConstraint;
	private final QName m_name;
	private final SmRestrictedXPath m_selector;

	public IdentityConstraintImpl(final QName name, final SmIdentityConstraintKind category, final SmRestrictedXPath selector, final List<SmRestrictedXPath> fields, final SmIdentityConstraint<A> keyConstraint)
	{
		super(name, false, SmScopeExtent.Global);
		m_name = PreCondition.assertArgumentNotNull(name, "name");
		m_category = PreCondition.assertArgumentNotNull(category, "category");
		m_selector = PreCondition.assertArgumentNotNull(selector, "selector");
		m_fields = PreCondition.assertArgumentNotNull(fields, "fields");
		m_keyConstraint = keyConstraint;
	}

	public SmIdentityConstraintKind getCategory()
	{
		return m_category;
	}

	public List<SmRestrictedXPath> getFields()
	{
		return m_fields;
	}

	public SmIdentityConstraint<A> getKeyConstraint()
	{
		return m_keyConstraint;
	}

	public SmRestrictedXPath getSelector()
	{
		return m_selector;
	}

	public boolean sameAs(final SmIdentityConstraint<A> constraint)
	{
		PreCondition.assertArgumentNotNull(constraint, "constraint");
		if (this == constraint)
		{
			return true;
		}
		else
		{
			return getName().equals(constraint.getName());
		}
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(m_category);
		sb.append(":");
		sb.append(m_name);
		return sb.toString();
	}
}