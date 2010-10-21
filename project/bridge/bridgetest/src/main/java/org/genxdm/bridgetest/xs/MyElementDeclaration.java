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
package org.genxdm.bridgetest.xs;

import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.IllegalNullArgumentException;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.Schema;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.constraints.SmValueConstraint;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.NodeKind;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmType;

public final class MyElementDeclaration<A> implements ElementDefinition<A>
{
	@SuppressWarnings("unused")
	private final Schema<A> schema;
	@SuppressWarnings("unused")
	private final AtomBridge<A> atomBridge;
	private final QName name;

	public MyElementDeclaration(final String namespaceURI, final String localName, final Schema<A> schema, final AtomBridge<A> atomBridge)
	{
		this.schema = IllegalNullArgumentException.check(schema, "schema");
		this.atomBridge = atomBridge;
		IllegalNullArgumentException.check(namespaceURI, "namespaceURI");
		IllegalNullArgumentException.check(localName, "localName");
		this.name = new QName(namespaceURI, localName);
	}

	public void accept(SmSequenceTypeVisitor<A> visitor)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Set<DerivationMethod> getDisallowedSubtitutions()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<SmIdentityConstraint<A>> getIdentityConstraints()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmPrimeTypeKind getKind()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public String getLocalName()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public QName getName()
	{
		return name;
	}

	public NodeKind getNodeKind()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public ScopeExtent getScopeExtent()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public ElementDefinition<A> getSubstitutionGroup()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Set<DerivationMethod> getSubstitutionGroupExclusions()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<ElementDefinition<A>> getSubstitutionGroupMembers()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public String getTargetNamespace()
	{
		return name.getNamespaceURI();
	}

	public SmType<A> getType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmValueConstraint<A> getValueConstraint()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean hasIdentityConstraints()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean hasSubstitutionGroup()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean hasSubstitutionGroupMembers()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean isAbstract()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean isAnonymous()
	{
		return false;
	}

	public boolean isChoice()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean isNative()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean isNillable()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean isNone()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmPrimeType<A> prime()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public KeeneQuantifier quantifier()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean subtype(SmPrimeType<A> rhs)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
}
