/*
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
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;

/**
 * Defines a bag of schema components.
 * <p>
 * Typically used for the bulk-loading of components into a schema.
 * </p>
 * 
 */
public interface ComponentBag
{
    /**
     * Returns the Attribute group definitions.
     */
    Iterable<AttributeGroupDefinition> getAttributeGroups();

    /**
     * Returns the Attribute declarations.
     */
    Iterable<AttributeDefinition> getAttributes();

    /**
     * Returns the Complex type definitions.
     */
    Iterable<ComplexType> getComplexTypes();

    /**
     * Returns the Element declarations.
     */
    Iterable<ElementDefinition> getElements();

    /**
     * Returns the Identity-constraint definitions.
     */
    Iterable<IdentityConstraint> getIdentityConstraints();

    /**
     * Returns the Model group definitions.
     */
    Iterable<ModelGroup> getModelGroups();

    /**
     * Returns the Notation declarations.
     */
    Iterable<NotationDefinition> getNotations();

    /**
     * Returns the Simple type definitions.
     */
    Iterable<SimpleType> getSimpleTypes();
}
