/**
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.gxml.names;

/**
 * Prefix to namespace-uri resolver.
 */
public interface PrefixResolver
{
    /**
     * Given a prefix, get a corresponding namespace-uri.
     *
     * @param prefix The prefix to map, cannot be <code>null</code> (but may be the empty string,
     *               representing the default/global prefix).
     * @return the namespace-uri to which this prefix maps.  The empty string indicates that it
     *         maps to the default "unnamed" or "global" namespace.  <code>null</code> indicates
     *         that there is no prefix-to-namespace mapping in scope for this prefix.
     */
    String getNamespace(String prefix);
}
