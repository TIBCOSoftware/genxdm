/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.bridge.axiom;

import org.apache.axiom.om.OMAttribute;

public class AttributeIdentity
{
    
    AttributeIdentity(OMAttribute attribute)
    {
        this.attribute = attribute;
    }

    // this is the reason for this to exist
    // we change the semantics of equality
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof AttributeIdentity)
            return ((AttributeIdentity)o).attribute == attribute;
        else if (o instanceof OMAttribute)
            return (OMAttribute)o == attribute;
        return false;
    }
    
    // we also need hashcode semantics, but we need it to be object equality
    // at the attribute level.
    @Override
    public int hashCode()
    {
        return ((Object)attribute).hashCode();
    }

    private final OMAttribute attribute;
}
