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
package org.genxdm.typed.types;

import org.genxdm.exceptions.SpillagePolicy;


/**
 * Defines the role of a context that provides default information for casting of atomic values.
 * <p>
 * While casting is usually defined for XPath and XQuery, this interface allows generic casting utilities to be defined
 * and used for any XML-based language without having to define adapters.
 * </p>
 * 
 * @param <A>
 *            The atom handle.
 */
public interface CastingContext<A>
{
	/**
	 * Return the emulation mode which principally determines how numeric values are parsed and serialized.
	 */
	Emulation getEmulation();

	/**
	 * Return a policy indicating the action to take for numeric operations resulting in overflow or underflow.
	 */
	SpillagePolicy getSpillagePolicy();
}
