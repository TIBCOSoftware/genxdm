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
package org.genxdm.processor.w3c.xs.xmlrep;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.impl.SrcFrozenLocation;

abstract class XMLComponent extends XMLTag
{
    public FAMap foreignAttributes = new FAMap();
    private final XMLScope scope;

    public XMLComponent(final XMLScope scope)
    {
        this.scope = PreCondition.assertArgumentNotNull(scope, "scope");
    }

    public XMLComponent(final XMLScope scope, final SrcFrozenLocation location)
    {
        super(location);
        this.scope = PreCondition.assertArgumentNotNull(scope, "scope");
    }

    public abstract QName getName();

    public final XMLScope getScope()
    {
        return scope;
    }
}
