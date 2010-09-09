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
package org.gxml.bridgekit.axes;

import java.util.Iterator;

import org.gxml.base.Model;
import org.gxml.exceptions.IllegalNullArgumentException;

/**
 * A ready-to-go ancestor axis {@link Iterable}.
 */
public final class IterableAncestorAxis<N> implements Iterable<N>
{
	private final N m_origin;
	private final Model<N> m_model;

	public IterableAncestorAxis(final N origin, final Model<N> model)
	{
		this.m_origin = origin;
		this.m_model = IllegalNullArgumentException.check(model, "model");
	}

	public Iterator<N> iterator()
	{
		return new IteratorAncestorAxis<N>(m_origin, m_model);
	}
}
