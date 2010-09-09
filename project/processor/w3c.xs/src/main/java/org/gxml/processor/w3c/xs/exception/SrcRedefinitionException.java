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

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.enums.SmOutcome;
import org.gxml.xs.exceptions.SmException;
import org.gxml.xs.resolve.SmLocation;

@SuppressWarnings("serial")
public abstract class SrcRedefinitionException extends SmLocationException
{
	public static final String PART_SCHEMA_LOCATION_RESOLVES = "1";
	public static final String PART_SCHEMA_NOT_WELL_FORMED = "2";
	public static final String PART_REDEFINTION_NAMESPACE_MISMATCH = "3.1";
	public static final String PART_TYPE_SELF_REFERENCE = "5";

	public SrcRedefinitionException(final String partNumber, final SmLocation location)
	{
		super(SmOutcome.SRC_Redefine, partNumber, location);
	}

	public SrcRedefinitionException(final String partNumber, final SmLocation location, final SmException cause)
	{
		super(SmOutcome.SRC_Redefine, partNumber, location, PreCondition.assertArgumentNotNull(cause, "cause"));
	}
}
