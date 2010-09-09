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
package org.gxml.processor.w3c.xs.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;

import org.gxml.processor.w3c.xs.exception.CvcMissingKeyFieldException;
import org.gxml.processor.w3c.xs.exception.SrcDuplicateKeyTargetException;
import org.gxml.xs.constraints.SmIdentityConstraint;
import org.gxml.xs.constraints.SmIdentityConstraintKind;
import org.gxml.xs.exceptions.SmAbortException;
import org.gxml.xs.exceptions.SmExceptionHandler;
import org.gxml.xs.resolve.SmLocation;


/**
 * Specialization of a scope for xs:key and xs:unique.
 * 
 * Note: According to the XML Schema Part 1 Specification, both xs:key and xs:unique can be referenced by xs:keyref.
 * This is why this class is used to represent both xs:key and xs:unique. However, there are some differences. Both key
 * and unique assert uniqueness, with respect to the content identified by the selector, of the tuples resulting from
 * the fields. Only key further requires that all selected content has such tuples.
 */
final class IdentityScopeKey<A> extends IdentityScope<A>
{
	public final HashMap<IdentityTuple<A>, IdentityVariant<A>> m_qualifiedTargets = new HashMap<IdentityTuple<A>, IdentityVariant<A>>();

	public IdentityScopeKey(final int elementIndex, final SmIdentityConstraint<A> constraint, final SmExceptionHandler errorHandler, final SmLocation location)
	{
		super(elementIndex, constraint, errorHandler, location);
	}

	@Override
	protected void onKeysComplete(final ArrayList<IdentityKey<A>> keyValues, final int elementIndex) throws SmAbortException
	{
		final IdentityTuple<A> key = new IdentityTuple<A>(keyValues);

		final IdentityVariant<A> mapped = m_qualifiedTargets.get(key);

		if (mapped == null || mapped.isDanglingRefs())
		{
			// List was the undeclared refs
			m_qualifiedTargets.put(key, new IdentityVariant<A>(Boolean.TRUE));
		}
		else if (mapped.isValue())
		{
			m_errorHandler.error(new SrcDuplicateKeyTargetException(getConstraint().getName(), keyValues, m_location));
		}
		else
		{
			// Unexpected.
			throw new AssertionError();
		}
	}

	@Override
	protected void onScopeEnd(final int elementIndex, final Locatable locatable) throws SmAbortException
	{
		final SmIdentityConstraint<A> constraint = getConstraint();
		final SmIdentityConstraintKind category = constraint.getCategory();
		// xs:key must have bound values while xs:unique need not exist.
		if (category.isKey())
		{
			final ArrayList<IdentityField<A>> elementHandlers = m_fieldEvals.get(elementIndex);
			PreCondition.assertArgumentNotNull(elementHandlers, "elementHandlers");
			if (m_boundFields.get(elementIndex) < elementHandlers.size())
			{
				final SmLocation frozenLocation = locatable.getLocation();
				for (int i = 0; i < elementHandlers.size(); i++)
				{
					m_errorHandler.error(new CvcMissingKeyFieldException(constraint.getName(), i + 1, frozenLocation));
				}
			}
		}
	}
}
