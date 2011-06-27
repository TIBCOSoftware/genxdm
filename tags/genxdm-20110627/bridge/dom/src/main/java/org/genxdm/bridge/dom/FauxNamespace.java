/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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

import javax.xml.XMLConstants;

import org.genxdm.NodeKind;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.Emulation;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * A Fake namespace used to implement the highly dubious namespace axis.
 * 
 * <p>
 * The key feature of this implementation is the fake of the parent node.
 */
public final class FauxNamespace implements Attr
{
	private final Element parent;
	private final Document owner;
	private final String prefix;
	private final String uri;

	public FauxNamespace(final String prefix, final String uri, final Element parent, final Document owner)
	{
		this.prefix = PreCondition.assertArgumentNotNull(prefix);
		this.uri = PreCondition.assertArgumentNotNull(uri);
		this.parent = PreCondition.assertArgumentNotNull(parent);
		this.owner = PreCondition.assertArgumentNotNull(owner);
	}

	public String getName()
	{
		throw new UnsupportedOperationException();
	}

	public Element getOwnerElement()
	{
		return parent;
	}

	public TypeInfo getSchemaTypeInfo()
	{
		return null;
	}

	public boolean getSpecified()
	{
		return false;
	}

	public String getValue()
	{
		throw new UnsupportedOperationException();
	}

	public boolean isId()
	{
		return false;
	}

	public void setValue(String value) throws DOMException
	{
		throw new UnsupportedOperationException();
	}

	public Node appendChild(Node newChild) throws DOMException
	{
		throw new UnsupportedOperationException();
	}

	public Node cloneNode(boolean deep)
	{
		throw new UnsupportedOperationException();
	}

	public short compareDocumentPosition(Node other) throws DOMException
	{
		throw new UnsupportedOperationException();
	}

	public NamedNodeMap getAttributes()
	{
		throw new UnsupportedOperationException();
	}

	public String getBaseURI()
	{
		throw new UnsupportedOperationException();
	}

	public NodeList getChildNodes()
	{
		throw new UnsupportedOperationException();
	}

	public Object getFeature(String feature, String version)
	{
		throw new UnsupportedOperationException();
	}

	public Node getFirstChild()
	{
		return null;
	}

	public Node getLastChild()
	{
		return null;
	}

	public String getLocalName()
	{
		return prefix;
	}

	public String getNamespaceURI()
	{
		// This is important in the DOM world otherwise attributes and namespaces get confused.
		return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
	}

	public Node getNextSibling()
	{
		return null;
	}

	public String getNodeName()
	{
		throw new UnsupportedOperationException();
	}

	public short getNodeType()
	{
		// In DOM world we are an attribute node.
		return Node.ATTRIBUTE_NODE;
	}

	public String getNodeValue()
	{
		return uri;
	}

	public Document getOwnerDocument()
	{
		return owner;
	}

	public Node getParentNode()
	{
		return parent;
	}

	public String getPrefix()
	{
		throw new UnsupportedOperationException();
	}

	public Node getPreviousSibling()
	{
		return null;
	}

	public String getTextContent() throws DOMException
	{
		throw new UnsupportedOperationException();
	}

	public Object getUserData(String key)
	{
		throw new UnsupportedOperationException();
	}

	public boolean hasAttributes()
	{
		return false;
	}

	public boolean hasChildNodes()
	{
		return false;
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException
	{
		throw new UnsupportedOperationException();
	}

	public boolean isDefaultNamespace(String namespaceURI)
	{
		throw new UnsupportedOperationException();
	}

	public boolean isEqualNode(Node arg)
	{
		throw new UnsupportedOperationException();
	}

	public boolean isSameNode(final Node other)
	{
		if (other instanceof Attr)
		{
			if (DomSupport.getNodeKind(other) == NodeKind.NAMESPACE)
			{
				if (prefix.equals(DomSupport.getLocalNameAsString(other)))
				{
					if (uri.equals(DomSupport.getStringValue(other, " ", Emulation.C14N)))
					{
						final Node otherParent = DomSupport.getParentNode(other);
						if (null != otherParent)
						{
							return parent.isSameNode(otherParent);
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public boolean isSupported(String feature, String version)
	{
		return false;
	}

	public String lookupNamespaceURI(String prefix)
	{
		throw new UnsupportedOperationException();
	}

	public String lookupPrefix(String namespaceURI)
	{
		throw new UnsupportedOperationException();
	}

	public void normalize()
	{
		throw new UnsupportedOperationException();
	}

	public Node removeChild(Node oldChild) throws DOMException
	{
		throw new UnsupportedOperationException();
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException
	{
		throw new UnsupportedOperationException();
	}

	public void setNodeValue(String nodeValue) throws DOMException
	{
		throw new UnsupportedOperationException();
	}

	public void setPrefix(String prefix) throws DOMException
	{
		throw new UnsupportedOperationException();
	}

	public void setTextContent(String textContent) throws DOMException
	{
		throw new UnsupportedOperationException();
	}

	public Object setUserData(String key, Object data, UserDataHandler handler)
	{
		return null;
	}
}
