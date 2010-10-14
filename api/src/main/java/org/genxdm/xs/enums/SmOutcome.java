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
package org.genxdm.xs.enums;

import org.genxdm.exceptions.PreCondition;

/**
 * Enumeration indicating the outcome category for schema validation.
 */
public enum SmOutcome
{
	// Validation Rules
	CVC_Complex_Type("cvc-complex-type"), CVC_Attribute("cvc-attribute"), CVC_Element_Locally_Valid("cvc-elt"), CVC_Schema_Validity_Assessment_Element("cvc-assess-elt"), CVC_Datatype("cvc-datatype-valid"), CVC_Facet("cvc-facet-valid"), CVC_Pattern("cvc-pattern-valid"), CVC_Simple_Type("cvc-simple-type"), CVC_Type("cvc-type"), CVC_ID("cvc-id"), CVC_IdentityConstraint("cvc-identity-constraint"), CVC_Length("cvc-length-valid"), CVC_MaxExclusive("cvc-maxExclusive-valid"), CVC_MaxInclusive(
			"cvc-maxInclusive-valid"), CVC_MaxLength("cvc-maxLength-valid"), CVC_MinExclusive("cvc-minExclusive-valid"), CVC_MinInclusive("cvc-minInclusive-valid"), CVC_MinLength("cvc-minLength-valid"),

	// Schema Representation Constraints
	SRC_Attribute("src-attribute"), SRC_ComplexType("src-ct"), SRC_Element("src-element"), SRC_Import("src-import"), SRC_Include("src-include"), SRC_QName("src-qname"), SRC_Redefine("src-redefine"), SRC_SimpleType("src-simple-type"), SRC_NotWellFormed("src-not-well-formed"), SRC_TopLevel("TODO"),

	// Schema Component Constraints
	SCC_All_Group_Limited("cos-all-limited"), SCC_Attribute_Declaration_Properties_Correct("a-props-correct"), SCC_Attribute_Group_Definition_Properties_Correct("ag-props-correct"), SCC_Identity_Constraint_Definition_Properties_Correct("c-props-correct"), SCC_Type_Derivation_OK_Complex("cos-ct-derived-ok"), SCC_Type_Derivation_OK_Simple("cos-st-derived-ok"), SCC_Derivation_Valid_Extension("cos-ct-extends"), SCC_Derivation_Valid_Restriction_Complex("derivation-ok-restriction"), SCC_FractionDigitsTotalDigits(
			"fractionDigits-totalDigits"), SCC_TotalDigitsValidRestriction("totalDigits-valid-restriction"), SCC_FractionDigitsValidRestriction("fractionDigits-valid-restriction"), SCC_LengthValidRestriction("length-valid-restriction"), SCC_Length_MinLength_Or_MaxLength("length-minLength-maxLength"), SCC_MaxInclusiveAndMaxExclusive("minInclusive-and-maxExclusive"), SCC_MaxExclusiveValidRestriction("maxExclusive-valid-restriction"), SCC_MinExclusiveValidRestriction("minExclusive-valid-restriction"), SCC_MaxInclusiveValidRestriction(
			"maxInclusive-valid-restriction"), SCC_MinInclusiveValidRestriction("minInclusive-valid-restriction"), SCC_MinLengthLessThanEqualToMaxLength("minLength-less-than-equal-to-maxLength"), SCC_MinInclusiveLessThanEqualToMaxInclusive("minInclusive-less-than-equal-to-maxInclusive"), SCC_MinExclusiveLessThanEqualToMaxExclusive("minExclusive-less-than-equal-to-maxExclusive"), SCC_Element_Declaration_Properties_Correct("e-props-correct"), SCC_Element_Default_Valid_Immediate("cos-valid-default"), SCC_Derivation_Valid_Restriction_Simple(
			"cos-st-restricts"), SCC_Substitution_Group_OK_Transitive("cos-equiv-derived-ok-rec"), SCC_ModelGroup("mg-props-correct"), SCC_xmlns_Not_Allowed("no-xmlns"), SCC_xsi_Not_Allowed("no-xsi"), SCC_Complex_Type_Definition_Properties("ct-props-correct"), SCC_Simple_Type_Definition_Properties("st-props-correct"), SCC_Particle_Correct("p-props-correct"), SCC_Occurrence_Range("range-ok"), SCC_Attribute_Wildcard_Intersection("cos-aw-intersect"), SCC_Attribute_Wildcard_Union("cos-aw-union"), SCC_WhiteSpaceValidRestriction(
			"whiteSpace-valid-restriction"),

	// Schema Implementation Constraints
	SIC_Limitation("implementation-limitation"),

	TODO("Unknown");

	private final String m_section;

	SmOutcome(final String section)
	{
		m_section = PreCondition.assertArgumentNotNull(section, "section");
	}

	public String getSection()
	{
		return m_section;
	}
}
