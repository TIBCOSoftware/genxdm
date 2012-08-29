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

import java.util.Set;

import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.Type;

/**
 * The {@link ElementDefinition} interface models an element declaration, which could be global within the schema or
 * local to some other element. <br/>
 * An element declaration is an association between a name and a type.
 */
public interface ElementDefinition extends ElementNodeType, SchemaDataComponent, ParticleTerm
{
    /**
     * {disallowed substitutions} property. <br/>
     * Returns the set of derivation methods that are blocked from substitution. Determines whether this element can be
     * used for a xsi:type or substitution group substitution.
     * <p/>
     * This is a instance constraint.
     */
    Set<DerivationMethod> getDisallowedSubtitutions();

    /**
     * Returns the {identity-constraints definitions} for this element.
     */
    Iterable<IdentityConstraint> getIdentityConstraints();

    /**
     * The {scope} property.
     */
    ScopeExtent getScopeExtent();

    /**
     * The element declaration resolved to by the actual value of the substitutionGroup [attribute], if present,
     * otherwise <code>null</code>.
     * <p/>
     * Returns the head element of the substitution group that this element belongs to. May be <code>null</code> if there
     * is no substitution group for this element.
     */
    ElementDefinition getSubstitutionGroup();

    /**
     * Returns the set of derivation methods that are final. Determines whether this element can be used as the head of
     * a substitution group for elements whose types are derived by extension or restriction from the type of the
     * element.
     * <p/>
     * This is a schema constraint.
     */
    Set<DerivationMethod> getSubstitutionGroupExclusions();

    /**
     * Returns the elements that are members of the group with this element as the head.
     */
    Iterable<ElementDefinition> getSubstitutionGroupMembers();

    /**
     * The {type definition} property.
     */
    Type getType();

    /**
     * Determines (quickly) whether this element has {identity-constraints definitions}.
     */
    boolean hasIdentityConstraints();

    /**
     * Determines whether this element declaration is affiliated with a substitution group.
     */
    boolean hasSubstitutionGroup();

    /**
     * Determines if the substitution group, with this element as the head, has members.
     */
    boolean hasSubstitutionGroupMembers();

    /**
     * Returns the {abstract} property for this element.
     * 
     * @return <code>true</code> if abstract, otherwise <code>false</code>.
     */
    boolean isAbstract();
}
