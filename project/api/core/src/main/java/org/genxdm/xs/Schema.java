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
package org.genxdm.xs;

import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ComponentBag;
import org.genxdm.xs.components.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;

public interface Schema extends ComponentBag, ComponentProvider
{
    void declareAttribute(final AttributeDefinition attribute);

    void declareElement(final ElementDefinition element);

    void declareNotation(final NotationDefinition notation);

    void defineAttributeGroup(final AttributeGroupDefinition attributeGroup);

    void defineComplexType(final ComplexType complexType);

    void defineIdentityConstraint(final IdentityConstraint identityConstraint);

    void defineModelGroup(final ModelGroup modelGroup);

    void defineSimpleType(final SimpleType simpleType);

    Iterable<String> getNamespaces();

    void register(ComponentBag components);
}
