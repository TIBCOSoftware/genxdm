/*
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

import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.xs.simple.Gregorian;
import org.genxdm.exceptions.AtomCastException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.processor.w3c.xs.exception.scc.SccAllGroupAppearsException;
import org.genxdm.processor.w3c.xs.exception.scc.SccAttributeDeclarationTypeIDConflictsValueConstraintException;
import org.genxdm.processor.w3c.xs.exception.scc.SccAttributeDerivationRequiredConflictException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeAttributeFixedOverrideException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeAttributeUniquenessException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeBaseComplexDerivationException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeBaseSimpleDerivationException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeBaseUrTypeException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeDerivationHierarchyException;
import org.genxdm.processor.w3c.xs.exception.scc.SccComplexTypeDerivationMethodException;
import org.genxdm.processor.w3c.xs.exception.scc.SccContentTypeMismatchBaseTypeContainsExtensionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccDerivationExtensionContentTypeException;
import org.genxdm.processor.w3c.xs.exception.scc.SccDerivationExtensionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccDerivationRestrictionContentTypeException;
import org.genxdm.processor.w3c.xs.exception.scc.SccElementDeclarationDerivedFromIDWithValueConstraintException;
import org.genxdm.processor.w3c.xs.exception.scc.SccElementDeclarationSubstitutionGroupTypeDerivationException;
import org.genxdm.processor.w3c.xs.exception.scc.SccFinalOfBaseTypeContainsExtensionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccFinalOfBaseTypeContainsRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccFractionDigitsRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccFractionDigitsTotalDigitsException;
import org.genxdm.processor.w3c.xs.exception.scc.SccIdentityConstraintKeyrefFieldsCardinalityException;
import org.genxdm.processor.w3c.xs.exception.scc.SccIdentityConstraintKeyrefReferenceException;
import org.genxdm.processor.w3c.xs.exception.scc.SccLengthRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMaxExclusionRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMaxInclusionRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMaxInclusiveAndMaxExclusiveException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMaxLengthCompatibleException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMaxLengthDerivedException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMaxLengthRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMaxOccursGeOneException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMaxOccursRangeException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMinExclusionRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMinInclusionRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMinInclusiveLessThanEqualToMaxInclusiveException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMinLengthCompatibleException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMinLengthDerivedException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMinLengthLessThanEqualToMaxLengthException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMinLengthRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMinOccursLeMaxOccursException;
import org.genxdm.processor.w3c.xs.exception.scc.SccMinOccursRangeException;
import org.genxdm.processor.w3c.xs.exception.scc.SccNotationNotInValueSpace;
import org.genxdm.processor.w3c.xs.exception.scc.SccSimpleTypeDerivationException;
import org.genxdm.processor.w3c.xs.exception.scc.SccSimpleTypeDerivationRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccTotalDigitsRestrictionException;
import org.genxdm.processor.w3c.xs.exception.scc.SccWhiteSpaceParentCollapseException;
import org.genxdm.processor.w3c.xs.exception.scc.SccWhiteSpaceParentReplaceException;
import org.genxdm.processor.w3c.xs.exception.scc.SccXmlnsNotAllowedException;
import org.genxdm.processor.w3c.xs.exception.scc.SccXsiNotAllowedException;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.components.ParticleTerm;
import org.genxdm.xs.components.SchemaComponent;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.components.SchemaWildcard;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.constraints.ModelGroupUse;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.ComponentConstraintException;
import org.genxdm.xs.exceptions.FacetException;
import org.genxdm.xs.exceptions.SchemaConstraintHandler;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.facets.Facet;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.FractionDigits;
import org.genxdm.xs.facets.Length;
import org.genxdm.xs.facets.Limit;
import org.genxdm.xs.facets.MaxLength;
import org.genxdm.xs.facets.MinLength;
import org.genxdm.xs.facets.TotalDigits;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.ListSimpleType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;
import org.genxdm.xs.types.UnionSimpleType;


/**
 * Utility for checking Schema Component Constraints.
 */
final class SchemaConstraintChecker
{
    // TODO: the basic architecture here *sucks*. "i like procedural programming with libraries"
    // TODO: look for commented-out bits.  they're disabled and broken.  Probably fix technique:
    // create a canonical atom bridge for use here only, and use it just as it's shown in use.
    private static int FOURTEEN_HOURS_IN_MINUTES = 840;
    
    SchemaConstraintChecker(final ComponentBag bag, final ComponentProvider existing)
    {
        this.components = PreCondition.assertNotNull(bag, "bag");
        this.provider = PreCondition.assertNotNull(existing, "provider");
        this.atomBridge = new CanonicalAtomBridge(provider);
    }

    private void append(final XmlAtom atom, final StringBuilder buffer)
    {
        final QName typeName = atomBridge.getDataType(atom);

        final String displayString = atomBridge.getC14NForm(atom);

        final String localName = typeName.getLocalPart();

        buffer.append(localName.toString());
        buffer.append("('");
        buffer.append(displayString);
        buffer.append("')");
    }

