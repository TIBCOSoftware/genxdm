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
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmAttributeGroup;
import org.genxdm.xs.components.SmComponentProvider;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.components.SmEnumeration;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.components.SmNotation;
import org.genxdm.xs.components.SmParticle;
import org.genxdm.xs.components.SmWildcard;
import org.genxdm.xs.components.SmWildcardUse;
import org.genxdm.xs.constraints.SmAttributeUse;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.constraints.SmModelGroupUse;
import org.genxdm.xs.constraints.SmNamespaceConstraint;
import org.genxdm.xs.constraints.SmValueConstraint;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmProcessContentsMode;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.enums.SmWhiteSpacePolicy;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmDatatypeException;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.exceptions.SmExceptionHandler;
import org.genxdm.xs.exceptions.SmSimpleTypeException;
import org.genxdm.xs.facets.SmFacet;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.facets.SmLimit;
import org.genxdm.xs.facets.SmPattern;
import org.genxdm.xs.facets.SmRegExPattern;
import org.genxdm.xs.resolve.SmPrefixResolver;
import org.genxdm.xs.types.SmAtomicType;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmComplexUrType;
import org.genxdm.xs.types.SmContentType;
import org.genxdm.xs.types.SmListSimpleType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmSimpleUrType;
import org.genxdm.xs.types.SmType;
import org.genxdm.xs.types.SmUnionSimpleType;
import org.gxml.bridgekit.xs.AtomicTypeImpl;
import org.gxml.bridgekit.xs.AttributeDeclTypeImpl;
import org.gxml.bridgekit.xs.AttributeGroupImpl;
import org.gxml.bridgekit.xs.AttributeUseImpl;
import org.gxml.bridgekit.xs.ComplexTypeImpl;
import org.gxml.bridgekit.xs.ContentTypeImpl;
import org.gxml.bridgekit.xs.ElementDeclTypeImpl;
import org.gxml.bridgekit.xs.FacetFractionDigitsImpl;
import org.gxml.bridgekit.xs.FacetLengthImpl;
import org.gxml.bridgekit.xs.FacetMaxLengthImpl;
import org.gxml.bridgekit.xs.FacetMinLengthImpl;
import org.gxml.bridgekit.xs.FacetPatternImpl;
import org.gxml.bridgekit.xs.FacetTotalDigitsImpl;
import org.gxml.bridgekit.xs.FacetValueCompImpl;
import org.gxml.bridgekit.xs.IdentityConstraintImpl;
import org.gxml.bridgekit.xs.ListTypeImpl;
import org.gxml.bridgekit.xs.ModelGroupImpl;
import org.gxml.bridgekit.xs.NotationImpl;
import org.gxml.bridgekit.xs.ParticleWithElementTerm;
import org.gxml.bridgekit.xs.ParticleWithModelGroupTerm;
import org.gxml.bridgekit.xs.ParticleWithWildcardTerm;
import org.gxml.bridgekit.xs.SimpleTypeImpl;
import org.gxml.bridgekit.xs.UnionTypeImpl;
import org.gxml.bridgekit.xs.WildcardImpl;
import org.gxml.processor.w3c.xs.exception.SccAttributeDeclarationSimpleTypeException;
import org.gxml.processor.w3c.xs.exception.SccAttributeGroupMemberNamesException;
import org.gxml.processor.w3c.xs.exception.SccBaseTypeMustBeSimpleTypeException;
import org.gxml.processor.w3c.xs.exception.SccCyclicAttributeException;
import org.gxml.processor.w3c.xs.exception.SccCyclicAttributeGroupException;
import org.gxml.processor.w3c.xs.exception.SccCyclicElementException;
import org.gxml.processor.w3c.xs.exception.SccCyclicIdentityConstraintException;
import org.gxml.processor.w3c.xs.exception.SccCyclicModelGroupException;
import org.gxml.processor.w3c.xs.exception.SccItemTypeMustBeAtomicOrUnionException;
import org.gxml.processor.w3c.xs.exception.SccMemberTypeMustBeAtomicOrListException;
import org.gxml.processor.w3c.xs.exception.SicOversizedIntegerException;
import org.gxml.processor.w3c.xs.exception.SmAttributeUseException;
import org.gxml.processor.w3c.xs.exception.SmCyclicTypeException;
import org.gxml.processor.w3c.xs.exception.SmUndeclaredReferenceException;
import org.gxml.processor.w3c.xs.exception.SrcBaseContentTypeCannotBeSimpleException;
import org.gxml.processor.w3c.xs.exception.SrcBaseMustHaveSimpleOrMixedContentTypeComplexTypeException;
import org.gxml.processor.w3c.xs.exception.SrcBaseTypeMustBeComplexTypeException;
import org.gxml.processor.w3c.xs.exception.SrcSimpleTypeAmongChildrenOfRestrictionException;


/**
 * The main purpose of this conversion class is to build the schema from the cache. <br/>
 * Note: Using a dedicated class allows us to make the plumbing instance members so that we don't have to pass
 * distracting arguments to methods. The use of a static entry point and a private initializer protects against multiple
 * invocations.
 */
final class XMLSchemaConverter<A>
{
	/**
	 * Integer.MAX_VALUE as a BigInteger; needed to ensure that we throw an exception rather than attempt to convert
	 * BigInteger values larger than Integer.MAX_VALUE.
	 */
	private static final BigInteger MAX_INT_SIZE = BigInteger.valueOf(Integer.MAX_VALUE);

	public static <A> Pair<SmComponentBagImpl<A>, XMLComponentLocator<A>> convert(final SmRegExCompiler regexc, final SmComponentProvider<A> rtmCache, final AtomBridge<A> atomBridge, final XMLSchemaCache<A> xmlCache, final SmExceptionHandler errors) throws SmAbortException
	{
		final SmComponentBagImpl<A> schema = new SmComponentBagImpl<A>();
		final XMLComponentLocator<A> locations = new XMLComponentLocator<A>();

		final XMLSchemaConverter<A> converter = new XMLSchemaConverter<A>(regexc, rtmCache, atomBridge, xmlCache, schema, locations, errors);

		xmlCache.computeSubstitutionGroups();

		converter.convertTypes();
		converter.convertAttributes();
		converter.convertElements();
		converter.convertAttributeGroups();
		converter.convertIdentityConstraints();
		converter.convertModelGroups();
		converter.convertNotations();

		return new Pair<SmComponentBagImpl<A>, XMLComponentLocator<A>>(schema, locations);
	}

	static boolean isMaxOccursUnbounded(final BigInteger maxOccurs) throws SicOversizedIntegerException
	{
		PreCondition.assertArgumentNotNull(maxOccurs, "maxOccurs");

		if (XMLParticle.UNBOUNDED.equals(maxOccurs))
		{
			return true;
		}
		else
		{
			if (MAX_INT_SIZE.compareTo(maxOccurs) < 0)
			{
				throw new SicOversizedIntegerException(maxOccurs);
			}
			else
			{
				return false;
			}
		}
	}

	static int maxOccurs(final BigInteger maxOccurs) throws SicOversizedIntegerException
	{
		PreCondition.assertArgumentNotNull(maxOccurs, "maxOccurs");

		if (XMLParticle.UNBOUNDED.equals(maxOccurs))
		{
			throw new IllegalStateException("maxOccurs is unbounded");
		}
		else
		{
			if (MAX_INT_SIZE.compareTo(maxOccurs) < 0)
			{
				throw new SicOversizedIntegerException(maxOccurs);
			}
			else
			{
				return maxOccurs.intValue();
			}
		}
	}

	static int minOccurs(final BigInteger minOccurs) throws SicOversizedIntegerException
	{
		PreCondition.assertArgumentNotNull(minOccurs, "minOccurs");
		if (MAX_INT_SIZE.compareTo(minOccurs) < 0)
		{
			throw new SicOversizedIntegerException(minOccurs);
		}
		else
		{
			PreCondition.assertTrue(minOccurs.compareTo(BigInteger.ZERO) >= 0, "minOccurs >= 0");
			return minOccurs.intValue();
		}
	}

	private static <A> boolean subtype(final SmType<A> lhs, final SmType<A> rhs)
	{
		PreCondition.assertArgumentNotNull(lhs, "lhs");
		PreCondition.assertArgumentNotNull(rhs, "rhs");
		if (!rhs.isComplexUrType())
		{
			SmType<A> currentType = lhs;
			while (true)
			{
				if (currentType == rhs)
				{
					return true;
				}
				else
				{
					if (!currentType.isComplexUrType())
					{
						currentType = currentType.getBaseType();
					}
					else
					{
						return false;
					}
				}
			}
		}
		else
		{
			// All item types are derived from the Complex Ur-type.
			return true;
		}
	}

