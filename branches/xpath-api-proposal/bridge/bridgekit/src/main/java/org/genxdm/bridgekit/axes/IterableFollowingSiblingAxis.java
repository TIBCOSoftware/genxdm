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
package org.genxdm.bridgekit.axes;

import java.util.Iterator;

import org.genxdm.Model;
import org.genxdm.exceptions.PreCondition;


/**
 * A ready-to-go following-sibling axis iterable.
 * 
 * <br/>
 * 
 * Provides support for implementing the following-sibling axis on a model.
 */
public final class IterableFollowingSiblingAxis<N> implements Iterable<N>
{
	private final N m_origin;
	private final Model<N> m_navigator;

	public IterableFollowingSiblingAxis(final N origin, final Model<N> navigator)
	{
		m_navigator = PreCondition.assertArgumentNotNull(navigator);
		m_origin = PreCondition.assertArgumentNotNull(origin);
	}

	public Iterator<N> iterator()
	{
		return new IteratorFollowingSiblingAxis<N>(m_origin, m_navigator);
	}
}
