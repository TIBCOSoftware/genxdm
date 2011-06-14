package org.genxdm.processor.w3c.xs;

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
    public boolean isMappedSystemId(URI systemId)
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
    public void mapSystemId(URI systemId, URI uri)
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
    public URI retrieveSystemId(URI systemId)
    {
        return systems.get(systemId);
    }

    @Override
    public URI retrieveURI(URI original)
    {
        return uris.get(original);
    }

    private final Map<String, URI> pubs = new HashMap<String, URI>();
    private final Map<URI, URI> systems = new HashMap<URI, URI>();
    private final Map<URI, URI> uris = new HashMap<URI, URI>();
}
