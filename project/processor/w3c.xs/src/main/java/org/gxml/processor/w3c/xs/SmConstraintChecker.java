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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.exceptions.GxmlAtomCastException;
import org.genxdm.names.NameSource;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmAttributeGroup;
import org.genxdm.xs.components.SmComponent;
import org.genxdm.xs.components.SmComponentBag;
import org.genxdm.xs.components.SmComponentProvider;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.components.SmEnumeration;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.components.SmNotation;
import org.genxdm.xs.components.SmParticle;
import org.genxdm.xs.components.SmParticleTerm;
import org.genxdm.xs.components.SmWildcard;
import org.genxdm.xs.constraints.SmAttributeUse;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.constraints.SmModelGroupUse;
import org.genxdm.xs.constraints.SmValueConstraint;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.enums.SmWhiteSpacePolicy;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmComponentConstraintException;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.exceptions.SmFacetMinMaxException;
import org.genxdm.xs.facets.SmFacet;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.facets.SmFractionDigits;
import org.genxdm.xs.facets.SmLength;
import org.genxdm.xs.facets.SmLimit;
import org.genxdm.xs.facets.SmMaxLength;
import org.genxdm.xs.facets.SmMinLength;
import org.genxdm.xs.facets.SmTotalDigits;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmContentType;
import org.genxdm.xs.types.SmListSimpleType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmType;
import org.genxdm.xs.types.SmUnionSimpleType;
import org.gxml.bridgekit.xs.Gregorian;
import org.gxml.processor.w3c.xs.exception.SccAllGroupAppearsException;
import org.gxml.processor.w3c.xs.exception.SccAttributeDeclarationTypeIDConflictsValueConstraintException;
import org.gxml.processor.w3c.xs.exception.SccAttributeDerivationRequiredConflictException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeAttributeFixedOverrideException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeAttributeUniquenessException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeBaseComplexDerivationException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeBaseSimpleDerivationException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeBaseUrTypeException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeDerivationHierarchyException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeDerivationMethodException;
import org.gxml.processor.w3c.xs.exception.SccContentTypeMismatchBaseTypeContainsExtensionException;
import org.gxml.processor.w3c.xs.exception.SccDerivationExtensionContentTypeException;
import org.gxml.processor.w3c.xs.exception.SccDerivationExtensionException;
import org.gxml.processor.w3c.xs.exception.SccDerivationRestrictionContentTypeException;
import org.gxml.processor.w3c.xs.exception.SccElementDeclarationDerivedFromIDWithValueConstraintException;
import org.gxml.processor.w3c.xs.exception.SccElementDeclarationSubstitutionGroupTypeDerivationException;
import org.gxml.processor.w3c.xs.exception.SccFinalOfBaseTypeContainsExtensionException;
import org.gxml.processor.w3c.xs.exception.SccFinalOfBaseTypeContainsRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccFractionDigitsRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccFractionDigitsTotalDigitsException;
import org.gxml.processor.w3c.xs.exception.SccIdentityConstraintKeyrefFieldsCardinalityException;
import org.gxml.processor.w3c.xs.exception.SccIdentityConstraintKeyrefReferenceException;
import org.gxml.processor.w3c.xs.exception.SccLengthRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccMaxExclusionRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccMaxInclusionRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccMaxInclusiveAndMaxExclusiveException;
import org.gxml.processor.w3c.xs.exception.SccMaxLengthCompatibleException;
import org.gxml.processor.w3c.xs.exception.SccMaxLengthDerivedException;
import org.gxml.processor.w3c.xs.exception.SccMaxLengthRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccMaxOccursGeOneException;
import org.gxml.processor.w3c.xs.exception.SccMaxOccursRangeException;
import org.gxml.processor.w3c.xs.exception.SccMinExclusionRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccMinInclusionRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccMinInclusiveLessThanEqualToMaxInclusiveException;
import org.gxml.processor.w3c.xs.exception.SccMinLengthCompatibleException;
import org.gxml.processor.w3c.xs.exception.SccMinLengthDerivedException;
import org.gxml.processor.w3c.xs.exception.SccMinLengthLessThanEqualToMaxLengthException;
import org.gxml.processor.w3c.xs.exception.SccMinLengthRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccMinOccursLeMaxOccursException;
import org.gxml.processor.w3c.xs.exception.SccMinOccursRangeException;
import org.gxml.processor.w3c.xs.exception.SccNotationNotInValueSpace;
import org.gxml.processor.w3c.xs.exception.SccSimpleTypeDerivationException;
import org.gxml.processor.w3c.xs.exception.SccSimpleTypeDerivationRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccTotalDigitsRestrictionException;
import org.gxml.processor.w3c.xs.exception.SccWhiteSpaceParentCollapseException;
import org.gxml.processor.w3c.xs.exception.SccWhiteSpaceParentReplaceException;
import org.gxml.processor.w3c.xs.exception.SccXmlnsNotAllowedException;
import org.gxml.processor.w3c.xs.exception.SccXsiNotAllowedException;


/**
 * Utility for checking Schema Component Constraints.
 */
final class SmConstraintChecker
{
	private static int FOURTEEN_HOURS_IN_MINUTES = 840;

	private static <A> void append(final A atom, final StringBuilder buffer, final AtomBridge<A> atomBridge)
	{
		final QName typeName = atomBridge.getDataType(atom);

		final String displayString = atomBridge.getC14NForm(atom);

		final String localName = typeName.getLocalPart();

		buffer.append(localName.toString());
		buffer.append("('");
		buffer.append(displayString);
		buffer.append("')");
	}

	private static <A> A castUp(final A sourceAtom, final SmNativeType targetType, final AtomBridge<A> atomBridge)
	{
		// TODO: This has the hallmark of a helper function?
		switch (targetType)
		{
			case FLOAT:
			{
				return atomBridge.createFloat(atomBridge.getFloat(sourceAtom));
			}
			case DECIMAL:
			{
				return atomBridge.createDecimal(atomBridge.getDecimal(sourceAtom));
			}
			case INT:
			{
				return atomBridge.createInt(atomBridge.getInt(sourceAtom));
			}
			case DATE:
			{
				return atomBridge.createDate(atomBridge.getYear(sourceAtom), atomBridge.getMonth(sourceAtom), atomBridge.getDayOfMonth(sourceAtom), atomBridge.getGmtOffset(sourceAtom));
			}
			default:
			{
				throw new AssertionError(targetType);
			}
		}
	}

	private static <A> void checkAttribute(final SmAttribute<A> attribute, final SmConstraintHandler<A> errors, NameSource nameBridge) throws SmAbortException
	{
		checkAttributeDeclarationPropertiesCorrect(attribute, errors, nameBridge);

		checkAttributeForXmlnsNotAllowed(attribute, errors, nameBridge);

		checkAttributeForXsiNotAllowed(attribute, errors, nameBridge);
	}

	private static <A> void checkAttributeDeclarationPropertiesCorrect(final SmAttribute<A> attribute, final SmConstraintHandler<A> errors, NameSource nameBridge) throws SmAbortException
	{
		final SmSimpleType<A> attributeType = (SmSimpleType<A>)attribute.getType();
		if (null != attributeType)
		{
			if (null != attribute.getValueConstraint())
			{
				if (attributeType.isID())
				{
					errors.error(attribute, new SccAttributeDeclarationTypeIDConflictsValueConstraintException(attribute.getName()));
				}
			}
		}
		else
		{
			// A Missing Sub-component.
		}
	}

	private static <A> void checkAttributeDerivationRestrictionComplexType(final SmComplexType<A> complexType, final SmComplexType<A> baseType, final AtomBridge<A> atomBridge, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		final NameSource nameBridge = atomBridge.getNameBridge();

		final HashMap<QName, SmAttributeUse<A>> attributes = new HashMap<QName, SmAttributeUse<A>>();
		for (final SmAttributeUse<A> attributeUse : complexType.getAttributeUses().values())
		{
			final SmAttribute<A> attribute = attributeUse.getAttribute();
			final QName attributeName = attribute.getName();

			if (attributes.containsKey(attributeName))
			{
				errors.error(complexType, new SccComplexTypeAttributeUniquenessException(complexType.getName(), attributeName));
			}
			else
			{
				attributes.put(attributeName, attributeUse);
			}

			if (attribute.getScopeExtent() != SmScopeExtent.Global)
			{
				checkAttribute(attribute, errors, nameBridge);
			}
		}

		for (final SmAttributeUse<A> B : baseType.getAttributeUses().values())
		{
			final QName attributeName = B.getAttribute().getName();
			if (attributes.containsKey(attributeName))
			{
				final SmAttributeUse<A> R = attributes.get(attributeName);
				if (!R.isRequired() && B.isRequired())
				{
					errors.error(complexType, new SccAttributeDerivationRequiredConflictException(complexType.getName(), attributeName));
				}

				final SmValueConstraint<A> vcB = B.getEffectiveValueConstraint();
				if (null == vcB || vcB.getVariety().isDefault())
				{
					// OK
				}
				else
				{
					final SmValueConstraint<A> vcR = R.getEffectiveValueConstraint();
					if (null != vcR)
					{
						if (vcR.getVariety().isFixed())
						{
							final List<A> vB = vcB.getValue();
							final List<A> vR = vcR.getValue();
							if (!equalValues(vR, vB))
							{
								// TODO: Wrong Exception
								errors.error(complexType, new SccAttributeDerivationRequiredConflictException(qname(complexType.getName()), qname(attributeName)));
							}
						}
					}
					else
					{
						// error?
					}
				}
			}
		}
	}

