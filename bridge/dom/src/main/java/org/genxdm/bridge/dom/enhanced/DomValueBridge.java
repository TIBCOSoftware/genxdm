/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain XmlAtom copy of the License at
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
import java.util.List;

import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.typed.variant.Item;
import org.genxdm.typed.variant.VariantBridge;
import org.genxdm.typed.variant.VariantKind;
import org.genxdm.typed.variant.XmlVariant;
import org.w3c.dom.Node;

final class DomValueBridge implements VariantBridge<Node, XmlAtom>
{
	public XmlVariant atom(final XmlAtom atom)
	{
		if (null != atom)
		{
			return XmlVariant.atom(atom);
		}
		else
		{
			return XmlVariant.empty();
		}
	}

	public XmlVariant atomSet(final List<? extends XmlAtom> atoms)
	{
		return XmlVariant.atomSet(atoms);
	}

	public XmlVariant booleanValue(final Boolean booval)
	{
		return XmlVariant.booleanValue(booval);
	}

	public XmlVariant decimalValue(final BigDecimal decval)
	{
		return XmlVariant.decimalValue(decval);
	}

	public XmlVariant doubleValue(final Double dblval)
	{
		return XmlVariant.doubleValue(dblval);
	}

	public XmlVariant empty()
	{
		return XmlVariant.empty();
	}

	public XmlAtom getAtom(final XmlVariant value)
	{
		return (XmlAtom)value.getObject();
	}

	@SuppressWarnings("unchecked")
	public List<XmlAtom> getAtomSet(final XmlVariant value)
	{
		return (List<XmlAtom>)value.getObject();
	}

	public Boolean getBoolean(final XmlVariant value)
	{
		if (null != value)
		{
			return (Boolean)value.getObject();
		}
		else
		{
			return null;
		}
	}

	public BigDecimal getDecimal(final XmlVariant value)
	{
		return (BigDecimal)value.getObject();
	}

	public Double getDouble(final XmlVariant value)
	{
		return (Double)value.getObject();
	}

	public BigInteger getInteger(XmlVariant value)
	{
		return (BigInteger)value.getObject();
	}

	@SuppressWarnings("unchecked")
    public Item<Node, XmlAtom> getItem(final XmlVariant value)
	{
		return (Item<Node, XmlAtom>)value.getObject();
	}

	@SuppressWarnings("unchecked")
    public Iterable<Item<Node, XmlAtom>> getItemSet(final XmlVariant value)
	{
		return (Iterable<Item<Node, XmlAtom>>)value.getObject();
	}

	public VariantKind getNature(final XmlVariant value)
	{
		return value.getNature();
	}

	public Node getNode(final XmlVariant value)
	{
		return (Node)value.getObject();
	}

	@SuppressWarnings("unchecked")
	public Iterable<Node> getNodeSet(final XmlVariant value)
	{
		return (Iterable<Node>)value.getObject();
	}

	public String getString(final XmlVariant value)
	{
		if (null != value)
		{
			return (String)value.getObject();
		}
		else
		{
			return null;
		}
	}

	public XmlVariant integerValue(final BigInteger intval)
	{
		return XmlVariant.integerValue(intval);
	}

	public XmlVariant item(final Item<Node, XmlAtom> item)
	{
		return XmlVariant.item(item);
	}

	public XmlVariant itemSet(final Iterable<Item<Node, XmlAtom>> items)
	{
		return XmlVariant.itemSet(items);
	}

	public XmlVariant node(final Node node)
	{
		return XmlVariant.node(node);
	}

	public XmlVariant nodeSet(final Iterable<? extends Node> nodes)
	{
		return XmlVariant.nodeSet(nodes);
	}

	public XmlVariant stringValue(final String strval)
	{
		return XmlVariant.stringValue(strval);
	}

	public XmlVariant[] valueArray(final int size)
	{
		return new XmlVariant[size];
	}
}