	private final AtomBridge<A> atomBridge;
	private final SmContentType<A> EMPTY_CONTENT = new ContentTypeImpl<A>();
	private final XMLCycles<A> m_cycles;

	private final SmExceptionHandler m_errors;

	private final SmComponentProvider<A> m_existingCache;

	private final XMLSchemaCache<A> m_inCache;

	private final XMLComponentLocator<A> m_locations;
	private final NameSource nameBridge;

	private final SmComponentBagImpl<A> m_outBag;

	private final SmRegExCompiler regexc;

	private XMLSchemaConverter(final SmRegExCompiler regexc, final SmComponentProvider<A> outCache, final AtomBridge<A> atomBridge, final XMLSchemaCache<A> inCache, final SmComponentBagImpl<A> schema, final XMLComponentLocator<A> locations, final SmExceptionHandler errors)
	{
		this.regexc = regexc;
		this.m_existingCache = outCache;
		this.atomBridge = atomBridge;
		this.nameBridge = atomBridge.getNameBridge();
		this.m_inCache = inCache;
		this.m_outBag = schema;
		this.m_locations = locations;
		this.m_errors = errors;
		this.m_cycles = new XMLCycles<A>();
	}

	private SmWildcard<A> attributeWildcard(final SmType<A> baseType)
	{
		if (baseType instanceof SmComplexType<?>)
		{
			final SmComplexType<A> complexBase = (SmComplexType<A>)baseType;
			final SmWildcard<A> attributeWildcard = complexBase.getAttributeWildcard();
			if (null != attributeWildcard)
			{
				return attributeWildcard;
			}
			else
			{
				return null;
			}
		}
		else if (baseType instanceof SmSimpleType<?>)
		{
			return null;
		}
		else
		{
			throw new AssertionError(baseType);
		}
	}

	private SmWildcard<A> attributeWildcard(final XMLType<A> complexType) throws SmAbortException, SmException
	{
		final XMLWildcard<A> localWildcard = complexType.attributeWildcard;

		final SmDerivationMethod derivation = complexType.getDerivationMethod();
		switch (derivation)
		{
			case Restriction:
			{
				return completeWildcard(complexType.getAttributeGroups(), localWildcard);
			}
			case Extension:
			{
				final SmWildcard<A> baseWildcard = attributeWildcard(complexType.getBaseRef());
				if (null != baseWildcard)
				{
					final SmWildcard<A> completeWildcard = completeWildcard(complexType.getAttributeGroups(), localWildcard);
					if (null == completeWildcard)
					{
						return baseWildcard;
					}
					else
					{
						// {process contents} and {annotation} from complete
						// wildcard.
						// {namespace constraint} is union of the complete and
						// base wildcard.
						return new WildcardImpl<A>(completeWildcard.getProcessContents(), completeWildcard.getNamespaceConstraint().union(baseWildcard.getNamespaceConstraint()));
					}
				}
				else
				{
					return completeWildcard(complexType.getAttributeGroups(), localWildcard);
				}
			}
			default:
			{
				// Complex type must be derived by restriction or extension.
				throw new AssertionError(derivation);
			}
		}
	}

	private SmWildcard<A> attributeWildcard(final XMLTypeRef<A> typeRef) throws SmAbortException, SmException
	{
		final SmType<A> type = convertType(typeRef);
		return attributeWildcard(type);
	}

	private SmWildcard<A> completeWildcard(final Iterable<XMLAttributeGroup<A>> attributeGroups, final XMLWildcard<A> localWildcard) throws SmAbortException, SmException
	{
		SmNamespaceConstraint constraint = null;

		// Remember the first {process contents} within the
		// <attributeGroup>[children].
		SmProcessContentsMode processContents = null;
		if (null != attributeGroups)
		{
			for (final XMLAttributeGroup<A> xmlAttributeGroup : attributeGroups)
			{
				final SmAttributeGroup<A> attributeGroup = convertAttributeGroup(xmlAttributeGroup);
				final SmWildcard<A> groupWildcard = attributeGroup.getWildcard();
				if (null != groupWildcard)
				{
					if (null == constraint)
					{
						constraint = groupWildcard.getNamespaceConstraint();
						processContents = groupWildcard.getProcessContents();
					}
					else
					{
						constraint = constraint.intersection(groupWildcard.getNamespaceConstraint());
					}
				}
			}
		}

		if (null == constraint)
		{
			// If nothing is found in the <attributeGroup>[children]...
			if (null != localWildcard)
			{
				return new WildcardImpl<A>(localWildcard.getProcessContents(), convert(localWildcard.getNamespaceConstraint()));
			}
			else
			{
				return null;
			}
		}
		else
		{
			if (null != localWildcard)
			{
				// {process contents} and {annotation} are those of the local
				// wildcard.
				// {namespace constraint} defined by Attribute Wildcard
				// Intersection.
				return new WildcardImpl<A>(localWildcard.getProcessContents(), convert(localWildcard.getNamespaceConstraint()).intersection(constraint));
			}
			else
			{
				// {process contents} from first <attributeGroup>[children]
				// {namespace constraint} from the <attributeGroup>[children]
				// {annotation} is absent.
				return new WildcardImpl<A>(processContents, constraint);
			}
		}
	}

	/**
	 * Expand temporary variables used to hold syntactic constructs for attribute uses and wildcards.
	 */
	private Map<QName, SmAttributeUse<A>> computeAttributeUses(final XMLType<A> complexType) throws SmAbortException, SmException
	{
		final HashMap<QName, SmAttributeUse<A>> attributeUses = new HashMap<QName, SmAttributeUse<A>>();

		for (final XMLAttributeUse<A> attributeUse : complexType.getAttributeUses())
		{
			final QName attributeName = attributeUse.getDeclaration().getName();
			try
			{
				if (!attributeUses.containsKey(attributeName))
				{
					attributeUses.put(attributeName, convertAttributeUse(attributeUse));
				}
				else
				{
					m_errors.error(new SccAttributeGroupMemberNamesException());
				}
			}
			catch (final SmException e)
			{
				m_errors.error(e);
			}
		}

		for (final XMLAttributeGroup<A> xmlAttributeGroup : complexType.getAttributeGroups())
		{
			final SmAttributeGroup<A> attributeGroup = convertAttributeGroup(xmlAttributeGroup);
			if (attributeGroup.hasAttributeUses())
			{
				for (final SmAttributeUse<A> attributeUse : attributeGroup.getAttributeUses())
				{
					final SmAttribute<A> attribute = attributeUse.getAttribute();
					final QName attributeName = attribute.getName();
					if (!attributeUses.containsKey(attributeName))
					{
						attributeUses.put(attributeName, attributeUse);
					}
					else
					{
						m_errors.error(new SccAttributeGroupMemberNamesException());
					}
				}
			}
		}

		switch (complexType.getDerivationMethod())
		{
			case Restriction:
			{
				final SmType<A> typeB = convertType(complexType.getBaseRef());
				if (typeB instanceof SmComplexType<?>)
				{
					final SmComplexType<A> complexTypeB = (SmComplexType<A>)typeB;
					for (final SmAttributeUse<A> attributeUse : complexTypeB.getAttributeUses().values())
					{
						final QName attributeName = attributeUse.getAttribute().getName();
						if (!complexType.prohibited.contains(attributeName))
						{
							if (attributeUses.containsKey(attributeName))
							{
								// Obviously can't add it because that would
								// cause a non-unique name.
								// This collision will be analyzed during
								// schema constraint checking.
							}
							else
							{
								attributeUses.put(attributeName, attributeUse);
							}
						}
					}
				}
			}
			break;
			case Extension:
			{
				final SmType<A> typeB = convertType(complexType.getBaseRef());
				if (typeB instanceof SmComplexType<?>)
				{
					final SmComplexType<A> complexTypeB = (SmComplexType<A>)typeB;
					for (final SmAttributeUse<A> attributeUse : complexTypeB.getAttributeUses().values())
					{
						final QName attributeName = attributeUse.getAttribute().getName();
						attributeUses.put(attributeName, attributeUse);
					}
				}
			}
			break;
			default:
			{
				throw new RuntimeException(complexType.getDerivationMethod().name());
			}
		}

		return attributeUses;
	}

	/**
	 * Compile the enumeration facets for this type. <br/>
	 * Enumeration facets are not inherited during compilation, but must be subsets of base types.
	 */
	private void computeEnumerations(final SmSimpleType<A> baseType, final XMLType<A> type, final SimpleTypeImpl<A> target) throws SmAbortException
	{
		if (type.getEnumerations().size() > 0)
		{
			for (final XMLEnumeration<A> pattern : type.getEnumerations())
			{
				try
				{
					target.addEnumeration(enumeration(target, baseType, pattern));
				}
				catch (final SmAttributeUseException e)
				{
					m_errors.error(e);
				}
			}
		}
	}

