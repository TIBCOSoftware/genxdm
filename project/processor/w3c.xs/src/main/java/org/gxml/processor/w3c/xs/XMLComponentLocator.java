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
package org.gxml.processor.w3c.xs;

import java.util.HashMap;

import org.gxml.xs.components.SmAttribute;
import org.gxml.xs.components.SmAttributeGroup;
import org.gxml.xs.components.SmElement;
import org.gxml.xs.components.SmModelGroup;
import org.gxml.xs.components.SmNotation;
import org.gxml.xs.components.SmParticle;
import org.gxml.xs.constraints.SmIdentityConstraint;
import org.gxml.xs.resolve.SmLocation;
import org.gxml.xs.types.SmComplexType;
import org.gxml.xs.types.SmSimpleType;

/**
 * Used by the {@link XMLSchemaConverter} to keep track of the locations of created schema components so that errors may
 * be reported using locations.
 */
final class XMLComponentLocator<A>
{
	public final HashMap<SmSimpleType<A>, SmLocation> m_simpleTypeLocations = new HashMap<SmSimpleType<A>, SmLocation>();
	public final HashMap<SmComplexType<A>, SmLocation> m_complexTypeLocations = new HashMap<SmComplexType<A>, SmLocation>();
	public final HashMap<SmElement<A>, SmLocation> m_elementLocations = new HashMap<SmElement<A>, SmLocation>();
	public final HashMap<SmAttribute<A>, SmLocation> m_attributeLocations = new HashMap<SmAttribute<A>, SmLocation>();
	public final HashMap<SmModelGroup<A>, SmLocation> m_modelGroupLocations = new HashMap<SmModelGroup<A>, SmLocation>();
	public final HashMap<SmAttributeGroup<A>, SmLocation> m_attributeGroupLocations = new HashMap<SmAttributeGroup<A>, SmLocation>();
	public final HashMap<SmIdentityConstraint<A>, SmLocation> m_constraintLocations = new HashMap<SmIdentityConstraint<A>, SmLocation>();
	public final HashMap<SmNotation<A>, SmLocation> m_notationLocations = new HashMap<SmNotation<A>, SmLocation>();
	public final HashMap<SmParticle<A>, SmLocation> m_particleLocations = new HashMap<SmParticle<A>, SmLocation>();
}
