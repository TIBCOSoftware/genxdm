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
package org.gxml.processor.w3c.xs.validation.regex.impl.logging;

import java.util.List;

public class GenericRegExStepLogger<E, T> implements RegExStepLogger<E, T>
{
	public GenericRegExStepLogger(String prefix)
	{
		m_prefix = prefix;
	}

	public void logToken(T token)
	{
		if (token == null)
		{
			System.out.println(m_prefix + "Checking for end marker");
		}
		else
		{
			System.out.println(m_prefix + "Matching " + token + "(" + token.getClass() + ")");
		}
	}

	public void logResult(boolean result, List<? extends E> matchers)
	{
		System.out.println(m_prefix + "Got " + matchers.size() + " matches");
		System.out.print(m_prefix + "Matches: ");
		for (final E term : matchers)
		{
			System.out.print(term + " ");
		}
		System.out.println();
	}

	private String m_prefix;
}
