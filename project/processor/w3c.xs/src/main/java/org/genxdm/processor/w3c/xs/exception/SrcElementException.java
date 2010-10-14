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
public abstract class SrcElementException extends SmLocationException
{
	public static final String PART_DEFAULT_AND_FIXED_PRESENT = "1";
	public static final String PART_REF_XOR_NAME = "2.1";
	public static final String PART_REF_PRESENT = "2.2";
	public static final String PART_SIMPLE_XOR_COMPLEX = "3";
	public static final String PART_DECLARATIONS = "4";

	public SrcElementException(final String partNumber, final SmLocation location)
	{
		super(SmOutcome.SRC_Element, partNumber, location);
	}

	public SrcElementException(final String partNumber, final SmLocation location, final SmException cause)
	{
		super(SmOutcome.SRC_Element, partNumber, location, PreCondition.assertArgumentNotNull(cause, "cause"));
	}
}
