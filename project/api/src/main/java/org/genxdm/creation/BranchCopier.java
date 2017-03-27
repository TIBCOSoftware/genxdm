package org.genxdm.creation;

import org.genxdm.io.ContentGenerator;

/** A tool to copy an existing in-memory XML tree into some other tool,
 * presumptively one of the ContentHelper tools in this package.
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
