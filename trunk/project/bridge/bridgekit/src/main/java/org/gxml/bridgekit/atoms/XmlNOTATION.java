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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.types.SmNativeType;

/**
 * Corresponds to the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#NOTATION">NOTATION</a>.
 */
public final class XmlNOTATION extends XmlAbstractAtom
{
	private final String localName;
	private final String namespaceURI;
	private final String prefix;

	public XmlNOTATION(final String namespaceURI, final String localName, final String prefix)
	{
		this.namespaceURI = namespaceURI;
		this.localName = PreCondition.assertArgumentNotNull(localName, "localName");
		this.prefix = prefix;
	}

	public String getC14NForm()
	{
		return formatQNameC14N(localName, prefix);
	}

	private static String formatQNameC14N(final String localName, final String prefix)
	{
		PreCondition.assertArgumentNotNull(localName, "localName");
		PreCondition.assertArgumentNotNull(prefix, "prefix");
		if (prefix.length() > 0)
		{
			return prefix.concat(":").concat(localName);
		}
		else
		{
			return localName;
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
		return SmNativeType.NOTATION;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public boolean isWhiteSpace()
	{
		return false;
	}
}
