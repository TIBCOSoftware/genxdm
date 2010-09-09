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
package org.gxml.xs.types;

import org.gxml.xs.constraints.SmModelGroupUse;

/**
 * The Content Type of a Complex Type Definition.
 * 
 * @param <A>
 *            The atom handle.
 */
public interface SmContentType<A>
{
	/**
	 * Returns the content model (i.e. a Particle) part of the {content type} property. <br/>
	 * This is only valid when the kind part of the {content type} property is mixed or element-only.
	 */
	SmModelGroupUse<A> getContentModel();

	/**
	 * Returns the {variety} of this content type object.
	 */
	SmContentTypeKind getKind();

	/**
	 * Returns a simple type definition when the kind part is <em>{@link SmContentTypeKind#Simple}</em>.
	 */
	SmSimpleType<A> getSimpleType();

	/**
	 * Returns <code>true</code> if the {content type} property is Complex (Mixed or Element Only).
	 */
	boolean isComplex();

	/**
	 * Returns <code>true</code> if the {content type} property is Element Only.
	 */
	boolean isElementOnly();

	/**
	 * Returns <code>true</code> if the {content type} property is Empty.
	 */
	boolean isEmpty();

	/**
	 * Returns <code>true</code> if the {content type} property is Mixed.
	 */
	boolean isMixed();

	/**
	 * Returns <code>true</code> if the {content type} property is Simple.
	 */
	boolean isSimple();
}
