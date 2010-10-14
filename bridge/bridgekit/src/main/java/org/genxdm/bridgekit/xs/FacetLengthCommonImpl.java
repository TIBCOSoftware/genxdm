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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.exceptions.SmFacetException;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.facets.SmLengthFacetUOM;
import org.genxdm.xs.types.SmListSimpleType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSimpleType;

/**
 * Abstract base class for implementations of {@link org.genxdm.xs.facets.SmFacet}
 */
abstract class FacetLengthCommonImpl<A> extends FacetImpl<A>
{
	private static <A> int length(final A atom, final AtomBridge<A> atomBridge)
	{
		final SmNativeType nativeType = atomBridge.getNativeType(atom);
		if (nativeType.isString())
		{
			return atomBridge.getString(atom).length();
		}
		else if (nativeType == SmNativeType.ANY_URI)
		{
			return atomBridge.getURI(atom).toString().length();
		}
		else if (nativeType == SmNativeType.BASE64_BINARY)
		{
			return atomBridge.getBase64Binary(atom).length;
		}
		else if (nativeType == SmNativeType.HEX_BINARY)
		{
			return atomBridge.getHexBinary(atom).length;
		}
		else
		{
			throw new AssertionError(nativeType);
		}
	}

	private static <A> SmLengthFacetUOM uom(final A atom, final AtomBridge<A> atomBridge)
	{
		final SmNativeType nativeType = atomBridge.getNativeType(atom);
		if (nativeType.isString())
		{
			return SmLengthFacetUOM.Characters;
		}
		else if (nativeType == SmNativeType.ANY_URI)
		{
			return SmLengthFacetUOM.Characters;
		}
		else if (nativeType == SmNativeType.BASE64_BINARY)
		{
			return SmLengthFacetUOM.Octets;
		}
		else if (nativeType == SmNativeType.HEX_BINARY)
		{
			return SmLengthFacetUOM.Octets;
		}
		else
		{
			throw new AssertionError(nativeType);
		}
	}

	private final AtomBridge<A> atomBridge;

	private final SmFacetKind facetKind;

	public FacetLengthCommonImpl(final boolean isFixed, final AtomBridge<A> atomBridge, SmFacetKind facetKind)
	{
		super(isFixed);
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
		this.facetKind = PreCondition.assertArgumentNotNull(facetKind, "facetKind");
	}

	protected abstract void checkLength(final int length, final SmLengthFacetUOM uom) throws SmFacetException;

	public SmFacetKind getKind()
	{
		return facetKind;
	}

	public final void validate(final List<? extends A> actualValue, final SmSimpleType<A> simpleType) throws SmFacetException
	{
		if (simpleType instanceof SmListSimpleType<?>)
		{
			checkLength(actualValue.size(), SmLengthFacetUOM.ListItems);
		}
		else
		{
			for (final A atom : actualValue)
			{
				final SmLengthFacetUOM uom = uom(atom, atomBridge);
				switch (uom)
				{
					case NotApplicable:
					{
						// Ignore e.g. xs:QName and xs:NOTATION are deprecated.
					}
					break;
					default:
					{
						checkLength(length(atom, atomBridge), uom);
					}
				}
			}
		}
	}
}
