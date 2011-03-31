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
package org.genxdm.names;

/**
 * Namespace-uri to prefix resolver.
 */
public interface NamespaceResolver
{
    /**
     * Given a namespace-uri, get a corresponding prefix.
     *
     * @param namespaceURI         The namespace-uri, cannot be <code>null</code> (but may be the empty string).
     * @param prefixHint           The prefix hint, may be <code>null</code>.  This represents the preferred prefix, 
     *                             if multiple mappings for the namespace are in scope.
     * @param mayUseDefaultMapping Determines whether the default namespace mapping, if it exists, may be used.
     *                             This is separated from the prefix hint, because there are cases when the
     *                             default mapping is <em>not</em> permissible.
     * @return a prefix mapped to the specified namespace-uri, if there is one in scope.
     *         If only one prefix is mapped to the namespace-uri in this scope, it will be
     *         the one returned.  If more than one mapping is in scope, then one will be returned;
     *         if the prefixHint matches one of the mappings, that is the one that should be returned.
     *         If the only mapping in scope is the default mapping (that is, the prefix is the
     *         empty string), but mayUseDefaultMapping is <em>false</em>, then <code>null</code>
     *         will be returned.  A return of <code>null</code> indicates that no suitable
     *         mapping is in scope (but <code>null</code> should <em>not</em> be returned
     *         because the prefixHint does not match any mapping in scope, or maps a different
     *         namespace-uri; in that case, an arbitrary (but in scope) mapped prefix should
     *         be returned).
     */
    String getPrefix(String namespaceURI, String prefixHint, boolean mayUseDefaultMapping);
}
