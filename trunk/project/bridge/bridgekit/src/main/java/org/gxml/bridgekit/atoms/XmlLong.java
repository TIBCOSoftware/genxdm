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

import org.gxml.xs.types.SmNativeType;

/**
 * Corresponds to the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#long">long</a>.
 */
public final class XmlLong extends XmlAbstractAtom
{
	private final long longValue;

	public XmlLong(final long longValue)
	{
		this.longValue = longValue;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj instanceof XmlLong)
		{
			return longValue == ((XmlLong)obj).longValue;
		}
		else
		{
			return false;
		}
	}

	public String getC14NForm()
	{
		return Long.toString(longValue);
	}

	public long getLongValue()
	{
		return longValue;
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.LONG;
	}

	@Override
	public int hashCode()
	{
		return (int)(longValue ^ (longValue >>> 32));
	}

	public boolean isWhiteSpace()
	{
		return false;
	}
}
