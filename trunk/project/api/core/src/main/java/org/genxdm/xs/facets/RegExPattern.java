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
package org.genxdm.xs.facets;

/**
 * A compiled regular expression that may be used for pattern matching.
 */
public interface RegExPattern
{
    /**
     * Determines whether the input matches this compiled regular expression pattern.
     * 
     * @param input
     *            The input string.
     * @return <code>true<code> if the input matches the pattern, otherwise <code>false</code>.
     */
    boolean matches(String input);
}
