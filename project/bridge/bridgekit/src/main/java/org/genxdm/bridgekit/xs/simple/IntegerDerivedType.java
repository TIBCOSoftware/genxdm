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

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

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
import org.genxdm.xs.resolve.PrefixResolver;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleType;

public class IntegerDerivedType extends AbstractAtomType
{
	private static final BigInteger UNSIGNED_LONG_MAX_INCLUSIVE = BigInteger.valueOf(Long.MAX_VALUE).subtract(BigInteger.valueOf(Long.MIN_VALUE));
	private static final BigInteger UNSIGNED_INT_MAX_INCLUSIVE = BigInteger.valueOf(Integer.MAX_VALUE).subtract(BigInteger.valueOf(Integer.MIN_VALUE));
	private static final BigInteger UNSIGNED_SHORT_MAX_INCLUSIVE = BigInteger.valueOf(Short.MAX_VALUE).subtract(BigInteger.valueOf(Short.MIN_VALUE));
	private static final BigInteger UNSIGNED_BYTE_MAX_INCLUSIVE = BigInteger.valueOf(Byte.MAX_VALUE).subtract(BigInteger.valueOf(Byte.MIN_VALUE));
	private final NativeType nativeType;

	public IntegerDerivedType(final NativeType nativeType, final QName name, final SimpleType baseType)
	{
		super(name, baseType);
		this.nativeType = PreCondition.assertArgumentNotNull(nativeType, "nativeType");
	}

	public void accept(SequenceTypeVisitor visitor)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean derivedFrom(String namespace, String name, Set<DerivationMethod> derivationMethods)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<EnumerationDefinition> getEnumerations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Facet getFacetOfKind(final FacetKind facetKind)
	{
		return null;
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
		return nativeType;
	}

	public Iterable<Pattern> getPatterns()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public ScopeExtent getScopeExtent()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
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
		try
		{
			// Note that trimming eliminates a leading plus-sign, but leaves leading minus-sign.
			final String trimmed = trim(initialValue);
			final BigInteger integerValue = new BigInteger(trimmed);
			switch (nativeType)
			{
				case UNSIGNED_LONG:
				{
					if (integerValue.signum() >= 0 && integerValue.compareTo(UNSIGNED_LONG_MAX_INCLUSIVE) <= 0)
					{
						return atomBridge.wrapAtom(atomBridge.createIntegerDerived(integerValue, nativeType));
					}
					else
					{
						throw new DatatypeException(trimmed, this);
					}
				}
				case UNSIGNED_INT:
				{
					if (integerValue.signum() >= 0 && integerValue.compareTo(UNSIGNED_INT_MAX_INCLUSIVE) <= 0)
					{
						return atomBridge.wrapAtom(atomBridge.createIntegerDerived(integerValue.longValue(), nativeType));
					}
					else
					{
						throw new DatatypeException(trimmed, this);
					}
				}
				case UNSIGNED_SHORT:
				{
					if (integerValue.signum() >= 0 && integerValue.compareTo(UNSIGNED_SHORT_MAX_INCLUSIVE) <= 0)
					{
						return atomBridge.wrapAtom(atomBridge.createIntegerDerived(integerValue.intValue(), nativeType));
					}
					else
					{
						throw new DatatypeException(trimmed, this);
					}
				}
				case UNSIGNED_BYTE:
				{
					if (integerValue.signum() >= 0 && integerValue.compareTo(UNSIGNED_BYTE_MAX_INCLUSIVE) <= 0)
					{
						return atomBridge.wrapAtom(atomBridge.createIntegerDerived(integerValue.shortValue(), nativeType));
					}
					else
					{
						throw new DatatypeException(trimmed, this);
					}
				}
				case NEGATIVE_INTEGER:
				{
					if (integerValue.signum() < 0)
					{
						return atomBridge.wrapAtom(atomBridge.createIntegerDerived(integerValue, nativeType));
					}
					else
					{
						throw new DatatypeException(trimmed, this);
					}
				}
				case POSITIVE_INTEGER:
				{
					if (integerValue.signum() > 0)
					{
						return atomBridge.wrapAtom(atomBridge.createIntegerDerived(integerValue, NativeType.POSITIVE_INTEGER));
					}
					else
					{
						throw new DatatypeException(trimmed, this);
					}
				}
				case NON_POSITIVE_INTEGER:
				{
					if (integerValue.signum() <= 0)
					{
						return atomBridge.wrapAtom(atomBridge.createIntegerDerived(integerValue, nativeType));
					}
					else
					{
						throw new DatatypeException(trimmed, this);
					}
				}
				case NON_NEGATIVE_INTEGER:
				{
					if (integerValue.signum() >= 0)
					{
						return atomBridge.wrapAtom(atomBridge.createIntegerDerived(integerValue, nativeType));
					}
					else
					{
						throw new DatatypeException(trimmed, this);
					}
				}
				default:
				{
					throw new AssertionError(nativeType);
				}
			}
		}
		catch (final NumberFormatException e)
		{
			throw new DatatypeException(initialValue, this);
		}
	}

	public <A> List<A> validate(String initialValue, PrefixResolver resolver, AtomBridge<A> bridge) throws DatatypeException
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
}
