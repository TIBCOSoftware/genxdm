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
package org.genxdm.bridgekit.atoms;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.IllegalNullArgumentException;
import org.genxdm.xs.types.NativeType;

public final class XmlForeignAtom extends XmlAbstractAtom
{
    public final QName atomType;
    public final XmlAtom baseAtom;

    public XmlForeignAtom(final QName atomType, final XmlAtom baseAtom)
    {
        this.atomType = IllegalNullArgumentException.check(atomType, "atomType");
        this.baseAtom = IllegalNullArgumentException.check(baseAtom, "baseAtom");
    }

    public boolean equals(final Object obj)
    {
        if (obj instanceof XmlForeignAtom)
        {
            final XmlForeignAtom other = (XmlForeignAtom)obj;
            return atomType.equals(other.atomType) && baseAtom.equals(other.baseAtom);
        }
        else
        {
            return false;
        }
    }

    public String getC14NForm()
    {
        return baseAtom.getC14NForm();
    }

    public NativeType getNativeType()
    {
        return baseAtom.getNativeType();
    }

    public boolean isWhiteSpace()
    {
        return baseAtom.isWhiteSpace();
    }
}
