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
package org.genxdm.processor.w3c.xs.exception.scc;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;

@SuppressWarnings("serial")
public final class SccAttributeDerivationRequiredConflictException extends SccComplexTypeDerivationRestrictionException
{
    @SuppressWarnings("unused")
    private final QName m_attributeName;

    public SccAttributeDerivationRequiredConflictException(final QName typeName, final QName attributeName)
    {
        super(PART_ATTRIBUTE_REQUIRED_CONFLICT, typeName);
        m_attributeName = PreCondition.assertArgumentNotNull(attributeName, "attributeName");
    }

    @Override
    public String getMessage()
    {
        return "{required} for attribute use conflicts with {base type definition}.";
    }
}
