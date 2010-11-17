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

import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;

/**
 * Defines a bag of schema components.
 * <p>
 * Typically used for the bulk-loading of components into a schema.
 * </p>
 * 
 * @param <A>
 *            The atom handle.
 */
public interface ComponentBag<A>
{
	/**
	 * Returns the Attribute group definitions.
	 */
	Iterable<AttributeGroupDefinition<A>> getAttributeGroups();

	/**
	 * Returns the Attribute declarations.
	 */
	Iterable<AttributeDefinition<A>> getAttributes();

	/**
	 * Returns the Complex type definitions.
	 */
	Iterable<ComplexType<A>> getComplexTypes();

	/**
	 * Returns the Element declarations.
	 */
	Iterable<ElementDefinition<A>> getElements();

	/**
	 * Returns the Identity-constraint definitions.
	 */
	Iterable<IdentityConstraint<A>> getIdentityConstraints();

	/**
	 * Returns the Model group definitions.
	 */
	Iterable<ModelGroup<A>> getModelGroups();

	/**
	 * Returns the Notation declarations.
	 */
	Iterable<NotationDefinition<A>> getNotations();

	/**
	 * Returns the Simple type definitions.
	 */
	Iterable<SimpleType<A>> getSimpleTypes();
}
