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
package org.genxdm.bridgekit.xs.complex;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.enums.ScopeExtent;

public final class ModelGroupImpl extends NamedComponentImpl implements ModelGroup
{
    private final ModelGroup.SmCompositor compositor;
    private final List<SchemaParticle> particles;

    public ModelGroupImpl(final ModelGroup.SmCompositor compositor, final List<? extends SchemaParticle> particles, final QName name, final boolean isAnonymous, final ScopeExtent scope)
    {
        super(name, isAnonymous, scope);
        this.compositor = PreCondition.assertArgumentNotNull(compositor, "compositor");
        this.particles = Collections.unmodifiableList(particles);
    }

    public SmCompositor getCompositor()
    {
        return compositor;
    }

    public List<SchemaParticle> getParticles()
    {
        return particles;
    }
}
