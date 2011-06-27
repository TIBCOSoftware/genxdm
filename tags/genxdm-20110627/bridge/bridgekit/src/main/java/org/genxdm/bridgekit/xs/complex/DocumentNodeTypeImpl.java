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
import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.types.DocumentNodeType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;

public final class DocumentNodeTypeImpl extends AbstractBranchNodeType implements DocumentNodeType
{
    private final SequenceType m_contentType;

    public DocumentNodeTypeImpl(final SequenceType contentType, final ComponentProvider cache)
    {
        super(NodeKind.DOCUMENT, cache);
        m_contentType = PreCondition.assertArgumentNotNull(contentType, "contentType");
    }

    public void accept(final SequenceTypeVisitor visitor)
    {
        visitor.visit(this);
    }

    public DocumentNodeType prime()
    {
        return this;
    }

    public SequenceType getContentType()
    {
        return m_contentType;
    }

    public PrimeTypeKind getKind()
    {
        return PrimeTypeKind.DOCUMENT;
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
            case DOCUMENT:
            {
                final DocumentNodeType documentType = (DocumentNodeType)rhs;
                final SequenceType documentElementType = documentType.getContentType();
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
