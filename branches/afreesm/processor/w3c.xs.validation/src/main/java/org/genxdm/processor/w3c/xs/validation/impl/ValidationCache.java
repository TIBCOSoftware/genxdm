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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.genxdm.names.NameSource;
import org.genxdm.processor.w3c.xs.validation.api.VxSchemaDocumentLocationStrategy;
import org.genxdm.processor.w3c.xs.validation.api.VxValidator;
import org.genxdm.processor.w3c.xs.validation.api.VxValidatorCache;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExBridge;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExFactory;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExMachine;
import org.genxdm.processor.w3c.xs.validation.regex.api.RegExPattern;
import org.genxdm.processor.w3c.xs.validation.regex.impl.nfa.NfaFactory;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.ParticleTerm;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentType;


final class ValidationCache<A> implements VxValidatorCache<A>
{
	@SuppressWarnings("unused")
	private final ElementDefinition<A> elementDeclaration;
	private final ComponentProvider<A> provider;
	private final AtomBridge<A> bridge;
	private final VxSchemaDocumentLocationStrategy sdl;

	private final ConcurrentHashMap<ComplexType<A>, RegExPattern<ValidationExpr<A, ParticleTerm<A>>, QName>> m_patterns = new ConcurrentHashMap<ComplexType<A>, RegExPattern<ValidationExpr<A, ParticleTerm<A>>, QName>>();

	private final RegExBridge<ValidationExpr<A, ParticleTerm<A>>, QName> m_regexb;

	ValidationCache(final ElementDefinition<A> elementDeclaration, final ComponentProvider<A> provider, final AtomBridge<A> atomBridge, final VxSchemaDocumentLocationStrategy sdl)
	{
		this.elementDeclaration = elementDeclaration;
		this.provider = provider;
		this.bridge = atomBridge;
		m_regexb = new ValidationRegExBridge<A>(new NameSource());
		this.sdl = sdl;
	}

	public VxValidator<A> newValidator()
	{
		return new ValidationKernel<A>(provider, bridge, this, sdl);
	}

	SmContentFiniteStateMachine<A> getMachine(final ComplexType<A> complexType)
	{
		final ComplexType<A> itemType = (ComplexType<A>)complexType;
		final RegExPattern<ValidationExpr<A, ParticleTerm<A>>, QName> pattern = ensurePattern(itemType);

		final List<ValidationExpr<A, ParticleTerm<A>>> expectedFollowers = new LinkedList<ValidationExpr<A, ParticleTerm<A>>>();
		final RegExMachine<ValidationExpr<A, ParticleTerm<A>>, QName> regexm = PreCondition.assertArgumentNotNull(pattern.createRegExMachine(expectedFollowers), "createRegExMachine");

		return new SmMachineImpl<A>(regexm);
	}

	private RegExPattern<ValidationExpr<A, ParticleTerm<A>>, QName> ensurePattern(final ComplexType<A> complexType)
	{
		final RegExPattern<ValidationExpr<A, ParticleTerm<A>>, QName> cachedPattern = m_patterns.get(complexType);
		if (null != cachedPattern)
		{
			return cachedPattern;
		}
		else
		{
			final ContentType<A> contentType = complexType.getContentType();

			final SchemaParticle<A> contentModel = contentType.getContentModel();
			final ValidationExpr<A, ParticleTerm<A>> expression = expression(contentModel);
			final RegExFactory<ValidationExpr<A, ParticleTerm<A>>, QName> factory = new NfaFactory<ValidationExpr<A, ParticleTerm<A>>, QName>();
			final RegExPattern<ValidationExpr<A, ParticleTerm<A>>, QName> pattern = factory.newPattern(expression, m_regexb);
			m_patterns.put(complexType, pattern);
			return pattern;
		}
	}

	private ValidationExpr<A, ParticleTerm<A>> expression(final SchemaParticle<A> particle)
	{
		final ParticleTerm<A> term = particle.getTerm();
		if (term instanceof ModelGroup<?>)
		{
			final ModelGroup<A> group = (ModelGroup<A>)term;
			return new ModelGroupExpression<A>(particle, group);
		}
		else
		{
			return new ContentModelExpression<A>(particle);
		}
	}
}