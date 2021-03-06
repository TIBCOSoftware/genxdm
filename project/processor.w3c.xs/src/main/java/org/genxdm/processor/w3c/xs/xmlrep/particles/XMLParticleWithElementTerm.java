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
package org.genxdm.processor.w3c.xs.xmlrep.particles;

import java.math.BigInteger;

import org.genxdm.processor.w3c.xs.xmlrep.XMLValueConstraint;
import org.genxdm.processor.w3c.xs.xmlrep.components.XMLElement;
import org.genxdm.processor.w3c.xs.xmlrep.util.SrcFrozenLocation;

public final class XMLParticleWithElementTerm extends XMLParticle
{
    public final XMLValueConstraint valueConstraint;

    public XMLParticleWithElementTerm(final BigInteger minOccurs, final BigInteger maxOccurs, final XMLElement element, final XMLValueConstraint valueConstraint, final SrcFrozenLocation location)
    {
        super(minOccurs, maxOccurs, element, location);
        this.valueConstraint = valueConstraint;
    }

    public XMLElement getTerm()
    {
        return (XMLElement)super.getTerm();
    }
}
