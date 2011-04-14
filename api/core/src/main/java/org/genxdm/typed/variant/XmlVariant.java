/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm.typed.variant;

import java.math.BigDecimal;
import java.math.BigInteger;


public final class XmlVariant
{
	private final Object obj;
	private final VariantKind nature;

	private XmlVariant(final Object obj, final VariantKind nature)
	{
		this.obj = obj;
		this.nature = nature;
	}

	public static XmlVariant booleanValue(final Boolean value)
	{
		return new XmlVariant(value, VariantKind.BOOLEAN);
	}

	public static XmlVariant doubleValue(final double value)
	{
		return new XmlVariant(value, VariantKind.DOUBLE);
	}

	public static XmlVariant decimalValue(final BigDecimal value)
	{
		return new XmlVariant(value, VariantKind.DECIMAL);
	}

	public static XmlVariant integerValue(final BigInteger value)
	{
		return new XmlVariant(value, VariantKind.INTEGER);
	}

	public static XmlVariant stringValue(final String value)
	{
		return new XmlVariant(value, VariantKind.STRING);
	}

	public static <N, A> XmlVariant itemSet(final Iterable<Item<N, A>> items)
	{
		return new XmlVariant(items, VariantKind.ITEMS);
	}

	public static <N, A> XmlVariant item(final Item<N, A> item)
	{
		return new XmlVariant(item, VariantKind.ITEM);
	}

	public static <N> XmlVariant nodeSet(final Iterable<? extends N> nodes)
	{
		return new XmlVariant(nodes, VariantKind.NODES);
	}

	public static <N> XmlVariant node(final N node)
	{
		return new XmlVariant(node, VariantKind.NODE);
	}

	public static <A> XmlVariant atomSet(final Iterable<? extends A> atoms)
	{
		return new XmlVariant(atoms, VariantKind.ATOMS);
	}

	public static <A> XmlVariant atom(final A atom)
	{
		return new XmlVariant(atom, VariantKind.ATOM);
	}

	public static XmlVariant empty()
	{
		return new XmlVariant(null, VariantKind.EMPTY);
	}

	public Object getObject()
	{
		return obj;
	}

	public VariantKind getNature()
	{
		return nature;
	}

	@Override
	public String toString()
	{
		return obj + " : " + nature;
	}
}
