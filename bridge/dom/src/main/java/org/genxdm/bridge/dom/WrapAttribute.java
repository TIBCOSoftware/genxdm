/*
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

import org.genxdm.exceptions.PreCondition;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public final class WrapAttribute implements Attr
{
	private final Attr attribute;
	private final Element parent;

	public WrapAttribute(final Attr attribute, final Element parent)
	{
		this.attribute = PreCondition.assertArgumentNotNull(attribute);
		this.parent = PreCondition.assertArgumentNotNull(parent);
	}

	public Node appendChild(final Node newChild) throws DOMException
	{
		return attribute.appendChild(newChild);
	}

	public Node cloneNode(final boolean deep)
	{
		return attribute.cloneNode(deep);
	}

	public short compareDocumentPosition(final Node other) throws DOMException
	{
		return attribute.compareDocumentPosition(other);
	}

	public NamedNodeMap getAttributes()
	{
		return attribute.getAttributes();
	}

	public String getBaseURI()
	{
		return attribute.getBaseURI();
	}

	public NodeList getChildNodes()
	{
		return attribute.getChildNodes();
	}

	public Object getFeature(final String feature, final String version)
	{
		return attribute.getFeature(feature, version);
	}

	public Node getFirstChild()
	{
		return attribute.getFirstChild();
	}

	public Node getLastChild()
	{
		return attribute.getLastChild();
	}

	public String getLocalName()
	{
		return attribute.getLocalName();
	}

	public String getName()
	{
		return attribute.getName();
	}

	public String getNamespaceURI()
	{
		return attribute.getNamespaceURI();
	}

	public Node getNextSibling()
	{
		return attribute.getNextSibling();
	}

	public String getNodeName()
	{
		return attribute.getNodeName();
	}

	public short getNodeType()
	{
		return attribute.getNodeType();
	}

	public String getNodeValue() throws DOMException
	{
		return attribute.getNodeValue();
	}

	public Document getOwnerDocument()
	{
		return attribute.getOwnerDocument();
	}

	public Element getOwnerElement()
	{
		return parent;
	}

	public Node getParentNode()
	{
		return parent;
	}

	public String getPrefix()
	{
		return attribute.getPrefix();
	}

	public Node getPreviousSibling()
	{
		return attribute.getPreviousSibling();
	}

	public TypeInfo getSchemaTypeInfo()
	{
		return attribute.getSchemaTypeInfo();
	}

	public boolean getSpecified()
	{
		return attribute.getSpecified();
	}

	public String getTextContent() throws DOMException
	{
		return attribute.getTextContent();
	}

	public Object getUserData(final String key)
	{
		return attribute.getUserData(key);
	}

	public String getValue()
	{
		return attribute.getValue();
	}

	public boolean hasAttributes()
	{
		return attribute.hasAttributes();
	}

	public boolean hasChildNodes()
	{
		return attribute.hasChildNodes();
	}

	public Node insertBefore(final Node newChild, final Node refChild) throws DOMException
	{
		return attribute.insertBefore(newChild, refChild);
	}

	public boolean isDefaultNamespace(final String namespaceURI)
	{
		return attribute.isDefaultNamespace(namespaceURI);
	}

	public boolean isEqualNode(final Node arg)
	{
		return attribute.isEqualNode(arg);
	}

	public boolean isId()
	{
		return attribute.isId();
	}

	public boolean isSameNode(final Node other)
	{
		return attribute.isSameNode(other);
	}

	public boolean isSupported(final String feature, final String version)
	{
		return attribute.isSupported(feature, version);
	}

	public String lookupNamespaceURI(final String prefix)
	{
		return attribute.lookupNamespaceURI(prefix);
	}

	public String lookupPrefix(final String namespaceURI)
	{
		return attribute.lookupPrefix(namespaceURI);
	}

	public void normalize()
	{
		attribute.normalize();
	}

	public Node removeChild(final Node oldChild) throws DOMException
	{
		return attribute.removeChild(oldChild);
	}

	public Node replaceChild(final Node newChild, final Node oldChild) throws DOMException
	{
		return attribute.replaceChild(newChild, oldChild);
	}

	public void setNodeValue(final String nodeValue) throws DOMException
	{
		attribute.setNodeValue(nodeValue);
	}

	public void setPrefix(final String prefix) throws DOMException
	{
		attribute.setPrefix(prefix);
	}

	public void setTextContent(final String textContent) throws DOMException
	{
		attribute.setTextContent(textContent);
	}

	public Object setUserData(final String key, final Object data, final UserDataHandler handler)
	{
		return attribute.setUserData(key, data, handler);
	}

	public void setValue(final String value) throws DOMException
	{
		attribute.setValue(value);
	}

	@Override
	public String toString()
	{
		return attribute.toString();
	}
}
