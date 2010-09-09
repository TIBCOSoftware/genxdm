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
package org.gxml.bridgekit.xs;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.gxml.typed.types.AtomBridge;
import org.gxml.xs.components.SmEnumeration;
import org.gxml.xs.enums.SmDerivationMethod;
import org.gxml.xs.enums.SmScopeExtent;
import org.gxml.xs.enums.SmWhiteSpacePolicy;
import org.gxml.xs.exceptions.SmDatatypeException;
import org.gxml.xs.facets.SmFacet;
import org.gxml.xs.facets.SmFacetKind;
import org.gxml.xs.facets.SmPattern;
import org.gxml.xs.resolve.SmPrefixResolver;
import org.gxml.xs.types.SmNativeType;
import org.gxml.xs.types.SmSequenceTypeVisitor;
import org.gxml.xs.types.SmSimpleType;

final class HexBinaryType<A> extends AbstractAtomType<A>
{
	public HexBinaryType(final QName name, final SmSimpleType<A> baseType, final AtomBridge<A> atomBridge)
	{
		super(name, baseType, atomBridge);
	}

	public void accept(SmSequenceTypeVisitor<A> visitor)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public boolean derivedFrom(String namespace, String name, Set<SmDerivationMethod> derivationMethods)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<SmEnumeration<A>> getEnumerations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmFacet<A> getFacetOfKind(SmFacetKind facetKind)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<SmFacet<A>> getFacets()
	{
		return Collections.emptyList();
	}

	public Set<SmDerivationMethod> getFinal()
	{
		return Collections.emptySet();
	}

	public SmNativeType getNativeType()
	{
		return SmNativeType.HEX_BINARY;
	}

	public Iterable<SmPattern> getPatterns()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmScopeExtent getScopeExtent()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmWhiteSpacePolicy getWhiteSpacePolicy()
	{
		return SmWhiteSpacePolicy.COLLAPSE;
	}

	public boolean hasEnumerations()
	{
		return false;
	}

	public boolean hasFacetOfKind(final SmFacetKind facetKind)
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

	public List<A> validate(final String initialValue) throws SmDatatypeException
	{
		final String normalized = normalize(initialValue);
		try
		{
			return atomBridge.wrapAtom(atomBridge.createHexBinary(parseHexBinary(normalized, this)));
		}
		catch (final RuntimeException e)
		{
			throw new SmDatatypeException(initialValue, this);
		}
	}

	private static void illegal(final String s, final char c, final SmSimpleType<?> type) throws SmDatatypeException
	{
		throw new SmDatatypeException(s, type);
		// throw new SmDatatypeException(s, type, new IllegalArgumentException("Illegal hex character:" + c));
	}

	public static byte[] parseHexBinary(final String s, final SmSimpleType<?> type) throws SmDatatypeException
	{
		final char[] buf = s.toCharArray();

		if (buf.length % 2 != 0)
		{
			throw new SmDatatypeException(s, type);
			// throw new SmDatatypeException(s, type, new
			// IllegalArgumentException("Improperly encoded hex, odd# of characters"));
		}

		final byte[] b = new byte[buf.length >> 1];

		final int len = buf.length;

		for (int i = 0; i < len; i += 2)
		{
			final int hi = hexval(buf[i]);
			if (hi < 0)
			{
				illegal(s, (char)hi, type);
			}
			final int lo = hexval(buf[i + 1]);
			if (lo < 0)
			{
				illegal(s, (char)lo, type);
			}
			b[i >> 1] = (byte)((hi << 4) + lo);
		}
		return b;
	}

	private static int hexval(final char c)
	{
		if (c >= '0' && c <= '9')
		{
			return c - '0';
		}
		if (c >= 'A' && c <= 'F')
		{
			return c - ('A' - 10);
		}
		if (c >= 'a' && c <= 'f')
		{
			// according to schema, section 3.2.15hexBinary, lower case a-f are ok, too.
			return c - ('a' - 10);
		}
		else
		{
			return -1;
		}
	}

	public List<A> validate(String initialValue, SmPrefixResolver resolver) throws SmDatatypeException
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
}
