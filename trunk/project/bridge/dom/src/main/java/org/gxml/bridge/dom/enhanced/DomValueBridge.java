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
package org.gxml.bridge.dom.enhanced;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.gxml.typed.types.VariantBridge;
import org.gxml.typed.types.VariantKind;
import org.w3c.dom.Node;

final class DomValueBridge<A> implements VariantBridge<Node, A, Object>
{
	public Object atom(final A atom)
	{
		if (null != atom)
		{
			return DomValue.atom(atom);
		}
		else
		{
			return DomValue.empty();
		}
	}

	public Object atomSet(final List<? extends A> atoms)
	{
		return DomValue.atomSet(atoms);
	}

	public Object booleanValue(final Boolean booval)
	{
		return DomValue.booleanValue(booval);
	}

	public Object decimalValue(final BigDecimal decval)
	{
		return DomValue.decimalValue(decval);
	}

	public Object doubleValue(final Double dblval)
	{
		return DomValue.doubleValue(dblval);
	}

	private DomValue downcast(final Object value)
	{
		return (DomValue)value;
	}

	public Object empty()
	{
		return DomValue.empty();
	}

	@SuppressWarnings("unchecked")
	public A getAtom(final Object value)
	{
		return (A)downcast(value).getObject();
	}

	@SuppressWarnings("unchecked")
	public List<A> getAtomSet(final Object value)
	{
		return (List<A>)downcast(value).getObject();
	}

	public Boolean getBoolean(final Object value)
	{
		if (null != value)
		{
			return (Boolean)downcast(value).getObject();
		}
		else
		{
			return null;
		}
	}

	public BigDecimal getDecimal(final Object value)
	{
		return (BigDecimal)downcast(value).getObject();
	}

	public Double getDouble(final Object value)
	{
		return (Double)downcast(value).getObject();
	}

	public BigInteger getInteger(Object value)
	{
		return (BigInteger)downcast(value).getObject();
	}

	public Object getItem(final Object value)
	{
		return downcast(value).getObject();
	}

	@SuppressWarnings("unchecked")
	public Iterable<Object> getItemSet(final Object value)
	{
		return (Iterable<Object>)downcast(value).getObject();
	}

	public VariantKind getNature(final Object value)
	{
		return downcast(value).getNature();
	}

	public Node getNode(final Object value)
	{
		return (Node)downcast(value).getObject();
	}

	@SuppressWarnings("unchecked")
	public Iterable<Node> getNodeSet(final Object value)
	{
		return (Iterable<Node>)downcast(value).getObject();
	}

	public String getString(final Object value)
	{
		if (null != value)
		{
			return (String)downcast(value).getObject();
		}
		else
		{
			return null;
		}
	}

	public Object integerValue(final BigInteger intval)
	{
		return DomValue.integerValue(intval);
	}

	public Object item(final Object item)
	{
		return DomValue.item(item);
	}

	public Object itemSet(final Iterable<Object> items)
	{
		return DomValue.itemSet(items);
	}

	public Object node(final Node node)
	{
		return DomValue.node(node);
	}

	public Object nodeSet(final Iterable<? extends Node> nodes)
	{
		return DomValue.nodeSet(nodes);
	}

	public Object stringValue(final String strval)
	{
		return DomValue.stringValue(strval);
	}

	public Object[] valueArray(final int size)
	{
		return new DomValue[size];
	}
}
