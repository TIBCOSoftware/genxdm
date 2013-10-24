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
package org.genxdm.bridgekit.xs.constraint;

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.CanonicalCastingContext;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompare;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareByte;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareDecimal;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareDouble;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareDuration;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareFloat;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareGregorian;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareInt;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareInteger;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareIntegerRestricted;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareLong;
import org.genxdm.bridgekit.xs.operations.OpXMLSchemaCompareShort;
import org.genxdm.bridgekit.xs.operations.ValueComparator;
import org.genxdm.exceptions.AtomCastException;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.CastingContext;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.exceptions.FacetException;
import org.genxdm.xs.exceptions.FacetMinMaxException;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Limit;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;

public final class FacetValueCompImpl extends FacetImpl implements Limit
{

    public FacetValueCompImpl(final String value, final FacetKind kind, final SimpleType type, final boolean isFixed)
    {
        super(isFixed);

        this.m_value = PreCondition.assertArgumentNotNull(value, "value");
        this.m_kind = PreCondition.assertArgumentNotNull(kind, "kind");
        this.baseType = PreCondition.assertArgumentNotNull(type, "type");

    }

    public FacetKind getKind()
    {
        return m_kind;
    }

    @Override
    public <A> A getLimit(AtomBridge<A> bridge)
    {
        return calculateValue(bridge);
    }

    @Override
    public <A> void validate(final List<? extends A> actualValue, final SimpleType simpleType, AtomBridge<A> bridge) 
            throws FacetException
    {
        PreCondition.assertArgumentNotNull(simpleType, "simpleType");
        for (final A atom : actualValue)
        {
//          PreCondition.assertArgumentNotNull(atom, "atom");
    
            // TODO: If it is a list type we might have to work one way, union,
            // another
            SimpleType uberType = baseType.getNativeTypeDefinition();
    
            final A rhsAtom;
            try
            {
                rhsAtom = castAsUberType(getLimit(bridge), uberType.getName(), bridge);
            }
            catch (AtomCastException ace)
            {
                throw new GenXDMException(ace);
            }
            ValueComparator<A> comparator = calculateComparator(rhsAtom, m_kind, uberType.getNativeType(), bridge);
            
            try
            {
                final A lhsAtom = castAsUberType(atom, uberType.getName(), bridge);
    
                if (!comparator.compare(lhsAtom))
                {
                    final String actual = bridge.getC14NForm(lhsAtom);
                    throw new FacetMinMaxException(this, actual, bridge);
                }
            }
            catch (final AtomCastException e)
            {
                throw new FacetMinMaxException(this, e.getSourceValue(), bridge);
            }
        }
    }

    public static OpXMLSchemaCompare calculateOpCode(final FacetKind kind)
    {
        switch (kind)
        {
            case MaxExclusive:
            {
                return OpXMLSchemaCompare.Lt;
            }
            case MaxInclusive:
            {
                return OpXMLSchemaCompare.Le;
            }
            case MinExclusive:
            {
                return OpXMLSchemaCompare.Gt;
            }
            case MinInclusive:
            {
                return OpXMLSchemaCompare.Ge;
            }
            default:
            {
                throw new AssertionError(kind);
            }
        }
    }

    private static <A> ValueComparator<A> calculateComparator(final A rhsAtom, final FacetKind kind, final NativeType nativeType, final AtomBridge<A> atomBridge)
    {
        PreCondition.assertArgumentNotNull(nativeType, "uberType");

        final OpXMLSchemaCompare opcode = calculateOpCode(kind);

        switch (nativeType)
        {
            case DOUBLE:
            {
                return new OpXMLSchemaCompareDouble<A>(opcode, rhsAtom, atomBridge);
            }
            case FLOAT:
            {
                return new OpXMLSchemaCompareFloat<A>(opcode, rhsAtom, atomBridge);
            }
            case DECIMAL:
            {
                return new OpXMLSchemaCompareDecimal<A>(opcode, rhsAtom, atomBridge);
            }
            case INTEGER:
            {
                return new OpXMLSchemaCompareInteger<A>(opcode, rhsAtom, atomBridge);
            }
            case DATE:
            case DATETIME:
            case TIME:
            case GYEARMONTH:
            case GYEAR:
            case GMONTHDAY:
            case GDAY:
            case GMONTH:
            {
                return new OpXMLSchemaCompareGregorian<A>(opcode, rhsAtom, atomBridge);
            }
            case DURATION:
            {
                return new OpXMLSchemaCompareDuration<A>(opcode, rhsAtom, nativeType, atomBridge);
            }
            case LONG:
            {
                return new OpXMLSchemaCompareLong<A>(opcode, rhsAtom, atomBridge);
            }
            case INT:
            {
                return new OpXMLSchemaCompareInt<A>(opcode, rhsAtom, atomBridge);
            }
            case SHORT:
            {
                return new OpXMLSchemaCompareShort<A>(opcode, rhsAtom, atomBridge);
            }
            case BYTE:
            {
                return new OpXMLSchemaCompareByte<A>(opcode, rhsAtom, atomBridge);
            }
            case NON_POSITIVE_INTEGER:
            case NEGATIVE_INTEGER:
            case NON_NEGATIVE_INTEGER:
            case UNSIGNED_LONG:
            case UNSIGNED_INT:
            case UNSIGNED_SHORT:
            case UNSIGNED_BYTE:
            case POSITIVE_INTEGER:
            {
                return new OpXMLSchemaCompareIntegerRestricted<A>(opcode, rhsAtom, nativeType, atomBridge);
            }
            default:
            {
                throw new AssertionError(nativeType);
            }
        }
    }

    private <A> A castAsUberType(final A atom, final QName uberType, final AtomBridge<A> atomBridge)
        throws AtomCastException
    {
         return atomBridge.castAs(atom, uberType, castingContext);
    }
    
    // TODO: this is quick and dirty, just to make it compile.
    // it's stolen from the parser, which produces problems of its own.
    private <A> A calculateValue(AtomBridge<A> bridge)
//      throws DatatypeException
    {
        // TODO: this fails to handle schema errors at parse time.  hmmm.
//        final List<A> atomicValue;
//        {
//            try
//            {
                //atomicValue = baseType.validate(m_value, bridge);
        try {
        return baseType.validate(m_value, bridge).get(0);
        } catch (DatatypeException dte) {
            throw new RuntimeException(dte);
        }
//            }
//            catch (final DatatypeException dte)
//            {
//                final SimpleTypeException ste = new SimpleTypeException(m_value, baseType, dte);
//                final QName elementName = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, xmlFacet.elementName);
//                final QName attributeName = new QName(XMLRepresentation.LN_VALUE);
//                final SrcFrozenLocation location = xmlFacet.getLocation();
//                throw new SmAttributeUseException(elementName, attributeName, location, ste);
//            }
//        }
    }
    
    private final FacetKind m_kind;
    private final SimpleType baseType;
    private final String m_value;
    private final CastingContext castingContext = new CanonicalCastingContext();
}
