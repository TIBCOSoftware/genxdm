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
package org.gxml.bridge.axiom;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.gxml.exceptions.IllegalNullArgumentException;

final class FauxAttribute implements OMAttribute
{
	private final OMAttribute inner;
	private final OMElement parent;

	public FauxAttribute(final OMAttribute inner, final OMElement parent)
	{
		this.inner = IllegalNullArgumentException.check(inner, "inner");
		this.parent = IllegalNullArgumentException.check(parent, "parent");
	}

	public String getAttributeType()
	{
		return null;
	}

	public String getAttributeValue()
	{
		return inner.getAttributeValue();
	}

	public String getLocalName()
	{
		return inner.getLocalName();
	}

	public OMNamespace getNamespace()
	{
		return inner.getNamespace();
	}

	public OMFactory getOMFactory()
	{
		return inner.getOMFactory();
	}

	public OMElement getOwner()
	{
		return parent;
	}

	public QName getQName()
	{
		return inner.getQName();
	}

	public void setAttributeType(String arg0)
	{
		throw new UnsupportedOperationException();
	}

	public void setAttributeValue(final String arg0)
	{
		throw new UnsupportedOperationException();
	}

	public void setLocalName(final String arg0)
	{
		throw new UnsupportedOperationException();
	}

	public void setOMNamespace(final OMNamespace arg0)
	{
		throw new UnsupportedOperationException();
	}
}
