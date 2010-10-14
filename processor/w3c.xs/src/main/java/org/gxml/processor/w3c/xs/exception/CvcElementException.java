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
package org.gxml.processor.w3c.xs.exception;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.enums.SmOutcome;
import org.genxdm.xs.exceptions.SmComponentConstraintException;
import org.genxdm.xs.resolve.SmLocation;

/**
 * Corresponds to the <b>Element Locally Valid</b> validation rule.
 */
@SuppressWarnings("serial")
public abstract class CvcElementException extends SmLocationException
{
	private final SmElement<?> elementDeclaration;

	public static final String PART_ABSTRACT = "2";
	public static final String PART_NOT_NILLABLE = "3.1";
	public static final String PART_NO_CHILDREN = "3.2.1";
	public static final String PART_FIXED_AND_NILLED = "3.2.2";
	public static final String PART_LOCAL_TYPE_RESOLVE = "4.2";
	public static final String PART_LOCAL_TYPE_DERIVATION = "4.3";
	public static final String PART_VALUE_CONSTRAINT_WITH_CHILD_ELEMENT = "5.2.2.1";
	public static final String PART_VALUE_CONSTRAINT_MIXED = "5.2.2.2.1";
	public static final String PART_VALUE_CONSTRAINT_SIMPLE = "5.2.2.2.2";

	public CvcElementException(final String partNumber, final SmElement<?> elementDeclaration, final SmLocation location)
	{
		super(SmOutcome.CVC_Element_Locally_Valid, partNumber, location);
		this.elementDeclaration = PreCondition.assertArgumentNotNull(elementDeclaration, "elementDeclaration");
	}

	public CvcElementException(final String partNumber, final SmElement<?> elementDeclaration, final SmLocation location, final SmComponentConstraintException cause)
	{
		super(SmOutcome.CVC_Element_Locally_Valid, partNumber, location, cause);
		this.elementDeclaration = PreCondition.assertArgumentNotNull(elementDeclaration, "elementDeclaration");
	}

	public final SmElement<?> getElementDeclaration()
	{
		return elementDeclaration;
	}
}