	private void computeFacets(final SmSimpleType<A> baseType, final XMLType<A> type, final SimpleTypeImpl<A> target) throws SmAbortException, SmException
	{
		for (final XMLTotalDigitsFacet<A> xmlFacet : type.getTotalDigitsFacets())
		{
			target.addFacet(totalDigits(xmlFacet));
		}
		for (final XMLFractionDigitsFacet<A> xmlFacet : type.getFractionDigitsFacets())
		{
			target.addFacet(fractionDigits(xmlFacet));
		}
		// Note that the length, minLength and maxLength facets are deprecated
		// for types derived from QName or NOTATION.
		if (!subtype(target, m_existingCache.getAtomicType(SmNativeType.QNAME)) && !subtype(target, m_existingCache.getAtomicType(SmNativeType.NOTATION)))
		{
			for (final XMLLength<A> xmlFacet : type.getLengthFacets())
			{
				target.addFacet(length(xmlFacet));
			}
		}
		for (final XMLMinMaxFacet<A> xmlFacet : type.getMinMaxFacets())
		{
			if (baseType.isAtomicType())
			{
				try
				{
					target.addFacet(minmax(xmlFacet, (SmSimpleType<A>)baseType));
				}
				catch (final SmException e)
				{
					m_errors.error(e);
				}
			}
		}
	}

	private SmContentType<A> computeLocallyEmptyContent(final XMLType<A> complexType) throws SmException, SmAbortException
	{
		final SmDerivationMethod derivation = complexType.getDerivationMethod();
		switch (derivation)
		{
			case Restriction:
			{
				return EMPTY_CONTENT;
			}
			case Extension:
			{
				final SmType<A> baseType = convertType(complexType.getBaseRef());
				if (baseType instanceof SmComplexType<?>)
				{
					final SmComplexType<A> complexBase = (SmComplexType<A>)baseType;
					return complexBase.getContentType();
				}
				else if (baseType instanceof SmSimpleType<?>)
				{
					final SmSimpleType<A> simpleBase = (SmSimpleType<A>)baseType;
					return new ContentTypeImpl<A>(simpleBase);
				}
				else
				{
					throw new AssertionError(derivation);
				}
			}
			default:
			{
				throw new AssertionError(derivation);
			}
		}
	}

	/**
	 * Compile the pattern facets for this type. <br/>
	 * Pattern facets are not inherited during compilation.
	 */
	private void computePatterns(final LinkedList<XMLPatternFacet<A>> xmlFacets, final SimpleTypeImpl<A> target) throws SmAbortException
	{
		if (xmlFacets.size() > 0)
		{
			for (final XMLPatternFacet<A> pattern : xmlFacets)
			{
				try
				{
					target.addPattern(pattern(pattern));
				}
				catch (final SmAttributeUseException e)
				{
					m_errors.error(e);
				}
			}
		}
	}

	private Set<String> convert(final Iterable<String> strings)
	{
		final Set<String> result = new HashSet<String>();
		for (final String member : strings)
		{
			result.add(member);
		}
		return result;
	}

	private SmNamespaceConstraint convert(final SmNamespaceConstraint input)
	{
		switch (input.getMode())
		{
			case Any:
			{
				return SmNamespaceConstraint.Any(nameBridge);
			}
			case Include:
			{
				return SmNamespaceConstraint.include(convert(input.getNamespaces()), nameBridge);
			}
			case Exclude:
			{
				// This approach is a bit long-winded but it generalizes better
				// to multiple excusions.
				final Iterator<String> namespaces = convert(input.getNamespaces()).iterator();
				if (namespaces.hasNext())
				{
					return SmNamespaceConstraint.exclude(namespaces.next(), nameBridge);
				}
				else
				{
					throw new AssertionError();
				}
			}
			default:
			{
				throw new AssertionError(input.getMode());
			}
		}
	}

	private SmAttribute<A> convertAttribute(final XMLAttribute<A> xmlAttribute) throws SmAbortException, SmException
	{
		final QName name = xmlAttribute.getName();
		final SmScopeExtent scope = convertScope(xmlAttribute.getScope());
		if (scope == SmScopeExtent.Global)
		{
			if (m_outBag.hasAttribute(name))
			{
				return m_outBag.getAttribute(name);
			}
			if (m_existingCache.hasAttribute(name))
			{
				return m_existingCache.getAttributeDeclaration(name);
			}
			if (m_cycles.attributes.contains(xmlAttribute))
			{
				throw new SccCyclicAttributeException(name);
			}
			else
			{
				m_cycles.attributes.push(xmlAttribute);
			}
		}
		final AttributeDeclTypeImpl<A> attribute;
		try
		{
			attribute = new AttributeDeclTypeImpl<A>(name, scope, m_existingCache.getSimpleUrType());
			if (scope == SmScopeExtent.Global)
			{
				m_outBag.add(attribute);
			}
			m_locations.m_attributeLocations.put(attribute, xmlAttribute.getLocation());
		}
		finally
		{
			if (scope == SmScopeExtent.Global)
			{
				m_cycles.attributes.pop();
			}
		}
		final SmType<A> attributeType = convertType(xmlAttribute.typeRef);
		if (attributeType instanceof SmSimpleType<?>)
		{
			attribute.setType((SmSimpleType<A>)attributeType);
		}
		else
		{
			m_errors.error(new SccAttributeDeclarationSimpleTypeException(name));
		}
		if (null != xmlAttribute.m_valueConstraint)
		{
			try
			{
				attribute.setValueConstraint(convertValueConstraint(XMLRepresentation.LN_ATTRIBUTE, xmlAttribute.m_valueConstraint, (SmSimpleType<A>)attribute.getType()));
			}
			catch (final SmException e)
			{
				m_errors.error(e);
			}
		}
		return attribute;
	}

	private SmAttributeGroup<A> convertAttributeGroup(final XMLAttributeGroup<A> xmlAttributeGroup) throws SmAbortException, SmException
	{
		final QName agName = PreCondition.assertArgumentNotNull(xmlAttributeGroup.getName(), "name");
		final SmScopeExtent scope = convertScope(xmlAttributeGroup.getScope());
		if (scope == SmScopeExtent.Global)
		{
			if (m_outBag.hasAttributeGroup(agName))
			{
				return m_outBag.getAttributeGroup(agName);
			}
			if (m_existingCache.hasAttributeGroup(agName))
			{
				return m_existingCache.getAttributeGroup(agName);
			}
			if (m_cycles.attributeGroups.contains(xmlAttributeGroup))
			{
				throw new SccCyclicAttributeGroupException(xmlAttributeGroup.getName());
			}
			else
			{
				m_cycles.attributeGroups.push(xmlAttributeGroup);
			}
		}
		try
		{
			final HashMap<QName, SmAttributeUse<A>> attributeUses = new HashMap<QName, SmAttributeUse<A>>();
			for (final XMLAttributeGroup<A> group : xmlAttributeGroup.getGroups())
			{
				final SmAttributeGroup<A> attributeGroup = convertAttributeGroup(group);
				if (attributeGroup.hasAttributeUses())
				{
					for (final SmAttributeUse<A> attributeUse : attributeGroup.getAttributeUses())
					{
						attributeUses.put(attributeUse.getAttribute().getName(), attributeUse);
					}
				}
			}
			for (final XMLAttributeUse<A> attributeUse : xmlAttributeGroup.getAttributeUses())
			{
				final QName name = attributeUse.getDeclaration().getName();
				try
				{
					attributeUses.put(name, convertAttributeUse(attributeUse));
				}
				catch (final SmException e)
				{
					m_errors.error(e);
				}
			}
			final SmWildcard<A> completeWildcard = completeWildcard(xmlAttributeGroup.getGroups(), xmlAttributeGroup.wildcard);
			final SmAttributeGroup<A> attributeGroup;
			if (null != attributeUses)
			{
				attributeGroup = new AttributeGroupImpl<A>(agName, scope, attributeUses.values(), completeWildcard);
			}
			else
			{
				attributeGroup = new AttributeGroupImpl<A>(agName, scope, null, completeWildcard);
			}

			if (attributeGroup.getScopeExtent() == SmScopeExtent.Global)
			{
				m_outBag.add(attributeGroup);
			}
			m_locations.m_attributeGroupLocations.put(attributeGroup, xmlAttributeGroup.getLocation());
			return attributeGroup;
		}
		finally
		{
			if (scope == SmScopeExtent.Global)
			{
				m_cycles.attributeGroups.pop();
			}
		}
	}

