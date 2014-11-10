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
package org.genxdm.xs.resolve;

import java.net.URI;

/**
 * Used during schema loading to convert URIs in physical schema documents to systemIds used by a resolver.
 * <p>
 * Clients will implement the catalog interface to convert "logical" URIs in their schema documents to "physical" URIs
 * that are used for resolving byte streams.
 * </p>
 */
public interface SchemaCatalog
{
    /**
     * Invoked for xsi:schemaLocation hints.
     * 
     * @param baseURI
     *            The base URI of the containing schema document.
     * @param namespace
     *            The namespace specified by the hint.
     * @param schemaLocation
     *            The schema location specified.
     * @return A systemId for use by a resolver.
     */
    URI resolveNamespaceAndSchemaLocation(URI baseURI, String namespace, String schemaLocation);

    /**
     * Invoked for xsi:noNamespaceSchemaLocation hints.
     * 
     * @param baseURI
     *            The base URI of the containing schema document.
     * @param schemaLocation
     *            The schema location specified.
     * @return A systemId for use by a resolver.
     */
    URI resolveLocation(URI baseURI, String schemaLocation);
}
