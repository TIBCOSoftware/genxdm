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

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.exceptions.ComponentConstraintException;


@SuppressWarnings("serial")
public final class SccCyclicSubstitutionGroupException extends ComponentConstraintException
{
    private final List<QName> m_names;

    public SccCyclicSubstitutionGroupException(final List<QName> names)
    {
        super(ValidationOutcome.TODO, "?");
        m_names = PreCondition.assertArgumentNotNull(names, "names");
    }

    public List<QName> getNames()
    {
        return m_names;
    }

    public String getMessage()
    {
        return getClass().getName();
    }
}
