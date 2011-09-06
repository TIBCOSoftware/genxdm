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

import org.genxdm.processor.w3c.xs.validation.regex.api.RegExBridge;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExFactory;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExPattern;

public class LoggingRegExFactory<E, T> implements RegExFactory<E, T>
{
    public LoggingRegExFactory(RegExFactory<E, T> underlying, RegExStepLoggerFactory<E, T> loggerFactory)
    {
        m_underlying = underlying;
        m_loggerFactory = loggerFactory;
    }

    public RegExPattern<E, T> newPattern(E term, RegExBridge<E, T> bridge)
    {
        RegExPattern<E, T> realPattern = m_underlying.newPattern(term, bridge);
        return new LoggingRegExPattern<E, T>(realPattern, m_loggerFactory);
    }

    private RegExFactory<E, T> m_underlying;
    private RegExStepLoggerFactory<E, T> m_loggerFactory;
}
