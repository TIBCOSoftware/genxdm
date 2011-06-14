package org.genxdm.names;

import java.net.URI;

/** Provides a very simple interface for XML Catalogs.
 * 
 * Note that this interface does not, by design, attempt to represent all of XML
 * Catalog.  It may be regarded, instead, as the 'effective' catalog resulting
 * from application of the rules found in XML Catalog files.  Consequently, it
 * maps single System and Public IDs to URIs, and URIs to other URIs.  It is
 * not recursive.  It does not support rewriting or delegating.  It is a very
 * simple API, which can be manipulated in advance (and updated in use) to
 * store and retrieve fully-resolved mappings. 
 *
 */
public interface Catalog
{
    boolean isMappedPublicId(final String publicId);
    
    boolean isMappedSystemId(final URI systemId);
    
    boolean isMappedURI(final URI uri);
    
    void mapPublicId(final String publicId, final URI uri);
    
    void mapSystemId(final URI systemId, final URI uri);
    
    void mapURI(final URI original, final URI uri);
    
    URI retrievePublicId(final String publicId);
    
    URI retrieveSystemId(final URI systemId);
    
    URI retrieveURI(final URI original);
}
