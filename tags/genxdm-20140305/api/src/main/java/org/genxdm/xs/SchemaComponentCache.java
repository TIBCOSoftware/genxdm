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

/** A SchemaComponent registry.
 * 
 * This interface declares methods for provisioning a schema context.
 * register() provides a bulk-load: generate all the components
 * (somehow) and then register them all.  The other methods provide
 * component-kind declarations and definitions.  See ComponentBag
 * for methods that iterate over each of these component kinds.
 * See ComponentProvider for methods that access components by name.
 *
 */
public interface SchemaComponentCache
{
    void declareAttribute(final AttributeDefinition attribute);

    void declareElement(final ElementDefinition element);

    void declareNotation(final NotationDefinition notation);

    void defineAttributeGroup(final AttributeGroupDefinition attributeGroup);

    void defineComplexType(final ComplexType complexType);

    void defineIdentityConstraint(final IdentityConstraint identityConstraint);

    void defineModelGroup(final ModelGroup modelGroup);

    void defineSimpleType(final SimpleType simpleType);
    
    // TODO: overload with a namespace argument?
    ComponentProvider getComponentProvider();
    
    ComponentBag getComponents();
    
    // TODO: overload with a namespace argument?
//    ComponentBag getComponents(String namespace);

    Iterable<String> getNamespaces();
    
    /**
     * Determines whether this component collection is currently in a locked state.
     * 
     * @return true if lock() has been called; false otherwise.
     */
    boolean isLocked();

    /** Lock this schema component collection, preventing registry of additional
     * schema components.
     * 
     * The component collection is in an unlocked state after it has been created. 
     * While in this state an implementation is not required to provide safe 
     * multi-threaded access. Locking guarantees that 
     * the component collection is safe for access by different threads. 
     */
    void lock();

    void register(ComponentBag components);
}
