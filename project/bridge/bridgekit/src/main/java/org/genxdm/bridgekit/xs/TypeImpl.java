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

import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.types.SmType;

/**
 * Implementation Note: We're trying to maintain a single implementation of SmType to represent complex and simple types as well as lists and unions. The advantage of this is that the implementation does not have to downcast.
 */
abstract class TypeImpl<A> extends NamedComponentImpl<A> implements SmType<A>
{
	private final SmDerivationMethod derivation;
	private final QName name;
	private final NameSource nameBridge;

	public TypeImpl(final QName name, final boolean isAnonymous, final SmScopeExtent scope, final SmDerivationMethod derivation, final NameSource nameBridge)
	{
		super(name, isAnonymous, scope);
		this.name = PreCondition.assertArgumentNotNull(name, "name");
		this.nameBridge = nameBridge;
		this.derivation = PreCondition.assertArgumentNotNull(derivation, "derivation");
	}

	public boolean derivedFrom(final String namespace, final String name, final Set<SmDerivationMethod> derivationMethods)
	{
		return SmSupportImpl.derivedFrom(this, namespace, name, derivationMethods, nameBridge);
	}

	public boolean derivedFromType(final SmType<A> ancestorType, final Set<SmDerivationMethod> derivationMethods)
	{
		return SmSupportImpl.derivedFromType(this, ancestorType, derivationMethods, nameBridge);
	}

	public final SmDerivationMethod getDerivationMethod()
	{
		return derivation;
	}

	@Override
	public String toString()
	{
		return name.toString();
	}
}
