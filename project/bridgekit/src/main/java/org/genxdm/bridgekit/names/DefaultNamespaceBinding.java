/*
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
package org.genxdm.bridgekit.names;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NamespaceBinding;

public final class DefaultNamespaceBinding implements NamespaceBinding 
{
    private final String prefix;
    private final String uri;

    public DefaultNamespaceBinding(final String prefix, final String uri)
    {
        this.prefix = PreCondition.assertNotNull(prefix, "prefix");
        this.uri = PreCondition.assertNotNull(uri, "uri");
    }

    @Override
    public String getNamespaceURI()
    {
        return uri;
    }

    @Override
    public String getPrefix()
    {
        return prefix;
    }

    @Override
    public String toString()
    {
        return prefix + "=>" + uri;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof NamespaceBinding)
            return hashCode() == other.hashCode();
        return false;
    }
}