    private XmlAtom castUp(final XmlAtom sourceAtom, final NativeType targetType)
    {
        // TODO: This has the hallmark of a helper function?
        switch (targetType)
        {
            case INTEGER:
            {
                return atomBridge.createInteger(atomBridge.getInteger(sourceAtom));
            }
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

    private void checkAttribute(final AttributeDefinition attribute, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        checkAttributeDeclarationPropertiesCorrect(attribute, errors);

        checkAttributeForXmlnsNotAllowed(attribute, errors);

        checkAttributeForXsiNotAllowed(attribute, errors);
    }

    private void checkAttributeDeclarationPropertiesCorrect(final AttributeDefinition attribute, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        final SimpleType attributeType = (SimpleType)attribute.getType();
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

    private void checkAttributeDerivationRestrictionComplexType(final ComplexType complexType, final ComplexType baseType, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        final HashMap<QName, AttributeUse> attributes = new HashMap<QName, AttributeUse>();
        for (final AttributeUse attributeUse : complexType.getAttributeUses().values())
        {
            final AttributeDefinition attribute = attributeUse.getAttribute();
            final QName attributeName = attribute.getName();

            if (attributes.containsKey(attributeName))
            {
                errors.error(complexType, new SccComplexTypeAttributeUniquenessException(complexType.getName(), attributeName));
            }
            else
            {
                attributes.put(attributeName, attributeUse);
            }

            if (attribute.getScopeExtent() != ScopeExtent.Global)
            {
                checkAttribute(attribute, errors);
            }
        }

        for (final AttributeUse B : baseType.getAttributeUses().values())
        {
            final QName attributeName = B.getAttribute().getName();
            if (attributes.containsKey(attributeName))
            {
                final AttributeUse R = attributes.get(attributeName);
                if (!R.isRequired() && B.isRequired())
                {
                    errors.error(complexType, new SccAttributeDerivationRequiredConflictException(complexType.getName(), attributeName));
                }

                final ValueConstraint vcB = B.getEffectiveValueConstraint();
                if (null == vcB || vcB.getVariety().isDefault())
                {
                    // OK
                }
                else
                {
                    final ValueConstraint vcR = R.getEffectiveValueConstraint();
                    if (null != vcR)
                    {
                        if (vcR.getVariety().isFixed())
                        {
                            final List<XmlAtom> vB = vcB.getValue(atomBridge);
                            final List<XmlAtom> vR = vcR.getValue(atomBridge);
                            if (!equalValues(vR, vB))
                            {
                                // TODO: Wrong Exception
                                errors.error(complexType, new SccAttributeDerivationRequiredConflictException(complexType.getName(), attributeName));
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

    private void checkAttributeForXmlnsNotAllowed(final AttributeDefinition attribute, final SchemaConstraintHandler errors) 
        throws AbortException
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

    private void checkAttributeForXsiNotAllowed(final AttributeDefinition attribute, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI == attribute.getName().getNamespaceURI())
        {
            errors.error(attribute, new SccXsiNotAllowedException(attribute.getName()));
        }
    }

    private void checkAttributeGroup(final AttributeGroupDefinition attributeGroup, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        if (attributeGroup.hasAttributeUses())
        {
            final HashSet<QName> unique = new HashSet<QName>();
            for (final AttributeUse attributeUse : attributeGroup.getAttributeUses())
            {
                final AttributeDefinition attribute = attributeUse.getAttribute();
                final QName attributeName = attribute.getName();

                if (unique.contains(attributeName))
                {
                    errors.error(attributeGroup, new SccComplexTypeAttributeUniquenessException(attributeGroup.getName(), attributeName));
                }
                else
                {
                    unique.add(attributeName);
                }

                if (attribute.getScopeExtent() != ScopeExtent.Global)
                {
                    checkAttribute(attribute, errors);
                }
            }
        }
    }

    private void checkAttributeUses(final ComplexType complexType, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        // Check that the attribute names are unique, and check each local
        // attribute declaration.
        final HashSet<QName> unique = new HashSet<QName>();
        for (final AttributeUse attributeUse : complexType.getAttributeUses().values())
        {
            final AttributeDefinition attribute = attributeUse.getAttribute();
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
            final ValueConstraint valueConstraintUse = attributeUse.getValueConstraint();
            if (null != valueConstraintUse && valueConstraintUse.getVariety().isFixed())
            {
                final ValueConstraint valueConstraintAtt = attribute.getValueConstraint();
                if (null != valueConstraintAtt && valueConstraintAtt.getVariety().isFixed())
                {
                    final String valueUse = atomBridge.getC14NString(valueConstraintUse.getValue(atomBridge));
                    final String valueAtt = atomBridge.getC14NString(valueConstraintAtt.getValue(atomBridge));
                    if (!valueUse.equals(valueAtt))
                    {
                        errors.error(complexType, new SccComplexTypeAttributeFixedOverrideException(complexType.getName(), attribute.getName()));
                    }
                }
            }

            if (attribute.getScopeExtent() != ScopeExtent.Global)
            {
                checkAttribute(attribute, errors);
            }
        }
    }

    private void checkClause5(final ComplexType complexType, final ComplexType baseType, final SchemaConstraintHandler errors) 
        throws AbortException
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
                final ContentType contentTypeR = complexType.getContentType();
                final ContentType contentTypeB = baseType.getContentType();

                if (contentTypeR.isElementOnly() || (contentTypeR.isMixed() && contentTypeB.isMixed()))
                {
                    checkModelGroupUseValidRestriction(complexType, contentTypeR.getContentModel(), contentTypeB.getContentModel(), errors);
                }
                else
                {
                    errors.error(complexType, new SccDerivationRestrictionContentTypeException(complexType.getName()));
                }
            }
        }
    }

    private void checkComplexType(final ComplexType complexType, final ComponentBag components, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        checkComplexTypeDefinitionProperties(complexType, errors);

        if (complexType.getDerivationMethod().isExtension())
        {
            checkDerivationValidExtensionComplexType(complexType, errors);
        }
        if (complexType.getDerivationMethod().isRestriction())
        {
            checkDerivationValidRestrictionComplexType(complexType, errors);
        }

        checkAttributeUses(complexType, errors);
    }

    private void checkComplexTypeDefinitionProperties(final ComplexType complexType, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        complexType.getName();

        final ContentType contentType = complexType.getContentType();
        if (contentType.isMixed() || contentType.isElementOnly())
        {
            final ModelGroupUse contentModel = contentType.getContentModel();
            checkOccurrences(contentModel, errors);

            final ModelGroup modelGroup = contentModel.getTerm();
            if (modelGroup.getScopeExtent() != ScopeExtent.Global)
            {
                checkModelGroup(modelGroup, errors);
            }
        }
    }

    private void checkDerivationValidExtensionComplexType(final ComplexType complexType, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        if (complexType.getBaseType() instanceof ComplexType)
        {
            final ComplexType baseType = (ComplexType)complexType.getBaseType();
            // The {final} of the {base type definition} must not contain
            // extension.
            if (baseType.isFinal(DerivationMethod.Extension))
            {
                errors.error(complexType, new SccFinalOfBaseTypeContainsExtensionException(SccDerivationExtensionException.PART_WHEN_BASE_COMPLEX_TYPE_FINAL_OF_BASE_MUST_NOT_CONTAINT_EXTENSION, complexType.getName()));
            }

            // TODO: 1.2 {attribute uses} must be a subset...

            // TODO: 1.3 {attribute wildcard}...

            if (!clause14(complexType, baseType))
            {
                errors.error(complexType, new SccDerivationExtensionContentTypeException(complexType.getName()));
            }
        }
        else if (complexType.getBaseType() instanceof SimpleType)
        {
            final SimpleType simpleTypeB = (SimpleType)complexType.getBaseType();
            final ContentType contentTypeD = complexType.getContentType();

            if (!clause21(contentTypeD, simpleTypeB))
            {
                errors.error(complexType, new SccContentTypeMismatchBaseTypeContainsExtensionException(complexType.getName()));
            }

            if (!clause22(simpleTypeB))
            {
                errors.error(complexType, new SccFinalOfBaseTypeContainsExtensionException(SccDerivationExtensionException.PART_WHEN_BASE_SIMPLE_TYPE_FINAL_OF_BASE_MUST_NOT_CONTAINT_EXTENSION, complexType.getName()));
            }
        }
        else
        {
            throw new AssertionError(complexType.getBaseType());
        }
    }

    private void checkDerivationValidRestrictionComplexType(final ComplexType complexType, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        // The {base type definition} must be a complex type definition.
        if (complexType.getBaseType() instanceof ComplexType)
        {
            final ComplexType baseType = (ComplexType)complexType.getBaseType();

            // The {final} of the {base type definition} must not contain
            // restriction.
            if (baseType.isFinal(DerivationMethod.Restriction))
            {
                errors.error(complexType, new SccFinalOfBaseTypeContainsRestrictionException(complexType.getName()));
            }

            checkAttributeDerivationRestrictionComplexType(complexType, baseType, errors);

            checkClause5(complexType, baseType, errors);
        }
        else
        {
            // TODO: Check tis out for the TIBCOxml test suite.
            // errors.error(complexType, new
            // SccDerivationRestrictionBaseTypeMustBeComplexTypeException(qname(complexType.getName(),
            // nameBridge)));
        }
    }

    private void checkElement(final ElementDefinition element, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        checkElementDeclaration(element, errors);
    }

    private void checkElementDeclaration(final ElementDefinition element, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        // 4. If there is a {substitution group affiliation}, the {type
        // definition} of the element must be
        // validly derived from the {type definition} of the affiliation, given
        // the exclusions of the affiliation.
        if (element.hasSubstitutionGroup())
        {
            final ElementDefinition substitutionGroup = element.getSubstitutionGroup();
            try
            {
                checkTypeDerivationOK(element.getType(), substitutionGroup.getType(), substitutionGroup.getSubstitutionGroupExclusions());
            }
            catch (final ComponentConstraintException e)
            {
                errors.error(element, new SccElementDeclarationSubstitutionGroupTypeDerivationException(element.getName(), e));
            }
        }

        // 5. If the {type definition} or {type definition}'s {content type} is
        // or is derived from ID then there must not be a {value constraint}.
        final ValueConstraint valueConstraint = element.getValueConstraint();
        if (null != valueConstraint)
        {
            final Type elementType = element.getType();
            if (elementType instanceof SimpleType)
            {
                @SuppressWarnings("unused")
                final SimpleType simpleType = (SimpleType)elementType;
            }
            else if (elementType instanceof ComplexType)
            {
                final ComplexType complexType = (ComplexType)elementType;
                final ContentType contentType = complexType.getContentType();
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
                    if (isDerivedFrom(elementType, NameSource.SINGLETON.nativeType(NativeType.ID)))
                    {
                        errors.error(element, new SccElementDeclarationDerivedFromIDWithValueConstraintException(element.getName()));
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

    private void checkIdentityConstraint(final IdentityConstraint constraint, final SchemaConstraintHandler errors) 
        throws AbortException
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
                final IdentityConstraint referencedKey = constraint.getKeyConstraint();
                switch (referencedKey.getCategory())
                {
                    case Key:
                    case Unique:
                    {
                        if (referencedKey.getFields().size() != constraint.getFields().size())
                        {
                            errors.error(constraint, new SccIdentityConstraintKeyrefFieldsCardinalityException(constraint.getName(), constraint.getFields().size(), referencedKey.getName(), referencedKey.getFields().size()));
                        }
                    }
                    break;
                    case KeyRef:
                    {
                        errors.error(constraint, new SccIdentityConstraintKeyrefReferenceException(constraint.getName()));
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

    private void checkLimitRestriction(final Limit restrictingLimit, final Limit parentMaxInclusive, 
                                       final Limit parentMaxExclusive, final Limit parentMinInclusive, final Limit parentMinExclusive,
                                       final SimpleType simpleType, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        final XmlAtom derivedAtom = restrictingLimit.getLimit(atomBridge);

        switch (restrictingLimit.getKind())
        {
            case MinInclusive:
            {
                if (parentMaxExclusive != null)
                {
                    final XmlAtom baseAtom = parentMaxExclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result != CompareKind.BEFORE)
                        {
                            errors.error(simpleType, new SccMinInclusionRestrictionException(FacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMinInclusionRestrictionException(FacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
                    }
                }
                if (parentMaxInclusive != null)
                {
                    final XmlAtom baseAtom = parentMaxInclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result == CompareKind.AFTER)
                        {
                            errors.error(simpleType, new SccMinInclusionRestrictionException(FacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMinInclusionRestrictionException(FacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
                    }
                }
                if (parentMinInclusive != null)
                {
                    final XmlAtom baseAtom = parentMinInclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result == CompareKind.BEFORE)
                        {
                            errors.error(simpleType, new SccMinInclusionRestrictionException(FacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMinInclusionRestrictionException(FacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
                    }
                }
                if (parentMinExclusive != null)
                {
                    final XmlAtom baseAtom = parentMinExclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result != CompareKind.AFTER)
                        {
                            errors.error(simpleType, new SccMinInclusionRestrictionException(FacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMinInclusionRestrictionException(FacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
                    }
                }
            }
            break;
            case MaxInclusive:
            {
                if (parentMaxExclusive != null)
                {
                    final XmlAtom baseAtom = parentMaxExclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result != CompareKind.BEFORE)
                        {
                            errors.error(simpleType, new SccMaxInclusionRestrictionException(FacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMaxInclusionRestrictionException(FacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
                    }
                }
                if (parentMaxInclusive != null)
                {
                    final XmlAtom baseAtom = parentMaxInclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result == CompareKind.AFTER)
                        {
                            errors.error(simpleType, new SccMaxInclusionRestrictionException(FacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMaxInclusionRestrictionException(FacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
                    }
                }
                if (parentMinInclusive != null)
                {
                    final XmlAtom baseAtom = parentMinInclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result == CompareKind.BEFORE)
                        {
                            errors.error(simpleType, new SccMaxInclusionRestrictionException(FacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMaxInclusionRestrictionException(FacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
                    }
                }
                if (parentMinExclusive != null)
                {
                    final XmlAtom baseAtom = parentMinExclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result != CompareKind.AFTER)
                        {
                            errors.error(simpleType, new SccMaxInclusionRestrictionException(FacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMaxInclusionRestrictionException(FacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
                    }
                }
            }
            break;
            case MinExclusive:
            {
                if (parentMaxExclusive != null)
                {
                    final XmlAtom baseAtom = parentMaxExclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result != CompareKind.BEFORE)
                        {
                            errors.error(simpleType, new SccMinExclusionRestrictionException(FacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMinExclusionRestrictionException(FacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
                    }
                }
                if (parentMaxInclusive != null)
                {
                    final XmlAtom baseAtom = parentMaxInclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result == CompareKind.AFTER)
                        {
                            errors.error(simpleType, new SccMinExclusionRestrictionException(FacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMinExclusionRestrictionException(FacetKind.MaxInclusive, restrictingLimit, parentMaxExclusive));
                    }
                }
                if (parentMinInclusive != null)
                {
                    final XmlAtom baseAtom = parentMinInclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result == CompareKind.BEFORE)
                        {
                            errors.error(simpleType, new SccMinExclusionRestrictionException(FacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMinExclusionRestrictionException(FacetKind.MinExclusive, restrictingLimit, parentMinInclusive));
                    }
                }
                if (parentMinExclusive != null)
                {
                    final XmlAtom baseAtom = parentMinExclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result == CompareKind.BEFORE)
                        {
                            errors.error(simpleType, new SccMinExclusionRestrictionException(FacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMinExclusionRestrictionException(FacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
                    }
                }
            }
            break;
            case MaxExclusive:
            {
                if (parentMaxExclusive != null)
                {
                    final XmlAtom baseAtom = parentMaxExclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result == CompareKind.AFTER)
                        {
                            errors.error(simpleType, new SccMaxExclusionRestrictionException(FacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMaxExclusionRestrictionException(FacetKind.MaxExclusive, restrictingLimit, parentMaxExclusive));
                    }
                }
                if (parentMaxInclusive != null)
                {
                    final XmlAtom baseAtom = parentMaxInclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result == CompareKind.AFTER)
                        {
                            errors.error(simpleType, new SccMaxExclusionRestrictionException(FacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMaxExclusionRestrictionException(FacetKind.MaxInclusive, restrictingLimit, parentMaxInclusive));
                    }
                }
                if (parentMinInclusive != null)
                {
                    final XmlAtom baseAtom = parentMinInclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result != CompareKind.AFTER)
                        {
                            errors.error(simpleType, new SccMaxExclusionRestrictionException(FacetKind.MinInclusive, restrictingLimit, parentMinInclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMaxExclusionRestrictionException(FacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
                    }
                }
                if (parentMinExclusive != null)
                {
                    final XmlAtom baseAtom = parentMinExclusive.getLimit(atomBridge);
                    try
                    {
                        final CompareKind result = compare(upcast(derivedAtom, baseAtom), baseAtom);
                        if (!result.isDeterminate() || result != CompareKind.AFTER)
                        {
                            errors.error(simpleType, new SccMaxExclusionRestrictionException(FacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
                        }
                    }
                    catch (final AtomCastException e)
                    {
                        errors.error(simpleType, new SccMaxExclusionRestrictionException(FacetKind.MinExclusive, restrictingLimit, parentMinExclusive));
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

    private void checkMinExclusiveLessThanEqualToMaxExclusive(final Limit minExclusive, final Limit maxExclusive, final SimpleType simpleType, final SchemaConstraintHandler errors)
        throws AbortException
    {
        final XmlAtom min = minExclusive.getLimit(atomBridge);
        try
        {
            maxExclusive.validate(min, simpleType, atomBridge);
        }
        catch (final FacetException e)
        {
            final String minString = atomBridge.getC14NForm(min);
            final String maxString = getDisplayString(maxExclusive.getLimit(atomBridge));
            errors.error(simpleType, new SccMinInclusiveLessThanEqualToMaxInclusiveException(minString, maxString));
        }
    }

    private void checkModelGroup(final ModelGroup modelGroup, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        for (final SchemaParticle particle : modelGroup.getParticles())
        {
            checkOccurrences(particle, errors);

            if (particle instanceof ModelGroupUse)
            {
                final ModelGroupUse modelGroupUse = (ModelGroupUse)particle;

                final ModelGroup term = modelGroupUse.getTerm();
                if (term.getCompositor().isAll())
                {
                    errors.error(modelGroup, new SccAllGroupAppearsException());
                    if (modelGroup.getScopeExtent() != ScopeExtent.Global)
                    {
                        checkModelGroup(term, errors);
                    }
                }
            }
        }
    }

    private void checkModelGroupUseValidRestriction(final ComplexType complexType, final ModelGroupUse R, final ModelGroupUse B, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        if (equalBounds(R, B) && (R.getTerm() == B.getTerm()))
        {
            // It's the same particle.
        }
        else
        {
            final ModelGroup groupR = R.getTerm();
            final ModelGroup groupB = B.getTerm();
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

    private void checkNotation(final NotationDefinition notation, final SchemaConstraintHandler errors) 
        throws AbortException
    {
    }

    private void checkOccurrenceRangeOK(final ComplexType complexType, final SchemaParticle one, final SchemaParticle two, final SchemaConstraintHandler errors) 
        throws AbortException
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

    private void checkOccurrences(final SchemaParticle particle, final SchemaConstraintHandler errors) 
        throws AbortException
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

    private void checkParticleDerivationOKAllAllSequenceSequence(final ComplexType complexType, final ModelGroupUse R, final ModelGroupUse B, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        checkOccurrenceRangeOK(complexType, R, B, errors);

        final Iterator<? extends SchemaParticle> particlesR = R.getTerm().getParticles().iterator();
        final Iterator<? extends SchemaParticle> particlesB = B.getTerm().getParticles().iterator();
        if (particlesR.hasNext())
        {
            if (particlesB.hasNext())
            {
                final SchemaParticle particleR = particlesR.next();
                final SchemaParticle particleB = particlesB.next();
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

    private void checkParticleValidRestriction(final ComplexType complexType, final SchemaParticle R, final SchemaParticle B, final SchemaConstraintHandler errors) 
        throws AbortException
    {

    }

    /**
     * Checks the compiled components satisfy Schema Component Constraints. <br>
     * The cache is used only to provide the locations for error reporting purposes since the locations are not maintained in the compiled schema model.
     */
    public void checkSchemaComponentConstraints(final SchemaConstraintHandler errors) throws AbortException
    {
        for (final SimpleType simpleType : components.getSimpleTypes())
        {
            checkSimpleType(simpleType, errors);
        }
        for (final ComplexType complexType : components.getComplexTypes())
        {
            checkComplexType(complexType, components, errors);
        }
        for (final AttributeDefinition attribute : components.getAttributes())
        {
            checkAttribute(attribute, errors);
        }
        for (final ElementDefinition element : components.getElements())
        {
            checkElement(element, errors);
        }
        for (final AttributeGroupDefinition attributeGroup : components.getAttributeGroups())
        {
            checkAttributeGroup(attributeGroup, errors);
        }
        for (final IdentityConstraint constraint : components.getIdentityConstraints())
        {
            checkIdentityConstraint(constraint, errors);
        }
        for (final ModelGroup modelGroup : components.getModelGroups())
        {
            checkModelGroup(modelGroup, errors);
        }
        for (final NotationDefinition notation : components.getNotations())
        {
            checkNotation(notation, errors);
        }
    }

    private void checkSimpleType(final SimpleType simpleType, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        final WhiteSpacePolicy whiteSpace = simpleType.getWhiteSpacePolicy();
        if (null != whiteSpace)
        {
            checkWhitespaceValidRestriction(whiteSpace, simpleType, errors);
        }
        if (simpleType.hasFacets())
        {
            Length length = null;
            MinLength minLength = null;
            MaxLength maxLength = null;
            Limit minExclusive = null;
            Limit maxExclusive = null;
            Limit minInclusive = null;
            Limit maxInclusive = null;
            TotalDigits totalDigits = null;
            FractionDigits fractionDigits = null;
            for (final Facet facet : simpleType.getFacets())
            {
                if (facet instanceof Length)
                {
                    length = (Length)facet;
                    final Length parentLength = (Length)(getParentFacet(FacetKind.Length, simpleType));
                    if (parentLength != null && parentLength.getValue() != length.getValue())
                    {
                        errors.error(simpleType, new SccLengthRestrictionException(length.getValue(), parentLength.getValue()));
                    }
                }
                else if (facet instanceof MinLength)
                {
                    minLength = (MinLength)facet;
                    final MinLength parentMinLength = (MinLength)(getParentFacet(FacetKind.MinLength, simpleType));
                    if (parentMinLength != null && parentMinLength.getMinLength() > minLength.getMinLength())
                    {
                        errors.error(simpleType, new SccMinLengthRestrictionException(minLength.getMinLength(), parentMinLength.getMinLength()));
                    }
                }
                else if (facet instanceof MaxLength)
                {
                    maxLength = (MaxLength)facet;
                    final MaxLength parentMaxLength = (MaxLength)(getParentFacet(FacetKind.MaxLength, simpleType));
                    if (parentMaxLength != null && parentMaxLength.getMaxLength() < maxLength.getMaxLength())
                    {
                        errors.error(simpleType, new SccMaxLengthRestrictionException(maxLength.getMaxLength(), parentMaxLength.getMaxLength()));
                    }
                }
                else if (facet instanceof Limit)
                {
                    final Limit parentMinInclusive = (Limit)(getParentFacet(FacetKind.MinInclusive, simpleType));
                    final Limit parentMaxInclusive = (Limit)(getParentFacet(FacetKind.MaxInclusive, simpleType));
                    final Limit parentMinExclusive = (Limit)(getParentFacet(FacetKind.MinExclusive, simpleType));
                    final Limit parentMaxExclusive = (Limit)(getParentFacet(FacetKind.MaxExclusive, simpleType));
                    final Limit limit = (Limit)facet;

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
                    checkLimitRestriction(limit, parentMaxInclusive, parentMaxExclusive, parentMinInclusive, parentMinExclusive, simpleType, errors);
                }
                else if (facet instanceof TotalDigits)
                {
                    totalDigits = (TotalDigits)facet;
                    final TotalDigits parentTotalDigits = (TotalDigits)(getParentFacet(FacetKind.TotalDigits, simpleType));
                    if (parentTotalDigits != null && parentTotalDigits.getTotalDigits() < totalDigits.getTotalDigits())
                    {
                        errors.error(simpleType, new SccTotalDigitsRestrictionException(totalDigits.getTotalDigits(), parentTotalDigits.getTotalDigits()));
                    }
                }
                else if (facet instanceof FractionDigits)
                {
                    fractionDigits = (FractionDigits)facet;
                    final FractionDigits parentFractionDigits = (FractionDigits)(getParentFacet(FacetKind.FractionDigits, simpleType));
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
                final String maxIncString = getDisplayString(maxInclusive.getLimit(atomBridge));
                final String maxExcString = getDisplayString(maxExclusive.getLimit(atomBridge));
                errors.error(simpleType, new SccMaxInclusiveAndMaxExclusiveException(maxIncString, maxExcString));
            }
            if (minInclusive != null && maxInclusive != null)
            {
                final XmlAtom min = minInclusive.getLimit(atomBridge);
                final XmlAtom max = maxInclusive.getLimit(atomBridge);

                try
                {
                    minInclusive.validate(max, simpleType, atomBridge);
                }
                catch (final FacetException e)
                {
                    final String minString = getDisplayString(min);
                    final String maxString = getDisplayString(max);
                    errors.error(simpleType, new SccMinInclusiveLessThanEqualToMaxInclusiveException(minString, maxString));
                }
                try
                {
                    maxInclusive.validate(min, simpleType, atomBridge);
                }
                catch (final FacetException e)
                {
                    final String minString = getDisplayString(min);
                    final String maxString = getDisplayString(max);
                    errors.error(simpleType, new SccMinInclusiveLessThanEqualToMaxInclusiveException(minString, maxString));
                }
            }
            if (minExclusive != null && maxExclusive != null)
            {
                checkMinExclusiveLessThanEqualToMaxExclusive(minExclusive, maxExclusive, simpleType, errors);
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
                            errors.error(simpleType, new SccMinLengthDerivedException(minLength.getMinLength(), length.getValue(), simpleType.getName()));
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
                            errors.error(simpleType, new SccMaxLengthDerivedException(maxLength.getMaxLength(), length.getValue(), simpleType.getName()));
                        }
                    }
                    else
                    {
                        errors.error(simpleType, new SccMaxLengthCompatibleException());
                    }
                }
            }
        }
        final SimpleType notationType = provider.getAtomicType(NativeType.NOTATION);
        if (simpleType.derivedFromType(notationType, EnumSet.of(DerivationMethod.Restriction)))
        {
            for (final EnumerationDefinition enumeration : simpleType.getEnumerations())
            {
                for (final XmlAtom atom : enumeration.getValue(atomBridge))
                {
                    final QName reference = atomBridge.getNotation(atom);

                    boolean match = false;
                    for (final NotationDefinition notation : components.getNotations())
                    {
                        if (reference.equals(notation.getName()))
                        {
                            match = true;
                            break;
                        }
                    }
                    if (!match)
                    {
                        final Set<QName> names = getComponentNames(components.getNotations());
                        errors.error(simpleType, new SccNotationNotInValueSpace(reference, names));
                    }
                }
            }
        }
    }

    private void checkTypeDerivationOK(final Type D, final Type B, final Set<DerivationMethod> subset) 
        throws ComponentConstraintException
    {
        if (D instanceof SimpleType)
        {
            checkTypeDerivationOKSimple((SimpleType)D, B, subset);
        }
        else if (D instanceof ComplexType)
        {
            checkTypeDerivationOKComplex((ComplexType)D, B, subset);
        }
        else
        {
            throw new AssertionError(D);
        }
    }

    private void checkTypeDerivationOKComplex(final ComplexType D, final Type B, final Set<DerivationMethod> subset) 
        throws ComponentConstraintException
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

            final Type deeBaseType = D.getBaseType();
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
                    if (deeBaseType instanceof ComplexType)
                    {
                        try
                        {
                            checkTypeDerivationOK(deeBaseType, B, subset);
                        }
                        catch (final ComponentConstraintException e)
                        {
                            throw new SccComplexTypeBaseComplexDerivationException(D, B, subset, e);
                        }
                    }
                    else if (deeBaseType instanceof SimpleType)
                    {
                        try
                        {
                            checkTypeDerivationOKSimple((SimpleType)deeBaseType, B, subset);
                        }
                        catch (final ComponentConstraintException e)
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
    private void checkTypeDerivationOKSimple(final SimpleType D, final Type B, final Set<DerivationMethod> subset) 
        throws ComponentConstraintException
    {
        if (D == B)
        {
            // They are the same type definition.
        }
        else
        {
            if (subset.contains(DerivationMethod.Restriction) || (D.getBaseType().getFinal().contains(DerivationMethod.Restriction)))
            {
                throw new SccSimpleTypeDerivationRestrictionException(D.getName());
            }

            boolean isOK = false;
            if (D.getBaseType() == B)
            {
                isOK = true;
            }
            else if (!D.getBaseType().isComplexUrType() && isTypeDerivationOK(D.getBaseType(), B, subset))
            {
                isOK = true;
            }
            else if ((D instanceof ListSimpleType || D instanceof UnionSimpleType) && B.isSimpleUrType())
            {
                isOK = true;
            }
            else if (B instanceof UnionSimpleType)
            {
                final UnionSimpleType unionType = (UnionSimpleType)B;
                for (final SimpleType memberType : unionType.getMemberTypes())
                {
                    if (isTypeDerivationOK(D, memberType, subset))
                    {
                        isOK = true;
                        break;
                    }
                }
            }
            if (!isOK)
            {
                throw new SccSimpleTypeDerivationException(D.getName());
            }
        }
    }

    private void checkWhitespaceValidRestriction(final WhiteSpacePolicy ws, final SimpleType simpleType, final SchemaConstraintHandler errors) 
        throws AbortException
    {
        final Type baseType = simpleType.getBaseType();
        if (baseType instanceof SimpleType)
        {
            final SimpleType parent = (SimpleType)baseType;
            final WhiteSpacePolicy pws = parent.getWhiteSpacePolicy();
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

    private boolean clause14(final ComplexType complexType, final ComplexType baseType)
    {
        // 1.4 One of the following must be true.
        final ContentType contentTypeD = complexType.getContentType();
        final ContentType contentTypeB = baseType.getContentType();

        return clause141(contentTypeD, contentTypeB) || clause142(contentTypeD, contentTypeB) || clause143(contentTypeD, contentTypeB);
    }

    private boolean clause141(final ContentType contentTypeD, final ContentType contentTypeB)
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

    private boolean clause142(final ContentType contentTypeD, final ContentType contentTypeB)
    {
        return contentTypeD.isEmpty() && contentTypeB.isEmpty();
    }

    private boolean clause143(final ContentType contentTypeD, final ContentType contentTypeB)
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

    private boolean clause1431(final ContentType contentTypeD)
    {
        return contentTypeD.isMixed() || contentTypeD.isElementOnly();
    }

    private boolean clause1432(final ContentType contentTypeD, final ContentType contentTypeB)
    {
        return clause14321(contentTypeB) || clause14322(contentTypeD, contentTypeB);
    }

    private boolean clause14321(final ContentType contentTypeB)
    {
        return contentTypeB.isEmpty();
    }

    private boolean clause14322(final ContentType contentTypeD, final ContentType contentTypeB)
    {
        if (clause143221(contentTypeD, contentTypeB))
        {
            final ModelGroupUse contentModelD = contentTypeD.getContentModel();
            final ModelGroupUse contentModelB = contentTypeB.getContentModel();
            return clause143222(contentModelD, contentModelB);
        }
        else
        {
            return false;
        }
    }

    private boolean clause143221(final ContentType contentTypeD, final ContentType contentTypeB)
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

    private boolean clause143222(final ModelGroupUse contentModelD, final ModelGroupUse contentModelB)
    {
        return isValidParticleExtension(contentModelD, contentModelB);
    }

    private boolean clause21(final ContentType contentTypeD, final SimpleType simpleTypeB)
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

    private boolean clause22(final SimpleType simpleTypeB)
    {
        return !simpleTypeB.isFinal(DerivationMethod.Extension);
    }

    private boolean clause52(final ComplexType complexType, final ComplexType baseType)
    {
        // 5.2 All the following must be true.
        return contentTypeMustBeSimple521(complexType) && clause522(complexType, baseType);
    }

    private boolean clause522(final ComplexType complexType, final ComplexType baseType)
    {
        return clause5221(complexType, baseType) || isMixedWithEmptiableParticle5222(baseType);

    }

    private boolean clause5221(final ComplexType complexType, final ComplexType baseType)
    {
        // 5.2.2.1 The {content type} of the {base type definition} must be a
        // simple type definition from which the
        // {content type} is validly derived given the empty set as defined in
        // Type Derivation OK (Simple) (3.14.6).
        final ContentType contentType = baseType.getContentType();
        if (contentType.isSimple())
        {
            return isSimpleDerivationOK(complexType, baseType);
        }
        else
        {
            return false;
        }
    }

    private boolean clause53(final ComplexType complexType, final ComplexType baseType)
    {
        return complexType.getContentType().isEmpty() && clause532(baseType);
    }

    private boolean clause532(final ComplexType baseType)
    {
        return clause5321(baseType) || clause5322(baseType);
    }

    private boolean clause5321(final ComplexType baseType)
    {
        return baseType.getContentType().isEmpty();
    }

    private boolean clause5322(final ComplexType baseType)
    {
        final ContentType contentType = baseType.getContentType();
        if (contentType.isElementOnly() || contentType.isMixed())
        {
            return contentType.getContentModel().isEmptiable();
        }
        else
        {
            return false;
        }
    }

    private CompareKind compare(final XmlAtom lhsAtom, final XmlAtom rhsAtom)
    {
        final NativeType lhsNativeType = atomBridge.getNativeType(lhsAtom);
        if (lhsNativeType.isInt())
        {
            final int lhs = atomBridge.getInt(lhsAtom);
            final int rhs = atomBridge.getInt(castUp(rhsAtom, NativeType.INT));
            if (lhs > rhs)
            {
                return CompareKind.AFTER;
            }
            else if (lhs < rhs)
            {
                return CompareKind.BEFORE;
            }
            else
            {
                return CompareKind.EQUAL;
            }
        }
        else if (lhsNativeType.isInteger())
        {
            final BigInteger lhs = atomBridge.getInteger(lhsAtom);
            final BigInteger rhs = atomBridge.getInteger(castUp(rhsAtom, NativeType.INTEGER));
            return CompareKind.lookup(lhs.compareTo(rhs));
        }
        else if (lhsNativeType == NativeType.DECIMAL)
        {
            final BigDecimal lhs = atomBridge.getDecimal(lhsAtom);
            final BigDecimal rhs = atomBridge.getDecimal(castUp(rhsAtom, NativeType.DECIMAL));
            return CompareKind.lookup(lhs.compareTo(rhs));
        }
        else if (lhsNativeType == NativeType.FLOAT)
        {
            final float lhs = atomBridge.getFloat(lhsAtom);
            // final float lhs = atomBridge.getFloat(castUp(lhsAtom,
            // NativeType.FLOAT, atomBridge));
            final float rhs = atomBridge.getFloat(castUp(rhsAtom, NativeType.FLOAT));
            if (lhs > rhs)
            {
                return CompareKind.AFTER;
            }
            else if (lhs < rhs)
            {
                return CompareKind.BEFORE;
            }
            else
            {
                return CompareKind.EQUAL;
            }
        }
        else if (lhsNativeType == NativeType.DOUBLE)
        {
            final double lhs = atomBridge.getDouble(lhsAtom);
            final double rhs = atomBridge.getDouble(rhsAtom);
            if (lhs > rhs)
            {
                return CompareKind.AFTER;
            }
            else if (lhs < rhs)
            {
                return CompareKind.BEFORE;
            }
            else if (lhs == rhs)
            {
                return CompareKind.EQUAL;
            }
            else
            {
                throw new AssertionError("lhs=" + lhs + ", rhs=" + rhs);
            }
        }
        else if (lhsNativeType == NativeType.DATE)
        {
            final Gregorian P = normalize(Gregorian.dateTime(castUp(lhsAtom, NativeType.DATE), atomBridge));
            final Gregorian Q = normalize(Gregorian.dateTime(castUp(rhsAtom, NativeType.DATE), atomBridge));
            if (P.hasGmtOffset())
            {
                if (Q.hasGmtOffset())
                {
                    return compareFields(P, Q);
                }
                else
                {
                    if (compareFields(P, Q.normalize(+FOURTEEN_HOURS_IN_MINUTES)) == CompareKind.BEFORE)
                    {
                        return CompareKind.BEFORE;
                    }
                    else if (compareFields(P, Q.normalize(-FOURTEEN_HOURS_IN_MINUTES)) == CompareKind.AFTER)
                    {
                        return CompareKind.AFTER;
                    }
                    else
                    {
                        return CompareKind.INDETERMINATE;
                    }
                }
            }
            else
            {
                if (Q.hasGmtOffset())
                {
                    if (compareFields(P.normalize(-FOURTEEN_HOURS_IN_MINUTES), Q) == CompareKind.BEFORE)
                    {
                        return CompareKind.BEFORE;
                    }
                    else if (compareFields(P.normalize(+FOURTEEN_HOURS_IN_MINUTES), Q) == CompareKind.AFTER)
                    {
                        return CompareKind.AFTER;
                    }
                    else
                    {
                        return CompareKind.INDETERMINATE;
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

    private CompareKind compareFields(final Gregorian P, final Gregorian Q)
    {
        if (P.hasYear())
        {
            if (Q.hasYear())
            {
                final int lhs = P.getYear();
                final int rhs = Q.getYear();
                if (lhs > rhs)
                {
                    return CompareKind.AFTER;
                }
                else if (lhs < rhs)
                {
                    return CompareKind.BEFORE;
                }
            }
            else
            {
                return CompareKind.INDETERMINATE;
            }
        }
        else
        {
            if (Q.hasYear())
            {
                return CompareKind.INDETERMINATE;
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
                    return CompareKind.AFTER;
                }
                else if (lhs < rhs)
                {
                    return CompareKind.BEFORE;
                }
            }
            else
            {
                return CompareKind.INDETERMINATE;
            }
        }
        else
        {
            if (Q.hasMonth())
            {
                return CompareKind.INDETERMINATE;
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
                    return CompareKind.AFTER;
                }
                else if (lhs < rhs)
                {
                    return CompareKind.BEFORE;
                }
            }
            else
            {
                return CompareKind.INDETERMINATE;
            }
        }
        else
        {
            if (Q.hasDay())
            {
                return CompareKind.INDETERMINATE;
            }
        }
        return CompareKind.EQUAL;
    }

    private Gregorian normalize(final Gregorian gregorian)
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

    private boolean contentTypeMustBeSimple521(final ComplexType complexType)
    {
        return complexType.getContentType().isSimple();
    }

    private boolean equalBounds(final SchemaParticle one, final SchemaParticle two)
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

    private boolean equalValues(final List<XmlAtom> expect, final List<XmlAtom> actual)
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

    private boolean existsTypeDerivedByRestrictionWithMaxLength(final SimpleType simpleType, int maxLength)
    {
        if (simpleType.getDerivationMethod().isRestriction())
        {
            Type currentType = simpleType.getBaseType();
            while (!currentType.isComplexUrType())
            {
                if (currentType instanceof SimpleType)
                {
                    if (hasMaxLengthAndNoLength((SimpleType)currentType, maxLength))
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

    private boolean existsTypeDerivedByRestrictionWithMinLength(final SimpleType simpleType, int minLength)
    {
        if (simpleType.getDerivationMethod().isRestriction())
        {
            Type currentType = simpleType.getBaseType();
            while (!currentType.isComplexUrType())
            {
                if (currentType instanceof SimpleType)
                {
                    if (hasMinLengthAndNoLength((SimpleType)currentType, minLength))
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

    private Set<QName> getComponentNames(final Iterable<? extends SchemaComponent> components)
    {
        final Set<QName> names = new HashSet<QName>();
        for (final SchemaComponent component : components)
        {
            names.add(component.getName());
        }
        return Collections.unmodifiableSet(names);
    }

    private String getDisplayString(final XmlAtom atom)
    {
        if (null != atom)
        {
            final StringBuilder buffer = new StringBuilder();

            append(atom, buffer);

            return buffer.toString();
        }
        else
        {
            return "()";
        }
    }

//    private String getDisplayString(final List<XmlAtom> value)
//    {
//        final Iterator<XmlAtom> atoms = value.iterator();
//
//        if (atoms.hasNext())
//        {
//            final XmlAtom first = atoms.next();
//
//            if (atoms.hasNext())
//            {
//                final StringBuilder buffer = new StringBuilder();
//
//                buffer.append('(');
//
//                append(first, buffer);
//
//                while (atoms.hasNext())
//                {
//                    buffer.append(", ");
//
//                    append(atoms.next(), buffer);
//                }
//
//                buffer.append(')');
//
//                return buffer.toString();
//            }
//            else
//            {
//                return getDisplayString(first);
//            }
//        }
//        else
//        {
//            return "()";
//        }
//    }

    /**
     * Searches up the inheritance tree and returns to first facet of the requested kind, or null if no such facet is inherited
     * 
     * @param facetKind
     * @param simpleType
     * @return the requested, inherited facet or null if no such facet exists
     */
    private Facet getParentFacet(final FacetKind facetKind, final SimpleType simpleType)
    {
        Type baseType = simpleType.getBaseType();
        Facet parentFacet = null;
        while (baseType instanceof SimpleType && parentFacet == null)
        {
            final SimpleType parent = (SimpleType)baseType;
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

    private boolean hasMaxLengthAndNoLength(final SimpleType simpleType, final int maxLength)
    {
        if (simpleType.hasFacets())
        {
            if (simpleType.getFacetOfKind(FacetKind.Length) != null)
            {
                return false;
            }
            else
            {
                MaxLength facet = (MaxLength)(simpleType.getFacetOfKind(FacetKind.MaxLength));
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

    private boolean hasMinLengthAndNoLength(final SimpleType simpleType, final int minLength)
    {
        if (simpleType.hasFacets())
        {
            if (simpleType.getFacetOfKind(FacetKind.Length) != null)
            {
                return false;
            }
            else
            {
                MinLength facet = (MinLength)(simpleType.getFacetOfKind(FacetKind.MinLength));
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

    private boolean isDerivedFrom(final Type type, final QName typeName)
    {
        Type currentType = type;
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

    private boolean isMixedWithEmptiableParticle5222(final ComplexType type)
    {
        final ContentType contentType = type.getContentType();
        if (contentType.isMixed())
        {
            return contentType.getContentModel().isEmptiable();
        }
        else
        {
            return false;
        }
    }

    private boolean isSimpleDerivationOK(final Type complexType, final Type baseType)
    {
        // TODO See checkTypeDerivationSimple
        complexType.getName();
        baseType.getName();
        // TODO:
        return true;
    }

    private boolean isTypeDerivationOK(final Type D, final Type B, final Set<DerivationMethod> subset)
    {
        try
        {
            checkTypeDerivationOK(D, B, subset);
        }
        catch (final SchemaException e)
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
    private boolean isValidParticleExtension(final ModelGroupUse E, final ModelGroupUse B)
    {
        final int minOccurs = E.getMinOccurs();
        
        // Use Integer.MIN_VALUE to represent unbounded; okay in this case because we're only comparing values
        final int maxOccurs = E.isMaxOccursUnbounded() ? Integer.MIN_VALUE : E.getMaxOccurs();
        final int maxOccursB = B.isMaxOccursUnbounded() ? Integer.MIN_VALUE : B.getMaxOccurs();

        if (minOccurs == B.getMinOccurs() && maxOccurs == maxOccursB && (E.getTerm() == B.getTerm()))
        {
            // It's the same particle.
            return true;

        }
        else if (1 == minOccurs && 1 == maxOccurs)
        {
            final ModelGroup groupE = E.getTerm();
            if (groupE.getCompositor().isSequence())
            {
                final Iterator<? extends SchemaParticle> particles = groupE.getParticles().iterator();
                if (particles.hasNext())
                {
                    final SchemaParticle firstMember = particles.next();
                    if (firstMember instanceof ModelGroupUse)
                    {
                        final ModelGroupUse F = (ModelGroupUse)firstMember;
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

    private boolean recursivelyIdenticalElements(final ElementDefinition one, final ElementDefinition two)
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

    private boolean recursivelyIdenticalModelGroups(final ModelGroup one, final ModelGroup two)
    {
        if (one == two)
        {
            return true;
        }
        else
        {
            if (one.getCompositor() == two.getCompositor())
            {
                final Iterator<? extends SchemaParticle> particlesOne = one.getParticles().iterator();
                final Iterator<? extends SchemaParticle> particlesTwo = two.getParticles().iterator();
                if (particlesOne.hasNext())
                {
                    if (particlesTwo.hasNext())
                    {
                        final SchemaParticle particleOne = particlesOne.next();
                        final SchemaParticle particleTwo = particlesTwo.next();

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

    private boolean recursivelyIdenticalParticleTerms(final ParticleTerm one, final ParticleTerm two)
    {
        if (one == two)
        {
            return true;
        }
        else
        {
            if (one instanceof ElementDefinition && two instanceof ElementDefinition)
            {
                return recursivelyIdenticalElements((ElementDefinition)one, (ElementDefinition)two);
            }
            else if (one instanceof ModelGroup && two instanceof ModelGroup)
            {
                return recursivelyIdenticalModelGroups((ModelGroup)one, (ModelGroup)two);
            }
            else if (one instanceof SchemaWildcard && two instanceof SchemaWildcard)
            {
                return recursivelyIdenticalWildcards((SchemaWildcard)one, (SchemaWildcard)two);
            }
            else
            {
                return false;
            }
        }
    }

    private boolean recursivelyIdenticalProperties(final ModelGroupUse one, final ModelGroupUse two)
    {
        if (one == two)
        {
            return true;
        }
        else
        {
            // Use Integer.MIN_VALUE to represent unbounded; okay in this case because we're only comparing values
            final int maxOccursOne = one.isMaxOccursUnbounded() ? Integer.MIN_VALUE : one.getMaxOccurs();
            final int maxOccursTwo = two.isMaxOccursUnbounded() ? Integer.MIN_VALUE : two.getMaxOccurs();
            if (one.getMinOccurs() == two.getMinOccurs() && maxOccursOne == maxOccursTwo)
            {
                return recursivelyIdenticalModelGroups(one.getTerm(), two.getTerm());
            }
            else
            {
                return false;
            }
        }
    }

    private boolean recursivelyIdenticalProperties(final SchemaParticle one, final SchemaParticle two)
    {
        if (one == two)
        {
            return true;
        }
        else
        {
            // Use Integer.MIN_VALUE to represent unbounded; okay in this case because we're only comparing values
            final int maxOccursOne = one.isMaxOccursUnbounded() ? Integer.MIN_VALUE : one.getMaxOccurs();
            final int maxOccursTwo = two.isMaxOccursUnbounded() ? Integer.MIN_VALUE : two.getMaxOccurs();
            if (one.getMinOccurs() == two.getMinOccurs() && maxOccursOne == maxOccursTwo)
            {
                return recursivelyIdenticalParticleTerms(one.getTerm(), two.getTerm());
            }
            else
            {
                return false;
            }
        }
    }

    private boolean recursivelyIdenticalWildcards(final SchemaWildcard one, final SchemaWildcard two)
    {
        // TODO:
        return true;
    }

    private XmlAtom upcast(final XmlAtom sourceAtom, final XmlAtom baseAtom)
        throws AtomCastException
    {
        final QName baseType = atomBridge.getDataType(baseAtom);
        final XmlAtom atom = atomBridge.upCast(sourceAtom);
        final QName atomType = atomBridge.getDataType(atom);
        if (baseType.equals(atomType))
        {
            return atom;
        }
        else
        {
            return upcast(atom, baseAtom);
        }
    }
    
    private final CanonicalAtomBridge atomBridge;
    private final ComponentBag components;
    private final ComponentProvider provider;
}
