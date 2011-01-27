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
package org.genxdm.bridgetest.typed;

import java.util.HashSet;
import java.util.Set;

import org.genxdm.bridgetest.GxTestBase;
import org.genxdm.typed.variant.VariantBridge;
import org.genxdm.typed.variant.VariantKind;
import org.genxdm.typed.variant.XmlVariant;

public abstract class ValueBridgeTestBase<N, A> 
    extends GxTestBase<N>
{
    abstract public VariantBridge<N, A> getVariantBridge();
    
	public void testProlog()
	{
		final Set<XmlVariant> seeds = new HashSet<XmlVariant>();

		final VariantBridge<N, A> valueBridge = getVariantBridge();

		seeds.add(valueBridge.empty());

		final Set<XmlVariant> values = computeClosure(seeds, valueBridge);

		for (final XmlVariant value : values)
		{
			System.out.println("value=" + valueBridge.getNature(value).name().toLowerCase() + "('" + valueBridge.getString(stringFunction(value, valueBridge)) + "')");
		}

		assertEquals(12, values.size());
	}

	private Set<XmlVariant> computeClosure(final Set<XmlVariant> seeds, final VariantBridge<N, A> valueBridge)
	{
		final Set<XmlVariant> values = new HashSet<XmlVariant>();
		values.addAll(seeds);

		boolean done = false;

		while (!done)
		{
			done = true;

			for (final XmlVariant value : values)
			{
				final XmlVariant booval = booleanFunction(value, valueBridge);
				if (contains(values, booval, valueBridge))
				{
					final XmlVariant strval = stringFunction(value, valueBridge);
					if (contains(values, strval, valueBridge))
					{
						final XmlVariant numval = numberFunction(value, valueBridge);
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

	private XmlVariant booleanFunction(final XmlVariant value, final VariantBridge<N, A> valueBridge)
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

	private XmlVariant numberFunction(final XmlVariant value, final VariantBridge<N, A> valueBridge)
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

	private XmlVariant stringFunction(final XmlVariant value, final VariantBridge<N, A> valueBridge)
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

	private boolean contains(final Set<XmlVariant> values, final XmlVariant value, final VariantBridge<N, A> valueBridge)
	{
		for (final XmlVariant candidate : values)
		{
			if (identical(candidate, value, valueBridge))
			{
				return true;
			}
		}
		return false;
	}

	private boolean identical(final XmlVariant lhs, final XmlVariant rhs, final VariantBridge<N, A> valueBridge)
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
	 * private boolean equals(final XmlVariant lhs, final XmlVariant rhs, final GxProcessingContext<N, A> pcx) { final
	 * GxValueBridge<N, A> valueBridge = pcx.getValueBridge(); switch (valueBridge.getNature(lhs)) { case EMPTY: {
	 * switch (valueBridge.getNature(rhs)) { case BOOLEAN: { return !valueBridge.getBoolean(rhs); } default: { throw new
	 * AssertionError(valueBridge.getNature(rhs)); } } } default: { throw new
	 * AssertionError(valueBridge.getNature(lhs)); } } }
	 */
}