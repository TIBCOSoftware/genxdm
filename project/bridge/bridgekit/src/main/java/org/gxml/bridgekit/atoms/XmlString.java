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
 * Corresponds to the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#string">string</a>.
 */
public final class XmlString extends XmlAbstractAtom
{
	private final int hashCode;
	private final String value;

	public XmlString(final String value)
	{
		this.value = PreCondition.assertArgumentNotNull(value, "value");
		this.hashCode = value.hashCode();
	}

	@Override
	public boolean equals(final Object object)
	{
		if (this == object)
		{
			return true;
		}
		else if (object == null || getClass() != object.getClass())
		{
			return false;
		}
		else
		{
			final XmlString other = (XmlString)object;
			return value.equals(other.value);
		}
	}

	public String getC14NForm()
	{
		return value;
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.STRING;
	}

	@Override
	public int hashCode()
	{
		return hashCode;
	}

	public boolean isWhiteSpace()
	{
		return value.trim().length() == 0;
	}
}
