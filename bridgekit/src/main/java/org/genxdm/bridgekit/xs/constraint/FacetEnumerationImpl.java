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
package org.genxdm.bridgekit.xs.constraint;

import java.util.List;

import org.genxdm.bridgekit.xs.ForeignAttributesImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.PrefixResolver;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.SimpleType;

public final class FacetEnumerationImpl
    extends ForeignAttributesImpl
    implements EnumerationDefinition
{
    public FacetEnumerationImpl(final String value, final SimpleType type, final PrefixResolver resolver)
    {
        this.value = PreCondition.assertNotNull(value, "value");
        this.type = PreCondition.assertNotNull(type, "type");
        this.resolver = resolver;
    }

    @Override
    public <A> List<A> getValue(AtomBridge<A> bridge)
    {
        try
        {
            if (resolver != null)
                return type.validate(value, resolver, bridge);
            return type.validate(value, bridge);
        }
        catch (DatatypeException dt)
        {
            // because we do validation before creating this type, we know it's happened once
            // successfully. So this should be a never-happen (unless there's a resolver problem,
            // which is always possible, I suppose).
            return null;
        }
    }

    @Override
    public String toString()
    {
        return value;
    }

    private final String value;
    private final SimpleType type;
    private final PrefixResolver resolver;
}
