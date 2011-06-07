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
package org.genxdm.processor.w3c.xs.impl;

import java.math.BigInteger;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.WhiteSpacePolicy;

final class XMLType extends XMLComponent
{
	private final QName name;
	private final EnumSet<DerivationMethod> m_block = EnumSet.noneOf(DerivationMethod.class);
	private final EnumSet<DerivationMethod> m_final = EnumSet.noneOf(DerivationMethod.class);
	private DerivationMethod m_derivation;
	private XMLTypeRef m_baseRef;
	private boolean m_isComplexUrType = false;
	private boolean m_isSimpleUrType = false;
	private boolean m_isAtomicUrType = false;
	private boolean m_isAbstract = false;
	private boolean m_isComplex = false;
	private boolean m_isSimple = false;
	private WhiteSpacePolicy m_whiteSpacePolicy = null;

	public XMLTypeRef itemRef = null; // xs:list
	public final LinkedList<XMLTypeRef> memberRefs = new LinkedList<XMLTypeRef>(); // xs:union

	private final LinkedList<XMLAttributeUse> attributeUses = new LinkedList<XMLAttributeUse>();
	private final LinkedList<XMLAttributeGroup> attributeGroups = new LinkedList<XMLAttributeGroup>();
	public final HashSet<QName> prohibited = new HashSet<QName>();
	public XMLWildcard attributeWildcard;
	public XMLWildcard elementWildcard;

	// The following trio will be used to construct the {content type} property for complex types.
	public XMLContentTypeKind m_contentKind = XMLContentTypeKind.ElementOnly; // ElementOnly, Mixed or Simple after
	// parsing.
	public XMLType simpleType = null; // May still be null after parsing.
	public XMLParticleWithModelGroupTerm m_contentModel = null; // May still be null after parsing.

	private final LinkedList<XMLEnumeration> m_enumerations = new LinkedList<XMLEnumeration>();
	private final LinkedList<XMLMinMaxFacet> m_minmaxFacets = new LinkedList<XMLMinMaxFacet>();
	private final LinkedList<XMLLength> m_lengthFacets = new LinkedList<XMLLength>();
	private final LinkedList<XMLPatternFacet> m_patternFacets = new LinkedList<XMLPatternFacet>();
	private final LinkedList<XMLTotalDigitsFacet> m_totalDigitsFacets = new LinkedList<XMLTotalDigitsFacet>();
	private final LinkedList<XMLFractionDigitsFacet> m_fractionDigitsFacets = new LinkedList<XMLFractionDigitsFacet>();

	public XMLType(final QName name, final XMLScope scope)
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

	public XMLType(final QName name, final XMLScope scope, final SrcFrozenLocation location)
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

	public DerivationMethod getDerivationMethod()
	{
		return m_derivation;
	}

	public XMLTypeRef getBaseRef()
	{
		return m_baseRef;
	}

	void setBase(final XMLTypeRef baseRef, final DerivationMethod derivation)
	{
		m_baseRef = PreCondition.assertArgumentNotNull(baseRef, "baseRef");
		m_derivation = PreCondition.assertArgumentNotNull(derivation, "derivation");
	}

	public EnumSet<DerivationMethod> getBlock()
	{
		return m_block;
	}

	public EnumSet<DerivationMethod> getFinal()
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

	public WhiteSpacePolicy getWhiteSpacePolicy()
	{
		return m_whiteSpacePolicy;
	}

	public void setWhiteSpacePolicy(final WhiteSpacePolicy whiteSpacePolicy)
	{
		this.m_whiteSpacePolicy = whiteSpacePolicy;
	}

	public LinkedList<XMLEnumeration> getEnumerations()
	{
		return m_enumerations;
	}

	public LinkedList<XMLMinMaxFacet> getMinMaxFacets()
	{
		return m_minmaxFacets;
	}

	public LinkedList<XMLLength> getLengthFacets()
	{
		return m_lengthFacets;
	}

	public LinkedList<XMLPatternFacet> getPatternFacets()
	{
		return m_patternFacets;
	}

	public LinkedList<XMLTotalDigitsFacet> getTotalDigitsFacets()
	{
		return m_totalDigitsFacets;
	}

	public LinkedList<XMLFractionDigitsFacet> getFractionDigitsFacets()
	{
		return m_fractionDigitsFacets;
	}

	public void extendContentType(final boolean mixed, final XMLParticleWithModelGroupTerm contentModel)
	{
		if (m_contentKind.isElementOnly() || m_contentKind.isMixed())
		{
			if (null != m_contentModel)
			{
				final XMLParticleWithModelGroupTerm existing = m_contentModel;

				PreCondition.assertNotNull(existing, "existing");

				final XMLModelGroup anonymous = new XMLModelGroup(ModelGroup.SmCompositor.Sequence, new XMLScope(this));
				anonymous.getParticles().add(PreCondition.assertNotNull(existing));
				anonymous.getParticles().add(PreCondition.assertNotNull(contentModel));
				m_contentModel = new XMLParticleWithModelGroupTerm(BigInteger.ONE, BigInteger.ONE, anonymous, existing.getLocation());
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

	public LinkedList<XMLAttributeUse> getAttributeUses()
	{
		return attributeUses;
	}

	public LinkedList<XMLAttributeGroup> getAttributeGroups()
	{
		return attributeGroups;
	}

	public String toString()
	{
		return getName().toString();
	}
}
