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
package org.genxdm.processor.w3c.xs.impl;

import java.util.ArrayList;

import org.genxdm.xs.constraints.RestrictedXPath;

final class RestrictedXPathImpl implements RestrictedXPath
{
	private final ArrayList<String> m_namespaces = new ArrayList<String>();
	private final ArrayList<String> m_localNames = new ArrayList<String>();
	private final ArrayList<Boolean> m_contextNode = new ArrayList<Boolean>();

	boolean m_isAttribute;

	boolean m_relocatable;

	RestrictedXPathImpl m_alternate;

	public boolean isContextNode(final int index)
	{
		return m_contextNode.get(index);
	}

	public String getStepNamespace(final int index)
	{
		return m_namespaces.get(index);
	}

	public String getStepLocalName(final int index)
	{
		return m_localNames.get(index);
	}

	public boolean isWildcardNamespace(final int index)
	{
		return null == m_namespaces.get(index);
	}

	public boolean isWildcardLocalName(final int index)
	{
		return null == m_localNames.get(index);
	}

	public boolean isRelocatable()
	{
		return m_relocatable;
	}

	public void setRelocatableFlag(final boolean relocatableFlag)
	{
		m_relocatable = relocatableFlag;
	}

	public boolean isAttribute()
	{
		return m_isAttribute;
	}

	public void setAttributeFlag(final boolean attributeFlag)
	{
		m_isAttribute = attributeFlag;
	}

	public int getStepLength()
	{
		return m_localNames.size();
	}

	public int getLBoundStep()
	{
		return 0;
	}

	public int getUBoundStep()
	{
		return m_localNames.size() - 1;
	}

	public RestrictedXPathImpl getAlternate()
	{
		return m_alternate;
	}

	public void setAlternate(final RestrictedXPathImpl alternate)
	{
		m_alternate = alternate;
	}

	public void addContextNodeStep()
	{
		m_namespaces.add(null);
		m_localNames.add(null);
		m_contextNode.add(Boolean.TRUE);
	}

	public void addNameStep(final String namespaceURI, final String localName)
	{
		m_namespaces.add(namespaceURI);
		m_localNames.add(localName);
		m_contextNode.add(Boolean.FALSE);
	}
}
