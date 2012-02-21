/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm;

public interface Feature
{
    static final String PREFIX = "http://genxdm.org/features/";

    // return true if the three attributes xml:space, xml:lang, and xml:base are supported as scoped attributes
    static final String ATTRIBUTE_AXIS_INHERIT = PREFIX + "axis/attribute/inherit";
    // return true if there is an N representing namespace nodes.
    static final String NAMESPACE_AXIS = PREFIX + "axis/namespace";

    // return true if the document uri is preserved
    static final String DOCUMENT_URI = PREFIX + "uri/document";
    // return true if xml:base is supported
    static final String BASE_URI = PREFIX + "uri/base";
    
    // return true if there is a mutable extension for this bridge.
    // getMutableContext must return non-null
    static final String MUTABILITY = PREFIX + "mutable";
    
    // return true if there is a typed extension for this bridge.
    // getTypedContext must return non-null.
    // at least one of TYPE_ANNOTATION or TYPED_VALUE must also be true
    static final String TYPED = PREFIX + "type";
    // if type annotations are supported, return true.
    // must not return true if TYPED is false
    // must return type annotations if this is true
    static final String TYPE_ANNOTATION = PREFIX + TYPED + "/annotation";
    // if typed values are supported, return true.
    // must not return true if TYPED is false
    // must return atoms (not just untyped-atomic) if true.
    static final String TYPED_VALUE = PREFIX + TYPED + "/typed-value";
    

    static final String UNSUPPORTED_MESSAGE = "Feature is not supported";
}
