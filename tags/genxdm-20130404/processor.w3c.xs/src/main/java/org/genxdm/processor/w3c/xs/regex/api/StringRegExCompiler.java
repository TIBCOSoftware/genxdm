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
package org.genxdm.processor.w3c.xs.regex.api;

/**
 * Regular Expression compiler providing an injectable implementation.
 */
public interface StringRegExCompiler
{
    /**
     * Compiles the specified regular expression into a pattern that can be used for matching.
     *
     * @param regex The lexical representation of the regular expression.
     * @return The compiled regular expression pattern.
     * @throws StringRegExException If the regular expression contains a syntax error.
     */
    StringRegExPattern compile(String regex) throws StringRegExException;

    StringRegExPattern compile(String regex, String flags) throws StringRegExException;
}
