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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Stack;

final class XMLAttributeGroupHierarchyComparator<A> implements Comparator<XMLAttributeGroup<A>>
{
	public XMLAttributeGroupHierarchyComparator()
	{
	}

	public int compare(final XMLAttributeGroup<A> g1, final XMLAttributeGroup<A> g2)
	{
		final HashSet<XMLAttributeGroup<A>> targets = new HashSet<XMLAttributeGroup<A>>();
		final Stack<XMLAttributeGroup<A>> stack = new Stack<XMLAttributeGroup<A>>();
		if (g1.getGroups().size() > 0)
		{
			for (final XMLAttributeGroup<A> attributeGroup : g1.getGroups())
			{
				stack.push(attributeGroup);
			}

			while (!stack.isEmpty())
			{
				final XMLAttributeGroup<A> popped = stack.pop();

				targets.add(popped);

				if (popped.getGroups().size() > 0)
				{
					for (final XMLAttributeGroup<A> attributeGroup : popped.getGroups())
					{
						stack.push(attributeGroup);
					}
				}
			}
		}

		if (targets.contains(g2))
		{
			return +1;
		}
		else
		{
			return -1;
		}
	}
}
