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

import java.util.EnumSet;
import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.SmDerivationMethod;

final class XMLElement<A> extends XMLDeclaration<A> implements XMLParticleTerm<A>
{
	private final EnumSet<SmDerivationMethod> m_block = EnumSet.noneOf(SmDerivationMethod.class);
	private final EnumSet<SmDerivationMethod> m_final = EnumSet.noneOf(SmDerivationMethod.class);
	private boolean m_isAbstract = false;
	private boolean m_isNillable = false;
	public XMLElement<A> substitutionGroup;
	private final LinkedList<XMLIdentityConstraint<A>> m_identityConstraints = new LinkedList<XMLIdentityConstraint<A>>();

	public XMLElement(final QName name, final XMLScope<A> scope, final XMLTypeRef<A> anyType, final SrcFrozenLocation location)
	{
		super(PreCondition.assertArgumentNotNull(name, "name"), scope, anyType, location);
	}

	public XMLElement(final QName name, final XMLScope<A> scope, final XMLTypeRef<A> anyType)
	{
		super(PreCondition.assertArgumentNotNull(name, "name"), scope, anyType);
	}

	public EnumSet<SmDerivationMethod> getBlock()
	{
		return m_block;
	}

	public EnumSet<SmDerivationMethod> getFinal()
	{
		return m_final;
	}

	public boolean isAbstract()
	{
		return m_isAbstract;
	}

	public void setAbstractFlag(final boolean isAbstract)
	{
		m_isAbstract = isAbstract;
	}

	public boolean isNillable()
	{
		return m_isNillable;
	}

	public void setNillableFlag(final boolean isNillable)
	{
		m_isNillable = isNillable;
	}

	public LinkedList<XMLIdentityConstraint<A>> getIdentityConstraints()
	{
		return m_identityConstraints;
	}
}
