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

/**
 * A type that is a union of other simple types.
 * 
 * @param <A>
 *            The atom handle.
 */
public interface UnionSimpleType<A> extends SimpleType<A>
{
    /**
     * Returns the {base type definition} of this union type.
     */
    SimpleType<A> getBaseType();

    /**
     * Returns the member types for union and list simple types. For list types, there will be a single member type.
     */
    Iterable<SimpleType<A>> getMemberTypes();
}
