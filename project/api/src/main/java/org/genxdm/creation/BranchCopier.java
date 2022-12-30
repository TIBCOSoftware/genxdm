package org.genxdm.creation;

import org.genxdm.io.ContentGenerator;

/** A tool to copy an existing in-memory XML tree, or more commonly 
 * a sequence of content events (method calls on a ContentHandler) 
 * such as an implementation of EventQueue, into some other tree
 * (or target that exposes a ContentHandler interface).
 *
 * BranchCopier is typically a secondary interface implemented by a
 * class that implements ContentHelper.
 * 
 */
public interface BranchCopier
{
    /** Couple the ContentGenerator's write() method to a ContentHandler
     * of some sort in order to copy the content of the branch into the
     * target.
     * 
     * @param generator the generator of method calls into ContentHandler;
     *        must not be null
     */
    void copyTreeAt(ContentGenerator generator);
}
