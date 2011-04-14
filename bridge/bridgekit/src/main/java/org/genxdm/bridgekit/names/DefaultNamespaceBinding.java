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

import org.genxdm.exceptions.IllegalNullArgumentException;
import org.genxdm.names.NamespaceBinding;

public final class DefaultNamespaceBinding implements NamespaceBinding 
{
    private final String prefix;
    private final String uri;

    public DefaultNamespaceBinding(final String prefix, final String uri)
    {
        this.prefix = IllegalNullArgumentException.check(prefix, "prefix");
        this.uri = IllegalNullArgumentException.check(uri, "uri");
    }

    public String getNamespaceURI()
    {
        return uri;
    }

    public String getPrefix()
    {
        return prefix;
    }

    @Override
    public String toString()
    {
        return prefix + " => " + uri.toString();
    }
}
