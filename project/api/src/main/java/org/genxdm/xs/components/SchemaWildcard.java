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
package org.genxdm.xs.components;

import org.genxdm.xs.constraints.SmNamespaceConstraint;
import org.genxdm.xs.enums.ProcessContentsMode;

/**
 * SchemaWildcard represent wildcard terms of content models. This interface accommodates variations in the different schema
 * flavors. There are two lists in the wildcard model: allowed namespaces and prohibited namespaces. One or both of
 * these must be empty.
 * <p/>
 * An item matches a wildcard:
 * <ul>
 * <li>if both lists are empty</li>
 * <li>otherwise if the item's namespace is in the allowed list</li>
 * <li>otherwise if the item's namespace is not in the prohibited list.</li>
 * </ul>
 */
public interface SchemaWildcard<A> extends ParticleTerm<A>
{
	SmNamespaceConstraint getNamespaceConstraint();

	/**
	 * Returns how this term should be processed once it is matches against this wildcard term.
	 */
	ProcessContentsMode getProcessContents();
}
