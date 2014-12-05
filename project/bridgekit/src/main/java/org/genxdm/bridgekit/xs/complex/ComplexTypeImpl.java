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
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.components.SchemaWildcard;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.Type;

/**
 * A complex type, but not the Complex Ur-Type.
 */
public final class ComplexTypeImpl extends TypeImpl implements ComplexType, PrimeType
{
    public ComplexTypeImpl(final QName name, final boolean isNative, final boolean isAnonymous, final ScopeExtent scope, final Type baseType, final DerivationMethod derivation, final Map<QName, AttributeUse> attributeUses,
            final ContentType contentType, final Set<DerivationMethod> block, final AtomicType atoms)
    {
        super(PreCondition.assertArgumentNotNull(name, "name"), isAnonymous, scope, derivation);
        this.isNative = isNative;
        this.m_atoms = PreCondition.assertArgumentNotNull(atoms, "atoms");
        m_baseType = baseType; // PreCondition.assertArgumentNotNull(baseType, "baseType");
        m_contentType = PreCondition.assertArgumentNotNull(contentType, "contentType");
        m_block = PreCondition.assertArgumentNotNull(block, "block");
        m_blockUnmodifiable = Collections.unmodifiableSet(m_block);
        m_attributeUses = PreCondition.assertArgumentNotNull(attributeUses, "attributeUses");
        m_attributeWildcard = computeAttributeWildcard(baseType, derivation);
    }

    public void accept(final SequenceTypeVisitor visitor)
    {
        visitor.visit(this);
    }

    public SequenceType atomSet()
    {
        return m_atoms;
    }

    public Map<QName, AttributeUse> getAttributeUses()
    {
        return m_attributeUses;
    }

    public SchemaWildcard getAttributeWildcard()
    {
        return m_attributeWildcard;
    }

    public Type getBaseType()
    {
        return m_baseType;
    }
    public void setBaseType(Type baseType)
    {
    	m_baseType = baseType;
    }
    public ContentType getContentType()
    {
        return m_contentType;
    }

    public final Set<DerivationMethod> getFinal()
    {
        return m_final;
    }

    public PrimeTypeKind getKind()
    {
        return PrimeTypeKind.COMPLEX;
    }

    public Set<DerivationMethod> getProhibitedSubstitutions()
    {
        return m_blockUnmodifiable;
    }

    public boolean isAbstract()
    {
        return m_isAbstract;
    }

    public boolean isAtomicType()
    {
        return false;
    }

    public final boolean isAtomicUrType()
    {
        return false;
    }

    public boolean isChoice()
    {
        return false;
    }

    public final boolean isComplexUrType()
    {
        return false;
    }

    public boolean isFinal(final DerivationMethod derivation)
    {
        PreCondition.assertArgumentNotNull(derivation, "derivation");
        return m_final.contains(derivation);
    }

    public boolean isNative()
    {
        return isNative;
    }

    public boolean isNone()
    {
        return false;
    }

    public final boolean isSimpleUrType()
    {
        return false;
    }

    public final PrimeType prime()
    {
        return this;
    }

    public Quantifier quantifier()
    {
        return Quantifier.EXACTLY_ONE;
    }

    public void setAbstract(final boolean isAbstract)
    {
        assertNotLocked();
        m_isAbstract = isAbstract;
    }

    public void setAttributeWildcard(final SchemaWildcard attributeWildcard)
    {
        assertNotLocked();
        m_attributeWildcard = attributeWildcard;
    }

    public void setBlock(final DerivationMethod derivation, final boolean enabled)
    {
        assertNotLocked();
        if (enabled)
        {
            m_block.add(derivation);
        }
        else
        {
            m_block.remove(derivation);
        }
    }

    public void setContentType(final ContentType contentType)
    {
        assertNotLocked();
        m_contentType = PreCondition.assertArgumentNotNull(contentType, "name");
    }

    public void setFinal(final DerivationMethod derivation, final boolean enabled)
    {
        assertNotLocked();
        PreCondition.assertArgumentNotNull(derivation, "derivation");
        PreCondition.assertTrue(derivation.isExtension() || derivation.isRestriction(), "derivation (" + derivation + ") must be extension or restriction for a complex type");
        if (enabled)
        {
            m_final.add(derivation);
        }
        else
        {
            m_final.remove(derivation);
        }
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
                // case ANY_TYPE:
                // case COMPLEX:
                // {
                // final ComplexType complexType = (ComplexType)rhs;
                // return SchemaSupport.subtype(this, complexType);
                // }
            case ELEMENT:
            {
                @SuppressWarnings("unused")
                final ElementNodeType element = (ElementNodeType)rhs;
                return false;
            }
            default:
            {
                return false;
            }
        }
    }

    private static  SchemaWildcard computeAttributeWildcard(final Type baseType, final DerivationMethod derivation)
    {
        if (derivation.isRestriction())
        {
            return null;
        }
        if (derivation.isExtension())
        {
            if (baseType instanceof ComplexType)
            {
                return ((ComplexType)baseType).getAttributeWildcard();
            }
            else
            {
                return null;
            }
        }
        else
        {
            throw new AssertionError(derivation);
        }
    }

    private final boolean isNative;
    private final Map<QName, AttributeUse> m_attributeUses;
    /**
     * {attribute wildcard} is mutable
     */
    private SchemaWildcard m_attributeWildcard = null;
    private Type m_baseType;

    private final Set<DerivationMethod> m_block;

    private final Set<DerivationMethod> m_blockUnmodifiable;

    private final AtomicType m_atoms;

    /**
     * {content type} is mutable.
     */
    private ContentType m_contentType;

    /**
     * {final} is mutable.
     */
    private final EnumSet<DerivationMethod> m_final = EnumSet.noneOf(DerivationMethod.class);

    /**
     * {abstract} is mutable and defaults to <code>false</code>.
     */
    private boolean m_isAbstract = false;
}
