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
package org.genxdm.bridgekit.xs;

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GxmlAtomCastException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.CastingContext;
import org.genxdm.xs.exceptions.AtomCastException;
import org.genxdm.xs.exceptions.FacetException;
import org.genxdm.xs.exceptions.FacetMinMaxException;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Limit;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;

public final class FacetValueCompImpl<A> extends FacetImpl<A> implements Limit<A>
{
	private final ValueComparator<A> m_comparator;
	private final FacetKind m_kind;
	private final QName m_uberType;
	private final A m_value;
	private final AtomBridge<A> m_atomBridge;
	private final CastingContext<A> castingContext = new CanonicalCastingContext<A>();;

	public FacetValueCompImpl(final A value, final FacetKind kind, final SimpleType<A> type, final boolean isFixed, final AtomBridge<A> atomBridge)
	{
		super(isFixed);

		this.m_value = PreCondition.assertArgumentNotNull(value, "value");
		this.m_kind = PreCondition.assertArgumentNotNull(kind, "kind");
		this.m_atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");

		// TODO: If it is a list type we might have to work one way, union,
		// another
		final SimpleType<A> nativeType = type.getNativeTypeDefinition();
		this.m_uberType = nativeType.getName();

		final A rhsAtom = castAsUberType(PreCondition.assertArgumentNotNull(value, "value"), m_uberType, atomBridge);
		m_comparator = calculateComparator(rhsAtom, kind, nativeType.getNativeType(), atomBridge);
	}

	public FacetKind getKind()
	{
		return m_kind;
	}

	public A getLimit()
	{
		return m_value;
	}

	public void validate(final A atom, final SimpleType<A> simpleType) throws FacetMinMaxException
	{
		PreCondition.assertArgumentNotNull(atom, "atom");
		PreCondition.assertArgumentNotNull(simpleType, "simpleType");
		try
		{
			final A lhsAtom = castAsUberType(atom, m_uberType, m_atomBridge);

			if (!m_comparator.compare(lhsAtom))
			{
				final String actual = m_atomBridge.getC14NForm(lhsAtom);
				throw new FacetMinMaxException(this, actual);
			}
		}
		catch (final AtomCastException e)
		{
			throw new FacetMinMaxException(this, e.getSourceValue());
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

	public void validate(final List<? extends A> actualValue, final SimpleType<A> simpleType) throws FacetException
	{
		for (final A atom : actualValue)
		{
			validate(atom, simpleType);
		}
	}

	private A castAsUberType(final A atom, final QName uberType, final AtomBridge<A> atomBridge)
	{
		try
		{
			return atomBridge.castAs(atom, uberType, castingContext);
		}
		catch (final GxmlAtomCastException e)
		{
			throw new AssertionError(e);
		}
	}
}
