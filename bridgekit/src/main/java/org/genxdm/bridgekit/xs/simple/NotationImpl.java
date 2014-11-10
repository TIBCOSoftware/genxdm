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
package org.genxdm.bridgekit.xs.simple;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.complex.NamedComponentImpl;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.enums.ScopeExtent;

public final class NotationImpl extends NamedComponentImpl implements NotationDefinition
{
    private final String publicId;
    private final String systemId;

    public NotationImpl(final QName name, final String publicId, final String systemId)
    {
        super(name, false, ScopeExtent.Global);
        this.publicId = publicId;
        this.systemId = systemId;
    }

    public String getPublicId()
    {
        return publicId;
    }

    public String getSystemId()
    {
        return systemId;
    }

    public String toString()
    {
        return "notation name=" + getName() + " public=" + getPublicId() + " system=" + getSystemId();
    }
}
