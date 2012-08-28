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

import org.genxdm.xs.types.AttributeNodeType;
import org.genxdm.xs.types.SimpleType;

/**
 * An attribute declaration, which may be global or local to some complex type.
 */
public interface AttributeDefinition extends AttributeNodeType, SchemaDataComponent
{
    /**
     * Returns the {type definition} for the attribute declaration.
     * <p>
     * This may be a simple type definition or the simple ur-type definition.
     * </p>
     */
    SimpleType getType();
}
