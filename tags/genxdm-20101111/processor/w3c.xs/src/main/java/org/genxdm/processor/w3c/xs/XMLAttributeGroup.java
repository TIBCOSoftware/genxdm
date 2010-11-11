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
package org.genxdm.processor.w3c.xs;

import java.util.HashSet;
import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;

final class XMLAttributeGroup<A> extends XMLComponent<A>
{
	private final QName name;
	private final LinkedList<XMLAttributeUse<A>> attributeUses = new LinkedList<XMLAttributeUse<A>>();
	private final LinkedList<XMLAttributeGroup<A>> groups = new LinkedList<XMLAttributeGroup<A>>();
	public final HashSet<QName> prohibited = new HashSet<QName>();
	public XMLWildcard<A> wildcard;

	public XMLAttributeGroup(final QName name, final XMLScope<A> scope, final SrcFrozenLocation location)
	{
		super(scope, location);
		if (scope.isGlobal())
		{
			this.name = PreCondition.assertArgumentNotNull(name, "name");
		}
		else
		{
			PreCondition.assertNull(name, "name");
			this.name = null;
		}
	}

	public XMLAttributeGroup(final QName name, final XMLScope<A> scope)
	{
		super(scope);
		if (scope.isGlobal())
		{
			this.name = PreCondition.assertArgumentNotNull(name, "name");
		}
		else
		{
			PreCondition.assertNull(name, "name");
			this.name = null;
		}
	}

	public QName getName()
	{
		if (getScope().isGlobal())
		{
			return name;
		}
		else
		{
			throw new AssertionError();
		}
	}

	public LinkedList<XMLAttributeUse<A>> getAttributeUses()
	{
		return attributeUses;
	}

	public LinkedList<XMLAttributeGroup<A>> getGroups()
	{
		return groups;
	}
}
