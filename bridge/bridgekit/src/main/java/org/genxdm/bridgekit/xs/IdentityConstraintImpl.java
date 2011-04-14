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

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.constraints.IdentityConstraintKind;
import org.genxdm.xs.constraints.RestrictedXPath;
import org.genxdm.xs.enums.ScopeExtent;

public final class IdentityConstraintImpl<A> extends NamedComponentImpl<A> implements IdentityConstraint<A>
{
	private final IdentityConstraintKind m_category;
	private final List<RestrictedXPath> m_fields;
	private final IdentityConstraint<A> m_keyConstraint;
	private final QName m_name;
	private final RestrictedXPath m_selector;

	public IdentityConstraintImpl(final QName name, final IdentityConstraintKind category, final RestrictedXPath selector, final List<RestrictedXPath> fields, final IdentityConstraint<A> keyConstraint)
	{
		super(name, false, ScopeExtent.Global);
		m_name = PreCondition.assertArgumentNotNull(name, "name");
		m_category = PreCondition.assertArgumentNotNull(category, "category");
		m_selector = PreCondition.assertArgumentNotNull(selector, "selector");
		m_fields = PreCondition.assertArgumentNotNull(fields, "fields");
		m_keyConstraint = keyConstraint;
	}

	public IdentityConstraintKind getCategory()
	{
		return m_category;
	}

	public List<RestrictedXPath> getFields()
	{
		return m_fields;
	}

	public IdentityConstraint<A> getKeyConstraint()
	{
		return m_keyConstraint;
	}

	public RestrictedXPath getSelector()
	{
		return m_selector;
	}

	public boolean sameAs(final IdentityConstraint<A> constraint)
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
