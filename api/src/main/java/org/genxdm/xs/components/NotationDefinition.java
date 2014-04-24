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


/**
 * Notation
 */
public interface NotationDefinition extends SchemaComponent
{
    /**
     * Returns the notation's {system identifier} property. Optional if {public identifier} is present. Must be a valid
     * URI reference.
     */
    String getSystemId();

    /**
     * Returns the notation's {public identifier} property. Optional if {system identifier} is present. Must be valid
     * per ISO 8879.
     */
    String getPublicId();
}
