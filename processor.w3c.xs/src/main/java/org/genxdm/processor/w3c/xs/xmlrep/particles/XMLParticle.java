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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.impl.SrcFrozenLocation;
import org.genxdm.processor.w3c.xs.xmlrep.XMLParticleTerm;
import org.genxdm.processor.w3c.xs.xmlrep.XMLTag;

public abstract class XMLParticle extends XMLTag
{
    private final BigInteger m_minOccurs;
    private final BigInteger m_maxOccurs;
    private final XMLParticleTerm m_term;

    /**
     * Sentinel value for {@link #getMaxOccurs} being "unbounded". <br/>
     * The datatype of the {max occurs} property is xs:positiveInteger so any xs:nonPositiveInteger value should work.
     * We dont use the maximum integer value because that would conflict with a genuine parsed value. We choose an
     * xs:negativeInteger to assist debugging.
     */
    public static BigInteger UNBOUNDED = BigInteger.ONE.negate();

    public XMLParticle(final BigInteger minOccurs, final BigInteger maxOccurs, final XMLParticleTerm term, final SrcFrozenLocation location)
    {
        super(location);
        this.m_minOccurs = PreCondition.assertArgumentNotNull(minOccurs, "minOccurs");
        this.m_maxOccurs = PreCondition.assertArgumentNotNull(maxOccurs, "maxOccurs");
        this.m_term = PreCondition.assertArgumentNotNull(term, "term");
    }

    public final BigInteger getMinOccurs()
    {
        return m_minOccurs;
    }

    public final BigInteger getMaxOccurs()
    {
        return m_maxOccurs;
    }

    public XMLParticleTerm getTerm()
    {
        return m_term;
    }
}