	private void convertAttributeGroups() throws SmAbortException
	{
		for (final XMLAttributeGroup<A> source : m_inCache.m_attributeGroups.values())
		{
			try
			{
				convertAttributeGroup(source);
			}
			catch (final SmException e)
			{
				m_errors.error(e);
			}
		}
	}

	private void convertAttributes() throws SmAbortException
	{
		for (final XMLAttribute<A> source : m_inCache.m_attributes.values())
		{
			try
			{
				convertAttribute(source);
			}
			catch (final SmException e)
			{
				m_errors.error(e);
			}
		}
	}

	private SmAttributeUse<A> convertAttributeUse(final XMLAttributeUse<A> xmlAttributeUse) throws SmAbortException, SmException
	{
		final SmAttribute<A> attribute = convertAttribute(xmlAttributeUse.getDeclaration());
		final AttributeUseImpl<A> attributeUse = new AttributeUseImpl<A>(xmlAttributeUse.isRequired(), attribute);
		if (null != xmlAttributeUse.getValueConstraint())
		{
			final SmType<A> attributeType = attribute.getType();
			if (attributeType instanceof SmSimpleType<?>)
			{
				final SmSimpleType<A> simpleType = (SmSimpleType<A>)attributeType;
				try
				{
					attributeUse.setValueConstraint(convertValueConstraint(XMLRepresentation.LN_ATTRIBUTE, xmlAttributeUse.getValueConstraint(), simpleType));
				}
				catch (final SmException e)
				{
					m_errors.error(e);
				}
			}
			else if (attributeType instanceof SmSimpleUrType<?>)
			{
				// TODO: Do we set the value constraint with xs:untypedAtomic?
			}
			else
			{
				throw new AssertionError(attributeType);
			}
		}
		return attributeUse;
	}

	private SmComplexType<A> convertComplexType(final QName outName, final boolean isAnonymous, final XMLType<A> xmlComplexType) throws SmAbortException, SmException
	{
		final SmScopeExtent scope = convertScope(xmlComplexType.getScope());
		if (scope == SmScopeExtent.Global)
		{
			if (m_outBag.hasComplexType(outName))
			{
				return m_outBag.getComplexType(outName);
			}
			if (m_existingCache.hasComplexType(outName))
			{
				return m_existingCache.getComplexType(outName);
			}
			if (m_cycles.types.contains(xmlComplexType))
			{
				throw new SmCyclicTypeException(outName);
			}

			m_cycles.types.push(xmlComplexType);
		}
		try
		{

			final Map<QName, SmAttributeUse<A>> attributeUses = computeAttributeUses(xmlComplexType);
			final SmType<A> baseType = convertType(xmlComplexType.getBaseRef());

			// Constructing and registering the complex type allows it to be
			// referenced in the {content type} property.
			final ComplexTypeImpl<A> complexType;
			if (null != attributeUses)
			{
				complexType = new ComplexTypeImpl<A>(outName, false, isAnonymous, scope, baseType, xmlComplexType.getDerivationMethod(), attributeUses, EMPTY_CONTENT, xmlComplexType.getBlock(), nameBridge, m_existingCache);
			}
			else
			{
				complexType = new ComplexTypeImpl<A>(outName, false, isAnonymous, scope, baseType, xmlComplexType.getDerivationMethod(), null, EMPTY_CONTENT, xmlComplexType.getBlock(), nameBridge, m_existingCache);
			}
			m_outBag.add(complexType);
			m_locations.m_complexTypeLocations.put(complexType, xmlComplexType.getLocation());

			complexType.setContentType(convertContentType(xmlComplexType));

			complexType.setAbstract(xmlComplexType.isAbstract());
			complexType.setAttributeWildcard(attributeWildcard(xmlComplexType));

			for (final SmDerivationMethod derivation : xmlComplexType.getBlock())
			{
				complexType.setBlock(derivation, true);
			}

			for (final SmDerivationMethod derivation : xmlComplexType.getFinal())
			{
				if (derivation.isExtension() || derivation.isRestriction())
				{
					complexType.setFinal(derivation, true);
				}
				else
				{
					// TODO:
					throw new AssertionError(derivation);
				}
			}

			return complexType;
		}
		finally
		{
			if (scope == SmScopeExtent.Global)
			{
				m_cycles.types.pop();
			}
		}
	}

	private SmContentType<A> convertContentType(final XMLType<A> xmlComplexType) throws SmAbortException, SmException
	{
		final SmDerivationMethod derivation = xmlComplexType.getDerivationMethod();

		if (xmlComplexType.m_contentKind.isComplex())
		{
			final boolean mixed = xmlComplexType.m_contentKind.isMixed();
			final SmModelGroupUse<A> effectiveContent = effectiveContent(mixed, xmlComplexType.m_contentModel);
			if (derivation.isRestriction())
			{
				if (null == effectiveContent)
				{
					return EMPTY_CONTENT;
				}
				else
				{
					if (mixed)
					{
						return new ContentTypeImpl<A>(mixed, effectiveContent);
					}
					else
					{
						if (effectiveContent.getTerm() == null || effectiveContent.getTerm().getParticles().isEmpty())
						{
							return EMPTY_CONTENT;
						}
						else
						{
							return new ContentTypeImpl<A>(mixed, effectiveContent);
						}
					}
				}
			}
			else if (derivation.isExtension())
			{
				final SmType<A> typeB = convertType(xmlComplexType.getBaseRef());
				if (typeB instanceof SmComplexType<?>)
				{
					final SmComplexType<A> complexTypeB = (SmComplexType<A>)typeB;
					final SmContentType<A> contentTypeB = complexTypeB.getContentType();
					if (null == effectiveContent)
					{
						return contentTypeB;
					}
					else if (contentTypeB.isEmpty())
					{
						return new ContentTypeImpl<A>(mixed, effectiveContent);
					}
					else if (contentTypeB.isSimple())
					{
						throw new SrcBaseContentTypeCannotBeSimpleException(xmlComplexType.getName(), complexTypeB.getName(), xmlComplexType.getLocation());
					}
					else if (contentTypeB.isComplex())
					{
						final LinkedList<SmModelGroupUse<A>> particles = new LinkedList<SmModelGroupUse<A>>();
						particles.add(contentTypeB.getContentModel());
						particles.add(effectiveContent);
						final SmModelGroup<A> modelGroup = new ModelGroupImpl<A>(SmModelGroup.SmCompositor.Sequence, particles, null, true, SmScopeExtent.Local);
						final SmModelGroupUse<A> particle = new ParticleWithModelGroupTerm<A>(1, 1, modelGroup);
						return new ContentTypeImpl<A>(mixed, particle);
					}
					else
					{
						throw new AssertionError(contentTypeB.getKind());
					}
				}
				else
				{
					throw new SrcBaseTypeMustBeComplexTypeException(xmlComplexType.getLocation());
				}
			}
			else
			{
				throw new AssertionError(derivation);
			}
		}
		else if (xmlComplexType.m_contentKind.isSimple())
		{
			final SmType<A> typeB = convertType(xmlComplexType.getBaseRef());
			if (typeB instanceof SmComplexType<?>)
			{
				final SmComplexType<A> complexTypeB = (SmComplexType<A>)typeB;
				final SmContentType<A> contentTypeB = complexTypeB.getContentType();
				if (contentTypeB.isSimple())
				{
					if (derivation.isRestriction())
					{
						return simpleContent(xmlComplexType.simpleType, contentTypeB.getSimpleType());
					}
					else if (derivation.isExtension())
					{
						return contentTypeB;
					}
					else
					{
						throw new AssertionError(derivation);
					}
				}
				else
				{
					if (derivation.isRestriction())
					{
						if (contentTypeB.isMixed())
						{
							final SmModelGroupUse<A> contentModelB = contentTypeB.getContentModel();
							if (contentModelB.isEmptiable())
							{
								final XMLTypeRef<A> simpleType = xmlComplexType.simpleType.getBaseRef();
								if (null != simpleType)
								{
									final SmSimpleType<A> simpleBaseType = extractSimpleType(simpleType);
									return simpleContent(xmlComplexType.simpleType, simpleBaseType);
								}
								else
								{
									throw new SrcSimpleTypeAmongChildrenOfRestrictionException(xmlComplexType.getLocation());
								}
							}
							else
							{
								throw new SrcBaseMustHaveSimpleOrMixedContentTypeComplexTypeException(xmlComplexType.getLocation());
							}
						}
						else
						{
							throw new SrcBaseMustHaveSimpleOrMixedContentTypeComplexTypeException(xmlComplexType.getLocation());
						}
					}
					else if (derivation.isExtension())
					{
						throw new SrcBaseMustHaveSimpleOrMixedContentTypeComplexTypeException(xmlComplexType.getLocation());
					}
					else
					{
						throw new AssertionError(derivation);
					}
				}
			}
			else if (typeB instanceof SmSimpleType<?>)
			{
				final SmSimpleType<A> simpleTypeB = (SmSimpleType<A>)typeB;
				if (derivation.isExtension())
				{
					return new ContentTypeImpl<A>(simpleTypeB);
				}
				else if (derivation.isRestriction())
				{
					return new ContentTypeImpl<A>(simpleTypeB);
				}
				else
				{
					throw new AssertionError(derivation);
				}
			}
			else
			{
				throw new AssertionError(typeB);
			}
		}
		else
		{
			return computeLocallyEmptyContent(xmlComplexType);
		}
	}

