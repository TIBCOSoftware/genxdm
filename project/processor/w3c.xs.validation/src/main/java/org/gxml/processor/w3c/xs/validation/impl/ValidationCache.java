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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.gxml.processor.w3c.xs.validation.api.VxSchemaDocumentLocationStrategy;
import org.gxml.processor.w3c.xs.validation.api.VxValidationHost;
import org.gxml.processor.w3c.xs.validation.api.VxValidator;
import org.gxml.processor.w3c.xs.validation.api.VxValidatorCache;
import org.gxml.processor.w3c.xs.validation.regex.api.RegExBridge;
import org.gxml.processor.w3c.xs.validation.regex.api.RegExFactory;
import org.gxml.processor.w3c.xs.validation.regex.api.RegExMachine;
import org.gxml.processor.w3c.xs.validation.regex.api.RegExPattern;
import org.gxml.processor.w3c.xs.validation.regex.impl.nfa.NfaFactory;
import org.gxml.xs.components.SmElement;
import org.gxml.xs.components.SmModelGroup;
import org.gxml.xs.components.SmParticle;
import org.gxml.xs.components.SmParticleTerm;
import org.gxml.xs.types.SmComplexType;
import org.gxml.xs.types.SmContentType;


final class ValidationCache<A> implements VxValidatorCache<A>
{
	@SuppressWarnings("unused")
	private final SmElement<A> elementDeclaration;
	private final VxValidationHost<A> host;
	private final VxSchemaDocumentLocationStrategy sdl;

	private final ConcurrentHashMap<SmComplexType<A>, RegExPattern<ValidationExpr<A, SmParticleTerm<A>>, QName>> m_patterns = new ConcurrentHashMap<SmComplexType<A>, RegExPattern<ValidationExpr<A, SmParticleTerm<A>>, QName>>();

	private final RegExBridge<ValidationExpr<A, SmParticleTerm<A>>, QName> m_regexb;

	ValidationCache(final SmElement<A> elementDeclaration, final VxValidationHost<A> host, final VxSchemaDocumentLocationStrategy sdl)
	{
		this.elementDeclaration = elementDeclaration;
		this.host = host;
		m_regexb = new ValidationRegExBridge<A>(host.getNameBridge());
		this.sdl = sdl;
	}

	public VxValidator<A> newValidator()
	{
		return new ValidationKernel<A>(host, this, sdl);
	}

	SmContentFiniteStateMachine<A> getMachine(final SmComplexType<A> complexType)
	{
		final SmComplexType<A> itemType = (SmComplexType<A>)complexType;
		final RegExPattern<ValidationExpr<A, SmParticleTerm<A>>, QName> pattern = ensurePattern(itemType);

		final List<ValidationExpr<A, SmParticleTerm<A>>> expectedFollowers = new LinkedList<ValidationExpr<A, SmParticleTerm<A>>>();
		final RegExMachine<ValidationExpr<A, SmParticleTerm<A>>, QName> regexm = PreCondition.assertArgumentNotNull(pattern.createRegExMachine(expectedFollowers), "createRegExMachine");

		return new SmMachineImpl<A>(regexm);
	}

	private RegExPattern<ValidationExpr<A, SmParticleTerm<A>>, QName> ensurePattern(final SmComplexType<A> complexType)
	{
		final RegExPattern<ValidationExpr<A, SmParticleTerm<A>>, QName> cachedPattern = m_patterns.get(complexType);
		if (null != cachedPattern)
		{
			return cachedPattern;
		}
		else
		{
			final SmContentType<A> contentType = complexType.getContentType();

			final SmParticle<A> contentModel = contentType.getContentModel();
			final ValidationExpr<A, SmParticleTerm<A>> expression = expression(contentModel);
			final RegExFactory<ValidationExpr<A, SmParticleTerm<A>>, QName> factory = new NfaFactory<ValidationExpr<A, SmParticleTerm<A>>, QName>();
			final RegExPattern<ValidationExpr<A, SmParticleTerm<A>>, QName> pattern = factory.newPattern(expression, m_regexb);
			m_patterns.put(complexType, pattern);
			return pattern;
		}
	}

	private ValidationExpr<A, SmParticleTerm<A>> expression(final SmParticle<A> particle)
	{
		final SmParticleTerm<A> term = particle.getTerm();
		if (term instanceof SmModelGroup<?>)
		{
			final SmModelGroup<A> group = (SmModelGroup<A>)term;
			return new ModelGroupExpression<A>(particle, group);
		}
		else
		{
			return new ContentModelExpression<A>(particle);
		}
	}
}