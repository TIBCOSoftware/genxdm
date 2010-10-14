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
package org.genxdm.processor.w3c.xs.validation.impl;

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

final class ContentModelExpression<A> implements ValidationExpr<A, SmParticleTerm<A>>
{
	private static <A> void compile(final SmParticle<A> particle, final ArrayList<ValidationExpr<A, SmParticleTerm<A>>> subTerms)
	{
		final SmParticleTerm<A> term = particle.getTerm();

		if (term instanceof SmElement<?>)
		{
			final SmElement<A> element = (SmElement<A>)term;
			if (element.hasSubstitutionGroupMembers())
			{
				// Build a set of substitution element choices (which includes the original element).
				final Stack<SmElement<A>> stack = new Stack<SmElement<A>>();
				stack.push(element);
				final HashSet<SmElement<A>> choices = new HashSet<SmElement<A>>();
				while (!stack.isEmpty())
				{
					final SmElement<A> popped = stack.pop();

					if (!popped.isAbstract())
					{
						choices.add(popped);
					}

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
			throw new AssertionError(term);
		}
		else if (term instanceof SmModelGroup<?>)
		{
			throw new RuntimeException();
			// subTerms.add(new ModelGroupExpression<A>(particle, (SmModelGroup<A>)term));
		}
		else if (term instanceof SmWildcard<?>)
		{
			throw new RuntimeException();
			// subTerms.add(new ParticleWildcardExpression<A>(particle, (SmWildcard<A>)term));
		}
		else
		{
			throw new RuntimeException();
		}
	}

	private static <A> Iterable<ValidationExpr<A, SmParticleTerm<A>>> compileSubTerms(final SmParticle<A> particle)
	{
		final ArrayList<ValidationExpr<A, SmParticleTerm<A>>> subTerms = new ArrayList<ValidationExpr<A, SmParticleTerm<A>>>();
		compile(particle, subTerms);
		return subTerms;
	}

	private final SmParticle<A> m_particle;

	private final Iterable<ValidationExpr<A, SmParticleTerm<A>>> m_subTerms;

	public ContentModelExpression(final SmParticle<A> particle)
	{
		m_particle = PreCondition.assertArgumentNotNull(particle, "particle");
		m_subTerms = compileSubTerms(particle);
	}

	public SmParticleTerm<A> getParticleTerm()
	{
		return m_particle.getTerm();
	}

	public Iterable<ValidationExpr<A, SmParticleTerm<A>>> getSubTerms()
	{
		return m_subTerms;
	}

	public boolean intersects(final ValidationExpr<A, SmParticleTerm<A>> other)
	{
		throw new RuntimeException();
	}

	public boolean isChoice()
	{
		return false;
	}

	public boolean isGroup()
	{
		return true;
	}

	public boolean isInterleave()
	{
		return false;
	}

	public boolean isMaxOccursUnbounded()
	{
		return m_particle.isMaxOccursUnbounded();
	}

	public boolean isSequence()
	{
		return true;
	}

	public boolean matches(final QName token)
	{
		throw new RuntimeException();
	}

	public int maxOccurs()
	{
		return m_particle.getMaxOccurs();
	}

	public int minOccurs()
	{
		return m_particle.getMinOccurs();
	}
}
