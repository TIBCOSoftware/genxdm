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
package org.genxdm.xs.types;

public interface PrimeType extends SequenceType
{
    PrimeTypeKind getKind();

    boolean subtype(PrimeType rhs);

    boolean isChoice();

    /**
     * Determines whether this type is the "none" type.
     * <p>
     * This is a convenience method equivalent to checking the kind for the "none" type.
     * </p>
     */
    boolean isNone();

    /**
     * Determines whether this type is one of the W3C XML Schema Built-in types.
     */
    boolean isNative();
}
