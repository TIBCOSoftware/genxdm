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

import java.math.BigInteger;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmWhiteSpacePolicy;

final class XMLType<A> extends XMLComponent<A>
{
	private final QName name;
	private final EnumSet<SmDerivationMethod> m_block = EnumSet.noneOf(SmDerivationMethod.class);
	private final EnumSet<SmDerivationMethod> m_final = EnumSet.noneOf(SmDerivationMethod.class);
	private SmDerivationMethod m_derivation;
	private XMLTypeRef<A> m_baseRef;
	private boolean m_isComplexUrType = false;
	private boolean m_isSimpleUrType = false;
	private boolean m_isAtomicUrType = false;
	private boolean m_isAbstract = false;
	private boolean m_isComplex = false;
	private boolean m_isSimple = false;
	private SmWhiteSpacePolicy m_whiteSpacePolicy = null;

	public XMLTypeRef<A> itemRef = null; // xs:list
	public final LinkedList<XMLTypeRef<A>> memberRefs = new LinkedList<XMLTypeRef<A>>(); // xs:union

	private final LinkedList<XMLAttributeUse<A>> attributeUses = new LinkedList<XMLAttributeUse<A>>();
	private final LinkedList<XMLAttributeGroup<A>> attributeGroups = new LinkedList<XMLAttributeGroup<A>>();
	public final HashSet<QName> prohibited = new HashSet<QName>();
	public XMLWildcard<A> attributeWildcard;
	public XMLWildcard<A> elementWildcard;

	// The following trio will be used to construct the {content type} property for complex types.
	public XMLContentTypeKind m_contentKind = XMLContentTypeKind.ElementOnly; // ElementOnly, Mixed or Simple after
	// parsing.
	public XMLType<A> simpleType = null; // May still be null after parsing.
	public XMLParticleWithModelGroupTerm<A> m_contentModel = null; // May still be null after parsing.

	private final LinkedList<XMLEnumeration<A>> m_enumerations = new LinkedList<XMLEnumeration<A>>();
	private final LinkedList<XMLMinMaxFacet<A>> m_minmaxFacets = new LinkedList<XMLMinMaxFacet<A>>();
	private final LinkedList<XMLLength<A>> m_lengthFacets = new LinkedList<XMLLength<A>>();
	private final LinkedList<XMLPatternFacet<A>> m_patternFacets = new LinkedList<XMLPatternFacet<A>>();
	private final LinkedList<XMLTotalDigitsFacet<A>> m_totalDigitsFacets = new LinkedList<XMLTotalDigitsFacet<A>>();
	private final LinkedList<XMLFractionDigitsFacet<A>> m_fractionDigitsFacets = new LinkedList<XMLFractionDigitsFacet<A>>();

	public XMLType(final QName name, final XMLScope<A> scope)
	{
		super(scope);
		if (scope.isGlobal())
		{
			this.name = PreCondition.assertArgumentNotNull(name, "name");
		}
		else
		{
			PreCondition.assertNull(name, "name");
			this.name = null;
		}
	}

	public XMLType(final QName name, final XMLScope<A> scope, final SrcFrozenLocation location)
	{
		super(scope, location);
		if (scope.isGlobal())
		{
			this.name = PreCondition.assertArgumentNotNull(name, "name");
		}
		else
		{
			PreCondition.assertNull(name, "name");
			this.name = null;
		}
	}

	public QName getName()
	{
		if (getScope().isGlobal())
		{
			return name;
		}
		else
		{
			throw new AssertionError();
		}
	}

	public boolean isComplexUrType()
	{
		return m_isComplexUrType;
	}

	public boolean isSimpleUrType()
	{
		return m_isSimpleUrType;
	}

	public boolean isAtomicUrType()
	{
		return m_isAtomicUrType;
	}

	public SmDerivationMethod getDerivationMethod()
	{
		return m_derivation;
	}

	public XMLTypeRef<A> getBaseRef()
	{
		return m_baseRef;
	}

	void setBase(final XMLTypeRef<A> baseRef, final SmDerivationMethod derivation)
	{
		m_baseRef = PreCondition.assertArgumentNotNull(baseRef, "baseRef");
		m_derivation = PreCondition.assertArgumentNotNull(derivation, "derivation");
	}

	public EnumSet<SmDerivationMethod> getBlock()
	{
		return m_block;
	}

	public EnumSet<SmDerivationMethod> getFinal()
	{
		return m_final;
	}

	public boolean isComplex()
	{
		return m_isComplex;
	}

	void setComplexFlag()
	{
		m_isComplex = true;
		m_isSimple = false;
	}

	public boolean isSimple()
	{
		return m_isSimple;
	}

	void setSimpleFlag()
	{
		m_isSimple = true;
		m_isComplex = false;
	}

	public boolean isAbstract()
	{
		return m_isAbstract;
	}

	public void setAbstractFlag(final boolean isAbstract)
	{
		m_isAbstract = isAbstract;
	}

	public SmWhiteSpacePolicy getWhiteSpacePolicy()
	{
		return m_whiteSpacePolicy;
	}

	public void setWhiteSpacePolicy(final SmWhiteSpacePolicy whiteSpacePolicy)
	{
		this.m_whiteSpacePolicy = whiteSpacePolicy;
	}

	public LinkedList<XMLEnumeration<A>> getEnumerations()
	{
		return m_enumerations;
	}

	public LinkedList<XMLMinMaxFacet<A>> getMinMaxFacets()
	{
		return m_minmaxFacets;
	}

	public LinkedList<XMLLength<A>> getLengthFacets()
	{
		return m_lengthFacets;
	}

	public LinkedList<XMLPatternFacet<A>> getPatternFacets()
	{
		return m_patternFacets;
	}

	public LinkedList<XMLTotalDigitsFacet<A>> getTotalDigitsFacets()
	{
		return m_totalDigitsFacets;
	}

	public LinkedList<XMLFractionDigitsFacet<A>> getFractionDigitsFacets()
	{
		return m_fractionDigitsFacets;
	}

	public void extendContentType(final boolean mixed, final XMLParticleWithModelGroupTerm<A> contentModel)
	{
		if (m_contentKind.isElementOnly() || m_contentKind.isMixed())
		{
			if (null != m_contentModel)
			{
				final XMLParticleWithModelGroupTerm<A> existing = m_contentModel;

				PreCondition.assertNotNull(existing, "existing");

				final XMLModelGroup<A> anonymous = new XMLModelGroup<A>(SmModelGroup.SmCompositor.Sequence, new XMLScope<A>(this));
				anonymous.getParticles().add(PreCondition.assertNotNull(existing));
				anonymous.getParticles().add(PreCondition.assertNotNull(contentModel));
				m_contentModel = new XMLParticleWithModelGroupTerm<A>(BigInteger.ONE, BigInteger.ONE, anonymous, existing.getLocation());
			}
			else
			{
				m_contentModel = contentModel;
			}
			m_contentKind = mixed ? XMLContentTypeKind.Mixed : (m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly);
		}
		else if (m_contentKind.isSimple())
		{
			throw new AssertionError();
		}
		else
		{
			throw new AssertionError();
		}
	}

	public LinkedList<XMLAttributeUse<A>> getAttributeUses()
	{
		return attributeUses;
	}

	public LinkedList<XMLAttributeGroup<A>> getAttributeGroups()
	{
		return attributeGroups;
	}

	public String toString()
	{
		return getName().toString();
	}
}
