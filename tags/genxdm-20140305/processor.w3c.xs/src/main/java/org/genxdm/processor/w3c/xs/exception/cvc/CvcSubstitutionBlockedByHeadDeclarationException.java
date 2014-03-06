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
package org.genxdm.processor.w3c.xs.exception.cvc;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.sm.SmLocationException;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.resolve.LocationInSchema;

@SuppressWarnings("serial")
public final class CvcSubstitutionBlockedByHeadDeclarationException extends SmLocationException
{
    private final ElementDefinition m_name;
    private final ElementDefinition m_substitutionGroup;

    public CvcSubstitutionBlockedByHeadDeclarationException(final ElementDefinition elementDeclaration, final ElementDefinition substitutionGroup, final LocationInSchema location)
    {
        super(ValidationOutcome.SCC_Substitution_Group_OK_Transitive, "2.1", location);
        m_name = PreCondition.assertArgumentNotNull(elementDeclaration, "name");
        m_substitutionGroup = PreCondition.assertArgumentNotNull(substitutionGroup, "substitutionGroup");
    }

    public String getMessage()
    {
        return "Substitution using element " + m_name + " is blocked by the declaration of the substitution group, " + m_substitutionGroup + ".";
    }
}
