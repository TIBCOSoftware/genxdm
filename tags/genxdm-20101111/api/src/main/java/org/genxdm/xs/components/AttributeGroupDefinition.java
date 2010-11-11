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

import org.genxdm.xs.constraints.AttributeUse;

/**
 * An Attribute Group Definition Schema Component.
 */
public interface AttributeGroupDefinition<A> extends SchemaComponent<A>
{
	/**
	 * Determines whether the {attribute uses} property is non-empty.
	 */
	boolean hasAttributeUses();

	/**
	 * Returns the {attribute uses} property of this Attribute Group Definition. <br/>
	 * A set of attribute uses.
	 */
	Iterable<AttributeUse<A>> getAttributeUses();

	/**
	 * Returns the {attribute wildcard} property of this Attribute Group Definition. <br/>
	 * Optional; may return <code>null</code>. A wildcard.
	 */
	SchemaWildcard<A> getWildcard();
}
