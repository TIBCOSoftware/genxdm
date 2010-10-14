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

import org.genxdm.xs.enums.SmNodeKind;
import org.genxdm.xs.types.SmPrimeChoiceType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmTextNodeType;

final class TextNodeType<A> extends AbstractLeafNodeType<A> implements SmTextNodeType<A>
{
	public TextNodeType(final SmCache<A> cache)
	{
		super(SmNodeKind.TEXT, cache);
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.TEXT;
	}

	public SmTextNodeType<A> prime()
	{
		return this;
	}

	public boolean subtype(final SmPrimeType<A> rhs)
	{
		switch (rhs.getKind())
		{
			case CHOICE:
			{
				final SmPrimeChoiceType<A> choiceType = (SmPrimeChoiceType<A>)rhs;
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
