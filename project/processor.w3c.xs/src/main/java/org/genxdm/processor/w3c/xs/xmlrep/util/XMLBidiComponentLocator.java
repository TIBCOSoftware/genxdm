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
package org.genxdm.processor.w3c.xs.xmlrep.util;

import org.genxdm.bridgekit.misc.ReversibleHashMap;
import org.genxdm.bridgekit.misc.ReversibleMap;
import org.genxdm.processor.w3c.xs.impl.XMLSchemaConverter;
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
 * 
 * This version requires that both sides be unique, which seems to be the case,
 * and is much less likely to produce a memory leak as a result. It replaces
 * the deprecated XMLComponentLocator, but doesn't simply change it because we
 * know of at least one external implementation.
 */
public final class XMLBidiComponentLocator
{
    public final ReversibleMap<LocationInSchema, SimpleType> m_simpleTypeLocations = new ReversibleHashMap<LocationInSchema, SimpleType>();
    public final ReversibleMap<LocationInSchema, ComplexType> m_complexTypeLocations = new ReversibleHashMap<LocationInSchema, ComplexType>();
    public final ReversibleMap<LocationInSchema, ElementDefinition> m_elementLocations = new ReversibleHashMap<LocationInSchema, ElementDefinition>();
    public final ReversibleMap<LocationInSchema, AttributeDefinition> m_attributeLocations = new ReversibleHashMap<LocationInSchema, AttributeDefinition>();
    public final ReversibleMap<LocationInSchema, ModelGroup> m_modelGroupLocations = new ReversibleHashMap<LocationInSchema, ModelGroup>();
    public final ReversibleMap<LocationInSchema, AttributeGroupDefinition> m_attributeGroupLocations = new ReversibleHashMap<LocationInSchema, AttributeGroupDefinition>();
    public final ReversibleMap<LocationInSchema, IdentityConstraint> m_constraintLocations = new ReversibleHashMap<LocationInSchema, IdentityConstraint>();
    public final ReversibleMap<LocationInSchema, NotationDefinition> m_notationLocations = new ReversibleHashMap<LocationInSchema, NotationDefinition>();
    public final ReversibleMap<LocationInSchema, SchemaParticle> m_particleLocations = new ReversibleHashMap<LocationInSchema, SchemaParticle>();
}
