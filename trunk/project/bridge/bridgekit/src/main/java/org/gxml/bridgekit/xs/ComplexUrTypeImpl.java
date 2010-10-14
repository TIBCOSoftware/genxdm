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

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.components.SmParticle;
import org.genxdm.xs.components.SmWildcard;
import org.genxdm.xs.constraints.SmAttributeUse;
import org.genxdm.xs.constraints.SmNamespaceConstraint;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmProcessContentsMode;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.types.SmComplexUrType;
import org.genxdm.xs.types.SmContentType;
import org.genxdm.xs.types.SmPrimeChoiceType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmType;

/**
 * xs:anyType
 */
final class ComplexUrTypeImpl<A> extends AbstractPrimeExcludingNoneType<A> implements SmComplexUrType<A>
{
	private final SmWildcard<A> attributeWildcard;
	private final SmContentType<A> contentType;
	private final QName name;

	public ComplexUrTypeImpl(final String W3C_XML_SCHEMA_NS_URI, final NameSource nameBridge)
	{
		this.name = new QName(W3C_XML_SCHEMA_NS_URI, "anyType");

		final SmWildcard<A> anyTerm = new WildcardImpl<A>(SmProcessContentsMode.Lax, SmNamespaceConstraint.Any(nameBridge));

		final LinkedList<SmParticle<A>> particles = new LinkedList<SmParticle<A>>();
		particles.add(new ParticleWithWildcardTerm<A>(0, anyTerm));

		final SmModelGroup<A> modelGroup = new ModelGroupImpl<A>(SmModelGroup.SmCompositor.Sequence, particles, null, true, SmScopeExtent.Local);

		this.contentType = new ContentTypeImpl<A>(true, new ParticleWithModelGroupTerm<A>(1, 1, modelGroup));
		this.attributeWildcard = anyTerm;
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public boolean derivedFrom(final String namespace, final String name, final Set<SmDerivationMethod> derivationMethods)
	{
		return false;
	}

	public boolean derivedFromType(final SmType<A> ancestorType, final Set<SmDerivationMethod> derivationMethods)
	{
		return false;
	}

	public Map<QName, SmAttributeUse<A>> getAttributeUses()
	{
		return Collections.emptyMap();
	}

	public SmWildcard<A> getAttributeWildcard()
	{
		return attributeWildcard;
	}

	public ComplexUrTypeImpl<A> getBaseType()
	{
		return null;
	}

	public SmContentType<A> getContentType()
	{
		return contentType;
	}

	public SmDerivationMethod getDerivationMethod()
	{
		return SmDerivationMethod.Restriction;
	}

	public Set<SmDerivationMethod> getFinal()
	{
		return EnumSet.noneOf(SmDerivationMethod.class);
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.ANY_TYPE;
	}

	public String getLocalName()
	{
		return name.getLocalPart();
	}

	public QName getName()
	{
		return name;
	}

	public Set<SmDerivationMethod> getProhibitedSubstitutions()
	{
		// TODO: Is this immutable? If not, wrap it with Collections.unmod
		return EnumSet.noneOf(SmDerivationMethod.class);
	}

	public SmScopeExtent getScopeExtent()
	{
		return SmScopeExtent.Global;
	}

	public String getTargetNamespace()
	{
		return name.getNamespaceURI();
	}

	public boolean isAbstract()
	{
		return false;
	}

	public boolean isAnonymous()
	{
		return false;
	}

	public boolean isAtomicType()
	{
		return false;
	}

	public boolean isAtomicUrType()
	{
		return false;
	}

	public boolean isChoice()
	{
		return false;
	}

	public boolean isComplexUrType()
	{
		return true;
	}

	public boolean isFinal(final SmDerivationMethod derivation)
	{
		return false;
	}

	public boolean isLocked()
	{
		return true;
	}

	public boolean isNative()
	{
		return true;
	}

	public boolean isSimpleUrType()
	{
		return false;
	}

	public void lock()
	{
		// Ignore.
	}

	public SmPrimeType<A> prime()
	{
		return this;
	}

	public void setAbstract(final boolean isAbstract)
	{
		throw new AssertionError(getName());
	}

	public void setAttributeWildcard(final SmWildcard<A> attributeWildcard)
	{
		throw new AssertionError(getName());
	}

	public void setBlock(final SmDerivationMethod derivation, final boolean enabled)
	{
		throw new AssertionError(getName());
	}

	public void setContentType(final SmContentType<A> contentType)
	{
		throw new AssertionError(getName());
	}

	public void setFinal(final SmDerivationMethod derivation, final boolean enabled)
	{
		throw new AssertionError(getName());
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		PreCondition.assertArgumentNotNull(rhs, "type");
		switch (rhs.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)rhs;
				return subtype(choiceType.getLHS()) || subtype(choiceType.getRHS());
			}
			case ANY_TYPE:
			{
				return true;
			}
			default:
			{
				return false;
			}
		}
	}

	@Override
	public final String toString()
	{
		return getName().toString();
	}
}
