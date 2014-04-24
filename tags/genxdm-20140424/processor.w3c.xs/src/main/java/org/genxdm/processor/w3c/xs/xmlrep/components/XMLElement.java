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
package org.genxdm.processor.w3c.xs.xmlrep.components;

import java.util.EnumSet;
import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.xmlrep.XMLParticleTerm;
import org.genxdm.processor.w3c.xs.xmlrep.XMLScope;
import org.genxdm.processor.w3c.xs.xmlrep.XMLTypeRef;
import org.genxdm.processor.w3c.xs.xmlrep.util.SrcFrozenLocation;
import org.genxdm.xs.enums.DerivationMethod;

public final class XMLElement extends XMLDeclaration implements XMLParticleTerm
{
    private final EnumSet<DerivationMethod> m_block = EnumSet.noneOf(DerivationMethod.class);
    private final EnumSet<DerivationMethod> m_final = EnumSet.noneOf(DerivationMethod.class);
    private boolean m_isAbstract = false;
    private boolean m_isNillable = false;
    public XMLElement substitutionGroup;
    private final LinkedList<XMLIdentityConstraint> m_identityConstraints = new LinkedList<XMLIdentityConstraint>();

    public XMLElement(final QName name, final XMLScope scope, final XMLTypeRef anyType, final SrcFrozenLocation location)
    {
        super(PreCondition.assertArgumentNotNull(name, "name"), scope, anyType, location);
    }

    public XMLElement(final QName name, final XMLScope scope, final XMLTypeRef anyType)
    {
        super(PreCondition.assertArgumentNotNull(name, "name"), scope, anyType);
    }

    public EnumSet<DerivationMethod> getBlock()
    {
        return m_block;
    }

    public EnumSet<DerivationMethod> getFinal()
    {
        return m_final;
    }

    public boolean isAbstract()
    {
        return m_isAbstract;
    }

    public void setAbstractFlag(final boolean isAbstract)
    {
        m_isAbstract = isAbstract;
    }

    public boolean isNillable()
    {
        return m_isNillable;
    }

    public void setNillableFlag(final boolean isNillable)
    {
        m_isNillable = isNillable;
    }

    public LinkedList<XMLIdentityConstraint> getIdentityConstraints()
    {
        return m_identityConstraints;
    }
}
