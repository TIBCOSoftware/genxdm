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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;


/**
 * Resolves requests for XML documents based upon a URI.
 */
public interface Resolver
{
	/**
	 * Resolves a {@link URI} into an {@link InputStream} and a systemID.
	 * 
	 * @param location
	 *            The {@link URI} to be resolved.
	 * @throws IOException
	 *             if an exception occurs while opening the {@link InputStream}.
	 */
	Resolved<InputStream> resolveInputStream(URI location) throws IOException;
	
	Resolved<Reader> resolveReader(URI location) throws IOException;
}
