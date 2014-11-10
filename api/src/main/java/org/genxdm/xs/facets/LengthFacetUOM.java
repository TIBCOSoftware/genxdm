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
package org.genxdm.xs.facets;

/**
 * Indicates the Unit of Measure for the length facet of a simple type value.
 */
public enum LengthFacetUOM
{
    /**
     * Used for xs:string, types derived from xs:string, and xs:anyURI.
     * Indicates the number of characters in the atomic value.
     */
    Characters,

    /**
     * Used for xs:hexBinary and xs:base64Binary.
     * Indicates the number of octets (8 bits) of binary data in the atomic value.
     */
    Octets,

    /**
     * Used for types derived by list.
     * Indicates the number of list items.
     */
    ListItems,

    /**
     * Used to indicate that the length facet is not applicable for this type.
     */
    NotApplicable
}
