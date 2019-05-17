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
package org.genxdm.bridgekit.xs.simple;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.SchemaSupport;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.PrefixResolver;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.facets.Facet;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Pattern;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.AtomicUrType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.PrimeChoiceType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.Type;

public final class AtomicUrTypeImpl 
    extends AbstractPrimeExcludingNoneType 
    implements AtomicUrType
{
    private final SimpleUrType m_baseType;
    private final QName m_name;

    public AtomicUrTypeImpl(final String W3C_XML_SCHEMA_NS_URI, final SimpleUrType baseType)
    {
        this.m_name = new QName(W3C_XML_SCHEMA_NS_URI, "anyAtomicType");
        this.m_baseType = PreCondition.assertArgumentNotNull(baseType, "baseType");
    }

    public void accept(final SequenceTypeVisitor visitor)
    {
        visitor.visit(this);
    }

    public boolean derivedFrom(final String namespace, final String name, final Set<DerivationMethod> derivationMethods)
    {
        return SchemaSupport.derivedFrom(this, namespace, name, derivationMethods);
    }

    public boolean derivedFromType(final Type ancestorType, final Set<DerivationMethod> derivationMethods)
    {
        return SchemaSupport.derivedFromType(this, ancestorType, derivationMethods);
    }

    public SimpleUrType getBaseType()
    {
        return m_baseType;
    }

    public DerivationMethod getDerivationMethod()
    {
        return DerivationMethod.Restriction;
    }

    public Iterable<EnumerationDefinition> getEnumerations()
    {
        throw new AssertionError(getName());
    }

    public Facet getFacetOfKind(final FacetKind facetKind)
    {
        throw new AssertionError(getName());
    }

    public Iterable<Facet> getFacets()
    {
        throw new AssertionError(getName());
    }

    public Set<DerivationMethod> getFinal()
    {
        return EnumSet.noneOf(DerivationMethod.class);
    }

    public PrimeTypeKind getKind()
    {
        return PrimeTypeKind.ANY_ATOMIC_TYPE;
    }

    public String getLocalName()
    {
        return m_name.getLocalPart();
    }

    public QName getName()
    {
        return m_name;
    }

    public NativeType getNativeType()
    {
        return NativeType.ANY_ATOMIC_TYPE;
    }

    public Iterable<Pattern> getPatterns()
    {
        throw new AssertionError(getName());
    }

    public AtomicType getNativeTypeDefinition()
    {
        return this;
    }

    public ScopeExtent getScopeExtent()
    {
        return ScopeExtent.Global;
    }

    public String getTargetNamespace()
    {
        return m_name.getNamespaceURI();
    }

    public WhiteSpacePolicy getWhiteSpacePolicy()
    {
        return WhiteSpacePolicy.PRESERVE;
    }

    public boolean hasEnumerations()
    {
        return false;
    }

    public boolean hasFacetOfKind(FacetKind facetKind)
    {
        return false;
    }

    public boolean hasFacets()
    {
        return false;
    }

    public boolean hasPatterns()
    {
        return false;
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
        return true;
    }

    public boolean isAtomicUrType()
    {
        return true;
    }

    public boolean isChoice()
    {
        return false;
    }

    public boolean isComplexUrType()
    {
        return false;
    }

    public boolean isFinal(final DerivationMethod derivation)
    {
        return false;
    }

    public boolean isID()
    {
        return false;
    }

    public boolean isIDREF()
    {
        return false;
    }

    public boolean isIDREFS()
    {
        return false;
    }

    public boolean isListType()
    {
        return false;
    }

    public boolean isNative()
    {
        return true;
    }

    public boolean isSimpleUrType()
    {
        return false;
    }

    public boolean isUnionType()
    {
        return false;
    }

    public String normalize(final String initialValue)
    {
        return initialValue;
    }

    public AtomicUrType prime()
    {
        return this;
    }

    public boolean subtype(final PrimeType rhs)
    {
        switch (rhs.getKind())
        {
            case CHOICE:
            {
                final PrimeChoiceType choiceType = (PrimeChoiceType)rhs;
                return subtype(choiceType.getLHS()) || subtype(choiceType.getRHS());
            }
            case ANY_ATOMIC_TYPE:
            case ANY_SIMPLE_TYPE:
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
    public String toString()
    {
        return "xs:anyAtomicType";
    }

    public <A> List<A> validate(final List<? extends A> value, AtomBridge<A> atomBridge)
    {
        final String strval = atomBridge.getC14NString(value);
        return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(strval));
    }

    public <A> List<A> validate(final String initialValue, AtomBridge<A> atomBridge)
    {
        return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(initialValue));
    }

    public <A> List<A> validate(final String value, final PrefixResolver resolver, AtomBridge<A> atomBridge)
    {
        return validate(value, atomBridge);
    }
}
