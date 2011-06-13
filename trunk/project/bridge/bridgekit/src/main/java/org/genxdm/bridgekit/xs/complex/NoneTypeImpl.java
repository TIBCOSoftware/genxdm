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

import javax.xml.namespace.QName;

import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.types.NoneType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceTypeVisitor;

public final class NoneTypeImpl extends AbstractType implements NoneType
{
    private final QName errorCode;

    public NoneTypeImpl()
    {
        errorCode = null;
    }

    public NoneTypeImpl(final QName errorCode)
    {
        this.errorCode = errorCode;
    }

    public void accept(final SequenceTypeVisitor visitor)
    {
        visitor.visit(this);
    }

    public QName getErrorCode()
    {
        return errorCode;
    }

    public PrimeTypeKind getKind()
    {
        return PrimeTypeKind.NONE;
    }

    public boolean isChoice()
    {
        return false;
    }

    public boolean isNative()
    {
        return false;
    }

    public boolean isNone()
    {
        return true;
    }

    public final PrimeType prime()
    {
        return this;
    }

    public Quantifier quantifier()
    {
        return Quantifier.EXACTLY_ONE;
    }

    public boolean subtype(final PrimeType rhs)
    {
        switch (rhs.getKind())
        {
            case NONE:
            {
                return true;
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
        return "none";
    }
}
