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

import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.types.SmNodeUrType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmSequenceTypeVisitor;

final class NodeUrType<A> implements SmNodeUrType<A>
{
	public NodeUrType(final SmCache<A> cache)
	{
	}

	public SmPrimeType<A> prime()
	{
		return this;
	}

	public SmQuantifier quantifier()
	{
		return SmQuantifier.EXACTLY_ONE;
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
		return SmPrimeTypeKind.NODE;
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
		return "node()";
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}
}
