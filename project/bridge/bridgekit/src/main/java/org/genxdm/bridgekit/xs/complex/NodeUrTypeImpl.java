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
package org.genxdm.bridgekit.xs.complex;

import org.genxdm.bridgekit.xs.SchemaCache;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.types.NodeUrType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;

final class NodeUrTypeImpl implements NodeUrType
{
	public NodeUrTypeImpl(final SchemaCache cache)
	{
	}

	public PrimeType prime()
	{
		return this;
	}

	public KeeneQuantifier quantifier()
	{
		return KeeneQuantifier.EXACTLY_ONE;
	}

	public boolean isNone()
	{
		return false;
	}

	public boolean subtype(final PrimeType rhs)
	{
		return rhs.quantifier().contains(KeeneQuantifier.EMPTY);
	}

	public PrimeTypeKind getKind()
	{
		return PrimeTypeKind.NODE;
	}

	public boolean isNative()
	{
		return false;
	}

	public boolean isChoice()
	{
		return false;
	}

	public SequenceType atomSet()
	{
		return this;
	}

	@Override
	public String toString()
	{
		return "node()";
	}

	public void accept(final SequenceTypeVisitor visitor)
	{
		visitor.visit(this);
	}
}
