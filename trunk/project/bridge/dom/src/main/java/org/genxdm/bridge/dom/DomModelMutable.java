/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.bridge.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.mutable.MutableModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * (Mutable) Model implementation for W3C Document Object Model.
 */
public final class DomModelMutable 
    extends DomModel implements MutableModel<Node>
{

    public DomNodeFactory getFactory(final Node context)
    {
        PreCondition.assertNotNull(context, "context");
        // TODO: cache this, probably with a least-recent first-dropped algo, rather
        // than creating a new one each time as we're doing here.  Basically, a Map<Document, NodeFactory>,
        // possibly user-configurable in size with a reasonable default (six?); check whether
        // we have one already or not.  verify that this really is a better solution than
        // creating a nodefactory each time; it's not a heavy abstraction (has only doc or
        // dbf as state).
        return new DomNodeFactory(DomSupport.getOwner(context));
    }
    
    public void appendChild(final Node parent, final Node newChild)
    {
        PreCondition.assertArgumentNotNull(parent, "parent");
        PreCondition.assertArgumentNotNull(newChild, "newChild");
        parent.appendChild(ensureOwnership(DomSupport.getOwner(parent), newChild));
    }
    
    public void appendChildren(final Node parent, final Iterable<Node> content)
    {
        // TODO: probably highly inefficient, but easy to implement.
        PreCondition.assertNotNull(content, "content");
        final Document owner = parent.getOwnerDocument();
        for (Node node : content)
        {
            parent.appendChild(ensureOwnership(owner, node));
        }
    }
    
    public void prependChild(final Node parent, final Node content)
    {
        PreCondition.assertNotNull(parent, "parent");
        PreCondition.assertNotNull(content, "content");
        parent.insertBefore(ensureOwnership(parent.getOwnerDocument(), content), parent.getFirstChild());
    }
    
    public void prependChildren(final Node parent, final Iterable<Node> content)
    {
        // probably highly inefficient, but easy to implement.
        List<Node> reversed = new ArrayList<Node>();
        for (Node node : content)
        {
            reversed.add(node);
        }
        Collections.reverse(reversed);
        for (Node node : reversed)
        {
            prependChild(parent, node);
        }
    }

    public Node copyNode(final Node source, final boolean deep)
    {
        PreCondition.assertArgumentNotNull(source, "source");
        Node copy = source.cloneNode(deep);
        if (copy instanceof Document)
            ((Document)copy).setDocumentURI(((Document)source).getDocumentURI());
        return copy;
    }

    public void insertBefore(final Node target, final Node content)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertNotNull(content, "content");
        final Node parent = target.getParentNode();
        if (parent != null)
        {
            parent.insertBefore(ensureOwnership(target.getOwnerDocument(), content), target);
        }
    }
    
    public void insertBefore(final Node target, final Iterable<Node> content)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertNotNull(content, "content");
        final Node parent = target.getParentNode();
        if (parent != null)
        {
            final Document owner = parent.getOwnerDocument();
            for (Node node : content)
            {
                parent.insertBefore(ensureOwnership(owner, node), target);
            }
        }
    }
    
    public void insertAfter(final Node target, final Node content)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertNotNull(content, "content");
        final Node parent = target.getParentNode();
        final Node next = target.getNextSibling();
        // need a bloody closure, dammit
        if (parent != null)
        {
            final Document owner = parent.getOwnerDocument();
            if (next != null)
                parent.insertBefore(ensureOwnership(owner, content), next);
            else
                parent.appendChild(ensureOwnership(owner, content));
        }
    }
    
    public void insertAfter(final Node target, final Iterable<Node> content)
    {
        PreCondition.assertNotNull(target, "target");
        PreCondition.assertNotNull(content, "content");
        final Node parent = target.getParentNode();
        final Node next = target.getNextSibling();
        if (parent != null)
        {
            final Document owner = parent.getOwnerDocument();
            for (Node node : content)
            {
                if (next != null)
                    parent.insertBefore(ensureOwnership(owner, node), next);
                else
                    parent.appendChild(ensureOwnership(owner, node));
            }
        }
    }

    public Node delete(final Node target)
    {
        PreCondition.assertArgumentNotNull(target, "target");
        if (target instanceof Attr)
        {
            return ((Attr)target).getOwnerElement().removeAttributeNode((Attr)target);
        }
        Node parent = target.getParentNode();
        if (parent != null)
            return parent.removeChild(target);
        return null;
    }
    
    public Iterable<Node> deleteChildren(final Node target)
    {
        PreCondition.assertNotNull(target, "target");
        List<Node> deleted = new ArrayList<Node>();
        if (getNodeKind(target).isContainer())
            for (Node child : getChildAxis(target))
            {
                deleted.add(target.removeChild(child));
            }
        return deleted;
    }

    public Node replace(final Node target, final Node content)
    {
        PreCondition.assertArgumentNotNull(content, "newChild");
        PreCondition.assertArgumentNotNull(target, "oldChild");
        if (target instanceof Attr)
        {
            final Element owner = ((Attr)target).getOwnerElement();
            Node old = owner.removeAttributeNode((Attr)target);
            owner.setAttributeNode((Attr)ensureOwnership(target.getOwnerDocument(), content));
            return old;
        }
        final Node parent = target.getParentNode();
        if (parent != null)
            return parent.replaceChild(ensureOwnership(target.getOwnerDocument(), content), target);
        return null;
    }
    
    public String replaceValue(final Node target, final String value)
    {
        PreCondition.assertNotNull(target, "target");
        if (getNodeKind(target).isContainer() || getNodeKind(target).isNamespace() )
            return null; // throw an exception, really.
        String retval = getStringValue(target);
        target.setNodeValue(value);
        return retval;
    }

    public void insertAttribute(final Node element, final Node attribute)
    {
        ((Element)element).setAttributeNodeNS((Attr)ensureOwnership(element.getOwnerDocument(), attribute));
    }
    
    public void insertAttributes(final Node element, final Iterable<Node> attributes)
    {
        for (Node attr : attributes)
            insertAttribute(element, attr);
    }

    public Node insertNamespace(final Node element, final String prefix, final String uri)
    {
        PreCondition.assertArgumentNotNull(element, "element");
        PreCondition.assertArgumentNotNull(prefix, "prefix");
        PreCondition.assertArgumentNotNull(uri, "uri");
        return DomSupport.setNamespace(element, prefix, uri);
    }
    
    private Node ensureOwnership(Document d, Node n)
    {
        if (n.getOwnerDocument() != d) {
        	// Following doesn't work
        	// d.adoptNode(n);
        	// This is diabolical.  It looks like, from the DOM API, that you could use adoptNode.
        	// UNFORTUNATELY, the Xerces implementation bundled into the JRE appears to have serious
        	// bugs, wherein "adoptNode" appears to incorrectly migrate "deferred" text data - it
        	// preserves text locations as pointed to in the original document, rather than "undeferring"
        	// said values, but apparently only keeps a positional reference, not the underlying data itself.
        	// Consequence is that nested text and attribute values, when used, are taken from a random
        	// position in the new document being imported into.
        	//
        	// attempts to work around the issue by traversing the descendant axis, or "normalize()" the
        	// source document both failed.
            return d.importNode(n, true);
        }
        return n;
    }
}
