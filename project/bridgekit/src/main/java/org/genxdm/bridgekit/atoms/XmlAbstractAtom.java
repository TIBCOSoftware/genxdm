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

import org.genxdm.bridgekit.misc.AbstractUnaryList;

public abstract class XmlAbstractAtom 
    extends AbstractUnaryList<XmlAtom> 
    implements XmlAtom
{
    public final XmlAtom get(final int index)
    {
        if (0 == index)
        {
            return this;
        }
        else
        {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @SuppressWarnings("unchecked")
    public final <T> T[] toArray(T[] a)
    {
        final int size = size();
        if (a.length < size)
        {
            a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }
        System.arraycopy(toArray(), 0, a, 0, size);
        if (a.length > size)
        {
            a[size] = null;
        }
        return a;
    }

    @Override
    public final String toString()
    {
        return getNativeType().getLocalName() + "('" + getC14NForm() + "')";
    }
}
