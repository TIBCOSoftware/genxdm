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

import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.SmModelGroup;

final class XMLModelGroup<A> extends XMLComponent<A> implements XMLParticleTerm<A>
{
	private final QName m_name;
	private SmModelGroup.SmCompositor m_compositor;
	private final LinkedList<XMLParticle<A>> m_particles = new LinkedList<XMLParticle<A>>();

	public XMLModelGroup(final QName name, final XMLScope<A> scope, final SrcFrozenLocation location)
	{
		super(scope, location);
		if (scope.isGlobal())
		{
			this.m_name = PreCondition.assertArgumentNotNull(name, "name");
		}
		else
		{
			PreCondition.assertNull(name, "name");
			this.m_name = null;
		}
	}

	public XMLModelGroup(final QName name, final XMLScope<A> scope)
	{
		super(scope);
		if (scope.isGlobal())
		{
			this.m_name = PreCondition.assertArgumentNotNull(name, "name");
		}
		else
		{
			PreCondition.assertNull(name, "name");
			this.m_name = null;
		}
	}

	public XMLModelGroup(final SmModelGroup.SmCompositor compositor, final XMLScope<A> scope, final SrcFrozenLocation location)
	{
		super(scope, location);
		m_compositor = PreCondition.assertArgumentNotNull(compositor, "compositor");
		PreCondition.assertTrue(scope.isLocal());
		m_name = null;
	}

	public XMLModelGroup(final SmModelGroup.SmCompositor compositor, final XMLScope<A> scope)
	{
		super(scope);
		m_compositor = PreCondition.assertArgumentNotNull(compositor, "compositor");
		PreCondition.assertTrue(scope.isLocal());
		m_name = null;
	}

	public QName getName()
	{
		if (getScope().isGlobal())
		{
			return m_name;
		}
		else
		{
			throw new AssertionError();
		}
	}

	public SmModelGroup.SmCompositor getCompositor()
	{
		return m_compositor;
	}

	public void setCompositor(final SmModelGroup.SmCompositor compositor)
	{
		m_compositor = PreCondition.assertArgumentNotNull(compositor, "compositor");
	}

	public LinkedList<XMLParticle<A>> getParticles()
	{
		return m_particles;
	}
}
