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
package org.gxml.tests;

import junit.framework.TestCase;

import org.gxml.NodeKind;

public final class NodeKindTestCase extends TestCase
{
	public void testIsAttribute()
	{
		for (final NodeKind candidate : NodeKind.values())
		{
			switch (candidate)
			{
				case ATTRIBUTE:
				{
					assertTrue(candidate.name(), candidate.isAttribute());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isAttribute());
				}
			}
		}
	}

	public void testIsChild()
	{
		for (final NodeKind candidate : NodeKind.values())
		{
			switch (candidate)
			{
				case ELEMENT:
				case TEXT:
				case PROCESSING_INSTRUCTION:
				case COMMENT:
				{
					assertTrue(candidate.name(), candidate.isChild());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isChild());
				}
			}
		}
	}

	public void testIsNamespace()
	{
		for (final NodeKind candidate : NodeKind.values())
		{
			switch (candidate)
			{
				case NAMESPACE:
				{
					assertTrue(candidate.name(), candidate.isNamespace());
				}
				break;
				default:
				{
					assertFalse(candidate.name(), candidate.isNamespace());
				}
			}
		}
	}
}
