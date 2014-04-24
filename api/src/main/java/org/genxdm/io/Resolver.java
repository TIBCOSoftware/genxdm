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
package org.genxdm.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


/**
 * Resolves requests for XML documents based upon a URI.
 */
public interface Resolver
{
    /**
     * Resolves a {@link String} into an {@link InputStream} and a systemID.
     * 
     * @param baseURI
     *            A {@link String} to be used in resolution. This represents the base URI from
     *            which relative URIs are resolved.  May be null if location is null or if location
     *            is absolute.
     * @param location
     *            A {@link String} to be used in resolution. This represents the 'expected'
     *            or 'canonical' location for the target. May be null if the namespace argument
     *            is non-null.
     * @param namespace
     *            A {@link String} to be used in resolution. This represents the 'identifier'
     *            for the target, such as the target namespace for a schema-like document.
     *            May be null if the location argument is non-null.
     * @return the resolved document, or null if the document cannot be resolved. 
     * @throws IOException
     *             if an exception occurs while opening the {@link InputStream}.
     */
    Resolved<InputStream> resolveInputStream(String baseURI, String location, String namespace) throws IOException;
    
    /**
     * Resolves a {@link String} into an {@link InputStream} and a systemID.
     * 
     * @param baseURI
     *            A {@link String} to be used in resolution. This represents the base URI from
     *            which relative URIs are resolved.  May be null if location is null or if location
     *            is absolute.
     * @param location
     *            A {@link String} to be used in resolution. This represents the 'expected'
     *            or 'canonical' location for the target. May be null if the namespace argument
     *            is non-null.
     * @param namespace
     *            A {@link String} to be used in resolution. This represents the 'identifier'
     *            for the target, such as the target namespace for a schema-like document.
     *            May be null if the location argument is non-null.
     * @return the resolved document, or null if the document cannot be resolved. 
     * @throws IOException
     *             if an exception occurs while opening the {@link InputStream}.
     */
    Resolved<Reader> resolveReader(String baseURI, String location, String namespace) throws IOException;
}
