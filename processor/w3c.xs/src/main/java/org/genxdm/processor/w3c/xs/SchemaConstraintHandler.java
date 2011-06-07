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
package org.genxdm.processor.w3c.xs;

import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;

public interface SchemaConstraintHandler
{
    void error(SimpleType simpleType, SchemaException exception) throws AbortException;

    void error(ComplexType complexType, SchemaException exception) throws AbortException;

    void error(AttributeDefinition attribute, SchemaException exception) throws AbortException;

    void error(ElementDefinition element, SchemaException exception) throws AbortException;

    void error(ModelGroup modelGroup, SchemaException exception) throws AbortException;

    void error(AttributeGroupDefinition attributeGroup, SchemaException exception) throws AbortException;

    void error(IdentityConstraint constraint, SchemaException exception) throws AbortException;

    void error(NotationDefinition notation, SchemaException exception) throws AbortException;

    void error(SchemaParticle particle, SchemaException exception) throws AbortException;
}
