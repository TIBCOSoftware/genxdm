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

import org.genxdm.NodeKind;
import org.genxdm.bridgekit.xs.SchemaCache;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.TextNodeType;

public final class TextNodeTypeImpl extends AbstractLeafNodeType implements TextNodeType
{
	public TextNodeTypeImpl(final SchemaCache cache)
	{
		super(NodeKind.TEXT, cache);
	}

	public void accept(final SequenceTypeVisitor visitor)
	{
		visitor.visit(this);
	}

	public PrimeTypeKind getKind()
	{
		return PrimeTypeKind.TEXT;
	}

	public TextNodeType prime()
	{
		return this;
	}

	public boolean subtype(final PrimeType rhs)
	{
		switch (rhs.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType choiceType = (PrimeChoiceType)rhs;
				return subtype(choiceType.getLHS()) || subtype(choiceType.getRHS());
			}
			case TEXT:
			{
				return true;
			}
			default:
			{
				return false;
			}
		}
	}
}
