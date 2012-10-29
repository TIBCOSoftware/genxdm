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
package org.genxdm.bridgekit.xs.constraint;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.ForeignAttributesImpl;
import org.genxdm.bridgekit.xs.ForeignAttributesSink;
import org.genxdm.bridgekit.xs.complex.LockableImpl;
import org.genxdm.xs.facets.Facet;

/**
 * Abstract base class for implementations of {@link Facet}
 */
public abstract class FacetImpl 
    extends LockableImpl 
    implements Facet, ForeignAttributesSink
{
    public FacetImpl(final boolean isFixed)
    {
        this.isFixed = isFixed;
    }

    @Override
    public final boolean isFixed()
    {
        return isFixed;
    }

    @Override
    public Iterable<QName> getForeignAttributeNames()
    {
        return forAtts.getForeignAttributeNames();
    }

    @Override
    public String getForeignAttributeValue(QName name)
    {
        return forAtts.getForeignAttributeValue(name);
    }

    @Override
    public void putForeignAttribute(QName name, String value)
    {
        forAtts.putForeignAttribute(name, value);
    }

    private final boolean isFixed;
    private ForeignAttributesImpl forAtts = new ForeignAttributesImpl();
}
