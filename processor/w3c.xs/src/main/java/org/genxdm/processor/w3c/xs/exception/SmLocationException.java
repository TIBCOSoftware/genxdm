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

import org.genxdm.xs.enums.SmOutcome;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.resolve.SmLocation;

//TODO("This should ultimately be abstract.")
@SuppressWarnings("serial")
public class SmLocationException extends SmException
{
	private final SmLocation m_location;

	public SmLocationException(final SmOutcome outcome, final String partNumber, final SmLocation location)
	{
		super(outcome, partNumber);
		m_location = location;
	}

	public SmLocationException(final SmOutcome outcome, final String partNumber, final SmLocation location, final SmException cause)
	{
		super(outcome, partNumber, cause);
		m_location = location;
	}

	public final SmLocation getLocation()
	{
		return m_location;
	}

	@Override
	public String getMessage()
	{
		return getOutcome().getSection();
	}
}
