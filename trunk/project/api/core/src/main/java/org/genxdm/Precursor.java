package org.genxdm;

import org.genxdm.axes.Navigator;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.ContentHandler;
import org.genxdm.nodes.Informer;

/** A &lt;N>ode-free cursor-light abstraction.
 * 
 * Precursor supports the combination of navigation over and information about
 * a tree of nodes, without ever exposing the &lt;N>ode abstraction directly.
 * It may be used, for instance, to do navigation over a tree without caring
 * about the tree.  It is never implemented directly (only as {@link org.genxdm.Cursor}),
 * but processors could theoretically free themselves from knowledge of the
 * underlying tree model by specifying a Precursor argument rather than a
 * Cursor.
 */
public interface Precursor
    extends Informer, Navigator
{
    /**
     * Support for streaming the current node to a {@link ContentHandler}.
     * 
     * @param writer
     *            The handler for events generated.
     * 
     * @throws GenXDMException
     */
    void write(ContentHandler writer) throws GenXDMException;

}
