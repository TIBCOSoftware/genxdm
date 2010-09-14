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
package org.gxml.bridgekit.tree;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.gxml.NodeKind;
import org.gxml.base.io.ContentHandler;
import org.gxml.bridgekit.axes.IterableAncestorAxis;
import org.gxml.bridgekit.axes.IterableAncestorOrSelfAxis;
import org.gxml.bridgekit.axes.IterableChildAxis;
import org.gxml.bridgekit.axes.IterableChildAxisElements;
import org.gxml.bridgekit.axes.IterableChildAxisElementsByName;
import org.gxml.bridgekit.axes.IterableDescendantAxis;
import org.gxml.bridgekit.axes.IterableDescendantOrSelfAxis;
import org.gxml.bridgekit.axes.IterableFollowingAxis;
import org.gxml.bridgekit.axes.IterableFollowingSiblingAxis;
import org.gxml.bridgekit.axes.IterablePrecedingAxis;
import org.gxml.bridgekit.axes.IterablePrecedingSiblingAxis;
import org.gxml.exceptions.GxmlException;
import org.gxml.names.NamespaceBinding;
import org.gxml.typed.TypedModel;
import org.gxml.typed.io.SequenceHandler;
import org.gxml.typed.types.AtomBridge;

public final class CoreModelDecorator<N, A> 
    implements TypedModel<N, A>
{
	private final Set<CoreModelDecoration> delegation;
	private final TypedModel<N, A> model;
	private final AtomBridge<A> atomBridge;

	public CoreModelDecorator(final Set<CoreModelDecoration> delegation, final TypedModel<N, A> model, final AtomBridge<A> atomBridge)
	{
		this.delegation = delegation;
		this.model = model;
		this.atomBridge = atomBridge;
	}

	public int compare(final N one, final N two)
	{
		return Ordering.compareNodes(one, two, this);
	}

	public Iterable<N> getAncestorAxis(final N origin)
	{
		return new IterableAncestorAxis<N>(origin, this);
	}

	public Iterable<N> getAncestorOrSelfAxis(N origin)
	{
		return new IterableAncestorOrSelfAxis<N>(origin, this);
	}

	public N getAttribute(final N parent, final String namespaceURI, final String localName)
	{
		return model.getAttribute(parent, namespaceURI, localName);
	}
	
	public Iterable<N> getAttributeAxis(final N origin, final boolean inherit)
	{
		if (origin != null)
		{
			return model.getAttributeAxis(origin, inherit);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	public Iterable<QName> getAttributeNames(final N node, final boolean orderCanonical)
	{
		return model.getAttributeNames(node, orderCanonical);
	}

	public String getAttributeStringValue(final N parent, final String namespaceURI, final String localName)
	{
		if (false)
		{
			return model.getAttributeStringValue(parent, namespaceURI, localName);
		}
		else
		{
			final N attribute = getAttribute(parent, namespaceURI, localName);
			if (null != attribute)
			{
				return getStringValue(attribute);
			}
			else
			{
				return null;
			}
		}
	}

	public Iterable<? extends A> getAttributeValue(final N parent, final String namespaceURI, final String localName)
	{
		if (false)
		{
			return model.getAttributeValue(parent, namespaceURI, localName);
		}
		else
		{
			final N attribute = getAttribute(parent, namespaceURI, localName);
			if (null != attribute)
			{
				return getValue(attribute);
			}
			else
			{
				return null;
			}
		}
	}

	public QName getAttributeTypeName(final N parent, final String namespaceURI, final String localName)
	{
		return model.getAttributeTypeName(parent, namespaceURI, localName);
	}

    public URI getBaseURI(final N node)
    {
        return model.getBaseURI(node);
    }

	public Iterable<N> getChildAxis(final N origin)
	{
		if (delegation.contains(CoreModelDecoration.CHILD_AXIS))
		{
			if (origin != null)
			{
				return new IterableChildAxis<N>(origin, this);
			}
			else
			{
				return Collections.emptyList();
			}
		}
		else
		{
			return model.getChildAxis(origin);
		}
	}

	public Iterable<N> getChildElements(final N origin)
	{
		if (delegation.contains(CoreModelDecoration.CHILD_ELEMENTS))
		{
			if (origin != null)
			{
				return new IterableChildAxisElements<N>(origin, this);
			}
			else
			{
				return Collections.emptyList();
			}
		}
		else
		{
			return model.getChildElements(origin);
		}
	}

	public Iterable<N> getChildElementsByName(final N origin, final String namespaceURI, final String localName)
	{
		if (true)
		{
			return model.getChildElementsByName(origin, namespaceURI, localName);
		}
		else
		{
			return new IterableChildAxisElementsByName<N>(origin, namespaceURI, localName, this);
		}
	}

	public Iterable<N> getDescendantAxis(final N origin)
	{
		if (origin != null)
		{
			return new IterableDescendantAxis<N>(origin, this);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	public Iterable<N> getDescendantOrSelfAxis(final N origin)
	{
		if (origin != null)
		{
			return new IterableDescendantOrSelfAxis<N>(origin, this);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	public URI getDocumentURI(final N node)
	{
		return model.getDocumentURI(node);
	}
	
	public N getElementById(final N context, final String id)
	{
	    return model.getElementById(context, id);
	}

	public N getFirstChild(final N origin)
	{
		return model.getFirstChild(origin);
	}

	public N getFirstChildElement(final N origin)
	{
		if (false)
		{
			return model.getFirstChildElement(origin);
		}
		else
		{
			final N candidate = getFirstChild(origin);
			if (isElement(candidate))
			{
				return candidate;
			}
			else
			{
				return getNextSiblingElement(candidate);
			}
		}
	}

	public N getFirstChildElementByName(final N origin, final String namespaceURI, final String localName)
	{
		if (false)
		{
			return model.getFirstChildElementByName(origin, namespaceURI, localName);
		}
		else
		{
			final N element = getFirstChildElement(origin);
			if (matches(element, namespaceURI, localName))
			{
				return element;
			}
			else
			{
				return getNextSiblingElementByName(element, namespaceURI, localName);
			}
		}
	}

	public Iterable<N> getFollowingAxis(final N origin)
	{
		if (origin != null)
		{
			return new IterableFollowingAxis<N>(origin, this);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	public Iterable<N> getFollowingSiblingAxis(final N origin)
	{
		if (origin != null)
		{
			return new IterableFollowingSiblingAxis<N>(origin, this);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	public N getLastChild(final N origin)
	{
		return model.getLastChild(origin);
	}

	public String getLocalName(final N node)
	{
		return model.getLocalName(node);
	}

	public Iterable<N> getNamespaceAxis(final N node, final boolean inherit)
	{
		return model.getNamespaceAxis(node, inherit);
	}

	public Iterable<NamespaceBinding> getNamespaceBindings(final N node)
	{
		return model.getNamespaceBindings(node);
	}
	
	public String getNamespaceForPrefix(final N node, final String prefix)
	{
	    return model.getNamespaceForPrefix(node, prefix);
	}

	public Iterable<String> getNamespaceNames(final N node, final boolean orderCanonical)
	{
		return model.getNamespaceNames(node, orderCanonical);
	}

	public String getNamespaceURI(final N node)
	{
		return model.getNamespaceURI(node);
	}

	public N getNextSibling(final N origin)
	{
		return model.getNextSibling(origin);
	}

	public N getNextSiblingElement(final N node)
	{
		if (false)
		{
			return model.getNextSiblingElement(node);
		}
		else
		{
			final N candidate = getNextSibling(node);
			if (isElement(candidate))
			{
				return candidate;
			}
			else
			{
				if (candidate != null)
				{
					return getNextSiblingElement(candidate);
				}
				else
				{
					return null;
				}
			}
		}
	}

	public N getNextSiblingElementByName(final N node, final String namespaceURI, final String localName)
	{
		if (false)
		{
			return model.getNextSiblingElementByName(node, namespaceURI, localName);
		}
		else
		{
			final N element = getNextSiblingElement(node);
			if (matches(element, namespaceURI, localName))
			{
				return element;
			}
			else
			{
				if (element != null)
				{
					return getNextSiblingElementByName(element, namespaceURI, localName);
				}
				else
				{
					return null;
				}
			}
		}
	}
	
	public Object getNodeId(final N node)
	{
	    return model.getNodeId(node);
	}

	public NodeKind getNodeKind(final N node)
	{
		return model.getNodeKind(node);
	}

	public N getParent(final N origin)
	{
		return model.getParent(origin);
	}

	public Iterable<N> getPrecedingAxis(N origin)
	{
		if (origin != null)
		{
			return new IterablePrecedingAxis<N>(origin, this);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	public Iterable<N> getPrecedingSiblingAxis(final N origin)
	{
		if (origin != null)
		{
			return new IterablePrecedingSiblingAxis<N>(origin, this);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	public String getPrefix(final N node)
	{
		return model.getPrefix(node);
	}

	public N getPreviousSibling(final N origin)
	{
		return model.getPreviousSibling(origin);
	}

	public N getRoot(final N node)
	{
		return model.getRoot(node);
	}

	public String getStringValue(final N node)
	{
		return model.getStringValue(node);
	}

	public Iterable<? extends A> getValue(final N node)
	{
		if (false)
		{
			return model.getValue(node);
		}
		else
		{
			switch (getNodeKind(node))
			{
				case ELEMENT:
				case ATTRIBUTE:
				{
					return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(getStringValue(node)));
				}
				case COMMENT:
				case NAMESPACE:
				case PROCESSING_INSTRUCTION:
				{
					return atomBridge.wrapAtom(atomBridge.createString(getStringValue(node)));
				}
				default:
				{
					throw new AssertionError(getNodeKind(node));
				}
			}
		}
	}

	public QName getTypeName(N node)
	{
		// TODO Auto-generated method stub
		throw new AssertionError();
	}

	public boolean hasAttributes(final N node)
	{
		if (true)
		{
			return model.hasAttributes(node);
		}
		else
		{
			// TODO Auto-generated method stub
			throw new AssertionError();
		}
	}

	public boolean hasChildren(final N node)
	{
		if (true)
		{
			return model.hasChildren(node);
		}
		else
		{
			// TODO Auto-generated method stub
			throw new AssertionError();
		}
	}

	public boolean hasNamespaces(final N node)
	{
		if (true)
		{
			return model.hasNamespaces(node);
		}
		else
		{
			// TODO Auto-generated method stub
			throw new AssertionError();
		}
	}

	public boolean hasNextSibling(final N node)
	{
		if (false)
		{
			return model.hasNextSibling(node);
		}
		else
		{
			return model.getNextSibling(node) != null;
		}
	}

	public boolean hasParent(final N node)
	{
		return model.hasParent(node);
	}

	public boolean hasPreviousSibling(final N node)
	{
		if (false)
		{
			return model.hasPreviousSibling(node);
		}
		else
		{
			return model.getPreviousSibling(node) != null;
		}
	}

	public boolean isAttribute(final N node)
	{
		return model.getNodeKind(node) == NodeKind.ATTRIBUTE;
	}

	public boolean isElement(final N node)
	{
		if (node != null)
		{
			return model.getNodeKind(node) == NodeKind.ELEMENT;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isId(final N node)
	{
	    return model.isId(node);
	}
	
	public boolean isIdRefs(final N node)
	{
	    return model.isIdRefs(node);
	}

	public boolean isNamespace(final N node)
	{
		return model.getNodeKind(node) == NodeKind.NAMESPACE;
	}

	public boolean isText(final N node)
	{
		return model.getNodeKind(node) == NodeKind.TEXT;
	}

	public boolean matches(final N node, final NodeKind kind, final String namespaceURI, final String localName)
	{
		if (false)
		{
			return model.matches(node, kind, namespaceURI, localName);
		}
		else
		{
			if (kind != null)
			{
				if (getNodeKind(node) != kind)
				{
					return false;
				}
			}
			return matches(node, namespaceURI, localName);
		}
	}

	public boolean matches(final N node, final String namespaceURI, final String localName)
	{
		return model.matches(node, namespaceURI, localName);
	}

    public void stream(final N node, final boolean copyNamespaces, final ContentHandler handler) throws GxmlException
    {
    }

    public void stream(final N node, final boolean copyNamespaces, final boolean copyTypeAnnotations, final SequenceHandler<A> handler) throws GxmlException
	{
		// TODO Auto-generated method stub
		throw new AssertionError();
	}
}