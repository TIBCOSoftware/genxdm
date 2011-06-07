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
public final class SccComplexTypeAttributeFixedOverrideException extends SccComplexTypeDefinitionException
{
    private final QName m_attributeName;

    public SccComplexTypeAttributeFixedOverrideException(final QName typeName, final QName attributeName)
    {
        super(PART_FIXED_OVERRIDE, typeName);
        m_attributeName = PreCondition.assertArgumentNotNull(attributeName, "attributeName");
    }

    @Override
    public String getMessage()
    {
        return "Attribute declarations in the {attribute uses} of " + getTypeName() + " have identical {name}s and {target namespace}s, " + m_attributeName + ".";
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof SccComplexTypeAttributeFixedOverrideException)
        {
            final SccComplexTypeAttributeFixedOverrideException e = (SccComplexTypeAttributeFixedOverrideException)obj;
            return e.getTypeName().equals(getTypeName()) && e.m_attributeName.equals(m_attributeName);
        }
        else
        {
            return false;
        }
    }
}
