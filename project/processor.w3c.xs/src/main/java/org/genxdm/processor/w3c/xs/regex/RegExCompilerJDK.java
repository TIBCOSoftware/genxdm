/*
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
package org.genxdm.processor.w3c.xs.regex;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.exceptions.SchemaRegExCompileException;
import org.genxdm.xs.facets.RegExPattern;
import org.genxdm.xs.facets.SchemaRegExCompiler;

/**
 * Regular Expression implementation using the JDK implementation.
 */
final public class RegExCompilerJDK implements SchemaRegExCompiler
{
    private final Integer m_flags;

    /**
     * Constructs a JDK Regular Expression compiler.
     */
    public RegExCompilerJDK()
    {
        m_flags = null;
    }

    /**
     * Constructs a JDK Regular Expression compiler using the JDK flags. <br/>
     * The flags are passed through to the pattern compiler.
     * 
     * @param flags
     *            Flags using by the JDK during pattern compilation.
     */
    public RegExCompilerJDK(final int flags)
    {
        m_flags = flags;
    }

    public RegExPattern compile(final String regex, final String flags) throws SchemaRegExCompileException
    {
        // TODO: Handle flags.
        return compile(regex);
    }

    public RegExPattern compile(final String regex) throws SchemaRegExCompileException
    {
        PreCondition.assertArgumentNotNull(regex, "regex");
        try
        {
            if (null != m_flags)
            {
                return new RegExPatternJDK(Pattern.compile(regex, m_flags));
            }
            else
            {
                return new RegExPatternJDK(Pattern.compile(regex));
            }
        }
        catch (final PatternSyntaxException e)
        {
            throw new SchemaRegExCompileException(e, e.getPattern());
        }
    }
}
