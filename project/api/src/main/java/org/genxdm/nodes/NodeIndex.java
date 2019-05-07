package org.genxdm.nodes;

/** An abstraction that represents the position of a node within a tree by
 * an integer, with an 'isparent' indicator.
 *
 * The int represents either the parent node's index, where parent node must be an
 * element or document,
 * or the actual index of the node itself if and only if the node is an element
 * or document.
 * 
 * Most comparisons between non-element nodes are likely to be between non-element
 * children (or attributes or namespaces) in the scope of another element; comparing
 * the element indices is sufficient. Comparing an element against its own non-element
 * child (or attribute or namespace) returns the same int, but the child/namespace/attribute
 * is necessarily later in the document than its parent/container.
 * 
 * Within the scope of a single element namespace precede attributes, which
 * precede all children. Namespaces and attributes have canonical order which
 * defines their position as amongst one another (ascending lexicographic order
 * of serial form of qualified names); they can be compared directly in a
 * comparator for NodeIndex with two N-s. Child content is potentially more
 * complicated, but in practice the only node kind that commonly appears as
 * multiple children of a single parent is element, which is itself indexed.
 * Text nodes for simple values are next most common, but per the XDM spec, there
 * can be only one. Comment and processing instruction node children are
 * seldom encountered; if they must be sorted, it's likely to cost a bit
 * extra.
 * 
 * Expensive operations: sorting the children of elements that contain mixed
 * content, sorting the XML prolog. Element to element comparison (at least the
 * eighty percent case) is dead cheap, and most other comparisons (which compare
 * non-equal element to element, or compare non-equal node types with implicit
 * order in the scope of the same element, or equal node types with implicit order
 * in the scope of the same element) take us into the 90+% range. This seems
 * an acceptable design, for trees that are typically rarely mixed content.
 * 
 * For cases that make heavy use of mixed content, it would be best to not
 * use this in-tree indexing optimization, but instead to accept the cost of
 * a pre-processing pass over the entire tree to assign indexes by node ID.
 */
public interface NodeIndex
{
    /** For document and element nodes, its own index; for all other node
     * kinds its parent's index.
     *
     * Note that namespace and attribute nodes have canonical order and can
     * be sorted by that means (within the parent node), when needed.
     *
     * Text nodes, except within mixed content, are expected to appear only
     * once within an element. Comments and processing instructions may be
     * moved by the parser when text nodes are merged. Thus maintaining the
     * order of these things independently is potentially mistaken.
     *
     * For all five unindexed node kinds, the number of child nodes (including
     * elements in mixed content or as children of documents; text; comments;
     * processing instructions), of namespaces, and of attributes within a
     * single parent (including the document node) is most often going to
     * so small than creating a small ordered collection for distinguishing
     * these nodes will be generally faster than dealing with index volatility
     * if they were assigned numbers.
     */
    int getValue();
    
    /** A quick indicator of whether the returned index value is the
     * parent's or this node's.
     * 
     * Only element and document nodes have their own indexes; this is
     * simply a fast way to indicate that the value, for this node, refers
     * to the parent, not to itself. Equal indexes for nodes themselves is
     * an error; for children or namespaces or attributes is common.
     */
    boolean isParentIndex();
}