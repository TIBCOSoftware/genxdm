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

import org.genxdm.xs.exceptions.SmFacetMinMaxException;
import org.genxdm.xs.types.SmSimpleType;

/**
 * One of the xs:maxInclusive, xs:minInclusive, xs:maxExclusive and xs:minExclusive facets.
 * 
 * @param <A>
 *            The atom handle.
 */
public interface SmLimit<A> extends SmFacet<A>
{
	/**
	 * The value of the facet.
	 */
	A getLimit();

	/**
	 * Validates the specified atom, with the specified type against this facet.
	 * 
	 * @param atom
	 *            The atom to be validated.
	 * @param simpleType
	 *            The type of the atom.
	 * @throws SmFacetMinMaxException
	 *             if the atom does not comply with the facet.
	 */
	void validate(A atom, SmSimpleType<A> simpleType) throws SmFacetMinMaxException;
}
