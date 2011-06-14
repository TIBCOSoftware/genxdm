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
package org.genxdm.processor.w3c.xs;

import java.net.URI;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.Catalog;
import org.genxdm.xs.resolve.SchemaCatalog;

public class DefaultSchemaCatalog implements SchemaCatalog
{
    public DefaultSchemaCatalog(Catalog catalog)
    {
        this.catalog = PreCondition.assertNotNull(catalog, "catalog");
    }
    
    public URI resolveLocation(final URI baseURI, final URI schemaLocation)
    {
        URI location = PreCondition.assertNotNull(schemaLocation, "schemaLocation");
        if ((baseURI != null) && !schemaLocation.isAbsolute())
            location = makeAbsolute(baseURI, schemaLocation);
        if (catalog.isMappedURI(location))
            location = catalog.retrieveURI(location);
        return location;
    }

    public URI resolveNamespaceAndSchemaLocation(URI baseURI, URI namespace, URI schemaLocation)
    {
        URI location = null;
        if (schemaLocation != null)
            location = resolveLocation(baseURI, schemaLocation);
        if (location == null)
        {
            location = namespace;
            if ((baseURI != null) && !namespace.isAbsolute())
                location = makeAbsolute(baseURI, namespace);
            if (catalog.isMappedURI(location))
                location = catalog.retrieveURI(location);
        }
        return location;
    }
    
    private URI makeAbsolute(final URI baseURI, final URI relativeURI)
    {
        return baseURI.resolve(relativeURI);
    }

    private final Catalog catalog;
}
