/*
 * Copyright (c) 2011-12 TIBCO Software Inc.
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

import java.lang.ref.WeakReference;

import org.apache.axiom.om.OMAttribute;

public class AttributeIdentity
{
    
    AttributeIdentity(OMAttribute attribute)
    {
        this.attribute = new WeakReference<OMAttribute>(attribute);
    }

    // this is the reason for this to exist
    // we change the semantics of equality back to object identity.
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof AttributeIdentity) 
            return hashCode() == o.hashCode();
        else if (o instanceof OMAttribute)
            return hashCode() == System.identityHashCode(o);
        return false;
    }
    
    // we also need hashcode semantics, but we need it to be object equality
    // at the attribute level. There's a method for that!
    @Override
    public int hashCode()
    {
        return System.identityHashCode(attribute.get());
    }

    // hold the attribute as a weak reference to avoid memory loss, when
    // we happen to have a strong reference to an attribute identity somewhere.
    private final WeakReference<OMAttribute> attribute;
}
