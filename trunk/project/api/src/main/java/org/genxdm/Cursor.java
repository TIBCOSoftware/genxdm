package org.genxdm;

import org.genxdm.axes.Navigator;
import org.genxdm.io.ContentWriter;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;

/** A &lt;N>ode-free cursor-light abstraction.
 * 
 * <p>
 * Precursor supports the combination of navigation over and information about
 * a tree of nodes, without ever exposing the &lt;N>ode abstraction directly.
 * It may be used, for instance, to do navigation over an apparent tree without
 * having clearly defined "nodes" from the underlying data structure.
 * Processors can free themselves from knowledge of the underlying tree model
 * by specifying a Precursor argument rather than a Cursor.
 * </p>
 * 
 * @see Traverser
 * @see TraversingInformer
 */
public interface Cursor
    extends TraversingInformer, Navigator, ContentWriter
{
}
