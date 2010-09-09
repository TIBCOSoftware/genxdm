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
package org.gxml.processor.xpath.v10.expressions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.gxml.xpath.v10.expressions.ExprContextStatic;

final class ExprContextStaticImpl 
    implements ExprContextStatic
{
	private final Set<QName> variables;
	private final Map<String, String> namespaces;

	public ExprContextStaticImpl(final Set<? extends QName> variables, final Map<String, String> namespaces)
	{
		this.variables = new HashSet<QName>(variables);
		this.namespaces = new HashMap<String, String>(namespaces);
	}

	public boolean containsVariable(final QName name)
	{
		return variables.contains(name);
	}

	public String getNamespaceForPrefix(final String prefix)
	{
		return namespaces.get(prefix);
	}
}