	private static <A> void checkAttributeForXmlnsNotAllowed(final SmAttribute<A> attribute, final SmConstraintHandler<A> errors, final NameSource nameBridge) throws SmAbortException
	{
		final QName name = attribute.getName();
		if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI == name.getNamespaceURI())
		{
			errors.error(attribute, new SccXmlnsNotAllowedException(attribute.getName()));
		}
		if (XMLConstants.XMLNS_ATTRIBUTE == name.getLocalPart())
		{
			errors.error(attribute, new SccXmlnsNotAllowedException(attribute.getName()));
		}
	}

	private static <A> void checkAttributeForXsiNotAllowed(final SmAttribute<A> attribute, final SmConstraintHandler<A> errors, final NameSource nameBridge) throws SmAbortException
	{
		if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI == attribute.getName().getNamespaceURI())
		{
			errors.error(attribute, new SccXsiNotAllowedException(qname(attribute.getName())));
		}
	}

	private static <A> void checkAttributeGroup(final SmAttributeGroup<A> attributeGroup, final SmConstraintHandler<A> errors, NameSource nameBridge) throws SmAbortException
	{
		if (attributeGroup.hasAttributeUses())
		{
			final HashSet<QName> unique = new HashSet<QName>();
			for (final SmAttributeUse<A> attributeUse : attributeGroup.getAttributeUses())
			{
				final SmAttribute<A> attribute = attributeUse.getAttribute();
				final QName attributeName = attribute.getName();

				if (unique.contains(attributeName))
				{
					errors.error(attributeGroup, new SccComplexTypeAttributeUniquenessException(qname(attributeGroup.getName()), qname(attributeName)));
				}
				else
				{
					unique.add(attributeName);
				}

				if (attribute.getScopeExtent() != SmScopeExtent.Global)
				{
					checkAttribute(attribute, errors, nameBridge);
				}
			}
		}
	}

	private static <A> void checkAttributeUses(final SmComplexType<A> complexType, final SmConstraintHandler<A> errors, final AtomBridge<A> atomBridge) throws SmAbortException
	{
		final NameSource nameBridge = atomBridge.getNameBridge();
		// Check that the attribute names are unique, and check each local
		// attribute declaration.
		final HashSet<QName> unique = new HashSet<QName>();
		for (final SmAttributeUse<A> attributeUse : complexType.getAttributeUses().values())
		{
			final SmAttribute<A> attribute = attributeUse.getAttribute();
			final QName attributeName = attribute.getName();

			if (unique.contains(attributeName))
			{
				errors.error(complexType, new SccComplexTypeAttributeUniquenessException(complexType.getName(), attributeName));
			}
			else
			{
				unique.add(attributeName);
			}

			// Check that any value constraint on the attribute use is
			// consistent with the attribute declaration.
			final SmValueConstraint<A> valueConstraintUse = attributeUse.getValueConstraint();
			if (null != valueConstraintUse && valueConstraintUse.getVariety().isFixed())
			{
				final SmValueConstraint<A> valueConstraintAtt = attribute.getValueConstraint();
				if (null != valueConstraintAtt && valueConstraintAtt.getVariety().isFixed())
				{
					final String valueUse = atomBridge.getC14NString(valueConstraintUse.getValue());
					final String valueAtt = atomBridge.getC14NString(valueConstraintAtt.getValue());
					if (!valueUse.equals(valueAtt))
					{
						errors.error(complexType, new SccComplexTypeAttributeFixedOverrideException(qname(complexType.getName()), qname(attribute.getName())));
					}
				}
			}

			if (attribute.getScopeExtent() != SmScopeExtent.Global)
			{
				checkAttribute(attribute, errors, nameBridge);
			}
		}
	}

	private static <A> void checkClause5(final SmComplexType<A> complexType, final SmComplexType<A> baseType, final SmConstraintHandler<A> errors, NameSource nameBridge) throws SmAbortException
	{
		if (baseType.isComplexUrType())
		{
		}
		else
		{
			if (clause52(complexType, baseType))
			{
			}
			else if (clause53(complexType, baseType))
			{
			}
			else
			{
				final SmContentType<A> contentTypeR = complexType.getContentType();
				final SmContentType<A> contentTypeB = baseType.getContentType();

				if (contentTypeR.isElementOnly() || (contentTypeR.isMixed() && contentTypeB.isMixed()))
				{
					checkModelGroupUseValidRestriction(complexType, contentTypeR.getContentModel(), contentTypeB.getContentModel(), errors);
				}
				else
				{
					errors.error(complexType, new SccDerivationRestrictionContentTypeException(qname(complexType.getName())));
				}
			}
		}
	}

	private static <A> void checkComplexType(final SmComplexType<A> complexType, final SmComponentBagImpl<A> components, final AtomBridge<A> atomBridge, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		final NameSource nameBridge = atomBridge.getNameBridge();
		checkComplexTypeDefinitionProperties(complexType, errors);

		if (complexType.getDerivationMethod().isExtension())
		{
			checkDerivationValidExtensionComplexType(complexType, errors, nameBridge);
		}
		if (complexType.getDerivationMethod().isRestriction())
		{
			checkDerivationValidRestrictionComplexType(complexType, atomBridge, errors);
		}

		checkAttributeUses(complexType, errors, atomBridge);
	}

	private static <A> void checkComplexTypeDefinitionProperties(final SmComplexType<A> complexType, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		complexType.getName();

		final SmContentType<A> contentType = complexType.getContentType();
		if (contentType.isMixed() || contentType.isElementOnly())
		{
			final SmModelGroupUse<A> contentModel = contentType.getContentModel();
			checkOccurrences(contentModel, errors);

			final SmModelGroup<A> modelGroup = contentModel.getTerm();
			if (modelGroup.getScopeExtent() != SmScopeExtent.Global)
			{
				checkModelGroup(modelGroup, errors);
			}
		}
	}

	private static <A> void checkDerivationValidExtensionComplexType(final SmComplexType<A> complexType, final SmConstraintHandler<A> errors, NameSource nameBridge) throws SmAbortException
	{
		if (complexType.getBaseType() instanceof SmComplexType<?>)
		{
			final SmComplexType<A> baseType = (SmComplexType<A>)complexType.getBaseType();
			// The {final} of the {base type definition} must not contain
			// extension.
			if (baseType.isFinal(SmDerivationMethod.Extension))
			{
				errors.error(complexType, new SccFinalOfBaseTypeContainsExtensionException(SccDerivationExtensionException.PART_WHEN_BASE_COMPLEX_TYPE_FINAL_OF_BASE_MUST_NOT_CONTAINT_EXTENSION, qname(complexType.getName())));
			}

			// TODO: 1.2 {attribute uses} must be a subset...

			// TODO: 1.3 {attribute wildcard}...

			if (!clause14(complexType, baseType))
			{
				errors.error(complexType, new SccDerivationExtensionContentTypeException(qname(complexType.getName())));
			}
		}
		else if (complexType.getBaseType() instanceof SmSimpleType<?>)
		{
			final SmSimpleType<A> simpleTypeB = (SmSimpleType<A>)complexType.getBaseType();
			final SmContentType<A> contentTypeD = complexType.getContentType();

			if (!clause21(contentTypeD, simpleTypeB))
			{
				errors.error(complexType, new SccContentTypeMismatchBaseTypeContainsExtensionException(qname(complexType.getName())));
			}

			if (!clause22(simpleTypeB))
			{
				errors.error(complexType, new SccFinalOfBaseTypeContainsExtensionException(SccDerivationExtensionException.PART_WHEN_BASE_SIMPLE_TYPE_FINAL_OF_BASE_MUST_NOT_CONTAINT_EXTENSION, qname(complexType.getName())));
			}
		}
		else
		{
			throw new AssertionError(complexType.getBaseType());
		}
	}

	private static <A> void checkDerivationValidRestrictionComplexType(final SmComplexType<A> complexType, final AtomBridge<A> atomBridge, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		final NameSource nameBridge = atomBridge.getNameBridge();

		// The {base type definition} must be a complex type definition.
		if (complexType.getBaseType() instanceof SmComplexType<?>)
		{
			final SmComplexType<A> baseType = (SmComplexType<A>)complexType.getBaseType();

			// The {final} of the {base type definition} must not contain
			// restriction.
			if (baseType.isFinal(SmDerivationMethod.Restriction))
			{
				errors.error(complexType, new SccFinalOfBaseTypeContainsRestrictionException(complexType.getName()));
			}

			checkAttributeDerivationRestrictionComplexType(complexType, baseType, atomBridge, errors);

			checkClause5(complexType, baseType, errors, nameBridge);
		}
		else
		{
			// TODO: Check tis out for the TIBCOxml test suite.
			// errors.error(complexType, new
			// SccDerivationRestrictionBaseTypeMustBeComplexTypeException(qname(complexType.getName(),
			// nameBridge)));
		}
	}

	private static <A> void checkElement(final SmElement<A> element, final SmConstraintHandler<A> errors, NameSource nameBridge) throws SmAbortException
	{
		checkElementDeclaration(element, nameBridge, errors);
	}

	private static <A> void checkElementDeclaration(final SmElement<A> element, final NameSource nameBridge, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		// 4. If there is a {substitution group affiliation}, the {type
		// definition} of the element must be
		// validly derived from the {type definition} of the affiliation, given
		// the exclusions of the affiliation.
		if (element.hasSubstitutionGroup())
		{
			final SmElement<A> substitutionGroup = element.getSubstitutionGroup();
			try
			{
				checkTypeDerivationOK(element.getType(), substitutionGroup.getType(), substitutionGroup.getSubstitutionGroupExclusions(), nameBridge);
			}
			catch (final SmComponentConstraintException e)
			{
				errors.error(element, new SccElementDeclarationSubstitutionGroupTypeDerivationException(element.getName(), e));
			}
		}

		// 5. If the {type definition} or {type definition}'s {content type} is
		// or is derived from ID then there must not be a {value constraint}.
		final SmValueConstraint<A> valueConstraint = element.getValueConstraint();
		if (null != valueConstraint)
		{
			final SmType<A> elementType = element.getType();
			if (elementType instanceof SmSimpleType<?>)
			{
				@SuppressWarnings("unused")
				final SmSimpleType<A> simpleType = (SmSimpleType<A>)elementType;
			}
			else if (elementType instanceof SmComplexType<?>)
			{
				final SmComplexType<A> complexType = (SmComplexType<A>)elementType;
				final SmContentType<A> contentType = complexType.getContentType();
				if (contentType.isSimple())
				{

				}
				else if (contentType.isMixed())
				{

				}
				else
				{
					errors.error(element, new SccSimpleTypeDerivationRestrictionException(element.getName()));
				}
			}
			else
			{
				throw new AssertionError(elementType);
			}

			switch (valueConstraint.getVariety())
			{
				case Fixed:
				case Default:
				{
					if (isDerivedFrom(elementType, nameBridge.nativeType(SmNativeType.ID)))
					{
						errors.error(element, new SccElementDeclarationDerivedFromIDWithValueConstraintException(qname(element.getName())));
					}
				}
				break;
				default:
				{
					throw new AssertionError(valueConstraint.getVariety());
				}
			}
		}
	}

	private static <A> void checkIdentityConstraint(final SmIdentityConstraint<A> constraint, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		switch (constraint.getCategory())
		{
			case Key:
			case Unique:
			{
				// OK
			}
			break;
			case KeyRef:
			{
				final SmIdentityConstraint<A> referencedKey = constraint.getKeyConstraint();
				switch (referencedKey.getCategory())
				{
					case Key:
					case Unique:
					{
						if (referencedKey.getFields().size() != constraint.getFields().size())
						{
							errors.error(constraint, new SccIdentityConstraintKeyrefFieldsCardinalityException(qname(constraint.getName()), constraint.getFields().size(), qname(referencedKey.getName()), referencedKey.getFields().size()));
						}
					}
					break;
					case KeyRef:
					{
						errors.error(constraint, new SccIdentityConstraintKeyrefReferenceException(qname(constraint.getName())));
					}
					default:
					{
						throw new AssertionError(referencedKey.getCategory());
					}
				}
			}
			break;
			default:
			{
				throw new AssertionError(constraint.getCategory());
			}
		}
	}

	private static <A> void checkLimitRestriction(final SmLimit<A> restrictingLimit, final SmLimit<A> parentMaxInclusive, final SmLimit<A> parentMaxExclusive, final SmLimit<A> parentMinInclusive, final SmLimit<A> parentMinExclusive,
			final SmSimpleType<A> simpleType, final AtomBridge<A> atomBridge, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		final A derivedAtom = restrictingLimit.getLimit();

		switch (restrictingLimit.getKind())
		{
			case MinInclusive:
			{
				if (parentMaxExclusive != null)
				{
					final A baseAtom = parentMaxExclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result != SmCompareKind.BEFORE)
						{
							errors.error(simpleType, new SccMinInclusionRestrictionException(SmFacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMinInclusionRestrictionException(SmFacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
					}
				}
				if (parentMaxInclusive != null)
				{
					final A baseAtom = parentMaxInclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result == SmCompareKind.AFTER)
						{
							errors.error(simpleType, new SccMinInclusionRestrictionException(SmFacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMinInclusionRestrictionException(SmFacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
					}
				}
				if (parentMinInclusive != null)
				{
					final A baseAtom = parentMinInclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result == SmCompareKind.BEFORE)
						{
							errors.error(simpleType, new SccMinInclusionRestrictionException(SmFacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMinInclusionRestrictionException(SmFacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
					}
				}
				if (parentMinExclusive != null)
				{
					final A baseAtom = parentMinExclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result != SmCompareKind.AFTER)
						{
							errors.error(simpleType, new SccMinInclusionRestrictionException(SmFacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMinInclusionRestrictionException(SmFacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
					}
				}
			}
			break;
			case MaxInclusive:
			{
				if (parentMaxExclusive != null)
				{
					final A baseAtom = parentMaxExclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result != SmCompareKind.BEFORE)
						{
							errors.error(simpleType, new SccMaxInclusionRestrictionException(SmFacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMaxInclusionRestrictionException(SmFacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
					}
				}
				if (parentMaxInclusive != null)
				{
					final A baseAtom = parentMaxInclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result == SmCompareKind.AFTER)
						{
							errors.error(simpleType, new SccMaxInclusionRestrictionException(SmFacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMaxInclusionRestrictionException(SmFacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
					}
				}
				if (parentMinInclusive != null)
				{
					final A baseAtom = parentMinInclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result == SmCompareKind.BEFORE)
						{
							errors.error(simpleType, new SccMaxInclusionRestrictionException(SmFacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMaxInclusionRestrictionException(SmFacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
					}
				}
				if (parentMinExclusive != null)
				{
					final A baseAtom = parentMinExclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result != SmCompareKind.AFTER)
						{
							errors.error(simpleType, new SccMaxInclusionRestrictionException(SmFacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMaxInclusionRestrictionException(SmFacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
					}
				}
			}
			break;
			case MinExclusive:
			{
				if (parentMaxExclusive != null)
				{
					final A baseAtom = parentMaxExclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result != SmCompareKind.BEFORE)
						{
							errors.error(simpleType, new SccMinExclusionRestrictionException(SmFacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMinExclusionRestrictionException(SmFacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
					}
				}
				if (parentMaxInclusive != null)
				{
					final A baseAtom = parentMaxInclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result == SmCompareKind.AFTER)
						{
							errors.error(simpleType, new SccMinExclusionRestrictionException(SmFacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMinExclusionRestrictionException(SmFacetKind.MaxInclusive, restrictingLimit, parentMaxExclusive));
					}
				}
				if (parentMinInclusive != null)
				{
					final A baseAtom = parentMinInclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result == SmCompareKind.BEFORE)
						{
							errors.error(simpleType, new SccMinExclusionRestrictionException(SmFacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMinExclusionRestrictionException(SmFacetKind.MinExclusive, restrictingLimit, parentMinInclusive));
					}
				}
				if (parentMinExclusive != null)
				{
					final A baseAtom = parentMinExclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result == SmCompareKind.BEFORE)
						{
							errors.error(simpleType, new SccMinExclusionRestrictionException(SmFacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMinExclusionRestrictionException(SmFacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
					}
				}
			}
			break;
			case MaxExclusive:
			{
				if (parentMaxExclusive != null)
				{
					final A baseAtom = parentMaxExclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result == SmCompareKind.AFTER)
						{
							errors.error(simpleType, new SccMaxExclusionRestrictionException(SmFacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMaxExclusionRestrictionException(SmFacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
					}
				}
				if (parentMaxInclusive != null)
				{
					final A baseAtom = parentMaxInclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result == SmCompareKind.AFTER)
						{
							errors.error(simpleType, new SccMaxExclusionRestrictionException(SmFacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMaxExclusionRestrictionException(SmFacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
					}
				}
				if (parentMinInclusive != null)
				{
					final A baseAtom = parentMinInclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result != SmCompareKind.AFTER)
						{
							errors.error(simpleType, new SccMaxExclusionRestrictionException(SmFacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMaxExclusionRestrictionException(SmFacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
					}
				}
				if (parentMinExclusive != null)
				{
					final A baseAtom = parentMinExclusive.getLimit();
					try
					{
						final SmCompareKind result = compare(upcast(derivedAtom, baseAtom, atomBridge), baseAtom, atomBridge);
						if (!result.isDeterminate() || result != SmCompareKind.AFTER)
						{
							errors.error(simpleType, new SccMaxExclusionRestrictionException(SmFacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
						}
					}
					catch (final GxmlAtomCastException e)
					{
						errors.error(simpleType, new SccMaxExclusionRestrictionException(SmFacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
					}
				}
			}
			break;
			default:
			{
				throw new AssertionError(restrictingLimit.getKind());
			}
		}
	}

	private static <A> void checkMinExclusiveLessThanEqualToMaxExclusive(final SmLimit<A> minExclusive, final SmLimit<A> maxExclusive, final SmSimpleType<A> simpleType, final AtomBridge<A> atomBridge, final SmConstraintHandler<A> errors)
			throws SmAbortException
	{
		final A min = minExclusive.getLimit();
		try
		{
			maxExclusive.validate(min, simpleType);
		}
		catch (final SmFacetMinMaxException e)
		{
			final String minString = atomBridge.getC14NForm(min);
			final String maxString = getDisplayString(maxExclusive.getLimit(), atomBridge);
			errors.error(simpleType, new SccMinInclusiveLessThanEqualToMaxInclusiveException(minString, maxString));
		}
	}

	private static <A> void checkModelGroup(final SmModelGroup<A> modelGroup, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		for (final SmParticle<A> particle : modelGroup.getParticles())
		{
			checkOccurrences(particle, errors);

			if (particle instanceof SmModelGroupUse<?>)
			{
				final SmModelGroupUse<A> modelGroupUse = (SmModelGroupUse<A>)particle;

				final SmModelGroup<A> term = modelGroupUse.getTerm();
				if (term.getCompositor().isAll())
				{
					errors.error(modelGroup, new SccAllGroupAppearsException());
					if (modelGroup.getScopeExtent() != SmScopeExtent.Global)
					{
						checkModelGroup(term, errors);
					}
				}
			}
		}
	}

	private static <A> void checkModelGroupUseValidRestriction(final SmComplexType<A> complexType, final SmModelGroupUse<A> R, final SmModelGroupUse<A> B, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		if (equalBounds(R, B) && (R.getTerm() == B.getTerm()))
		{
			// It's the same particle.
		}
		else
		{
			final SmModelGroup<A> groupR = R.getTerm();
			final SmModelGroup<A> groupB = B.getTerm();
			switch (groupR.getCompositor())
			{
				case All:
				{
					switch (groupB.getCompositor())
					{
						case All:
						{
							// throw new AssertionError("TODO 1");
						}
						break;
						case Sequence:
						{
							// throw new AssertionError("TODO 2");
						}
						break;
						case Choice:
						{
							// throw new AssertionError("TODO 3");
						}
						break;
						default:
						{
							throw new AssertionError(groupR.getCompositor());
						}
					}
				}
				break;
				case Sequence:
				{
					switch (groupB.getCompositor())
					{
						case All:
						{
							// throw new AssertionError("TODO 4");
						}
						break;
						case Sequence:
						{
							checkParticleDerivationOKAllAllSequenceSequence(complexType, R, B, errors);
						}
						break;
						case Choice:
						{
							// throw new AssertionError("TODO 6");
						}
						break;
						default:
						{
							throw new AssertionError(groupR.getCompositor());
						}
					}
				}
				break;
				case Choice:
				{
					// throw new AssertionError("TODO 7");
				}
				break;
				default:
				{
					throw new AssertionError(groupR.getCompositor());
				}
			}
		}
	}

	private static <A> void checkNotation(final SmNotation<A> notation, final SmConstraintHandler<A> errors) throws SmAbortException
	{
	}

	private static <A> void checkOccurrenceRangeOK(final SmComplexType<A> complexType, final SmParticle<A> one, final SmParticle<A> two, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		if (one.getMinOccurs() < two.getMinOccurs())
		{
			errors.error(complexType, new SccMinOccursRangeException());
		}
		if (one.isMaxOccursUnbounded())
		{
			// OK
		}
		else
		{
			if (!two.isMaxOccursUnbounded())
			{
				if (one.getMaxOccurs() > two.getMaxOccurs())
				{
					errors.error(complexType, new SccMaxOccursRangeException());
				}
			}
			else
			{
				errors.error(complexType, new SccMaxOccursRangeException());
			}
		}
	}

	private static <A> void checkOccurrences(final SmParticle<A> particle, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		if (!particle.isMaxOccursUnbounded())
		{
			final int minOccurs = particle.getMinOccurs();
			final int maxOccurs = particle.getMaxOccurs();
			if (minOccurs > maxOccurs)
			{
				errors.error(particle, new SccMinOccursLeMaxOccursException(BigInteger.valueOf(minOccurs), BigInteger.valueOf(maxOccurs)));
			}
			if (maxOccurs < 0)
			{
				errors.error(particle, new SccMaxOccursGeOneException(BigInteger.valueOf(maxOccurs)));
			}
		}
	}

	private static <A> void checkParticleDerivationOKAllAllSequenceSequence(final SmComplexType<A> complexType, final SmModelGroupUse<A> R, final SmModelGroupUse<A> B, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		checkOccurrenceRangeOK(complexType, R, B, errors);

		final Iterator<? extends SmParticle<A>> particlesR = R.getTerm().getParticles().iterator();
		final Iterator<? extends SmParticle<A>> particlesB = B.getTerm().getParticles().iterator();
		if (particlesR.hasNext())
		{
			if (particlesB.hasNext())
			{
				final SmParticle<A> particleR = particlesR.next();
				final SmParticle<A> particleB = particlesB.next();
				checkParticleValidRestriction(complexType, particleR, particleB, errors);
			}
			else
			{

			}
		}
		else
		{

		}
	}

	private static <A> void checkParticleValidRestriction(final SmComplexType<A> complexType, final SmParticle<A> R, final SmParticle<A> B, final SmConstraintHandler<A> errors) throws SmAbortException
	{

	}

	/**
	 * Checks the compiled components satisfy Schema Component Constraints. <br>
	 * The cache is used only to provide the locations for error reporting purposes since the locations are not maintained in the compiled schema model.
	 */
	public static <A> void checkSchemaComponentConstraints(final SmComponentBagImpl<A> bag, final SmComponentProvider<A> existing, final AtomBridge<A> atomBridge, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		final NameSource nameBridge = atomBridge.getNameBridge();
		for (final SmSimpleType<A> simpleType : bag.getSimpleTypes())
		{
			checkSimpleType(simpleType, bag, existing, atomBridge, errors);
		}
		for (final SmComplexType<A> complexType : bag.getComplexTypes())
		{
			checkComplexType(complexType, bag, atomBridge, errors);
		}
		for (final SmAttribute<A> attribute : bag.getAttributes())
		{
			checkAttribute(attribute, errors, nameBridge);
		}
		for (final SmElement<A> element : bag.getElements())
		{
			checkElement(element, errors, nameBridge);
		}
		for (final SmAttributeGroup<A> attributeGroup : bag.getAttributeGroups())
		{
			checkAttributeGroup(attributeGroup, errors, nameBridge);
		}
		for (final SmIdentityConstraint<A> constraint : bag.getIdentityConstraints())
		{
			checkIdentityConstraint(constraint, errors);
		}
		for (final SmModelGroup<A> modelGroup : bag.getModelGroups())
		{
			checkModelGroup(modelGroup, errors);
		}
		for (final SmNotation<A> notation : bag.getNotations())
		{
			checkNotation(notation, errors);
		}
	}

	private static <A> void checkSimpleType(final SmSimpleType<A> simpleType, final SmComponentBag<A> bag, final SmComponentProvider<A> existing, final AtomBridge<A> atomBridge, final SmConstraintHandler<A> errors) throws SmAbortException
	{
		final NameSource nameBridge = atomBridge.getNameBridge();
		final SmWhiteSpacePolicy whiteSpace = simpleType.getWhiteSpacePolicy();
		if (null != whiteSpace)
		{
			checkWhitespaceValidRestriction(whiteSpace, simpleType, errors, nameBridge);
		}
		if (simpleType.hasFacets())
		{
			SmLength<A> length = null;
			SmMinLength<A> minLength = null;
			SmMaxLength<A> maxLength = null;
			SmLimit<A> minExclusive = null;
			SmLimit<A> maxExclusive = null;
			SmLimit<A> minInclusive = null;
			SmLimit<A> maxInclusive = null;
			SmTotalDigits<A> totalDigits = null;
			SmFractionDigits<A> fractionDigits = null;
			for (final SmFacet<A> facet : simpleType.getFacets())
			{
				if (facet instanceof SmLength<?>)
				{
					length = (SmLength<A>)facet;
					final SmLength<A> parentLength = (SmLength<A>)(getParentFacet(SmFacetKind.Length, simpleType));
					if (parentLength != null && parentLength.getValue() != length.getValue())
					{
						errors.error(simpleType, new SccLengthRestrictionException(length.getValue(), parentLength.getValue()));
					}
				}
				else if (facet instanceof SmMinLength<?>)
				{
					minLength = (SmMinLength<A>)facet;
					final SmMinLength<A> parentMinLength = (SmMinLength<A>)(getParentFacet(SmFacetKind.MinLength, simpleType));
					if (parentMinLength != null && parentMinLength.getMinLength() > minLength.getMinLength())
					{
						errors.error(simpleType, new SccMinLengthRestrictionException(minLength.getMinLength(), parentMinLength.getMinLength()));
					}
				}
				else if (facet instanceof SmMaxLength<?>)
				{
					maxLength = (SmMaxLength<A>)facet;
					final SmMaxLength<A> parentMaxLength = (SmMaxLength<A>)(getParentFacet(SmFacetKind.MaxLength, simpleType));
					if (parentMaxLength != null && parentMaxLength.getMaxLength() < maxLength.getMaxLength())
					{
						errors.error(simpleType, new SccMaxLengthRestrictionException(maxLength.getMaxLength(), parentMaxLength.getMaxLength()));
					}
				}
				else if (facet instanceof SmLimit<?>)
				{
					final SmLimit<A> parentMinInclusive = (SmLimit<A>)(getParentFacet(SmFacetKind.MinInclusive, simpleType));
					final SmLimit<A> parentMaxInclusive = (SmLimit<A>)(getParentFacet(SmFacetKind.MaxInclusive, simpleType));
					final SmLimit<A> parentMinExclusive = (SmLimit<A>)(getParentFacet(SmFacetKind.MinExclusive, simpleType));
					final SmLimit<A> parentMaxExclusive = (SmLimit<A>)(getParentFacet(SmFacetKind.MaxExclusive, simpleType));
					final SmLimit<A> limit = (SmLimit<A>)facet;

					switch (limit.getKind())
					{
						case MinInclusive:
						{
							minInclusive = limit;
						}
						break;
						case MaxInclusive:
						{
							maxInclusive = limit;
						}
						break;
						case MinExclusive:
						{
							minExclusive = limit;
						}
						break;
						case MaxExclusive:
						{
							maxExclusive = limit;
						}
						break;
						default:
						{
							throw new AssertionError(limit.getKind());
						}
					}
					checkLimitRestriction(limit, parentMaxInclusive, parentMaxExclusive, parentMinInclusive, parentMinExclusive, simpleType, atomBridge, errors);
				}
				else if (facet instanceof SmTotalDigits<?>)
				{
					totalDigits = (SmTotalDigits<A>)facet;
					final SmTotalDigits<A> parentTotalDigits = (SmTotalDigits<A>)(getParentFacet(SmFacetKind.TotalDigits, simpleType));
					if (parentTotalDigits != null && parentTotalDigits.getTotalDigits() < totalDigits.getTotalDigits())
					{
						errors.error(simpleType, new SccTotalDigitsRestrictionException(totalDigits.getTotalDigits(), parentTotalDigits.getTotalDigits()));
					}
				}
				else if (facet instanceof SmFractionDigits<?>)
				{
					fractionDigits = (SmFractionDigits<A>)facet;
					final SmFractionDigits<A> parentFractionDigits = (SmFractionDigits<A>)(getParentFacet(SmFacetKind.FractionDigits, simpleType));
					if (parentFractionDigits != null && parentFractionDigits.getFractionDigits() < fractionDigits.getFractionDigits())
					{
						errors.error(simpleType, new SccFractionDigitsRestrictionException(fractionDigits.getFractionDigits(), parentFractionDigits.getFractionDigits()));
					}
				}
			}
			if (totalDigits != null && fractionDigits != null)
			{
				if (fractionDigits.getFractionDigits() > totalDigits.getTotalDigits())
				{
					errors.error(simpleType, new SccFractionDigitsTotalDigitsException(fractionDigits.getFractionDigits(), totalDigits.getTotalDigits()));
				}
			}
			if (minLength != null && maxLength != null)
			{
				if (minLength.getMinLength() > maxLength.getMaxLength())
				{
					errors.error(simpleType, new SccMinLengthLessThanEqualToMaxLengthException(minLength.getMinLength(), maxLength.getMaxLength()));
				}
			}
			if (maxInclusive != null && maxExclusive != null)
			{
				final String maxIncString = getDisplayString(maxInclusive.getLimit(), atomBridge);
				final String maxExcString = getDisplayString(maxExclusive.getLimit(), atomBridge);
				errors.error(simpleType, new SccMaxInclusiveAndMaxExclusiveException(maxIncString, maxExcString));
			}
			if (minInclusive != null && maxInclusive != null)
			{
				final A min = minInclusive.getLimit();
				final A max = maxInclusive.getLimit();

				try
				{
					minInclusive.validate(max, simpleType);
				}
				catch (final SmFacetMinMaxException e)
				{
					final String minString = getDisplayString(min, atomBridge);
					final String maxString = getDisplayString(max, atomBridge);
					errors.error(simpleType, new SccMinInclusiveLessThanEqualToMaxInclusiveException(minString, maxString));
				}
				try
				{
					maxInclusive.validate(min, simpleType);
				}
				catch (final SmFacetMinMaxException e)
				{
					final String minString = getDisplayString(min, atomBridge);
					final String maxString = getDisplayString(max, atomBridge);
					errors.error(simpleType, new SccMinInclusiveLessThanEqualToMaxInclusiveException(minString, maxString));
				}
			}
			if (minExclusive != null && maxExclusive != null)
			{
				checkMinExclusiveLessThanEqualToMaxExclusive(minExclusive, maxExclusive, simpleType, atomBridge, errors);
			}
			// If length is a member of {facets} then
			if (length != null)
			{
				if (minLength != null)
				{
					if (minLength.getMinLength() <= length.getValue())
					{
						if (!existsTypeDerivedByRestrictionWithMinLength(simpleType, minLength.getMinLength()))
						{
							errors.error(simpleType, new SccMinLengthDerivedException(minLength.getMinLength(), length.getValue(), qname(simpleType.getName())));
						}
					}
					else
					{
						errors.error(simpleType, new SccMinLengthCompatibleException());
					}
				}
				if (maxLength != null)
				{
					if (maxLength.getMaxLength() >= length.getValue())
					{
						if (!existsTypeDerivedByRestrictionWithMaxLength(simpleType, maxLength.getMaxLength()))
						{
							errors.error(simpleType, new SccMaxLengthDerivedException(maxLength.getMaxLength(), length.getValue(), qname(simpleType.getName())));
						}
					}
					else
					{
						errors.error(simpleType, new SccMaxLengthCompatibleException());
					}
				}
			}
		}
		final SmSimpleType<A> notationType = existing.getAtomicType(SmNativeType.NOTATION);
		if (simpleType.derivedFromType(notationType, EnumSet.of(SmDerivationMethod.Restriction)))
		{
			for (final SmEnumeration<A> enumeration : simpleType.getEnumerations())
			{
				for (final A atom : enumeration.getValue())
				{
					final QName reference = atomBridge.getNotation(atom);

					boolean match = false;
					for (final SmNotation<A> notation : bag.getNotations())
					{
						if (reference.equals(notation.getName()))
						{
							match = true;
							break;
						}
					}
					if (!match)
					{
						final Set<QName> names = getComponentNames(bag.getNotations());
						errors.error(simpleType, new SccNotationNotInValueSpace(reference, names));
					}
				}
			}
		}
	}

	private static <A> void checkTypeDerivationOK(final SmType<A> D, final SmType<A> B, final Set<SmDerivationMethod> subset, NameSource nameBridge) throws SmComponentConstraintException
	{
		if (D instanceof SmSimpleType<?>)
		{
			checkTypeDerivationOKSimple((SmSimpleType<A>)D, B, subset, nameBridge);
		}
		else if (D instanceof SmComplexType<?>)
		{
			checkTypeDerivationOKComplex((SmComplexType<A>)D, B, subset, nameBridge);
		}
		else
		{
			throw new AssertionError(D);
		}
	}

	public static <A> void checkTypeDerivationOKComplex(final SmComplexType<A> D, final SmType<A> B, final Set<SmDerivationMethod> subset, NameSource nameBridge) throws SmComponentConstraintException
	{
		if (D == B)
		{
			// They are the same type definition.
		}
		else if (D.isComplexUrType())
		{
			throw new SccComplexTypeDerivationHierarchyException(D, B, subset);
		}
		else
		{
			if (subset.contains(D.getDerivationMethod()))
			{
				throw new SccComplexTypeDerivationMethodException(D, B, subset);
			}

			final SmType<A> deeBaseType = D.getBaseType();
			if (deeBaseType == B)
			{
				// B is D's {base type definition}
			}
			else
			{
				if (deeBaseType.isComplexUrType())
				{
					throw new SccComplexTypeBaseUrTypeException(D, B, subset);
				}
				else
				{
					if (deeBaseType instanceof SmComplexType<?>)
					{
						try
						{
							checkTypeDerivationOK(deeBaseType, B, subset, nameBridge);
						}
						catch (final SmComponentConstraintException e)
						{
							throw new SccComplexTypeBaseComplexDerivationException(D, B, subset, e);
						}
					}
					else if (deeBaseType instanceof SmSimpleType<?>)
					{
						try
						{
							checkTypeDerivationOKSimple((SmSimpleType<A>)deeBaseType, B, subset, nameBridge);
						}
						catch (final SmComponentConstraintException e)
						{
							throw new SccComplexTypeBaseSimpleDerivationException(D, B, subset, e);
						}
					}
					else
					{
						throw new AssertionError(deeBaseType);
					}
				}
			}
		}
	}

	/**
	 * Type Derivation OK (Simple) (3.14.6)
	 */
	public static <A> void checkTypeDerivationOKSimple(final SmSimpleType<A> D, final SmType<A> B, final Set<SmDerivationMethod> subset, final NameSource nameBridge) throws SmComponentConstraintException
	{
		if (D == B)
		{
			// They are the same type definition.
		}
		else
		{
			if (subset.contains(SmDerivationMethod.Restriction) || (D.getBaseType().getFinal().contains(SmDerivationMethod.Restriction)))
			{
				throw new SccSimpleTypeDerivationRestrictionException(qname(D.getName()));
			}

			boolean isOK = false;
			if (D.getBaseType() == B)
			{
				isOK = true;
			}
			else if (!D.getBaseType().isComplexUrType() && isTypeDerivationOK(D.getBaseType(), B, subset, nameBridge))
			{
				isOK = true;
			}
			else if ((D instanceof SmListSimpleType<?> || D instanceof SmUnionSimpleType<?>) && B.isSimpleUrType())
			{
				isOK = true;
			}
			else if (B instanceof SmUnionSimpleType<?>)
			{
				final SmUnionSimpleType<A> unionType = (SmUnionSimpleType<A>)B;
				for (final SmSimpleType<A> memberType : unionType.getMemberTypes())
				{
					if (isTypeDerivationOK(D, memberType, subset, nameBridge))
					{
						isOK = true;
						break;
					}
				}
			}
			if (!isOK)
			{
				throw new SccSimpleTypeDerivationException(qname(D.getName()));
			}
		}
	}

	private static <A> void checkWhitespaceValidRestriction(final SmWhiteSpacePolicy ws, final SmSimpleType<A> simpleType, final SmConstraintHandler<A> errors, final NameSource nameBridge) throws SmAbortException
	{
		final SmType<A> baseType = simpleType.getBaseType();
		if (baseType instanceof SmSimpleType<?>)
		{
			final SmSimpleType<A> parent = (SmSimpleType<A>)baseType;
			final SmWhiteSpacePolicy pws = parent.getWhiteSpacePolicy();
			if (null != pws)
			{
				if (pws.isCollapse())
				{
					if (ws.isReplace() || ws.isPreserve())
					{
						errors.error(simpleType, new SccWhiteSpaceParentCollapseException());
					}
				}
				else if (pws.isReplace())
				{
					if (ws.isPreserve())
					{
						errors.error(simpleType, new SccWhiteSpaceParentReplaceException());
					}
				}
			}
		}
	}

	private static <A> boolean clause14(final SmComplexType<A> complexType, final SmComplexType<A> baseType)
	{
		// 1.4 One of the following must be true.
		final SmContentType<A> contentTypeD = complexType.getContentType();
		final SmContentType<A> contentTypeB = baseType.getContentType();

		return clause141(contentTypeD, contentTypeB) || clause142(contentTypeD, contentTypeB) || clause143(contentTypeD, contentTypeB);
	}

	private static <A> boolean clause141(final SmContentType<A> contentTypeD, final SmContentType<A> contentTypeB)
	{
		if (contentTypeD.isSimple() && contentTypeB.isSimple())
		{
			return contentTypeD.getSimpleType() == contentTypeB.getSimpleType();
		}
		else
		{
			return false;
		}
	}

	private static <A> boolean clause142(final SmContentType<A> contentTypeD, final SmContentType<A> contentTypeB)
	{
		return contentTypeD.isEmpty() && contentTypeB.isEmpty();
	}

	private static <A> boolean clause143(final SmContentType<A> contentTypeD, final SmContentType<A> contentTypeB)
	{
		if (clause1431(contentTypeD))
		{
			return clause1432(contentTypeD, contentTypeB);
		}
		else
		{
			return false;
		}
	}

	private static <A> boolean clause1431(final SmContentType<A> contentTypeD)
	{
		return contentTypeD.isMixed() || contentTypeD.isElementOnly();
	}

	private static <A> boolean clause1432(final SmContentType<A> contentTypeD, final SmContentType<A> contentTypeB)
	{
		return clause14321(contentTypeB) || clause14322(contentTypeD, contentTypeB);
	}

	private static <A> boolean clause14321(final SmContentType<A> contentTypeB)
	{
		return contentTypeB.isEmpty();
	}

	private static <A> boolean clause14322(final SmContentType<A> contentTypeD, final SmContentType<A> contentTypeB)
	{
		if (clause143221(contentTypeD, contentTypeB))
		{
			final SmModelGroupUse<A> contentModelD = contentTypeD.getContentModel();
			final SmModelGroupUse<A> contentModelB = contentTypeB.getContentModel();
			return clause143222(contentModelD, contentModelB);
		}
		else
		{
			return false;
		}
	}

	private static <A> boolean clause143221(final SmContentType<A> contentTypeD, final SmContentType<A> contentTypeB)
	{
		if (contentTypeD.isMixed() && contentTypeB.isMixed())
		{
			return true;
		}
		else if (contentTypeD.isElementOnly() && contentTypeB.isElementOnly())
		{
			return true;
		}
		return false;
	}

	private static <A> boolean clause143222(final SmModelGroupUse<A> contentModelD, final SmModelGroupUse<A> contentModelB)
	{
		return isValidParticleExtension(contentModelD, contentModelB);
	}

	private static <A> boolean clause21(final SmContentType<A> contentTypeD, final SmSimpleType<A> simpleTypeB)
	{
		if (contentTypeD.isSimple())
		{
			return contentTypeD.getSimpleType() == simpleTypeB;
		}
		else
		{
			return false;
		}
	}

	private static <A> boolean clause22(final SmSimpleType<A> simpleTypeB)
	{
		return !simpleTypeB.isFinal(SmDerivationMethod.Extension);
	}

	private static <A> boolean clause52(final SmComplexType<A> complexType, final SmComplexType<A> baseType)
	{
		// 5.2 All the following must be true.
		return contentTypeMustBeSimple521(complexType) && clause522(complexType, baseType);
	}

	private static <A> boolean clause522(final SmComplexType<A> complexType, final SmComplexType<A> baseType)
	{
		return clause5221(complexType, baseType) || isMixedWithEmptiableParticle5222(baseType);

	}

	private static <A> boolean clause5221(final SmComplexType<A> complexType, final SmComplexType<A> baseType)
	{
		// 5.2.2.1 The {content type} of the {base type definition} must be a
		// simple type definition from which the
		// {content type} is validly derived given the empty set as defined in
		// Type Derivation OK (Simple) (3.14.6).
		final SmContentType<A> contentType = baseType.getContentType();
		if (contentType.isSimple())
		{
			return isSimpleDerivationOK(complexType, baseType);
		}
		else
		{
			return false;
		}
	}

	private static <A> boolean clause53(final SmComplexType<A> complexType, final SmComplexType<A> baseType)
	{
		return complexType.getContentType().isEmpty() && clause532(baseType);
	}

	private static <A> boolean clause532(final SmComplexType<A> baseType)
	{
		return clause5321(baseType) || clause5322(baseType);
	}

	private static <A> boolean clause5321(final SmComplexType<A> baseType)
	{
		return baseType.getContentType().isEmpty();
	}

	private static <A> boolean clause5322(final SmComplexType<A> baseType)
	{
		final SmContentType<A> contentType = baseType.getContentType();
		if (contentType.isElementOnly() || contentType.isMixed())
		{
			return contentType.getContentModel().isEmptiable();
		}
		else
		{
			return false;
		}
	}

	private static <A> SmCompareKind compare(final A lhsAtom, final A rhsAtom, final AtomBridge<A> atomBridge)
	{
		final SmNativeType lhsNativeType = atomBridge.getNativeType(lhsAtom);
		if (lhsNativeType.isInt())
		{
			final int lhs = atomBridge.getInt(lhsAtom);
			final int rhs = atomBridge.getInt(castUp(rhsAtom, SmNativeType.INT, atomBridge));
			if (lhs > rhs)
			{
				return SmCompareKind.AFTER;
			}
			else if (lhs < rhs)
			{
				return SmCompareKind.BEFORE;
			}
			else
			{
				return SmCompareKind.EQUAL;
			}
		}
		else if (lhsNativeType.isInteger())
		{
			final BigInteger lhs = atomBridge.getInteger(lhsAtom);
			final BigInteger rhs = atomBridge.getInteger(castUp(rhsAtom, SmNativeType.INTEGER, atomBridge));
			return SmCompareKind.lookup(lhs.compareTo(rhs));
		}
		else if (lhsNativeType == SmNativeType.DECIMAL)
		{
			final BigDecimal lhs = atomBridge.getDecimal(lhsAtom);
			final BigDecimal rhs = atomBridge.getDecimal(castUp(rhsAtom, SmNativeType.DECIMAL, atomBridge));
			return SmCompareKind.lookup(lhs.compareTo(rhs));
		}
		else if (lhsNativeType == SmNativeType.FLOAT)
		{
			final float lhs = atomBridge.getFloat(lhsAtom);
			// final float lhs = atomBridge.getFloat(castUp(lhsAtom,
			// SmNativeType.FLOAT, atomBridge));
			final float rhs = atomBridge.getFloat(castUp(rhsAtom, SmNativeType.FLOAT, atomBridge));
			if (lhs > rhs)
			{
				return SmCompareKind.AFTER;
			}
			else if (lhs < rhs)
			{
				return SmCompareKind.BEFORE;
			}
			else
			{
				return SmCompareKind.EQUAL;
			}
		}
		else if (lhsNativeType == SmNativeType.DOUBLE)
		{
			final double lhs = atomBridge.getDouble(lhsAtom);
			final double rhs = atomBridge.getDouble(rhsAtom);
			if (lhs > rhs)
			{
				return SmCompareKind.AFTER;
			}
			else if (lhs < rhs)
			{
				return SmCompareKind.BEFORE;
			}
			else if (lhs == rhs)
			{
				return SmCompareKind.EQUAL;
			}
			else
			{
				throw new AssertionError("lhs=" + lhs + ", rhs=" + rhs);
			}
		}
		else if (lhsNativeType == SmNativeType.DATE)
		{
			final Gregorian P = normalize(Gregorian.dateTime(castUp(lhsAtom, SmNativeType.DATE, atomBridge), atomBridge));
			final Gregorian Q = normalize(Gregorian.dateTime(castUp(rhsAtom, SmNativeType.DATE, atomBridge), atomBridge));
			if (P.hasGmtOffset())
			{
				if (Q.hasGmtOffset())
				{
					return compareFields(P, Q);
				}
				else
				{
					if (compareFields(P, Q.normalize(+FOURTEEN_HOURS_IN_MINUTES)) == SmCompareKind.BEFORE)
					{
						return SmCompareKind.BEFORE;
					}
					else if (compareFields(P, Q.normalize(-FOURTEEN_HOURS_IN_MINUTES)) == SmCompareKind.AFTER)
					{
						return SmCompareKind.AFTER;
					}
					else
					{
						return SmCompareKind.INDETERMINATE;
					}
				}
			}
			else
			{
				if (Q.hasGmtOffset())
				{
					if (compareFields(P.normalize(-FOURTEEN_HOURS_IN_MINUTES), Q) == SmCompareKind.BEFORE)
					{
						return SmCompareKind.BEFORE;
					}
					else if (compareFields(P.normalize(+FOURTEEN_HOURS_IN_MINUTES), Q) == SmCompareKind.AFTER)
					{
						return SmCompareKind.AFTER;
					}
					else
					{
						return SmCompareKind.INDETERMINATE;
					}
				}
				else
				{
					return compareFields(P, Q);
				}
			}
		}
		else
		{
			throw new AssertionError(lhsNativeType);
		}
	}

	private static SmCompareKind compareFields(final Gregorian P, final Gregorian Q)
	{
		if (P.hasYear())
		{
			if (Q.hasYear())
			{
				final int lhs = P.getYear();
				final int rhs = Q.getYear();
				if (lhs > rhs)
				{
					return SmCompareKind.AFTER;
				}
				else if (lhs < rhs)
				{
					return SmCompareKind.BEFORE;
				}
			}
			else
			{
				return SmCompareKind.INDETERMINATE;
			}
		}
		else
		{
			if (Q.hasYear())
			{
				return SmCompareKind.INDETERMINATE;
			}
		}
		if (P.hasMonth())
		{
			if (Q.hasMonth())
			{
				final int lhs = P.getMonth();
				final int rhs = Q.getMonth();
				if (lhs > rhs)
				{
					return SmCompareKind.AFTER;
				}
				else if (lhs < rhs)
				{
					return SmCompareKind.BEFORE;
				}
			}
			else
			{
				return SmCompareKind.INDETERMINATE;
			}
		}
		else
		{
			if (Q.hasMonth())
			{
				return SmCompareKind.INDETERMINATE;
			}
		}
		if (P.hasDay())
		{
			if (Q.hasDay())
			{
				final int lhs = P.getDay();
				final int rhs = Q.getDay();
				if (lhs > rhs)
				{
					return SmCompareKind.AFTER;
				}
				else if (lhs < rhs)
				{
					return SmCompareKind.BEFORE;
				}
			}
			else
			{
				return SmCompareKind.INDETERMINATE;
			}
		}
		else
		{
			if (Q.hasDay())
			{
				return SmCompareKind.INDETERMINATE;
			}
		}
		return SmCompareKind.EQUAL;
	}

	public static Gregorian normalize(final Gregorian gregorian)
	{
		if (gregorian.hasGmtOffset())
		{
			return gregorian.normalize(0);
		}
		else
		{
			return gregorian;
		}
	}

	private static <A> boolean contentTypeMustBeSimple521(final SmComplexType<A> complexType)
	{
		return complexType.getContentType().isSimple();
	}

	private static <A> boolean equalBounds(final SmParticle<A> one, final SmParticle<A> two)
	{
		if (one.getMinOccurs() == two.getMinOccurs())
		{
			if (one.isMaxOccursUnbounded())
			{
				return two.isMaxOccursUnbounded();
			}
			else
			{
				if (two.isMaxOccursUnbounded())
				{
					return false;
				}
				else
				{
					return one.getMaxOccurs() == two.getMaxOccurs();
				}
			}
		}
		else
		{
			return false;
		}
	}

	public static <A> boolean equalValues(final List<? extends A> expect, final List<? extends A> actual)
	{
		final int size = expect.size();
		if (size == actual.size())
		{
			for (int index = 0; index < size; index++)
			{
				if (!expect.get(index).equals(actual.get(index)))
				{
					return false;
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	private static <A> boolean existsTypeDerivedByRestrictionWithMaxLength(final SmSimpleType<A> simpleType, int maxLength)
	{
		if (simpleType.getDerivationMethod().isRestriction())
		{
			SmType<A> currentType = simpleType.getBaseType();
			while (!currentType.isComplexUrType())
			{
				if (currentType instanceof SmSimpleType<?>)
				{
					if (hasMaxLengthAndNoLength((SmSimpleType<A>)currentType, maxLength))
					{
						return true;
					}
					else
					{
						if (currentType.getDerivationMethod().isRestriction())
						{
							currentType = currentType.getBaseType();
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					return false;
				}
			}
			return false;
		}
		else
		{
			return false;
		}
	}

	private static <A> boolean existsTypeDerivedByRestrictionWithMinLength(final SmSimpleType<A> simpleType, int minLength)
	{
		if (simpleType.getDerivationMethod().isRestriction())
		{
			SmType<A> currentType = simpleType.getBaseType();
			while (!currentType.isComplexUrType())
			{
				if (currentType instanceof SmSimpleType<?>)
				{
					if (hasMinLengthAndNoLength((SmSimpleType<A>)currentType, minLength))
					{
						return true;
					}
					else
					{
						if (currentType.getDerivationMethod().isRestriction())
						{
							currentType = currentType.getBaseType();
						}
						else
						{
							return false;
						}
					}
				}
				else
				{
					return false;
				}
			}
			return false;
		}
		else
		{
			return false;
		}
	}

	private static <A> Set<QName> getComponentNames(final Iterable<? extends SmComponent<A>> components)
	{
		final Set<QName> names = new HashSet<QName>();
		for (final SmComponent<A> component : components)
		{
			names.add(component.getName());
		}
		return Collections.unmodifiableSet(names);
	}

	public static <A> String getDisplayString(final A atom, final AtomBridge<A> atomBridge)
	{
		if (null != atom)
		{
			final StringBuilder buffer = new StringBuilder();

			append(atom, buffer, atomBridge);

			return buffer.toString();
		}
		else
		{
			return "()";
		}
	}

	public static <A> String getDisplayString(final List<? extends A> value, final AtomBridge<A> atomBridge)
	{
		final Iterator<? extends A> atoms = value.iterator();

		if (atoms.hasNext())
		{
			final A first = atoms.next();

			if (atoms.hasNext())
			{
				final StringBuilder buffer = new StringBuilder();

				buffer.append('(');

				append(first, buffer, atomBridge);

				while (atoms.hasNext())
				{
					buffer.append(", ");

					append(atoms.next(), buffer, atomBridge);
				}

				buffer.append(')');

				return buffer.toString();
			}
			else
			{
				return getDisplayString(first, atomBridge);
			}
		}
		else
		{
			return "()";
		}
	}

	/**
	 * Searches up the inheritance tree and returns to first facet of the requested kind, or null if no such facet is inherited
	 * 
	 * @param facetKind
	 * @param simpleType
	 * @return the requested, inherited facet or null if no such facet exists
	 */
	private static <A> SmFacet<A> getParentFacet(final SmFacetKind facetKind, final SmSimpleType<A> simpleType)
	{
		SmType<A> baseType = simpleType.getBaseType();
		SmFacet<A> parentFacet = null;
		while (baseType instanceof SmSimpleType<?> && parentFacet == null)
		{
			final SmSimpleType<A> parent = (SmSimpleType<A>)baseType;
			if (parent.hasFacets())
			{
				parentFacet = parent.getFacetOfKind(facetKind);
			}
			if (parentFacet == null)
			{
				baseType = parent.getBaseType();
			}
		}
		return parentFacet;
	}

	private static <A> boolean hasMaxLengthAndNoLength(final SmSimpleType<A> simpleType, final int maxLength)
	{
		if (simpleType.hasFacets())
		{
			if (simpleType.getFacetOfKind(SmFacetKind.Length) != null)
			{
				return false;
			}
			else
			{
				SmMaxLength<A> facet = (SmMaxLength<A>)(simpleType.getFacetOfKind(SmFacetKind.MaxLength));
				if (facet != null)
				{
					return facet.getMaxLength() == maxLength;
				}
			}
			return false;
		}
		else
		{
			// No facets, no maxLength.
			return false;
		}

	}

	private static <A> boolean hasMinLengthAndNoLength(final SmSimpleType<A> simpleType, final int minLength)
	{
		if (simpleType.hasFacets())
		{
			if (simpleType.getFacetOfKind(SmFacetKind.Length) != null)
			{
				return false;
			}
			else
			{
				SmMinLength<A> facet = (SmMinLength<A>)(simpleType.getFacetOfKind(SmFacetKind.MinLength));
				if (facet != null)
				{
					return facet.getMinLength() == minLength;
				}
			}
			return false;
		}
		else
		{
			// No facets, no minLength.
			return false;
		}
	}

	private static <A> boolean isDerivedFrom(final SmType<A> type, final QName typeName)
	{
		SmType<A> currentType = type;
		while (!currentType.isComplexUrType())
		{
			if (currentType.getName().equals(typeName))
			{
				return true;
			}
			currentType = currentType.getBaseType();
		}
		return false;
	}

	private static <A> boolean isMixedWithEmptiableParticle5222(final SmComplexType<A> type)
	{
		final SmContentType<A> contentType = type.getContentType();
		if (contentType.isMixed())
		{
			return contentType.getContentModel().isEmptiable();
		}
		else
		{
			return false;
		}
	}

	private static <A> boolean isSimpleDerivationOK(final SmType<A> complexType, final SmType<A> baseType)
	{
		// TODO See checkTypeDerivationSimple
		complexType.getName();
		baseType.getName();
		// TODO:
		return true;
	}

	private static <A> boolean isTypeDerivationOK(final SmType<A> D, final SmType<A> B, final Set<SmDerivationMethod> subset, NameSource nameBridge)
	{
		try
		{
			checkTypeDerivationOK(D, B, subset, nameBridge);
		}
		catch (final SmException e)
		{
			return false;
		}
		return true;
	}

	/**
	 * Particle Valid (Extension) (3.9.6)
	 * 
	 * @param E
	 *            The extension particle.
	 * @param B
	 *            The base particle.
	 */
	private static <A> boolean isValidParticleExtension(final SmModelGroupUse<A> E, final SmModelGroupUse<A> B)
	{
		final int minOccurs = E.getMinOccurs();
		final int maxOccurs = E.getMaxOccurs();

		if (minOccurs == B.getMinOccurs() && maxOccurs == B.getMaxOccurs() && (E.getTerm() == B.getTerm()))
		{
			// It's the same particle.
			return true;

		}
		else if (1 == minOccurs && 1 == maxOccurs)
		{
			final SmModelGroup<A> groupE = E.getTerm();
			if (groupE.getCompositor().isSequence())
			{
				final Iterator<? extends SmParticle<A>> particles = groupE.getParticles().iterator();
				if (particles.hasNext())
				{
					final SmParticle<A> firstMember = particles.next();
					if (firstMember instanceof SmModelGroupUse<?>)
					{
						final SmModelGroupUse<A> F = (SmModelGroupUse<A>)firstMember;
						return recursivelyIdenticalProperties(F, B);
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	private static QName qname(final QName name)
	{
		return name;
	}

	private static <A> boolean recursivelyIdenticalElements(final SmElement<A> one, final SmElement<A> two)
	{
		if (one == two)
		{
			return true;
		}
		else
		{
			if (one.getName().equals(two.getName()))
			{
				if (one.getType().getName().equals(two.getType().getName()))
				{
					throw new AssertionError("!@#$%^");
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
	}

	private static <A> boolean recursivelyIdenticalModelGroups(final SmModelGroup<A> one, final SmModelGroup<A> two)
	{
		if (one == two)
		{
			return true;
		}
		else
		{
			if (one.getCompositor() == two.getCompositor())
			{
				final Iterator<? extends SmParticle<A>> particlesOne = one.getParticles().iterator();
				final Iterator<? extends SmParticle<A>> particlesTwo = two.getParticles().iterator();
				if (particlesOne.hasNext())
				{
					if (particlesTwo.hasNext())
					{
						final SmParticle<A> particleOne = particlesOne.next();
						final SmParticle<A> particleTwo = particlesTwo.next();

						return recursivelyIdenticalProperties(particleOne, particleTwo);
					}
					else
					{
						return false;
					}
				}
				else
				{
					return !particlesTwo.hasNext();
				}
			}
			else
			{
				return false;
			}
		}
	}

	private static <A> boolean recursivelyIdenticalParticleTerms(final SmParticleTerm<A> one, final SmParticleTerm<A> two)
	{
		if (one == two)
		{
			return true;
		}
		else
		{
			if (one instanceof SmElement<?> && two instanceof SmElement<?>)
			{
				return recursivelyIdenticalElements((SmElement<A>)one, (SmElement<A>)two);
			}
			else if (one instanceof SmModelGroup<?> && two instanceof SmModelGroup<?>)
			{
				return recursivelyIdenticalModelGroups((SmModelGroup<A>)one, (SmModelGroup<A>)two);
			}
			else if (one instanceof SmWildcard<?> && two instanceof SmWildcard<?>)
			{
				return recursivelyIdenticalWildcards((SmWildcard<A>)one, (SmWildcard<A>)two);
			}
			else
			{
				return false;
			}
		}
	}

	private static <A> boolean recursivelyIdenticalProperties(final SmModelGroupUse<A> one, final SmModelGroupUse<A> two)
	{
		if (one == two)
		{
			return true;
		}
		else
		{
			if (one.getMinOccurs() == two.getMinOccurs() && one.getMaxOccurs() == two.getMaxOccurs())
			{
				return recursivelyIdenticalModelGroups(one.getTerm(), two.getTerm());
			}
			else
			{
				return false;
			}
		}
	}

	private static <A> boolean recursivelyIdenticalProperties(final SmParticle<A> one, final SmParticle<A> two)
	{
		if (one == two)
		{
			return true;
		}
		else
		{
			if (one.getMinOccurs() == two.getMinOccurs() && one.getMaxOccurs() == two.getMaxOccurs())
			{
				return recursivelyIdenticalParticleTerms(one.getTerm(), two.getTerm());
			}
			else
			{
				return false;
			}
		}
	}

	private static <A> boolean recursivelyIdenticalWildcards(final SmWildcard<A> one, final SmWildcard<A> two)
	{
		// TODO:
		return true;
	}

	private static <A> A upcast(final A sourceAtom, final A baseAtom, final AtomBridge<A> atomBridge) throws GxmlAtomCastException
	{
		final QName baseType = atomBridge.getDataType(baseAtom);
		final A atom = atomBridge.upCast(sourceAtom);
		final QName atomType = atomBridge.getDataType(atom);
		if (baseType.equals(atomType))
		{
			return atom;
		}
		else
		{
			return upcast(atom, baseAtom, atomBridge);
		}
	}
}
