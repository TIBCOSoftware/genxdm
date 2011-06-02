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
package org.genxdm.xs.constraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genxdm.exceptions.PreCondition;

/**
 * A triple consisting of the value constraint variety, value and lexical form.
 */
public final class ValueConstraint<A>
{
    public enum Kind
    {
        Fixed, Default;

        public boolean isFixed()
        {
            return (this == Fixed);
        }

        public boolean isDefault()
        {
            return (this == Default);
        }
    }

    private final Kind variety;
    private final List<A> value;
    private final String lexicalForm;

    public ValueConstraint(final Kind variety, final List<A> value, final String lexicalForm)
    {
        this.variety = PreCondition.assertArgumentNotNull(variety, "variety");
        this.value = Collections.unmodifiableList(new ArrayList<A>(PreCondition.assertArgumentNotNull(value, "value")));
        this.lexicalForm = PreCondition.assertArgumentNotNull(lexicalForm, "lexicalForm");
    }

    /**
     * Returns the the {variety} property of the value constraint.
     */
    public Kind getVariety()
    {
        return variety;
    }

    /**
     * Returns the the {value} property of the value constraint.
     */
    public List<A> getValue()
    {
        return value;
    }

    /**
     * Returns the {lexical form} property of the value constraint.
     */
    public String getLexicalForm()
    {
        return lexicalForm;
    }
}
