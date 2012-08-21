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

import org.genxdm.xs.components.AttributeDefinition;

/**
 * Describes the use of an attribute declaration.
 */
public interface AttributeUse extends HasValueConstraint
{
    /**
     * Returns the {required} property. <br/>
     * In the XML representation, this is <code>true</code> if the use [attribute] is present with actual value
     * required, otherwise <code>false</code>.
     */
    boolean isRequired();

    /**
     * Returns the {attribute declaration} property. <br/>
     * In the XML representation, this is the (top-level) attribute declaration resolved by the actual value of the ref
     * [attribute].
     */
    AttributeDefinition getAttribute();

    /**
     * Returns the effective value constraint, may be <code>null</code>. [Definition:] Let the effective value
     * constraint of an attribute use be its {value constraint}, if present, otherwise its {attribute declaration}'s
     * {value constraint}.
     */
    ValueConstraint getEffectiveValueConstraint();
}
