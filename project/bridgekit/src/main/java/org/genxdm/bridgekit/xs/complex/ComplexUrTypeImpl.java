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
package org.genxdm.bridgekit.xs.complex;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.ForeignAttributesSink;
import org.genxdm.bridgekit.xs.simple.AbstractPrimeExcludingNoneType;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.components.SchemaWildcard;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.constraints.NamespaceConstraint;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ProcessContentsMode;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.ComplexUrType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.Type;

/**
 * xs:anyType
 */
public final class ComplexUrTypeImpl 
    extends AbstractPrimeExcludingNoneType 
    implements ComplexUrType, ForeignAttributesSink
{

    public ComplexUrTypeImpl(final String W3C_XML_SCHEMA_NS_URI)
    {
        this.name = new QName(W3C_XML_SCHEMA_NS_URI, "anyType");

        final SchemaWildcard anyTerm = new WildcardImpl(ProcessContentsMode.Lax, NamespaceConstraint.Any());

        final LinkedList<SchemaParticle> particles = new LinkedList<SchemaParticle>();
        particles.add(new ParticleWithWildcardTerm(0, anyTerm));

        final ModelGroup modelGroup = new ModelGroupImpl(ModelGroup.SmCompositor.Sequence, particles, null, true, ScopeExtent.Local);

        this.contentType = new ContentTypeImpl(true, new ParticleWithModelGroupTerm(1, 1, modelGroup));
        this.attributeWildcard = anyTerm;
    }

    public void accept(final SequenceTypeVisitor visitor)
    {
        visitor.visit(this);
    }

    public boolean derivedFrom(final String namespace, final String name, final Set<DerivationMethod> derivationMethods)
    {
        return false;
    }

    public boolean derivedFromType(final Type ancestorType, final Set<DerivationMethod> derivationMethods)
    {
        return false;
    }

    public Map<QName, AttributeUse> getAttributeUses()
    {
        return Collections.emptyMap();
    }

    public SchemaWildcard getAttributeWildcard()
    {
        return attributeWildcard;
    }

    public ComplexUrTypeImpl getBaseType()
    {
        return null;
    }

    public ContentType getContentType()
    {
        return contentType;
    }

    public DerivationMethod getDerivationMethod()
    {
        return DerivationMethod.Restriction;
    }

    public Set<DerivationMethod> getFinal()
    {
        return EnumSet.noneOf(DerivationMethod.class);
    }

    public PrimeTypeKind getKind()
    {
        return PrimeTypeKind.ANY_TYPE;
    }

    public String getLocalName()
    {
        return name.getLocalPart();
    }

    public QName getName()
    {
        return name;
    }

    public Set<DerivationMethod> getProhibitedSubstitutions()
    {
        // TODO: Is this immutable? If not, wrap it with Collections.unmod
        return EnumSet.noneOf(DerivationMethod.class);
    }

    public ScopeExtent getScopeExtent()
    {
        return ScopeExtent.Global;
    }

    public String getTargetNamespace()
    {
        return name.getNamespaceURI();
    }

    public boolean isAbstract()
    {
        return false;
    }

    public boolean isAnonymous()
    {
        return false;
    }

    public boolean isAtomicType()
    {
        return false;
    }

    public boolean isAtomicUrType()
    {
        return false;
    }

    public boolean isChoice()
    {
        return false;
    }

    public boolean isComplexUrType()
    {
        return true;
    }

    public boolean isFinal(final DerivationMethod derivation)
    {
        return false;
    }

    public boolean isLocked()
    {
        return true;
    }

    public boolean isNative()
    {
        return true;
    }

    public boolean isSimpleUrType()
    {
        return false;
    }

    public void lock()
    {
        // Ignore.
    }

    public PrimeType prime()
    {
        return this;
    }

    public void setAbstract(final boolean isAbstract)
    {
        throw new AssertionError(getName());
    }

    public void setAttributeWildcard(final SchemaWildcard attributeWildcard)
    {
        throw new AssertionError(getName());
    }

    public void setBlock(final DerivationMethod derivation, final boolean enabled)
    {
        throw new AssertionError(getName());
    }

    public void setContentType(final ContentType contentType)
    {
        throw new AssertionError(getName());
    }

    public void setFinal(final DerivationMethod derivation, final boolean enabled)
    {
        throw new AssertionError(getName());
    }

    public boolean subtype(final PrimeType rhs)
    {
        PreCondition.assertArgumentNotNull(rhs, "type");
        switch (rhs.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)rhs;
                return subtype(choiceType.getLHS()) || subtype(choiceType.getRHS());
            }
            case ANY_TYPE:
            {
                return true;
            }
            default:
            {
                return false;
            }
        }
    }

    @Override
    public final String toString()
    {
        return getName().toString();
    }
    private final SchemaWildcard attributeWildcard;
    private final ContentType contentType;
    private final QName name;
}
