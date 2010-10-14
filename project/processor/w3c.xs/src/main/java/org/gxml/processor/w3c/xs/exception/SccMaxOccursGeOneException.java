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

import java.math.BigInteger;

import org.genxdm.exceptions.PreCondition;

@SuppressWarnings("serial")
public final class SccMaxOccursGeOneException extends SccParticleCorrectException
{
	private final BigInteger m_maxOccurs;

	public SccMaxOccursGeOneException(final BigInteger maxOccurs)
	{
		super(PART_MIN_OCCURS_LE_MAX_OCCURS);
		m_maxOccurs = PreCondition.assertArgumentNotNull(maxOccurs, "maxOccurs");
	}

	public String getMessage()
	{
		return "If not unbounded, {max occurs} (" + m_maxOccurs + ") must be greater than or equal to 1.";
	}
}
