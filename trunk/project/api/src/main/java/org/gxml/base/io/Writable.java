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
package org.gxml.base.io;

import java.io.IOException;

import org.gxml.exceptions.GxmlMarshalException;

/**
 * Defines an XML abstraction as being able to be written to a {@link ContentHandler}.
 */
public interface Writable
{
	/**
	 * Streams the contents of the tree to a {@link ContentHandler}.
	 * 
	 * @param writer
	 *            The output writer.
	 */
	void write(ContentHandler writer) throws IOException, GxmlMarshalException;

}
