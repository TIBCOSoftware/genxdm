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

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.NodeKind;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleMarkerType;

public final class AttributeDeclWithParentAxisType implements AttributeDefinition
{
    private final AttributeDefinition m_attribute;

    public AttributeDeclWithParentAxisType(final AttributeDefinition element, final ElementDefinition parentAxis)
    {
        m_attribute = PreCondition.assertArgumentNotNull(element);
    }

    public void accept(final SequenceTypeVisitor visitor)
    {
        visitor.visit(this);
    }

    public PrimeTypeKind getKind()
    {
        return m_attribute.getKind();
    }

    public String getLocalName()
    {
        return m_attribute.getLocalName();
    }

    public QName getName()
    {
        return m_attribute.getName();
    }

    public NodeKind getNodeKind()
    {
        return m_attribute.getNodeKind();
    }

    public ScopeExtent getScopeExtent()
    {
        return m_attribute.getScopeExtent();
    }

    public String getTargetNamespace()
    {
        return m_attribute.getTargetNamespace();
    }

    public SimpleMarkerType getType()
    {
        return m_attribute.getType();
    }

    public ValueConstraint getValueConstraint()
    {
        return m_attribute.getValueConstraint();
    }

    public boolean isAnonymous()
    {
        return m_attribute.isAnonymous();
    }

    public boolean isChoice()
    {
        return m_attribute.isChoice();
    }

    public boolean isNative()
    {
        return m_attribute.isNative();
    }

    public boolean isNone()
    {
        return m_attribute.isNone();
    }

    public PrimeType prime()
    {
        return this;
    }

    public Quantifier quantifier()
    {
        return m_attribute.quantifier();
    }

    public boolean subtype(final PrimeType rhs)
    {
        return m_attribute.subtype(rhs);
    }

    @Override
    public String toString()
    {
        return m_attribute.toString();
    }
}
