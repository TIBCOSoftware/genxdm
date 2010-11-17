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

import org.genxdm.base.mutable.MutableModel;
import org.genxdm.exceptions.PreCondition;
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
//	public Node adoptNode(final Node target, final Node source)
//	{
//		PreCondition.assertArgumentNotNull(target, "target");
//		PreCondition.assertArgumentNotNull(source, "source");
//		final Document owner = DomSupport.getOwner(target);
//		return owner.adoptNode(source);
//	}

	public Node appendChild(final Node parent, final Node newChild)
	{
		PreCondition.assertArgumentNotNull(parent, "parent");
		PreCondition.assertArgumentNotNull(newChild, "newChild");
		return parent.appendChild(insureOwnership(parent.getOwnerDocument(), newChild));
	}
	
	public Node appendChildren(final Node parent, final Iterable<Node> content)
	{
		PreCondition.assertNotNull(parent, "parent");
		PreCondition.assertNotNull(content, "content");
		Node last = null;
		final Document owner = parent.getOwnerDocument();
		for (Node node : content)
		{
			last = parent.appendChild(insureOwnership(owner, node));
		}
		return last;
	}
	
	public Node prependChild(final Node parent, final Node content)
	{
		PreCondition.assertNotNull(parent, "parent");
		PreCondition.assertNotNull(content, "content");
		return parent.insertBefore(insureOwnership(parent.getOwnerDocument(), content), parent.getFirstChild());
	}
	
	public Node prependChildren(final Node parent, final Iterable<Node> content)
	{
		// probably highly inefficient, but easy to implement.
		Node last = null;
		for (Node node : content)
		{
			last = prependChild(parent, node);
		}
		return last;
	}

//	public Node cloneNode(final Node source, final boolean deep)
//	{
//		PreCondition.assertArgumentNotNull(source, "source");
//		return source.cloneNode(deep);
//	}
//
//	public Document getOwner(final Node node)
//	{
//		PreCondition.assertArgumentNotNull(node, "node");
//		return DomSupport.getOwner(node);
//	}
//
//	public Node importNode(final Node target, final Node source, final boolean deep)
//	{
//		PreCondition.assertArgumentNotNull(target, "target");
//		PreCondition.assertArgumentNotNull(source, "source");
//		final Document owner = DomSupport.getOwner(target);
//		return owner.importNode(source, deep);
//	}

	public Node insertBefore(final Node target, final Node content)
	{
		PreCondition.assertNotNull(target, "target");
		PreCondition.assertNotNull(content, "content");
		final Node parent = target.getParentNode();
		if (parent != null)
		{
			return parent.insertBefore(insureOwnership(target.getOwnerDocument(), content), target);
		}
		return null;
	}
	
	public Node insertBefore(final Node target, final Iterable<Node> content)
	{
		PreCondition.assertNotNull(target, "target");
		PreCondition.assertNotNull(content, "content");
		final Node parent = target.getParentNode();
		Node last = null;
		if (parent != null)
		{
			final Document owner = parent.getOwnerDocument();
			for (Node node : content)
			{
				last = parent.insertBefore(insureOwnership(owner, node), target);
			}
		}
		return last;
	}
	
	public Node insertAfter(final Node target, final Node content)
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
				return parent.insertBefore(insureOwnership(owner, content), next);
			else
				return parent.appendChild(insureOwnership(owner, content));
		}
		return null;
	}
	
	public Node insertAfter(final Node target, final Iterable<Node> content)
	{
		PreCondition.assertNotNull(target, "target");
		PreCondition.assertNotNull(content, "content");
		final Node parent = target.getParentNode();
		final Node next = target.getNextSibling();
		Node last = null;
		if (parent != null)
		{
			final Document owner = parent.getOwnerDocument();
			for (Node node : content)
			{
				if (next != null)
					last = parent.insertBefore(insureOwnership(owner, node), next);
				else
					last = parent.appendChild(insureOwnership(owner, node));
			}
		}
		return last;
	}

//	public void normalize(final Node node)
//	{
//		PreCondition.assertArgumentNotNull(node, "node");
//		node.normalize();
//	}

//	public void removeAttribute(final Node element, final String namespaceURI, final String localName)
//	{
//		PreCondition.assertArgumentNotNull(element, "element");
//		PreCondition.assertArgumentNotNull(namespaceURI, "namespaceURI");
//		PreCondition.assertArgumentNotNull(localName, "localName");
//		DomSupport.removeAttribute(element, namespaceURI, localName);
//	}

	public Node delete(final Node target)
	{
		PreCondition.assertArgumentNotNull(target, "target");
		Node parent = target.getParentNode();
		if (parent != null)
			return parent.removeChild(target);
		return null;
	}
	
	public Node deleteChildren(final Node target)
	{
		PreCondition.assertNotNull(target, "target");
		if (getNodeKind(target).isContainer())
			for (Node child : getChildAxis(target))
				delete(child);
		return target;
	}

//	public void removeNamespace(final Node element, final String prefix)
//	{
//		PreCondition.assertArgumentNotNull(element, "element");
//		PreCondition.assertArgumentNotNull(prefix, "prefix");
//		DomSupport.removeNamespace(element, prefix);
//	}

	public Node replace(final Node target, final Node content)
	{
		PreCondition.assertArgumentNotNull(content, "newChild");
		PreCondition.assertArgumentNotNull(target, "oldChild");
		final Node parent = target.getParentNode();
		if (parent != null)
			return parent.replaceChild(insureOwnership(target.getOwnerDocument(), content), target);
		return null;
	}
	
	public Node replaceValue(final Node target, final String value)
	{
		PreCondition.assertNotNull(target, "target");
		if (getNodeKind(target).isContainer() || getNodeKind(target).isNamespace() )
			return null; // throw an exception, really.
		target.setNodeValue(value);
		return target;
	}

	public Node insertAttribute(final Node element, final Node attribute)
	{
		((Element)element).setAttributeNodeNS((Attr)insureOwnership(element.getOwnerDocument(), attribute));
		return attribute;
	}
	
	public Node insertAttributes(final Node element, final Iterable<Node> attributes)
	{
		Node last = null;
		for (Node attr : attributes)
			last = insertAttribute(element, attr);
		return last;
	}

//	public Attr setAttribute(final Node element, final String namespaceURI, final String localName, final String prefix, final String value)
//	{
//		PreCondition.assertArgumentNotNull(element, "element");
//		PreCondition.assertArgumentNotNull(namespaceURI, "namespaceURI");
//		PreCondition.assertArgumentNotNull(localName, "localName");
//		PreCondition.assertArgumentNotNull(prefix, "prefix");
//		return DomSupport.setAttributeUntyped(element, namespaceURI, localName, prefix, value);
//	}

//	public void setNamespace(final Node element, final Node namespace)
//	{
//		((Element)element).setAttributeNodeNS((Attr)namespace);
//	}

	public Attr propagateNamespace(final Node element, final String prefix, final String uri)
	{
		PreCondition.assertArgumentNotNull(element, "element");
		PreCondition.assertArgumentNotNull(prefix, "prefix");
		PreCondition.assertArgumentNotNull(uri, "uri");
		return DomSupport.setNamespace(element, prefix, uri);
	}
	
	private Node insureOwnership(Document d, Node n)
	{
		if (n.getOwnerDocument() != d)
			d.adoptNode(n);
		return n;
	}
}
