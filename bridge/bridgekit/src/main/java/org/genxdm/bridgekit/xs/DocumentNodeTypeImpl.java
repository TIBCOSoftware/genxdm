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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.NodeKind;
import org.genxdm.xs.types.DocumentNodeType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;

final class DocumentNodeTypeImpl<A> extends AbstractBranchNodeType<A> implements DocumentNodeType<A>
{
	private final SequenceType<A> m_contentType;

	public DocumentNodeTypeImpl(final SequenceType<A> contentType, final SchemaCache<A> cache)
	{
		super(NodeKind.DOCUMENT, cache);
		m_contentType = PreCondition.assertArgumentNotNull(contentType, "contentType");
	}

	public void accept(final SequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public DocumentNodeType<A> prime()
	{
		return this;
	}

	public SequenceType<A> getContentType()
	{
		return m_contentType;
	}

	public PrimeTypeKind getKind()
	{
		return PrimeTypeKind.DOCUMENT;
	}

	public boolean subtype(final PrimeType<A> rhs)
	{
		switch (rhs.getKind())
		{
			case CHOICE:
			{
				final PrimeChoiceType<A> choiceType = (PrimeChoiceType<A>)rhs;
				return subtype(choiceType.getLHS()) || subtype(choiceType.getRHS());
			}
			case DOCUMENT:
			{
				final DocumentNodeType<A> documentType = (DocumentNodeType<A>)rhs;
				final SequenceType<A> documentElementType = documentType.getContentType();
				if (null != documentElementType)
				{
					if (null != m_contentType)
					{
						return m_contentType.prime().subtype(documentElementType.prime());
					}
					else
					{
						return false;
					}
				}
				else
				{
					// Unspecified document element type means wildcard.
					return true;
				}
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
		return "document-node(" + m_contentType + ")";
	}
}
