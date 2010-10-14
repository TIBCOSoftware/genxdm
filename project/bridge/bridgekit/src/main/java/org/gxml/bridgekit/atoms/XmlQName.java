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
package org.gxml.bridgekit.atoms;

import org.genxdm.xs.types.SmNativeType;

public class XmlQName extends XmlAbstractAtom
{
	private final String localName;

	private final String namespaceURI;

	private final String prefix;

	public XmlQName(final String namespaceURI, final String localName, final String prefix)
	{
		this.namespaceURI = namespaceURI;
		this.localName = localName;
		this.prefix = prefix;
	}

	@Override
	public boolean equals(final Object obj)
	{
		// TODO: revise using getClass and test for this.
		if (obj == null || !(obj instanceof XmlQName))
		{
			return false;
		}
		else
		{
			return equalsName((XmlQName)obj);
		}
	}

	public boolean equalsName(final XmlQName other)
	{
		return namespaceURI.equals(other.namespaceURI) && localName.equals(other.localName);
	}

	public String getC14NForm()
	{
		final int prefixLength = prefix.length();
		if (null != localName)
		{
			if (prefixLength > 0)
			{
				final int localNameLength = localName.length();
				return new StringBuilder(prefixLength + 1 + localNameLength).append(prefix).append(":").append(localName).toString();
			}
			else
			{
				return localName;
			}
		}
		else
		{
			if (prefixLength > 0)
			{
				return prefix.concat(":*");
			}
			else
			{
				return "*";
			}
		}
	}

	public String getLocalName()
	{
		return localName;
	}

	public String getNamespaceURI()
	{
		return namespaceURI;
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.QNAME;
	}

	public String getPrefix()
	{
		return prefix;
	}

	@Override
	public int hashCode()
	{
		if (null != namespaceURI)
		{
			if (null != localName)
			{
				return namespaceURI.hashCode() ^ localName.hashCode();
			}
			else
			{
				return namespaceURI.hashCode() ^ "*".hashCode();
			}
		}
		else
		{
			if (null != localName)
			{
				return "*".hashCode() ^ localName.hashCode();
			}
			else
			{
				return "*".hashCode();
			}
		}
	}

	public boolean isWhiteSpace()
	{
		return false;
	}
}
