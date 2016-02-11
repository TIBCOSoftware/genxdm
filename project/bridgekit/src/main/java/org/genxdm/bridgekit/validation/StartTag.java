package org.genxdm.bridgekit.validation;

import java.util.List;
import java.util.Map;

import org.genxdm.exceptions.PreCondition;

//this is basically a C struct, implementing an interface that
//allows us to differentiate between text nodes and element startTags.
//it's used in in-tree validation, by implementations of validating cursor.
//this one represents an element start tag, containing a reference to the
//element itself, the attributes inside the element's start tag, and the
//defined namespaces, all exposed directly. the bridge may set the type
//of the element and the type and value of attributes during validation;
//it should also track that the namespace bindings fired by the validator
//are not different from the ones fired into the validator.
//by defining the interface, the
//validating cursor is able to keep track of the nodes for which it has
//fired events in a standard java collection
public class StartTag<N>
    implements ChildNode<N>
{
    public StartTag(N el)
    {
        element = PreCondition.assertNotNull(el, "element");
    }
    
    public final N element; // never null
    public List<N> attributes; // may be null! often is
    public Map<String, String> bindings; // may be null! often is

    @Override
    public boolean isStartTag()
    {
        return true;
    }

    @Override
    public boolean isText()
    {
        return false;
    }

}
