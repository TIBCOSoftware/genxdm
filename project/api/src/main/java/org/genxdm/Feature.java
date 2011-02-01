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
package org.genxdm;

public interface Feature
{
    static final String PREFIX = "http://genxdm.org/features/";
    
    // TODO: are these reasonable?  If they're in use, then they
    // should be referenced at the point that the unsupported feature
    // provides a different-than-usual return or behavior.
	static final String ATTRIBUTE_AXIS_INHERIT = PREFIX + "axis/attribute/inherit";
    static final String NAMESPACE_AXIS = PREFIX + "axis/namespace";

    static final String DOCUMENT_URI = PREFIX + "uri/document";
    static final String BASE_URI = PREFIX + "uri/base";
	
    static final String TYPE_ANNOTATION = PREFIX + "type/annotation";
	static final String TYPED_VALUE = PREFIX + "type/typed-value";
	
	static final String MUTABILITY = PREFIX + "mutable";
	static final String TYPED = PREFIX + "type";

	static final String UNSUPPORTED_MESSAGE = "Feature is not supported";
}
