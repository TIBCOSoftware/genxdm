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
package org.genxdm.bridgekit.xs;

import java.net.URI;

import org.genxdm.bridgekit.misc.StringToURIParser;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.Catalog;
import org.genxdm.xs.resolve.SchemaCatalog;

/** Default SchemaCatalog implementation.
 *
 * This implementation wants an implementation of Catalog that it can
 * delegate to, at construction time.  Its general technique is to absolutize
 * supplied URIs (that is, if there's a base and a relative location,
 * first make an absolute location that joins the two; if location is not
 * good, then try the same with namespace).  Once the absolute URI is
 * created, check in the supplied catalog, ignoring system IDs and public
 * IDs, for URI mappings.
 *
 */
public class DefaultSchemaCatalog implements SchemaCatalog
{
    public DefaultSchemaCatalog(Catalog catalog)
    {
        this.catalog = PreCondition.assertNotNull(catalog, "catalog");
    }
    
    public URI resolveLocation(final URI baseURI, final String schemaLocation)
    {
        URI location = StringToURIParser.parse(PreCondition.assertNotNull(schemaLocation, "schemaLocation"));
        if ((baseURI != null) && !location.isAbsolute())
            location = baseURI.resolve(location);
        if (catalog.isMappedURI(location))
            location = catalog.retrieveURI(location);
        return location;
    }

    public URI resolveNamespaceAndSchemaLocation(URI baseURI, String namespace, String schemaLocation)
    {
        URI location = null;
        if (schemaLocation != null)
            location = resolveLocation(baseURI, schemaLocation);
        if (location == null)
        {
            location = resolveLocation(baseURI, namespace);
            if (catalog.isMappedURI(location))
                location = catalog.retrieveURI(location);
        }
        return location;
    }
    
    private final Catalog catalog;
}
