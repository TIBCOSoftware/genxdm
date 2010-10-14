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
package org.genxdm.xs.exceptions;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.SmOutcome;
import org.genxdm.xs.facets.SmPattern;

/**
 * Exception raised by xs:pattern facet validation.
 */
@SuppressWarnings("serial")
public final class SmPatternException extends SmException
{
	private final SmPattern regexp;
	private final String literal;

	public SmPatternException(final SmPattern regexp, final String literal)
	{
		super(SmOutcome.CVC_Pattern, "1");
		this.regexp = PreCondition.assertArgumentNotNull(regexp, "regexp");
		this.literal = PreCondition.assertArgumentNotNull(literal, "literal");
	}

	@Override
	public String getMessage()
	{
		final String localMessage = "The literal '" + literal + "' is not among the set of character sequences denoted by the regular expression '" + regexp.getValue() + "'.";

		final StringBuilder message = new StringBuilder();
		message.append(getOutcome().getSection());
		message.append(".");
		message.append(this.getPartNumber());
		message.append(": ");
		message.append(localMessage);
		return message.toString();
	}
}
