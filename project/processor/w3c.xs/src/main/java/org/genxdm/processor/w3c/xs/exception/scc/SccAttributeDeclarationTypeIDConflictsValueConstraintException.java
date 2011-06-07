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

@SuppressWarnings("serial")
public final class SccAttributeDeclarationTypeIDConflictsValueConstraintException extends SccAttributeDeclarationException
{
    public SccAttributeDeclarationTypeIDConflictsValueConstraintException(final QName attributeName)
    {
        super(PART_DERIVED_FROM_ID, attributeName);
    }

    @Override
    public String getMessage()
    {
        return "If the {type definition} is derived from ID then there must not be a {value constraint}.";
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof SccAttributeDeclarationTypeIDConflictsValueConstraintException)
        {
            final SccAttributeDeclarationTypeIDConflictsValueConstraintException e = (SccAttributeDeclarationTypeIDConflictsValueConstraintException)obj;
            return e.getAttributeName().equals(getAttributeName());
        }
        else
        {
            return false;
        }
    }
}
