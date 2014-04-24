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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.names.PrefixResolver;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.facets.Facet;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Pattern;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleType;

public final class BooleanType extends AbstractAtomType
{

    public BooleanType(final QName name, final SimpleType baseType)
    {
        super(name, baseType);
    }

    public void accept(SequenceTypeVisitor visitor)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public boolean derivedFrom(String namespace, String name, Set<DerivationMethod> derivationMethods)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Iterable<EnumerationDefinition> getEnumerations()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Facet getFacetOfKind(FacetKind facetKind)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public Iterable<Facet> getFacets()
    {
        return Collections.emptyList();
    }

    public Set<DerivationMethod> getFinal()
    {
        return Collections.emptySet();
    }

    public NativeType getNativeType()
    {
        return NativeType.BOOLEAN;
    }

    public Iterable<Pattern> getPatterns()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public ScopeExtent getScopeExtent()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("TODO");
    }

    public WhiteSpacePolicy getWhiteSpacePolicy()
    {
        return WhiteSpacePolicy.COLLAPSE;
    }

    public boolean hasEnumerations()
    {
        return false;
    }

    public boolean hasFacetOfKind(final FacetKind facetKind)
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

    public boolean isID()
    {
        return false;
    }

    public boolean isIDREF()
    {
        return false;
    }

    public <A> List<A> validate(final String initialValue, AtomBridge<A> atomBridge) throws DatatypeException
    {
        List<A> TRUE = atomBridge.wrapAtom(atomBridge.getBooleanTrue());
        List<A> FALSE = atomBridge.wrapAtom(atomBridge.getBooleanFalse());

        final String collapsed = PreCondition.assertArgumentNotNull(initialValue, "sourceValue").trim();

        if ("true".equals(collapsed))
        {
            return TRUE;
        }
        else if ("false".equals(collapsed))
        {
            return FALSE;
        }
        else if ("1".equals(collapsed))
        {
            return TRUE;
        }
        else if ("0".equals(collapsed))
        {
            return FALSE;
        }
        else
        {
            throw new DatatypeException(initialValue, this);
        }
    }

    public <A> List<A> validate(final String initialValue, final PrefixResolver resolver, AtomBridge<A> bridge) throws DatatypeException
    {
        return validate(initialValue, bridge);
    }
}
