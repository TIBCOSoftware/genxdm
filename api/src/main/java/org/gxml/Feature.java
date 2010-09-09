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
package org.gxml;

public interface Feature
{
    static final String PREFIX = "http://gxml.apache.org/features/";
	static final String ATTRIBUTE_AXIS_INHERIT = PREFIX + "axis/attribute/inherit";
	static final String DOCUMENT_URI = PREFIX + "document-uri";
	static final String NAMESPACE_AXIS = PREFIX + "axis/namespace";
	static final String TYPE_ANNOTATION = PREFIX + "annotation/type";
	static final String TYPED_VALUE = PREFIX + "typed-value";

	static final String UNSUPPORTED_MESSAGE = "Feature is not supported";
}
