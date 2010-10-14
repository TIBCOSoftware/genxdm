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
package org.genxdm.processor.w3c.xs.exception;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.SmOutcome;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.resolve.SmLocation;

@SuppressWarnings("serial")
public abstract class SmSourceAttributeException extends SmLocationException
{
	public static final String PART_DEFAULT_AND_FIXED_PRESENT = "1";
	public static final String PART_DEFAULT_AND_USE_IMPLIES_OPTIONAL = "2";
	public static final String PART_REF_XOR_NAME = "3.1";
	public static final String PART_REF_PRESENT = "3.2";
	public static final String PART_TYPE_XOR_SIMPLE_TYPE = "4";
	public static final String PART_DECLARATIONS = "5";

	public SmSourceAttributeException(final String partNumber, final SmLocation location)
	{
		super(SmOutcome.SRC_Attribute, partNumber, location);
	}

	public SmSourceAttributeException(final String partNumber, final SmLocation location, final SmException cause)
	{
		super(SmOutcome.SRC_Attribute, partNumber, location, PreCondition.assertArgumentNotNull(cause, "cause"));
	}
}
