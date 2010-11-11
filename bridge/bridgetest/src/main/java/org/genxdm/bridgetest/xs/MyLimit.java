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
package org.genxdm.bridgetest.xs;

import java.math.BigInteger;
import java.util.List;

import org.genxdm.exceptions.IllegalNullArgumentException;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.exceptions.FacetException;
import org.genxdm.xs.exceptions.FacetMinMaxException;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Limit;
import org.genxdm.xs.types.SimpleType;

/**
 * This facet has been designed with generality in mind rather than efficiency.
 * 
 * @param <A>
 *            The atom handle.
 */
public final class MyLimit<A> implements Limit<A>
{
	private final FacetKind kind;
	private final A limitValue;
	private final AtomBridge<A> atomBridge;

	public MyLimit(final FacetKind kind, final A value, final AtomBridge<A> atomBridge)
	{
		this.kind = IllegalNullArgumentException.check(kind, "kind");
		this.limitValue = IllegalNullArgumentException.check(value, "value");
		this.atomBridge = IllegalNullArgumentException.check(atomBridge, "atomBridge");
	}

	public A getLimit()
	{
		return limitValue;
	}

	public void validate(final A atom, final SimpleType<A> simpleType) throws FacetMinMaxException
	{
		IllegalNullArgumentException.check(atom, "atom");
		IllegalNullArgumentException.check(simpleType, "simpleType");
		switch (atomBridge.getNativeType(limitValue))
		{
			case INTEGER:
			{
				final BigInteger limitInteger = atomBridge.getInteger(limitValue);
				final BigInteger actualInteger = atomBridge.getInteger(atom);
				switch (kind)
				{
					case MinInclusive:
					{
						if (actualInteger.compareTo(limitInteger) < 0)
						{
							throw new FacetMinMaxException(this, atomBridge.getC14NForm(atom));
						}
					}
					break;
					case MaxExclusive:
					{
						if (actualInteger.compareTo(limitInteger) >= 0)
						{
							throw new FacetMinMaxException(this, atomBridge.getC14NForm(atom));
						}
					}
					break;
					default:
					{
						throw new AssertionError(kind);
					}
				}
			}
			break;
			default:
			{
				throw new AssertionError(atomBridge.getNativeType(limitValue));
			}
		}
	}

	public FacetKind getKind()
	{
		return kind;
	}

	public boolean isFixed()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public void validate(final List<? extends A> actualValue, final SimpleType<A> simpleType) throws FacetException
	{
		IllegalNullArgumentException.check(actualValue, "actualValue");
		IllegalNullArgumentException.check(simpleType, "simpleType");
		if (actualValue.size() == 1)
		{
			validate(actualValue.get(0), simpleType);
		}
		else
		{
			throw new AssertionError();
		}
	}
}
