package org.genxdm.io;

/** Provide a means of chaining content handlers.
 * 
 * Chain content handlers by allowing an output content handler to
 * be set. The outer handler presumably provides some form of modification
 * to the method calls that it is given, prior to passing them to the
 * output content handler.
 * 
 * This could be used, for instance, to extract a subset of an XML document
 * (filter out processing instructions and comments, or "insignificant white
 * space", for instance; or do namespace fixup, or something of the sort).
 */
public interface ContentFilter
    extends ContentHandler
{
    void setOutputContentHandler(ContentHandler output);
}
