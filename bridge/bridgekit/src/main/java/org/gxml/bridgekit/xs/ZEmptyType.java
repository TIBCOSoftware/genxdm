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
package org.gxml.bridgekit.xs;

import org.gxml.xs.enums.SmQuantifier;
import org.gxml.xs.types.SmEmptyType;
import org.gxml.xs.types.SmPrimeType;
import org.gxml.xs.types.SmPrimeTypeKind;
import org.gxml.xs.types.SmSequenceType;
import org.gxml.xs.types.SmSequenceTypeVisitor;

final class ZEmptyType<A> implements SmEmptyType<A>
{
	public ZEmptyType()
	{
	}

	public SmPrimeType<A> prime()
	{
		return new NoneType<A>();
	}

	public SmQuantifier quantifier()
	{
		return SmQuantifier.OPTIONAL;
	}

	public boolean isNone()
	{
		return false;
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		return rhs.quantifier().contains(SmQuantifier.EMPTY);
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.EMPTY;
	}

	public boolean isNative()
	{
		return false;
	}

	public boolean isChoice()
	{
		return false;
	}

	public SmSequenceType<A> atomSet()
	{
		return this;
	}

	@Override
	public String toString()
	{
		return "empty";
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}
}
