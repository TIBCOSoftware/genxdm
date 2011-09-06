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
package org.genxdm.bridge.cx.typed;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.typed.variant.Item;
import org.genxdm.typed.variant.ItemIterable;
import org.genxdm.typed.variant.VariantBridge;
import org.genxdm.typed.variant.VariantKind;
import org.genxdm.typed.variant.XmlVariant;

public final class XmlVariantBridge implements VariantBridge<XmlNode, XmlAtom>
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
			switch (value.getNature())
			{
				case BOOLEAN:
				{
					return (Boolean)value.getObject();
				}
				default:
				{
					throw new AssertionError(value.getNature());
				}
			}
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
    public Item<XmlNode, XmlAtom> getItem(final XmlVariant value)
	{
		return (Item<XmlNode, XmlAtom>)value.getObject();
	}

	@SuppressWarnings("unchecked")
	public ItemIterable<XmlNode, XmlAtom> getItemSet(final XmlVariant value)
	{
		return (ItemIterable<XmlNode, XmlAtom>)value.getObject();
	}

	public VariantKind getNature(final XmlVariant value)
	{
		return value.getNature();
	}

	public XmlNode getNode(final XmlVariant value)
	{
		return (XmlNode)value.getObject();
	}

	@SuppressWarnings("unchecked")
    public Iterable<XmlNode> getNodeSet(final XmlVariant value)
	{
		return (Iterable<XmlNode>)value.getObject();
	}

	public String getString(final XmlVariant value)
	{
		if (null != value)
		{
			switch (value.getNature())
			{
				case STRING:
				{
					return (String)value.getObject();
				}
				case EMPTY:
				{
					return null;
				}
				default:
				{
					throw new AssertionError(value.getNature());
				}
			}
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

	public XmlVariant item(final Item<XmlNode, XmlAtom> item)
	{
		return XmlVariant.item(item);
	}

	public XmlVariant itemSet(final ItemIterable<XmlNode, XmlAtom> items)
	{
		return XmlVariant.itemSet(items);
	}

	public XmlVariant node(final XmlNode node)
	{
		return XmlVariant.node(node);
	}

	public XmlVariant nodeSet(final Iterable<? extends XmlNode> nodes)
	{
		return XmlVariant.nodeSet(nodes);
	}

	public XmlVariant stringValue(final String strval)
	{
		if (null != strval)
		{
			return XmlVariant.stringValue(strval);
		}
		else
		{
			return XmlVariant.empty();
		}
	}

	public XmlVariant[] valueArray(final int size)
	{
		return new XmlVariant[size];
	}
}
