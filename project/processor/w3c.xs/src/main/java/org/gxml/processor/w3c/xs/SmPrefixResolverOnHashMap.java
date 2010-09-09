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
package org.gxml.processor.w3c.xs;

import java.util.HashMap;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.resolve.SmPrefixResolver;

final class SmPrefixResolverOnHashMap implements SmPrefixResolver
{
	private final HashMap<String, String> m_map;

	public SmPrefixResolverOnHashMap(final HashMap<String, String> map)
	{
		this.m_map = PreCondition.assertArgumentNotNull(map, "map");
	}

	public String getNamespaceURI(final String prefix)
	{
		return m_map.get(PreCondition.assertArgumentNotNull(prefix, "prefix"));
	}
}
