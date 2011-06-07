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
package org.genxdm.processor.w3c.xs.impl;

import java.util.HashMap;

import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.resolve.LocationInSchema;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;

/**
 * Used by the {@link XMLSchemaConverter} to keep track of the locations of created schema components so that errors may
 * be reported using locations.
 */
final class XMLComponentLocator
{
	public final HashMap<SimpleType, LocationInSchema> m_simpleTypeLocations = new HashMap<SimpleType, LocationInSchema>();
	public final HashMap<ComplexType, LocationInSchema> m_complexTypeLocations = new HashMap<ComplexType, LocationInSchema>();
	public final HashMap<ElementDefinition, LocationInSchema> m_elementLocations = new HashMap<ElementDefinition, LocationInSchema>();
	public final HashMap<AttributeDefinition, LocationInSchema> m_attributeLocations = new HashMap<AttributeDefinition, LocationInSchema>();
	public final HashMap<ModelGroup, LocationInSchema> m_modelGroupLocations = new HashMap<ModelGroup, LocationInSchema>();
	public final HashMap<AttributeGroupDefinition, LocationInSchema> m_attributeGroupLocations = new HashMap<AttributeGroupDefinition, LocationInSchema>();
	public final HashMap<IdentityConstraint, LocationInSchema> m_constraintLocations = new HashMap<IdentityConstraint, LocationInSchema>();
	public final HashMap<NotationDefinition, LocationInSchema> m_notationLocations = new HashMap<NotationDefinition, LocationInSchema>();
	public final HashMap<SchemaParticle, LocationInSchema> m_particleLocations = new HashMap<SchemaParticle, LocationInSchema>();
}
