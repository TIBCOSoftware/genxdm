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
package org.genxdm.xs.enums;

/**
 * Process Contents controls the impact on assessment of the information items allowed by wildcards.
 */
public enum ProcessContentsMode
{
    /**
     * Indicates that the processor should try to validate the contents of the element when it can. If the item has a
     * uniquely determined declaration available, it must be valid with respect to that definition.
     */
    Lax,

    /**
     * Indicates that the processor should validate the contents of the element according to the namespace given. There
     * must be a top-level declaration for the item available, or the item must have an <code>xsi:type</code>, and the
     * item must be valid as appropriate.
     */
    Strict,

    /**
     * (The default) indicates that the processor should not try to validate the content within this element. No
     * constraint at all: the item must simply be well-formed XML.
     */
    Skip
}
