package org.genxdm.bridgekit.validation;

import org.genxdm.exceptions.PreCondition;

// this is basically a C struct, implementing an interface that
// allows us to differentiate between text nodes and element startTags.
// it's used in in-tree validation, by implementations of validating cursor.
// the content is a reference to a text node in a tree; the bridge may change
// the value of this node during validation. by defining the interface, the
// validating cursor is able to keep track of events fired in a java collection 
public class TextNode<N>
    implements ChildNode<N>
{
    public TextNode(N text)
    {
        content = PreCondition.assertNotNull(text, "content");
    }
    
    public final N content; // never null

    @Override
    public boolean isStartTag()
    {
        return false;
    }

    @Override
    public boolean isText()
    {
        return true;
    }

}
