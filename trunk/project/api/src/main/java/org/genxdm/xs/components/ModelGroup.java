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
package org.genxdm.xs.components;

import java.util.List;


/**
 * ModelGroup represents a group of content sequences. A group consists of a compositor and a list of particles. For
 * example, the content sequences
 * <UL>
 * <LI>a,c
 * <LI>a,b,c
 * <LI>a,b,c,c
 * </UL>
 * <p/>
 * belong to the model group written as (a,b?,c+), which has a SEQUENCE compositor and three particles. Occurrence
 * information is kept with each particle.
 * <p/>
 * Model groups can also be used for attribute groups, in which case the compositor is always ALL.
 */
public interface ModelGroup<A> extends ParticleTerm<A>, SchemaComponent<A>
{
	public enum SmCompositor
	{
		Sequence, Choice, All;

		public boolean isSequence()
		{
			return this == Sequence;
		}

		public boolean isChoice()
		{
			return this == Choice;
		}

		public boolean isAll()
		{
			return this == All;
		}
	}

	/**
	 * Returns the {compositor} property of this model group.
	 */
	SmCompositor getCompositor();

	/**
	 * Returns the {particles} property of this model group.
	 */
	List<SchemaParticle<A>> getParticles();
}