	private SmElement<A> convertElement(final XMLElement<A> xmlElement) throws SmException, SmAbortException
	{
		final QName name = PreCondition.assertArgumentNotNull(xmlElement.getName(), "name");
		final SmScopeExtent scope = convertScope(xmlElement.getScope());
		if (scope == SmScopeExtent.Global)
		{
			if (m_outBag.hasElement(name))
			{
				return m_outBag.getElement(name);
			}
			if (m_existingCache.hasElement(name))
			{
				return m_existingCache.getElementDeclaration(name);
			}
			if (m_cycles.elements.contains(xmlElement))
			{
				throw new SccCyclicElementException(name);
			}
			else
			{
				m_cycles.elements.push(xmlElement);
			}
		}
		final ElementDeclTypeImpl<A> element;
		try
		{
			PreCondition.assertArgumentNotNull(xmlElement.typeRef, "{type definition} of " + name);

			// The element {type definition} defaults to xs:anyType because
			// there may be circularities.
			// {name}, {target namespace} and {scope} are set here. We set the
			// {type definition} and other
			// properties outside of the scope for checking cycles.
			final SmComplexUrType<A> anyType = m_existingCache.getComplexUrType();
			element = new ElementDeclTypeImpl<A>(name, scope, anyType);

			// {substitution group affiliation}
			if (null != xmlElement.substitutionGroup)
			{
				// TODO: Would be nice to avoid this downcast. Maybe by using name for group head?
				final ElementDeclTypeImpl<A> substitutionGroupHead = (ElementDeclTypeImpl<A>)convertElement(xmlElement.substitutionGroup);
				element.setSubstitutionGroup(substitutionGroupHead);
				substitutionGroupHead.addSubstitutionGroupMember(element);
			}

			// {identity-constraint definitions}
			for (final XMLIdentityConstraint<A> constraint : xmlElement.getIdentityConstraints())
			{
				element.addIdentityConstraint(convertIdentityConstraint(constraint));
			}
		}
		finally
		{
			if (scope == SmScopeExtent.Global)
			{
				m_cycles.elements.pop();
			}
		}

		if (element.getScopeExtent() == SmScopeExtent.Global)
		{
			m_outBag.add(element);
		}
		m_locations.m_elementLocations.put(element, xmlElement.getLocation());

		// {type definition}
		element.setType(convertType(xmlElement.typeRef));

		// {nillable}
		element.setNillable(xmlElement.isNillable());

		// {value constraint}
		if (null != xmlElement.m_valueConstraint)
		{
			if (element.getType() instanceof SmSimpleType<?>)
			{
				final SmSimpleType<A> elementType = (SmSimpleType<A>)element.getType();
				try
				{
					element.setValueConstraint(convertValueConstraint(XMLRepresentation.LN_ELEMENT, xmlElement.m_valueConstraint, elementType));
				}
				catch (final SmException e)
				{
					m_errors.error(e);
				}
			}
			else if (element.getType() instanceof SmComplexType<?>)
			{
				final SmComplexType<A> elementType = (SmComplexType<A>)element.getType();
				final SmContentType<A> contentType = elementType.getContentType();
				if (contentType.isSimple())
				{
					final SmSimpleType<A> simpleType = contentType.getSimpleType();
					try
					{
						element.setValueConstraint(convertValueConstraint(XMLRepresentation.LN_ELEMENT, xmlElement.m_valueConstraint, simpleType));
					}
					catch (final SmException e)
					{
						m_errors.error(e);
					}
				}
				else
				{
					final String initialValue = xmlElement.m_valueConstraint.getValue();
					final List<A> actualValue = atomBridge.wrapAtom(atomBridge.createUntypedAtomic(initialValue));
					element.setValueConstraint(new SmValueConstraint<A>(xmlElement.m_valueConstraint.kind, actualValue, initialValue));
				}
			}
			else
			{
				throw new AssertionError(element.getType());
			}
		}

		// {disallowed substitutions}
		for (final SmDerivationMethod derivation : xmlElement.getBlock())
		{
			element.setBlock(derivation, true);
		}

		// {substitution group exclusions}
		for (final SmDerivationMethod derivation : xmlElement.getFinal())
		{
			element.setFinal(derivation, true);
		}

		// {abstract}
		element.setAbstract(xmlElement.isAbstract());

		// {annotation} we don't care about.

		// We're done!
		return element;
	}

	private void convertElements() throws SmAbortException
	{
		for (final XMLElement<A> source : m_inCache.m_elements.values())
		{
			try
			{
				convertElement(source);
			}
			catch (final SmException e)
			{
				m_errors.error(e);
			}
		}
	}

	private SmParticle<A> convertElementUse(final XMLParticleWithElementTerm<A> particle) throws SmException, SmAbortException
	{
		final XMLElement<A> xmlElement = particle.getTerm();
		final SmElement<A> element = convertElement(xmlElement);

		final ParticleWithElementTerm<A> elementUse;
		if (isMaxOccursUnbounded(particle.getMaxOccurs()))
		{
			final int minOccurs = minOccurs(particle.getMinOccurs());
			elementUse = new ParticleWithElementTerm<A>(minOccurs, element);
		}
		else
		{
			final int minOccurs = minOccurs(particle.getMinOccurs());
			final int maxOccurs = maxOccurs(particle.getMaxOccurs());
			elementUse = new ParticleWithElementTerm<A>(minOccurs, maxOccurs, element);
		}
		m_locations.m_particleLocations.put(elementUse, particle.getLocation());
		if (null != particle.valueConstraint)
		{
			final SmValueConstraint<A> valueConstraint = convertElementValueConstraint(particle.valueConstraint, element.getType());
			elementUse.setValueConstraint(valueConstraint);
		}
		return elementUse;
	}

	private SmValueConstraint<A> convertElementValueConstraint(final XMLValueConstraint xmlValueConstraint, final SmType<A> type) throws SmException
	{
		if (null != xmlValueConstraint)
		{
			if (type instanceof SmSimpleType<?>)
			{
				final SmSimpleType<A> simpleType = (SmSimpleType<A>)type;
				return convertValueConstraint(XMLRepresentation.LN_ELEMENT, xmlValueConstraint, simpleType);
			}
			else if (type instanceof SmComplexType<?>)
			{
				final SmComplexType<A> elementType = (SmComplexType<A>)type;
				final SmContentType<A> contentType = elementType.getContentType();
				if (contentType.isSimple())
				{
					final SmSimpleType<A> simpleType = contentType.getSimpleType();
					return convertValueConstraint(XMLRepresentation.LN_ELEMENT, xmlValueConstraint, simpleType);
				}
				else
				{
					final String initialValue = xmlValueConstraint.getValue();
					final List<A> actualValue = atomBridge.wrapAtom(atomBridge.createUntypedAtomic(initialValue));
					return new SmValueConstraint<A>(xmlValueConstraint.kind, actualValue, initialValue);
				}
			}
			else
			{
				throw new AssertionError(type);
			}
		}
		else
		{
			return null;
		}
	}

	private SmIdentityConstraint<A> convertIdentityConstraint(final XMLIdentityConstraint<A> xmlConstraint) throws SmException
	{
		final QName name = xmlConstraint.getName();
		if (m_outBag.hasIdentityConstraint(name))
		{
			return m_outBag.getIdentityConstraint(name);
		}
		if (m_existingCache.hasIdentityConstraint(name))
		{
			return m_existingCache.getIdentityConstraint(name);
		}
		if (m_cycles.constraints.contains(xmlConstraint))
		{
			throw new SccCyclicIdentityConstraintException(name);
		}
		m_cycles.constraints.push(xmlConstraint);
		try
		{
			if (null == xmlConstraint.keyConstraint)
			{
				final SmIdentityConstraint<A> constraint = new IdentityConstraintImpl<A>(name, xmlConstraint.category, xmlConstraint.selector, xmlConstraint.fields, null);
				m_outBag.add(constraint);
				m_locations.m_constraintLocations.put(constraint, xmlConstraint.getLocation());
				return constraint;
			}
			else
			{
				final SmIdentityConstraint<A> keyConstraint = convertIdentityConstraint(xmlConstraint.keyConstraint);
				final SmIdentityConstraint<A> constraint = new IdentityConstraintImpl<A>(name, xmlConstraint.category, xmlConstraint.selector, xmlConstraint.fields, keyConstraint);
				m_outBag.add(constraint);
				m_locations.m_constraintLocations.put(constraint, xmlConstraint.getLocation());
				return constraint;
			}
		}
		finally
		{
			m_cycles.constraints.pop();
		}
	}

