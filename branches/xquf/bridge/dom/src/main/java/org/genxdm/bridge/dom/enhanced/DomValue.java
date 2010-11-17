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
package org.genxdm.bridge.dom.enhanced;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.genxdm.typed.types.VariantKind;

final class DomValue
{
	private final Object m_obj;
	private final VariantKind m_nature;

	private DomValue(final Object obj, final VariantKind nature)
	{
		m_obj = obj;
		m_nature = nature;
	}

	public static DomValue booleanValue(final Boolean value)
	{
		return new DomValue(value, VariantKind.BOOLEAN);
	}

	public static DomValue doubleValue(final double value)
	{
		return new DomValue(value, VariantKind.DOUBLE);
	}

	public static DomValue decimalValue(final BigDecimal value)
	{
		return new DomValue(value, VariantKind.DECIMAL);
	}

	public static DomValue integerValue(final BigInteger value)
	{
		return new DomValue(value, VariantKind.INTEGER);
	}

	public static DomValue stringValue(final String value)
	{
		return new DomValue(value, VariantKind.STRING);
	}

	public static DomValue itemSet(final Iterable<Object> items)
	{
		return new DomValue(items, VariantKind.ITEMS);
	}

	public static DomValue item(final Object item)
	{
		return new DomValue(item, VariantKind.ITEM);
	}

	public static <N> DomValue nodeSet(final Iterable<? extends N> nodes)
	{
		return new DomValue(nodes, VariantKind.NODES);
	}

	public static <N> DomValue node(final N node)
	{
		return new DomValue(node, VariantKind.NODE);
	}

	public static <A> DomValue atomSet(final Iterable<? extends A> atoms)
	{
		return new DomValue(atoms, VariantKind.ATOMS);
	}

	public static <A> DomValue atom(final A atom)
	{
		return new DomValue(atom, VariantKind.ATOM);
	}

	public static DomValue empty()
	{
		return new DomValue(null, VariantKind.EMPTY);
	}

	public Object getObject()
	{
		return m_obj;
	}

	public VariantKind getNature()
	{
		return m_nature;
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append(m_obj).append(" : ").append(m_nature).toString();
	}
}
