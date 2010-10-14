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
package org.gxml.processor.w3c.xs.validation.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.components.SmParticle;
import org.genxdm.xs.components.SmParticleTerm;
import org.genxdm.xs.components.SmWildcard;

final class ModelGroupExpression<A> implements ValidationExpr<A, SmParticleTerm<A>>
{
	private static <A> void compile(final SmParticle<A> particle, final ArrayList<ValidationExpr<A, SmParticleTerm<A>>> subTerms)
	{
		final SmParticleTerm<A> term = particle.getTerm();

		if (term instanceof SmElement<?>)
		{
			final SmElement<A> element = (SmElement<A>)term;
			if (element.hasSubstitutionGroupMembers())
			{
				// Build a set of substitution element choices.
				final Stack<SmElement<A>> stack = new Stack<SmElement<A>>();
				stack.push(element);
				final HashSet<SmElement<A>> choices = new HashSet<SmElement<A>>();
				while (!stack.isEmpty())
				{
					final SmElement<A> popped = stack.pop();
					choices.add(popped);
					for (final SmElement<A> substitution : popped.getSubstitutionGroupMembers())
					{
						stack.push(substitution);
					}
				}
				if (particle.isMaxOccursUnbounded())
				{
					subTerms.add(new SubstitutionGroupExpression<A>(particle.getMinOccurs(), choices));
				}
				else
				{
					subTerms.add(new SubstitutionGroupExpression<A>(particle.getMinOccurs(), particle.getMaxOccurs(), choices));
				}
			}
			else
			{
				if (particle.isMaxOccursUnbounded())
				{
					subTerms.add(new ParticleElementExpression<A>(particle.getMinOccurs(), element));
				}
				else
				{
					subTerms.add(new ParticleElementExpression<A>(particle.getMinOccurs(), particle.getMaxOccurs(), element));
				}
			}
		}
		else if (term instanceof SmAttribute<?>)
		{
			throw new RuntimeException();
		}
		else if (term instanceof SmModelGroup<?>)
		{
			subTerms.add(new ModelGroupExpression<A>(particle, (SmModelGroup<A>)term));
		}
		else if (term instanceof SmWildcard<?>)
		{
			subTerms.add(new ParticleWildcardExpression<A>(particle, (SmWildcard<A>)term));
		}
		else
		{
			throw new RuntimeException();
		}
	}

	private static <A> Iterable<ValidationExpr<A, SmParticleTerm<A>>> compileSubTerms(final SmModelGroup<A> group)
	{
		final ArrayList<ValidationExpr<A, SmParticleTerm<A>>> subTerms = new ArrayList<ValidationExpr<A, SmParticleTerm<A>>>();

		for (final SmParticle<A> particle : group.getParticles())
		{
			compile(particle, subTerms);
		}
		return subTerms;
	}

	private final SmModelGroup<A> m_group;

	private final SmParticle<A> m_particle;

	private final Iterable<ValidationExpr<A, SmParticleTerm<A>>> m_subTerms;

	public ModelGroupExpression(final SmParticle<A> particle, final SmModelGroup<A> group)
	{
		m_particle = particle;
		m_group = group;
		m_subTerms = compileSubTerms(group);
	}

	public SmParticleTerm<A> getParticleTerm()
	{
		return m_group;
	}

	public Iterable<ValidationExpr<A, SmParticleTerm<A>>> getSubTerms()
	{
		return m_subTerms;
	}

	public boolean intersects(final ValidationExpr<A, SmParticleTerm<A>> other)
	{
		return false;
	}

	public boolean isChoice()
	{
		return m_group.getCompositor() == SmModelGroup.SmCompositor.Choice;
	}

	public boolean isGroup()
	{
		return true;
	}

	public boolean isInterleave()
	{
		return m_group.getCompositor() == SmModelGroup.SmCompositor.All;
	}

	public boolean isMaxOccursUnbounded()
	{
		return (null != m_particle) ? m_particle.isMaxOccursUnbounded() : false;
	}

	public boolean isSequence()
	{
		return m_group.getCompositor() == SmModelGroup.SmCompositor.Sequence;
	}

	public boolean matches(final QName token)
	{
		return false;
	}

	public int maxOccurs()
	{
		return (null != m_particle) ? m_particle.getMaxOccurs() : 1;
	}

	public int minOccurs()
	{
		return (null != m_particle) ? m_particle.getMinOccurs() : 1;
	}
}
