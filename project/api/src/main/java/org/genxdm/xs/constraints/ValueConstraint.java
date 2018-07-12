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
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.exceptions.SimpleTypeException;
import org.genxdm.xs.types.SimpleType;

/**
 * A triple consisting of the value constraint variety, value and lexical form.
 */
public final class ValueConstraint
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
    private final SimpleType constrainedType;
    private final String lexicalForm;

    public ValueConstraint(final Kind variety, final SimpleType constrainedType, final String lexicalForm)
    {
        this.variety = PreCondition.assertArgumentNotNull(variety, "variety");
        this.constrainedType = PreCondition.assertArgumentNotNull(constrainedType, "constrainedType");
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
    public <A> List<A> getValue(AtomBridge<A> bridge)
//        throws SimpleTypeException
    {
        List<A> value = null;
        try
        {
            value = constrainedType.validate(lexicalForm, bridge);
        }
        catch (DatatypeException dte)
        {
            // this should never happen, mind.  if it does, then there's
            // breakage in the atom bridge supplied.
            // Note that this is a case in which we do not know the name of the element.
            throw new RuntimeException(new SimpleTypeException(lexicalForm, constrainedType, dte, null));
        }
        return Collections.unmodifiableList(new ArrayList<A>(value));
    }

    /**
     * Returns the {lexical form} property of the value constraint.
     */
    public String getLexicalForm()
    {
        return lexicalForm;
    }
}
