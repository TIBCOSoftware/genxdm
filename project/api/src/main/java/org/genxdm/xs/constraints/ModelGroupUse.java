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

import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.SchemaParticle;

/**
 * Describes the use of a model group.
 * 
 */
public interface ModelGroupUse 
    extends SchemaParticle
{
    /**
     * Returns the {particle term} property for this usage which is a model group.
     */
    ModelGroup getTerm();

    /**
     * Determines whether this particle is Emptiable (See W3C XML Schema Definition Language (XSD) Part 1: Structures).
     */
    boolean isEmptiable();
}
