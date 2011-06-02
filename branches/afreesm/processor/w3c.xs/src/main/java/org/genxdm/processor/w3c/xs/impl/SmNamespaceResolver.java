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
package org.genxdm.processor.w3c.xs.impl;

/**
 * A resolver for serialization.
 * 
 */
interface SmNamespaceResolver
{
	/**
	 * Given a namespace-uri, get a corresponding prefix.
	 * 
	 * @param namespaceURI
	 *            The namespace-uri, cannot be <code>null</code>.
	 * @param prefixHint
	 *            The prefix hint, cannot be <code>null</code>.
	 * @param mayUseDefaultMapping
	 *            Determines whether the default namespace mapping, if it exists, may be used.
	 */
	String getPrefix(String namespaceURI, String prefixHint, boolean mayUseDefaultMapping);
}
