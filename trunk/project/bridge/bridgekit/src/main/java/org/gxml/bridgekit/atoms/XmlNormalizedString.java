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

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.types.SmNativeType;

/**
 * Corresponds to the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#normalizedString">normalizedString</a>.
 */
public final class XmlNormalizedString extends XmlAbstractAtom
{
	private final String value;

	public XmlNormalizedString(final String value)
	{
		this.value = PreCondition.assertArgumentNotNull(value, "value");
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj instanceof XmlNormalizedString)
		{
			return value.equals(((XmlNormalizedString)obj).value);
		}
		else
		{
			return false;
		}
	}

	public String getC14NForm()
	{
		return value;
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.NORMALIZED_STRING;
	}

	public String getString()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	public boolean isWhiteSpace()
	{
		return value.trim().length() == 0;
	}
}
