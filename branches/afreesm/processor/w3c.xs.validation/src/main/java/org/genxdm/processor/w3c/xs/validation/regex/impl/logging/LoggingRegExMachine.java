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
package org.genxdm.processor.w3c.xs.validation.regex.impl.logging;

import java.util.List;

import org.genxdm.processor.w3c.xs.validation.regex.api.RegExMachine;



public class LoggingRegExMachine<E, T> implements RegExMachine<E, T>
{
	public LoggingRegExMachine(RegExMachine<E, T> underlying, RegExStepLogger<E, T> logger)
	{
		m_underlying = underlying;
		m_logger = logger;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean step(final T token, final List<? super E> matchers)
	{
		m_logger.logToken(token);
		final boolean result = m_underlying.step(token, matchers);
		m_logger.logResult(result, (List)matchers);
		return result;
	}

	private RegExMachine<E, T> m_underlying;
	private RegExStepLogger<E, T> m_logger;
}
