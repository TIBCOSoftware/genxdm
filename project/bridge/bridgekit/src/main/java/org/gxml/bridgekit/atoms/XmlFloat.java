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
 * Corresponds to the W3C XML Schema <a href="http://www.w3.org/TR/xmlschema-2/#float">float</a>.
 */
public final class XmlFloat extends XmlAbstractAtom
{
	private final float floatValue;

	public XmlFloat(final float floatValue)
	{
		this.floatValue = floatValue;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof XmlFloat)
		{
			return floatValue == ((XmlFloat)obj).floatValue;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns this value as a <code>float</code>.
	 */
	public float getFloatValue()
	{
		return floatValue;
	}

	public String getC14NForm()
	{
		return NumericSupport.formatFloatC14N(floatValue);
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.FLOAT;
	}

	@Override
	public int hashCode()
	{
		return Float.floatToIntBits(floatValue);
	}

	public boolean isWhiteSpace()
	{
		return false;
	}
}