	private void convertIdentityConstraints() throws SmAbortException
	{
		for (final XMLIdentityConstraint<A> source : m_inCache.m_constraints.values())
		{
			try
			{
				convertIdentityConstraint(source);
			}
			catch (final SmException e)
			{
				m_errors.error(e);
			}
		}
	}

	private SmSimpleType<A> convertItemType(final QName simpleType, final XMLTypeRef<A> typeRef) throws SmAbortException, SmException
	{
		final SmType<A> itemType = convertType(typeRef);
		if (itemType.isAtomicType())
		{
			return (SmSimpleType<A>)itemType;
		}
		else if (itemType instanceof SmUnionSimpleType<?>)
		{
			return (SmUnionSimpleType<A>)itemType;
		}
		else
		{
			throw new SccItemTypeMustBeAtomicOrUnionException(simpleType);
		}
	}

	private SmSimpleType<A> convertMemberType(final QName simpleType, final XMLTypeRef<A> typeRef) throws SmAbortException, SmException
	{
		final SmType<A> memberType = convertType(typeRef);
		if (memberType.isAtomicType())
		{
			return (SmSimpleType<A>)memberType;
		}
		else if (memberType instanceof SmListSimpleType<?>)
		{
			return (SmListSimpleType<A>)memberType;
		}
		else if (memberType instanceof SmSimpleType<?>)
		{
			if (memberType.isSimpleUrType())
			{
				return (SmSimpleType<A>)memberType;
			}
		}
		throw new SccMemberTypeMustBeAtomicOrListException(simpleType);
	}

	private SmModelGroup<A> convertModelGroup(final XMLModelGroup<A> xmlModelGroup) throws SmAbortException, SmException
	{
		final SmScopeExtent scope = convertScope(xmlModelGroup.getScope());
		final QName name;
		final boolean isAnonymous;
		if (scope == SmScopeExtent.Global)
		{
			name = xmlModelGroup.getName();
			isAnonymous = false;
			if (m_outBag.hasModelGroup(name))
			{
				return m_outBag.getModelGroup(name);
			}
			if (m_existingCache.hasModelGroup(name))
			{
				return m_existingCache.getModelGroup(name);
			}
			if (m_cycles.groups.contains(xmlModelGroup))
			{
				throw new SccCyclicModelGroupException(name, xmlModelGroup.getLocation());
			}
			else
			{
				m_cycles.groups.push(xmlModelGroup);
			}
		}
		else
		{
			name = null;
			isAnonymous = true;
		}

		try
		{
			final SmModelGroup.SmCompositor compositor = xmlModelGroup.getCompositor();
			final LinkedList<SmParticle<A>> particles = new LinkedList<SmParticle<A>>();
			for (final XMLParticle<A> xmlParticle : xmlModelGroup.getParticles())
			{
				try
				{
					if (xmlParticle instanceof XMLParticleWithModelGroupTerm<?>)
					{
						particles.add(convertModelGroupUse((XMLParticleWithModelGroupTerm<A>)xmlParticle));
					}
					else if (xmlParticle instanceof XMLParticleWithElementTerm<?>)
					{
						particles.add(convertElementUse((XMLParticleWithElementTerm<A>)xmlParticle));
					}
					else if (xmlParticle instanceof XMLParticleWithWildcardTerm<?>)
					{
						particles.add(convertWildcardUse((XMLParticleWithWildcardTerm<A>)xmlParticle));
					}
					else
					{
						throw new AssertionError(xmlParticle);
					}

				}
				catch (final SmException e)
				{
					m_errors.error(e);
				}
			}
			final SmModelGroup<A> modelGroup = new ModelGroupImpl<A>(compositor, particles, name, isAnonymous, scope);
			if (modelGroup.getScopeExtent() == SmScopeExtent.Global)
			{
				m_outBag.add(modelGroup);
			}
			m_locations.m_modelGroupLocations.put(modelGroup, xmlModelGroup.getLocation());
			return modelGroup;
		}
		finally
		{
			if (scope == SmScopeExtent.Global)
			{
				m_cycles.groups.pop();
			}
		}
	}

	private void convertModelGroups() throws SmAbortException
	{
		for (final XMLModelGroup<A> source : m_inCache.m_modelGroups.values())
		{
			try
			{
				convertModelGroup(source);
			}
			catch (final SmException e)
			{
				m_errors.error(e);
			}
		}
	}

	private SmModelGroupUse<A> convertModelGroupUse(final XMLParticleWithModelGroupTerm<A> particle) throws SmAbortException, SmException
	{
		final SmModelGroup<A> modelGroup = convertModelGroup(particle.getTerm());

		final SmModelGroupUse<A> modelGroupUse;
		if (isMaxOccursUnbounded(particle.getMaxOccurs()))
		{
			final int minOccurs = minOccurs(particle.getMinOccurs());
			modelGroupUse = new ParticleWithModelGroupTerm<A>(minOccurs, modelGroup);
		}
		else
		{
			final int minOccurs = minOccurs(particle.getMinOccurs());
			final int maxOccurs = maxOccurs(particle.getMaxOccurs());
			modelGroupUse = new ParticleWithModelGroupTerm<A>(minOccurs, maxOccurs, modelGroup);
		}
		m_locations.m_particleLocations.put(modelGroupUse, particle.getLocation());
		return modelGroupUse;
	}

	private SmNotation<A> convertNotation(final XMLNotation<A> xmlNotation)
	{
		final SmNotation<A> notation = new NotationImpl<A>(xmlNotation.getName(), xmlNotation.publicId, xmlNotation.systemId);
		m_outBag.add(notation);
		m_locations.m_notationLocations.put(notation, xmlNotation.getLocation());
		return notation;
	}

	private void convertNotations()
	{
		for (final XMLNotation<A> source : m_inCache.m_notations.values())
		{
			convertNotation(source);
		}
	}

	private SmScopeExtent convertScope(final XMLScope<A> scope)
	{
		PreCondition.assertArgumentNotNull(scope, "scope");

		return scope.isGlobal() ? SmScopeExtent.Global : SmScopeExtent.Local;
	}

	/**
	 * Applies the Schema Component Constraints to this Simple Type.
	 */
	private SmSimpleType<A> convertSimpleType(final QName name, final boolean isAnonymous, final XMLType<A> xmlSimpleType) throws SmAbortException, SmException
	{
		PreCondition.assertTrue(xmlSimpleType.isSimple(), "expecting a simple type for " + name);

		final SmScopeExtent scope = convertScope(xmlSimpleType.getScope());
		if (scope == SmScopeExtent.Global)
		{
			if (m_outBag.hasSimpleType(name))
			{
				return m_outBag.getSimpleType(name);
			}
			if (m_existingCache.hasSimpleType(name))
			{
				return m_existingCache.getSimpleType(name);
			}
			if (m_cycles.types.contains(xmlSimpleType))
			{
				throw new SmCyclicTypeException(name);
			}

			m_cycles.types.push(xmlSimpleType);
		}
		try
		{
			final SmSimpleType<A> simpleBaseType;
			if (null != xmlSimpleType.getBaseRef())
			{
				simpleBaseType = convertSimpleTypeBase(name, xmlSimpleType.getBaseRef());
			}
			else
			{
				simpleBaseType = convertSimpleTypeBase(name, xmlSimpleType.getScope().getType().getBaseRef());
			}

			final SimpleTypeImpl<A> simpleType;
			final SmDerivationMethod derivation = PreCondition.assertNotNull(xmlSimpleType.getDerivationMethod(), "{type definition} with base " + simpleBaseType.getName());
			final SmWhiteSpacePolicy whiteSpace = xmlSimpleType.getWhiteSpacePolicy();
			if (derivation.isUnion())
			{
				final LinkedList<SmSimpleType<A>> memberTypes = new LinkedList<SmSimpleType<A>>();
				for (final XMLTypeRef<A> memberRef : xmlSimpleType.memberRefs)
				{
					final SmSimpleType<A> memberType = convertMemberType(name, memberRef);
					memberTypes.add(memberType);
				}
				simpleType = new UnionTypeImpl<A>(name, isAnonymous, scope, simpleBaseType, memberTypes, whiteSpace, atomBridge);
				m_outBag.add(simpleType);
				m_locations.m_simpleTypeLocations.put(simpleType, xmlSimpleType.getLocation());
			}
			else if (derivation.isList())
			{
				final SmSimpleType<A> itemType = convertItemType(name, xmlSimpleType.itemRef);
				simpleType = new ListTypeImpl<A>(name, isAnonymous, scope, itemType, simpleBaseType, whiteSpace, atomBridge);
				m_outBag.add(simpleType);
				m_locations.m_simpleTypeLocations.put(simpleType, xmlSimpleType.getLocation());
			}
			else if (derivation.isRestriction())
			{
				simpleType = deriveSimpleType(name, isAnonymous, scope, simpleBaseType, whiteSpace, xmlSimpleType.getLocation());
			}
			else
			{
				throw new AssertionError(derivation.name());
			}
			computePatterns(xmlSimpleType.getPatternFacets(), simpleType);
			computeFacets(simpleBaseType, xmlSimpleType, simpleType);
			computeEnumerations(simpleBaseType, xmlSimpleType, simpleType);
			return simpleType;
		}
		finally
		{
			if (scope == SmScopeExtent.Global)
			{
				m_cycles.types.pop();
			}
		}
	}

