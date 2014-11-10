/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
import java.util.HashMap;
import java.util.Map;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.Catalog;

public class DefaultCatalog
    implements Catalog
{

    @Override
    public boolean isMappedPublicId(String publicId)
    {
        return pubs.containsKey(publicId);
    }

    @Override
    public boolean isMappedSystemId(String systemId)
    {
        return systems.containsKey(systemId);
    }

    @Override
    public boolean isMappedURI(URI uri)
    {
        return uris.containsKey(uri);
    }

    @Override
    public void mapPublicId(String publicId, URI uri)
    {
        PreCondition.assertNotNull(publicId, "key");
        if (uri == null)
            pubs.remove(publicId);
        else
            pubs.put(publicId, uri);
    }

    @Override
    public void mapSystemId(String systemId, URI uri)
    {
        PreCondition.assertNotNull(systemId, "key");
        if (uri == null)
            systems.remove(systemId);
        else
            systems.put(systemId, uri);
    }

    @Override
    public void mapURI(URI original, URI uri)
    {
        PreCondition.assertNotNull(original, "key");
        if (original == null)
            uris.remove(original);
        else
            uris.put(original, uri);
    }

    @Override
    public URI retrievePublicId(String publicId)
    {
        return pubs.get(publicId);
    }

    @Override
    public URI retrieveSystemId(String systemId)
    {
        return systems.get(systemId);
    }

    @Override
    public URI retrieveURI(URI original)
    {
        return uris.get(original);
    }

    private final Map<String, URI> pubs = new HashMap<String, URI>();
    private final Map<String, URI> systems = new HashMap<String, URI>();
    private final Map<URI, URI> uris = new HashMap<URI, URI>();
}
