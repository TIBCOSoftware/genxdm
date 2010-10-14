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
package org.genxdm.bridgekit.xs;

import javax.xml.namespace.QName;

import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.types.SmNoneType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceTypeVisitor;

final class NoneType<A> extends AbstractType<A> implements SmNoneType<A>
{
	private final QName errorCode;

	public NoneType()
	{
		errorCode = null;
	}

	public NoneType(final QName errorCode)
	{
		this.errorCode = errorCode;
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public QName getErrorCode()
	{
		return errorCode;
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.NONE;
	}

	public boolean isChoice()
	{
		return false;
	}

	public boolean isNative()
	{
		return false;
	}

	public boolean isNone()
	{
		return true;
	}

	public final SmPrimeType<A> prime()
	{
		return this;
	}

	public SmQuantifier quantifier()
	{
		return SmQuantifier.EXACTLY_ONE;
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		switch (rhs.getKind())
		{
			case NONE:
			{
				return true;
			}
			default:
			{
				return false;
			}
		}
	}

	@Override
	public String toString()
	{
		return "none";
	}
}
