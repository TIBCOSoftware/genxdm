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
package org.gxml.bridgetest.typed;

import java.util.HashSet;
import java.util.Set;

import org.genxdm.typed.types.VariantBridge;
import org.genxdm.typed.types.VariantKind;
import org.gxml.bridgetest.GxTestBase;

public abstract class ValueBridgeTestBase<N, A, X> 
    extends GxTestBase<N>
{
    abstract public VariantBridge<N, A, X> getVariantBridge();
    
	public void testProlog()
	{
		final Set<X> seeds = new HashSet<X>();

		final VariantBridge<N, A, X> valueBridge = getVariantBridge();

		seeds.add(valueBridge.empty());

		final Set<X> values = computeClosure(seeds, valueBridge);

		for (final X value : values)
		{
			System.out.println("value=" + valueBridge.getNature(value).name().toLowerCase() + "('" + valueBridge.getString(stringFunction(value, valueBridge)) + "')");
		}

		assertEquals(12, values.size());
	}

	private Set<X> computeClosure(final Set<X> seeds, final VariantBridge<N, A, X> valueBridge)
	{
		final Set<X> values = new HashSet<X>();
		values.addAll(seeds);

		boolean done = false;

		while (!done)
		{
			done = true;

			for (final X value : values)
			{
				final X booval = booleanFunction(value, valueBridge);
				if (contains(values, booval, valueBridge))
				{
					final X strval = stringFunction(value, valueBridge);
					if (contains(values, strval, valueBridge))
					{
						final X numval = numberFunction(value, valueBridge);
						if (contains(values, numval, valueBridge))
						{
						}
						else
						{
							values.add(numval);
							done = false;
							break;
						}
					}
					else
					{
						values.add(strval);
						done = false;
						break;
					}
				}
				else
				{
					values.add(booval);
					done = false;
					break;
				}
			}
		}

		return values;
	}

	private X booleanFunction(final X value, final VariantBridge<N, A, X> valueBridge)
	{
		final VariantKind nature = valueBridge.getNature(value);
		switch (nature)
		{
			case EMPTY:
			{
				return valueBridge.booleanValue(false);
			}
			case BOOLEAN:
			{
				return value;
			}
			case DOUBLE:
			{
				final double numval = valueBridge.getDouble(value);
				if (Double.isNaN(numval))
				{
					return valueBridge.booleanValue(false);
				}
				else
				{
					return valueBridge.booleanValue(numval != 0.0);
				}
			}
			case STRING:
			{
				return valueBridge.booleanValue(valueBridge.getString(value).length() != 0);
			}
			default:
			{
				throw new AssertionError(nature);
			}
		}
	}

	private X numberFunction(final X value, final VariantBridge<N, A, X> valueBridge)
	{
		final VariantKind nature = valueBridge.getNature(value);
		switch (nature)
		{
			case EMPTY:
			{
				return valueBridge.doubleValue(Double.NaN);
			}
			case BOOLEAN:
			{
				return valueBridge.doubleValue(valueBridge.getBoolean(value) ? 1.0 : 0.0);
			}
			case DOUBLE:
			{
				return value;
			}
			case STRING:
			{
				throw new AssertionError();
			}
			default:
			{
				throw new AssertionError(nature);
			}
		}
	}

	private X stringFunction(final X value, final VariantBridge<N, A, X> valueBridge)
	{
		final VariantKind nature = valueBridge.getNature(value);
		switch (nature)
		{
			case EMPTY:
			{
				return valueBridge.stringValue("");
			}
			case BOOLEAN:
			{
				return valueBridge.getBoolean(value) ? valueBridge.stringValue("true") : valueBridge.stringValue("false");
			}
			case DOUBLE:
			{
				throw new AssertionError();
			}
			case STRING:
			{
				return value;
			}
			default:
			{
				throw new AssertionError(nature);
			}
		}

	}

	private boolean contains(final Set<X> values, final X value, final VariantBridge<N, A, X> valueBridge)
	{
		for (final X candidate : values)
		{
			if (identical(candidate, value, valueBridge))
			{
				return true;
			}
		}
		return false;
	}

	private boolean identical(final X lhs, final X rhs, final VariantBridge<N, A, X> valueBridge)
	{
		switch (valueBridge.getNature(lhs))
		{
			case EMPTY:
			{
				switch (valueBridge.getNature(rhs))
				{
					case EMPTY:
					{
						return true;
					}
					default:
					{
						return false;
					}
				}
			}
			case BOOLEAN:
			{
				switch (valueBridge.getNature(rhs))
				{
					case BOOLEAN:
					{
						return valueBridge.getBoolean(lhs).equals(valueBridge.getBoolean(rhs));
					}
					default:
					{
						return false;
					}
				}
			}
			case DOUBLE:
			{
				switch (valueBridge.getNature(rhs))
				{
					case DOUBLE:
					{
						return valueBridge.getDouble(lhs).equals(valueBridge.getDouble(rhs));
					}
					default:
					{
						return false;
					}
				}
			}
			case STRING:
			{
				switch (valueBridge.getNature(rhs))
				{
					case STRING:
					{
						return valueBridge.getString(lhs).equals(valueBridge.getString(rhs));
					}
					default:
					{
						return false;
					}
				}
			}
			default:
			{
				throw new AssertionError(valueBridge.getNature(lhs));
			}
		}
	}

	/*
	 * private boolean equals(final X lhs, final X rhs, final GxProcessingContext<N, A> pcx) { final
	 * GxValueBridge<N, A, X> valueBridge = pcx.getValueBridge(); switch (valueBridge.getNature(lhs)) { case EMPTY: {
	 * switch (valueBridge.getNature(rhs)) { case BOOLEAN: { return !valueBridge.getBoolean(rhs); } default: { throw new
	 * AssertionError(valueBridge.getNature(rhs)); } } } default: { throw new
	 * AssertionError(valueBridge.getNature(lhs)); } } }
	 */
}