	private SmSimpleType<A> convertSimpleTypeBase(final QName simpleType, final XMLTypeRef<A> baseRef) throws SmAbortException, SmException
	{
		final SmType<A> baseType = convertType(baseRef);
		if (baseType instanceof SmSimpleType<?>)
		{
			return (SmSimpleType<A>)baseType;
		}
		else
		{
			throw new SccBaseTypeMustBeSimpleTypeException(simpleType);
		}
	}

	private SmType<A> convertType(final QName name, final boolean isAnonymous) throws SmException, SmAbortException
	{
		if (m_outBag.hasSimpleType(name))
		{
			return m_outBag.getSimpleType(name);
		}
		else if (m_outBag.hasComplexType(name))
		{
			return m_outBag.getComplexType(name);
		}
		else
		{
			if (m_inCache.m_globalTypes.containsKey(name))
			{
				final XMLType<A> type = m_inCache.m_globalTypes.get(name);
				if (type.isSimple())
				{
					return convertSimpleType(name, isAnonymous, type);
				}
				else if (type.isComplex())
				{
					return convertComplexType(name, isAnonymous, type);
				}
				else
				{
					throw new SmUndeclaredReferenceException(name, m_inCache.m_typesUnresolved.get(name));
				}
			}
			else if (m_existingCache.hasSimpleType(name))
			{
				return m_existingCache.getSimpleType(name);
			}
			else if (m_existingCache.hasComplexType(name))
			{
				return m_existingCache.getComplexType(name);
			}
			else
			{
				throw new SmUndeclaredReferenceException(name, m_inCache.m_typesUnresolved.get(name));
			}
		}
	}

	private SmType<A> convertType(final QName name, final boolean isAnonymous, final XMLType<A> type) throws SmAbortException, SmException
	{
		if (type.isSimple())
		{
			return convertSimpleType(name, isAnonymous, type);
		}
		else if (type.isComplex())
		{
			return convertComplexType(name, isAnonymous, type);
		}
		else
		{
			throw new SmUndeclaredReferenceException(name, m_inCache.m_typesUnresolved.get(name));
		}
	}

	private SmType<A> convertType(final XMLTypeRef<A> typeRef) throws SmAbortException, SmException
	{
		if (typeRef.isGlobal())
		{
			return convertType(typeRef.getName(), false);
		}
		else
		{
			return convertType(m_existingCache.generateUniqueName(), true, typeRef.getLocal());
		}
	}

	private void convertTypes() throws SmAbortException
	{
		for (final XMLType<A> sourceType : m_inCache.m_globalTypes.values())
		{
			// {name} is known because the type is global.
			final QName name = sourceType.getName();
			final boolean isAnonymous = false;
			try
			{
				if (sourceType.isComplex())
				{
					convertComplexType(name, isAnonymous, sourceType);
				}
				else if (sourceType.isSimple())
				{
					convertSimpleType(name, isAnonymous, sourceType);
				}
				else
				{
					throw new AssertionError(sourceType);
				}
			}
			catch (final SmException e)
			{
				m_errors.error(e);
			}
		}
	}

	private SmValueConstraint<A> convertValueConstraint(final String elementName, final XMLValueConstraint xmlValueConstraint, final SmSimpleType<A> simpleType) throws SmException
	{
		if (null != xmlValueConstraint)
		{
			final String initialValue = xmlValueConstraint.getValue();
			try
			{
				final List<A> value = simpleType.validate(initialValue);
				return new SmValueConstraint<A>(xmlValueConstraint.kind, value, initialValue);
			}
			catch (final SmDatatypeException dte)
			{
				final SmSimpleTypeException ste = new SmSimpleTypeException(initialValue, simpleType, dte);
				throw new SmAttributeUseException(new QName(elementName), xmlValueConstraint.getAttributeName(), xmlValueConstraint.getLocation(), ste);
			}
		}
		else
		{
			return null;
		}
	}

	private SmWildcard<A> convertWildcard(final XMLWildcard<A> wildcard)
	{
		if (null != wildcard)
		{
			return new WildcardImpl<A>(wildcard.getProcessContents(), convert(wildcard.getNamespaceConstraint()));
		}
		else
		{
			return null;
		}
	}

	private SmParticle<A> convertWildcardUse(final XMLParticleWithWildcardTerm<A> particle) throws SicOversizedIntegerException
	{
		final SmWildcard<A> wildcard = convertWildcard(particle.getTerm());

		final SmWildcardUse<A> wildcardUse;
		if (isMaxOccursUnbounded(particle.getMaxOccurs()))
		{
			final int minOccurs = minOccurs(particle.getMinOccurs());
			wildcardUse = new ParticleWithWildcardTerm<A>(minOccurs, wildcard);
		}
		else
		{
			final int minOccurs = minOccurs(particle.getMinOccurs());
			final int maxOccurs = maxOccurs(particle.getMaxOccurs());
			wildcardUse = new ParticleWithWildcardTerm<A>(minOccurs, maxOccurs, wildcard);
		}
		m_locations.m_particleLocations.put(wildcardUse, particle.getLocation());
		return wildcardUse;
	}

	private SimpleTypeImpl<A> deriveSimpleType(final QName name, final boolean isAnonymous, final SmScopeExtent scope, final SmSimpleType<A> simpleBaseType, final SmWhiteSpacePolicy whiteSpace, final SrcFrozenLocation location) throws SmException
	{
		final SimpleTypeImpl<A> simpleType;
		if (simpleBaseType.isAtomicType())
		{
			final SmAtomicType<A> atomicBaseType = (SmAtomicType<A>)simpleBaseType;
			simpleType = new AtomicTypeImpl<A>(name, isAnonymous, scope, atomicBaseType, whiteSpace, atomBridge);
			m_outBag.add(simpleType);
			m_locations.m_simpleTypeLocations.put(simpleType, location);
		}
		else if (simpleBaseType instanceof SmListSimpleType<?>)
		{
			final SmListSimpleType<A> listBaseListType = (SmListSimpleType<A>)simpleBaseType;
			simpleType = new ListTypeImpl<A>(name, isAnonymous, scope, listBaseListType.getItemType(), simpleBaseType, whiteSpace, atomBridge);
			m_outBag.add(simpleType);
			m_locations.m_simpleTypeLocations.put(simpleType, location);
		}
		else if (simpleBaseType instanceof SmUnionSimpleType<?>)
		{
			final SmUnionSimpleType<A> unionBaseType = (SmUnionSimpleType<A>)simpleBaseType;
			simpleType = new UnionTypeImpl<A>(name, isAnonymous, scope, simpleBaseType, unionBaseType.getMemberTypes(), whiteSpace, atomBridge);
			m_outBag.add(simpleType);
			m_locations.m_simpleTypeLocations.put(simpleType, location);
		}
		else if (simpleBaseType.isSimpleUrType())
		{
			throw new SccBaseTypeMustBeSimpleTypeException(name);
		}
		else
		{
			throw new AssertionError(simpleBaseType.getClass());
		}
		return simpleType;
	}

