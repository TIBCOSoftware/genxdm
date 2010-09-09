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
package org.gxml.xs;

import org.gxml.xs.components.SmAttribute;
import org.gxml.xs.components.SmAttributeGroup;
import org.gxml.xs.components.SmComponentBag;
import org.gxml.xs.components.SmComponentProvider;
import org.gxml.xs.components.SmElement;
import org.gxml.xs.components.SmModelGroup;
import org.gxml.xs.components.SmNotation;
import org.gxml.xs.constraints.SmIdentityConstraint;
import org.gxml.xs.types.SmComplexType;
import org.gxml.xs.types.SmSimpleType;

public interface SmSchema<A> extends SmComponentBag<A>, SmComponentProvider<A>
{
	void declareAttribute(final SmAttribute<A> attribute);

	void declareElement(final SmElement<A> element);

	void declareNotation(final SmNotation<A> notation);

	void defineAttributeGroup(final SmAttributeGroup<A> attributeGroup);

	void defineComplexType(final SmComplexType<A> complexType);

	void defineIdentityConstraint(final SmIdentityConstraint<A> identityConstraint);

	void defineModelGroup(final SmModelGroup<A> modelGroup);

	void defineSimpleType(final SmSimpleType<A> simpleType);

	Iterable<String> getNamespaces();

	void register(SmComponentBag<A> components);
}
