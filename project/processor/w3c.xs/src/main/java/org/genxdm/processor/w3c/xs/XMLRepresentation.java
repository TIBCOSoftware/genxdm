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
package org.genxdm.processor.w3c.xs;

import javax.xml.namespace.QName;

import org.genxdm.xs.constraints.SmValueConstraint;

class XMLRepresentation
{
	// Define symbols for the local names.
	public static final String LN_ABSTRACT = "abstract";
	public static final String LN_ALL = "all";
	public static final String LN_ANNOTATION = "annotation";
	public static final String LN_ANY = "any";
	public static final String LN_ANY_ATTRIBUTE = "anyAttribute";
	public static final String LN_ATTRIBUTE = "attribute";
	public static final String LN_ATTRIBUTE_GROUP = "attributeGroup";
	public static final String LN_BASE = "base";
	public static final String LN_BLOCK = "block";
	public static final String LN_CHOICE = "choice";
	public static final String LN_COMPLEX_CONTENT = "complexContent";
	public static final String LN_COMPLEX_TYPE = "complexType";
	public static final String LN_DEFAULT = "default";
	public static final String LN_ELEMENT = "element";
	public static final String LN_ENUMERATION = "enumeration";
	public static final String LN_EXTENSION = "extension";
	public static final String LN_FIELD = "field";
	public static final String LN_FINAL = "final";
	public static final String LN_FIXED = "fixed";
	public static final String LN_FORM = "form";
	public static final String LN_FRACTION_DIGITS = "fractionDigits";
	public static final String LN_GROUP = "group";
	public static final String LN_ID = "id";
	public static final String LN_IMPORT = "import";
	public static final String LN_INCLUDE = "include";
	public static final String LN_ITEM_TYPE = "itemType";
	public static final String LN_KEY = "key";
	public static final String LN_KEYREF = "keyref";
	public static final String LN_LENGTH = "length";
	public static final String LN_LIST = "list";
	public static final String LN_MAX_EXCLUSIVE = "maxExclusive";
	public static final String LN_MAX_INCLUSIVE = "maxInclusive";
	public static final String LN_MAX_LENGTH = "maxLength";
	public static final String LN_MAX_OCCURS = "maxOccurs";
	public static final String LN_MEMBER_TYPES = "memberTypes";
	public static final String LN_MIN_EXCLUSIVE = "minExclusive";
	public static final String LN_MIN_INCLUSIVE = "minInclusive";
	public static final String LN_MIN_LENGTH = "minLength";
	public static final String LN_MIN_OCCURS = "minOccurs";
	public static final String LN_MIXED = "mixed";
	public static final String LN_NAME = "name";
	public static final String LN_NAMESPACE = "namespace";
	public static final String LN_NILLABLE = "nillable";
	public static final String LN_NOTATION = "notation";
	public static final String LN_PATTERN = "pattern";
	public static final String LN_PROCESS_CONTENTS = "processContents";
	public static final String LN_PUBLIC = "public";
	public static final String LN_REDEFINE = "redefine";
	public static final String LN_REF = "ref";
	public static final String LN_REFER = "refer";
	public static final String LN_RESTRICTION = "restriction";
	public static final String LN_SCHEMA = "schema";
	public static final String LN_SCHEMA_LOCATION = "schemaLocation";
	public static final String LN_SELECTOR = "selector";
	public static final String LN_SEQUENCE = "sequence";
	public static final String LN_SIMPLE_CONTENT = "simpleContent";
	public static final String LN_SIMPLE_TYPE = "simpleType";
	public static final String LN_SOURCE = "source";
	public static final String LN_SUBSTITUTION_GROUP = "substitutionGroup";
	public static final String LN_SYSTEM = "system";
	public static final String LN_TARGET_NAMESPACE = "targetNamespace";
	public static final String LN_TOTAL_DIGITS = "totalDigits";
	public static final String LN_TYPE = "type";
	public static final String LN_UNION = "union";
	public static final String LN_UNIQUE = "unique";
	public static final String LN_USE = "use";
	public static final String LN_VALUE = "value";
	public static final String LN_WHITE_SPACE = "whiteSpace";
	public static final String LN_XPATH = "xpath";

	public static SmValueConstraint.Kind getValueConstraintKind(final QName attributeName)
	{
		if (attributeName.getLocalPart().equals(LN_FIXED))
		{
			return SmValueConstraint.Kind.Fixed;
		}
		else if (attributeName.getLocalPart().equals(LN_DEFAULT))
		{
			return SmValueConstraint.Kind.Default;
		}
		else
		{
			throw new IllegalArgumentException(attributeName.toString());
		}
	}
}
