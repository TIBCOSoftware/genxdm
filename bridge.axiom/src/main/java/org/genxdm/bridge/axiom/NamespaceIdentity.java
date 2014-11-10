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

import java.lang.ref.WeakReference;

import org.apache.axiom.om.OMNamespace;

public class NamespaceIdentity
{
    NamespaceIdentity(OMNamespace ns)
    {
        this.namespace = new WeakReference<OMNamespace>(ns);
    }
    
    // this is the reason for this to exist
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof NamespaceIdentity)
            return hashCode() == o.hashCode();
        else if (o instanceof OMNamespace)
            return hashCode() == System.identityHashCode(o);
        return false;
    }
    
    // by the rules, we need to override hashcode, too.
    // however, we *want* the Object hashcode.
    @Override
    public int hashCode()
    {
        return System.identityHashCode(namespace.get());
    }

    private final WeakReference<OMNamespace> namespace;

}
