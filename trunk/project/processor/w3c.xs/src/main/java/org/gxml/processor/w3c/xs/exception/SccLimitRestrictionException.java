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

import org.gxml.xs.enums.SmOutcome;
import org.gxml.xs.exceptions.SmComponentConstraintException;
import org.gxml.xs.facets.SmFacetKind;
import org.gxml.xs.facets.SmLimit;

@SuppressWarnings("serial")
public class SccLimitRestrictionException extends SmComponentConstraintException
{
	private final SmLimit<?> restrictingLimit;
	private final SmLimit<?> parentLimit;
	private final SmFacetKind childFacetKind;
	private final SmFacetKind parentFacetKind;

	public SccLimitRestrictionException(final SmOutcome outcome, final String partNumber, final SmFacetKind childFacetKind, final SmFacetKind parentFacetKind, final SmLimit<?> restrictingLimit, final SmLimit<?> parentLimit)
	{
		super(outcome, partNumber);
		this.restrictingLimit = restrictingLimit;
		this.parentLimit = parentLimit;
		this.childFacetKind = childFacetKind;
		this.parentFacetKind = parentFacetKind;
	}

	@Override
	public String getMessage()
	{
		return "The {value}, " + restrictingLimit + ", of " + childFacetKind + " is invalid in respect to the {value}, " + parentLimit + ", of its inherited " + parentFacetKind + ".";
	}
}
