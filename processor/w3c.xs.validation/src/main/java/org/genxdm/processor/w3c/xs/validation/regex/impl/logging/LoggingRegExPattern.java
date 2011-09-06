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
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExPattern;
import org.genxdm.processor.w3c.xs.validation.regex.impl.restricted.RestrictedBase;


public class LoggingRegExPattern<E, T> extends RestrictedBase<E, T> implements RegExPattern<E, T>
{
	public LoggingRegExPattern(RegExPattern<E, T> underlying, RegExStepLoggerFactory<E, T> loggerFactory)
	{
		m_underlying = underlying;
		m_loggerFactory = loggerFactory;
	}

	public RegExMachine<E, T> createRegExMachine(final List<E> followers)
	{
		final RegExMachine<E, T> realMachine = m_underlying.createRegExMachine(followers);
		return new LoggingRegExMachine<E, T>(realMachine, m_loggerFactory.newLogger(m_underlying));
	}

	private final RegExPattern<E, T> m_underlying;
	private final RegExStepLoggerFactory<E, T> m_loggerFactory;
}
