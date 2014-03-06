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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.Type;

public final class ElementDeclTypeImpl extends DataComponentImpl implements ElementDefinition
{
    public ElementDeclTypeImpl(final QName name, final ScopeExtent scope, final Type type)
    {
        super(name, scope);
        this.m_type = PreCondition.assertArgumentNotNull(type, "type");
    }

    public void accept(final SequenceTypeVisitor visitor)
    {
        visitor.visit(this);
    }

    public void addIdentityConstraint(final IdentityConstraint identityConstraint)
    {
        PreCondition.assertArgumentNotNull(identityConstraint, "identityConstraint");
        m_constraints.put(identityConstraint.getName(), identityConstraint);
    }

    public void addSubstitutionGroupMember(final ElementDefinition member)
    {
        assertNotLocked();
        PreCondition.assertArgumentNotNull(member, "member");
        m_substitutionGroupMembers.add(member);
    }

    public Set<DerivationMethod> getDisallowedSubtitutions()
    {
        return m_blockUnmodifiable;
    }

    public Iterable<IdentityConstraint> getIdentityConstraints()
    {
        return m_constraints.values();
    }

    public PrimeTypeKind getKind()
    {
        return PrimeTypeKind.SCHEMA_ELEMENT;
    }

    public NodeKind getNodeKind()
    {
        return NodeKind.ELEMENT;
    }

    public ElementDefinition getSubstitutionGroup()
    {
        return m_substitutionGroup;
    }

    public Set<DerivationMethod> getSubstitutionGroupExclusions()
    {
        return m_finalUnmodifiable;
    }

    public Iterable<ElementDefinition> getSubstitutionGroupMembers()
    {
        return m_substitutionGroupMembers;
    }

    public Type getType()
    {
        return m_type;
    }

    public boolean hasIdentityConstraints()
    {
        return !m_constraints.isEmpty();
    }

    public boolean hasSubstitutionGroup()
    {
        return (null != m_substitutionGroup);
    }

    public boolean hasSubstitutionGroupMembers()
    {
        return !m_substitutionGroupMembers.isEmpty();
    }

    public boolean isAbstract()
    {
        return m_isAbstract;
    }

    public boolean isChoice()
    {
        return false;
    }

    public boolean isDisallowedSubstitution(final DerivationMethod derivation)
    {
        PreCondition.assertArgumentNotNull(derivation, "derivation");
        return m_block.contains(derivation);
    }

    public boolean isNative()
    {
        return false;
    }

    public boolean isNillable()
    {
        return m_isNillable;
    }

    public boolean isNone()
    {
        return false;
    }

    public PrimeType prime()
    {
        return this;
    }

    public Quantifier quantifier()
    {
        return Quantifier.EXACTLY_ONE;
    }

    public void removeSubstitutionGroupMember(final ElementDefinition member)
    {
        assertNotLocked();
        PreCondition.assertArgumentNotNull(member, "member");
        m_substitutionGroupMembers.add(member);
    }

    public void setAbstract(boolean isAbstract)
    {
        assertNotLocked();
        m_isAbstract = isAbstract;
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

    public void setFinal(final DerivationMethod derivation, final boolean enabled)
    {
        assertNotLocked();
        if (enabled)
        {
            m_final.add(derivation);
        }
        else
        {
            m_final.remove(derivation);
        }
    }

    public void setNillable(boolean isNillable)
    {
        assertNotLocked();
        m_isNillable = isNillable;
    }

    public void setSubstitutionGroup(final ElementDeclTypeImpl substitutionGroup)
    {
        assertNotLocked();
        if (m_substitutionGroup != substitutionGroup)
        {
            if (null != m_substitutionGroup)
            {
                m_substitutionGroup.removeSubstitutionGroupMember(this);
            }
            if (null != substitutionGroup)
            {
                substitutionGroup.addSubstitutionGroupMember(this);
            }
            m_substitutionGroup = substitutionGroup;
        }
    }

    public void setType(final Type type)
    {
        assertNotLocked();
        m_type = PreCondition.assertArgumentNotNull(type, "type");
    }

    public boolean subtype(final PrimeType rhs)
    {
        PreCondition.assertArgumentNotNull(rhs, "rhs");
        switch (rhs.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)rhs;
                return subtype(choiceType.getLHS()) || subtype(choiceType.getRHS());
            }
            case SCHEMA_ELEMENT:
            {
                final ElementDefinition other = (ElementDefinition)rhs;
                return getName().equals(other.getName());
            }
            case ELEMENT:
            {
                final ElementNodeType other = (ElementNodeType)rhs;
                return getName().equals(other.getName());
            }
            case COMPLEX:
            case ANY_TYPE:
            case NODE:
            case ITEM:
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
    public String toString()
    {
        return "schema-element(" + getName().toString() + ")";
    }

    private final EnumSet<DerivationMethod> m_block = EnumSet.noneOf(DerivationMethod.class);
    private final Set<DerivationMethod> m_blockUnmodifiable = Collections.unmodifiableSet(m_block);
    private final HashMap<QName, IdentityConstraint> m_constraints = new HashMap<QName, IdentityConstraint>();
    private final EnumSet<DerivationMethod> m_final = EnumSet.noneOf(DerivationMethod.class);
    private final Set<DerivationMethod> m_finalUnmodifiable = Collections.unmodifiableSet(m_final);

    /**
     * The {abstract} property is mutable defaults to <code>false</code>
     */
    private boolean m_isAbstract = false;

    /**
     * The {nullable} property is mutable defaults to <code>false</code>
     */
    private boolean m_isNillable = false;

    /**
     * The {substitution group} (head) is mutable.
     */
    private ElementDeclTypeImpl m_substitutionGroup = null;

    private final HashSet<ElementDefinition> m_substitutionGroupMembers = new HashSet<ElementDefinition>();

    /**
     * The {type} property is mutable.
     * This permits it to be declared generically (but non-null) at instantiation,
     * and later fined down, after circularities (which are permitted) have been
     * resolved.  The alternative to requiring the type would be to set it to the
     * ComplexUrType by default, and let that be overridden in the constructor.
     */
    private Type m_type;

}