	private SmModelGroupUse<A> effectiveContent(final boolean mixed, final XMLParticleWithModelGroupTerm<A> contentModel) throws SmAbortException, SmException
	{
		if (null == contentModel)
		{
			if (mixed)
			{
				final List<SmParticle<A>> particles = Collections.emptyList();
				final SmModelGroup<A> modelGroup = new ModelGroupImpl<A>(SmModelGroup.SmCompositor.Sequence, particles, null, true, SmScopeExtent.Local);
				return new ParticleWithModelGroupTerm<A>(1, 1, modelGroup);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return convertModelGroupUse(contentModel);
		}
	}

	private SmEnumeration<A> enumeration(final SmSimpleType<A> type, final SmSimpleType<A> baseType, final XMLEnumeration<A> sourceEnum) throws SmAttributeUseException
	{
		try
		{
			final SmSimpleType<A> notationType = m_existingCache.getAtomicType(SmNativeType.NOTATION);
			if (baseType.getName().equals(notationType.getName()) || baseType.derivedFromType(notationType, EnumSet.of(SmDerivationMethod.Restriction)))
			{
				final SmPrefixResolver resolver = sourceEnum.getPrefixResolver();
				final List<A> value = baseType.validate(sourceEnum.getValue(), resolver);
				return new FacetEnumerationImpl<A>(value, atomBridge);
			}
			else
			{
				final List<A> value = baseType.validate(sourceEnum.getValue());
				return new FacetEnumerationImpl<A>(value, atomBridge);
			}
		}
		catch (final SmDatatypeException dte)
		{
			final SmSimpleTypeException ste = new SmSimpleTypeException(sourceEnum.getValue(), baseType, dte);
			final QName elementName = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, XMLRepresentation.LN_ENUMERATION);
			final QName attributeName = new QName(XMLRepresentation.LN_VALUE);
			throw new SmAttributeUseException(elementName, attributeName, sourceEnum.getLocation(), ste);
		}
	}

	private SmSimpleType<A> extractSimpleType(final XMLTypeRef<A> typeRef) throws SmAbortException, SmException
	{
		final SmType<A> type = convertType(typeRef);
		if (type instanceof SmSimpleType<?>)
		{
			return (SmSimpleType<A>)type;
		}
		else if (type instanceof SmComplexType<?>)
		{
			final SmComplexType<A> complexType = (SmComplexType<A>)type;
			final SmContentType<A> contentType = complexType.getContentType();
			if (contentType.isSimple())
			{
				return contentType.getSimpleType();
			}
			else if (contentType.isMixed())
			{
				return contentType.getSimpleType();
			}
			else
			{
				throw new AssertionError(contentType.getKind());
			}
		}
		else
		{
			throw new AssertionError(type);
		}
	}

	private SmFacet<A> fractionDigits(final XMLFractionDigitsFacet<A> xmlFacet) throws SicOversizedIntegerException
	{
		return new FacetFractionDigitsImpl<A>(getIntValue(xmlFacet.value), xmlFacet.fixed, atomBridge);
	}

	/**
	 * Converts a BigInteger value to an int value
	 * 
	 * @param value
	 *            the BigInteger to convert
	 * @return the int value equivalent of the incoming BigInteger value
	 * @throws SicOversizedIntegerException
	 *             if value is larger than Integer.MAX_VALUE
	 */
	private int getIntValue(final BigInteger value) throws SicOversizedIntegerException
	{
		PreCondition.assertArgumentNotNull(value, "value");
		if (value.compareTo(MAX_INT_SIZE) <= 0)
		{
			return value.intValue();
		}
		else
		{
			throw new SicOversizedIntegerException(value);
		}
	}

	private SmFacet<A> length(final XMLLength<A> xmlFacet) throws SicOversizedIntegerException
	{
		if (xmlFacet.minLength != null)
		{
			if (xmlFacet.maxLength != null)
			{
				if (xmlFacet.minLength.equals(xmlFacet.maxLength))
				{
					return new FacetLengthImpl<A>(getIntValue(xmlFacet.minLength), xmlFacet.fixed, atomBridge);
				}
				else
				{
					throw new AssertionError();
				}
			}
			else
			{
				return new FacetMinLengthImpl<A>(getIntValue(xmlFacet.minLength), xmlFacet.fixed, atomBridge);
			}
		}
		else
		{
			if (xmlFacet.maxLength != null)
			{
				return new FacetMaxLengthImpl<A>(getIntValue(xmlFacet.maxLength), xmlFacet.fixed, atomBridge);
			}
			else
			{
				throw new AssertionError();
			}
		}
	}

	private SmLimit<A> limit(final A value, final SmSimpleType<A> simpleType, final SmFacetKind kind, final boolean isFixed)
	{
		PreCondition.assertArgumentNotNull(value, "value");
		PreCondition.assertArgumentNotNull(simpleType, "simpleType");
		PreCondition.assertArgumentNotNull(kind, "kind");

		if (simpleType.isAtomicType())
		{
			return new FacetValueCompImpl<A>(value, kind, simpleType, isFixed, atomBridge);
		}
		else if (simpleType instanceof SmListSimpleType<?>)
		{
			final SmListSimpleType<A> listType = (SmListSimpleType<A>)simpleType;
			final SmSimpleType<A> itemType = listType.getItemType();
			if (itemType.isAtomicType())
			{
				final SmSimpleType<A> atomicType = (SmSimpleType<A>)itemType;
				return new FacetValueCompImpl<A>(value, kind, atomicType, isFixed, atomBridge);
			}
			else if (itemType instanceof SmUnionSimpleType<?>)
			{
				throw new UnsupportedOperationException();
			}
			else
			{
				// The specification forbids lists of lists.
				throw new UnsupportedOperationException();
			}
		}
		else if (simpleType instanceof SmUnionSimpleType<?>)
		{
			throw new UnsupportedOperationException();
		}
		else
		{
			// Simple Ur-Type?
			throw new UnsupportedOperationException();
		}
	}

	private SmFacet<A> minmax(final XMLMinMaxFacet<A> xmlFacet, final SmSimpleType<A> baseType) throws SmException
	{
		final List<A> value;
		{
			final String initialValue = xmlFacet.value;
			try
			{
				value = baseType.validate(initialValue);
			}
			catch (final SmDatatypeException dte)
			{
				final SmSimpleTypeException ste = new SmSimpleTypeException(initialValue, baseType, dte);
				final QName elementName = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, xmlFacet.elementName);
				final QName attributeName = new QName(XMLRepresentation.LN_VALUE);
				final SrcFrozenLocation location = xmlFacet.getLocation();
				throw new SmAttributeUseException(elementName, attributeName, location, ste);
			}
		}
		if (value.size() > 0)
		{
			return limit(value.get(0), baseType, xmlFacet.getOperator(), xmlFacet.fixed);
		}
		else
		{
			return null;
		}
	}

	private SmPattern pattern(final XMLPatternFacet<A> pattern) throws SmAttributeUseException
	{
		try
		{
			final String regex = pattern.value;
			try
			{
				final SmRegExPattern regexp = regexc.compile(regex);
				return new FacetPatternImpl(regexp, regex);
			}
			catch (final SmRegExCompileException e)
			{
				final SmDatatypeException dte = new SmDatatypeException(regex, null);
				throw new SmSimpleTypeException(regex, null, dte);
			}
		}
		catch (final SmSimpleTypeException ste)
		{
			final QName elementName = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, XMLRepresentation.LN_PATTERN);
			final QName attributeName = new QName(XMLRepresentation.LN_VALUE);
			throw new SmAttributeUseException(elementName, attributeName, pattern.getLocation(), ste);
		}
	}

	private SmContentType<A> simpleContent(final XMLType<A> simpleType, final SmSimpleType<A> simpleBaseType) throws SmAbortException, SmException
	{
		final QName name;
		final boolean isAnonymous;
		final SmScopeExtent scope = convertScope(simpleType.getScope());
		if (scope == SmScopeExtent.Global)
		{
			name = simpleType.getName();
			isAnonymous = false;
		}
		else
		{
			name = m_existingCache.generateUniqueName();
			isAnonymous = true;
		}
		final SmWhiteSpacePolicy whiteSpace = simpleType.getWhiteSpacePolicy();
		final SimpleTypeImpl<A> simpleTypeD = deriveSimpleType(name, isAnonymous, scope, simpleBaseType, whiteSpace, simpleType.getLocation());
		computePatterns(simpleType.getPatternFacets(), simpleTypeD);
		computeFacets(simpleBaseType, simpleType, simpleTypeD);
		computeEnumerations(simpleBaseType, simpleType, simpleTypeD);
		return new ContentTypeImpl<A>(simpleTypeD);
	}

	private SmFacet<A> totalDigits(final XMLTotalDigitsFacet<A> xmlFacet) throws SicOversizedIntegerException
	{
		return new FacetTotalDigitsImpl<A>(getIntValue(xmlFacet.value), xmlFacet.fixed, atomBridge);
	}
}
