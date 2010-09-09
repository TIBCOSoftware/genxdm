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

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.enums.SmNodeKind;
import org.gxml.xs.types.SmDocumentNodeType;
import org.gxml.xs.types.SmPrimeChoiceType;
import org.gxml.xs.types.SmPrimeType;
import org.gxml.xs.types.SmPrimeTypeKind;
import org.gxml.xs.types.SmSequenceType;
import org.gxml.xs.types.SmSequenceTypeVisitor;

final class DocumentNodeType<A> extends AbstractBranchNodeType<A> implements SmDocumentNodeType<A>
{
	private final SmSequenceType<A> m_contentType;

	public DocumentNodeType(final SmSequenceType<A> contentType, final SmCache<A> cache)
	{
		super(SmNodeKind.DOCUMENT, cache);
		m_contentType = PreCondition.assertArgumentNotNull(contentType, "contentType");
	}

	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		visitor.visit(this);
	}

	public SmDocumentNodeType<A> prime()
	{
		return this;
	}

	public SmSequenceType<A> getContentType()
	{
		return m_contentType;
	}

	public SmPrimeTypeKind getKind()
	{
		return SmPrimeTypeKind.DOCUMENT;
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
			case DOCUMENT:
			{
				final SmDocumentNodeType<A> documentType = (SmDocumentNodeType<A>)rhs;
				final SmSequenceType<A> documentElementType = documentType.getContentType();
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
