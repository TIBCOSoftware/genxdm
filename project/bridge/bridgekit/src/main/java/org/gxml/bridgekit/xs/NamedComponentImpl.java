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
package org.gxml.bridgekit.xs;

import javax.xml.namespace.QName;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.components.SmComponent;
import org.gxml.xs.enums.SmScopeExtent;

abstract class NamedComponentImpl<A> extends LockableImpl implements SmComponent<A>
{
	private final boolean isAnonymous;
	private final QName name;
	private final SmScopeExtent scope;

	public NamedComponentImpl(final QName name, final boolean isAnonymous, final SmScopeExtent scope)
	{
		if (isAnonymous)
		{
			this.name = name;
		}
		else
		{
			this.name = PreCondition.assertArgumentNotNull(name, "name");
		}
		this.isAnonymous = isAnonymous;
		this.scope = PreCondition.assertArgumentNotNull(scope, "scope");
	}

	public final String getLocalName()
	{
		return name.getLocalPart();
	}

	public final QName getName()
	{
		return name;
	}

	public final SmScopeExtent getScopeExtent()
	{
		return scope;
	}

	public final String getTargetNamespace()
	{
		return name.getNamespaceURI();
	}

	public final boolean isAnonymous()
	{
		return isAnonymous;
	}
}
