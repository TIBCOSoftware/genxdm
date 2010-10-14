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
package org.genxdm.xs;

import java.util.HashMap;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.resolve.SmCatalog;
import org.genxdm.xs.resolve.SmResolver;

/**
 * Defines arguments that condition the loading of a schema.
 */
public final class SmMetaLoadArgs
{
	private SmCatalog m_catalog;
	private SmResolver m_resolver;
	private HashMap<QName, String> m_options = new HashMap<QName, String>();

	public SmCatalog getCatalog()
	{
		return m_catalog;
	}

	public void setCatalog(final SmCatalog catalog)
	{
		m_catalog = PreCondition.assertArgumentNotNull(catalog, "catalog");
	}

	public SmResolver getResolver()
	{
		return m_resolver;
	}

	public void setResolver(final SmResolver resolver)
	{
		m_resolver = PreCondition.assertArgumentNotNull(resolver, "resolver");
	}

	public String getOption(final QName name)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		return m_options.get(name);
	}

	public String setOption(final QName name, final String value)
	{
		PreCondition.assertArgumentNotNull(name, "name");
		PreCondition.assertArgumentNotNull(value, "value");
		return m_options.put(name, value);
	}

	public Iterable<QName> getOptionNames()
	{
		return m_options.keySet();
	}
}
