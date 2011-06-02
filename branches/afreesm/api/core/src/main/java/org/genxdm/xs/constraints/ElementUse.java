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
package org.genxdm.xs.constraints;

import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.SchemaParticle;

/**
 * Describes the use of an element declaration.
 * 
 */
// TODO: the commented-out bits here were commented-out when we found them.
// why are they here?  Remove them, or uncomment them.
public interface ElementUse extends SchemaParticle/* , HasValueConstraint<A> */
{
    ElementDefinition getTerm();

    /**
     * Returns the effective value constraint, may be <code>null</code>. [Definition:] Let the effective value
     * constraint of an attribute use be its {value constraint}, if present, otherwise its {attribute declaration}'s
     * {value constraint}.
     */
    /* ValueConstraint<A> getEffectiveValueConstraint(); */
}
