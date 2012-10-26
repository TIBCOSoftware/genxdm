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
package org.genxdm.processor.w3c.xs.xmlrep.facets;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.PrefixResolver;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLType;
import org.genxdm.processor.w3c.xs.xmlrep.util.SrcFrozenLocation;

public final class XMLEnumeration extends XMLFacet
{
    private String value;
    private PrefixResolver snapshot;

    public XMLEnumeration(final XMLType simpleType, final SrcFrozenLocation location)
    {
        super(simpleType, location);
    }

    public void setValue(final String value, final PrefixResolver snapshot)
    {
        this.value = PreCondition.assertArgumentNotNull(value, "value");
        this.snapshot = PreCondition.assertArgumentNotNull(snapshot, "snapshot");
    }

    public String getValue()
    {
        return value;
    }

    public PrefixResolver getPrefixResolver()
    {
        return snapshot;
    }
}
