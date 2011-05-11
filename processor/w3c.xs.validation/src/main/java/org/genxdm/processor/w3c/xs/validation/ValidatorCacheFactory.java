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
package org.genxdm.processor.w3c.xs.validation;

import org.genxdm.names.NameSource;
import org.genxdm.processor.w3c.xs.validation.api.VxValidationHost;
import org.genxdm.processor.w3c.xs.validation.api.VxValidatorCache;
import org.genxdm.processor.w3c.xs.validation.api.VxValidatorCacheFactory;
import org.genxdm.processor.w3c.xs.validation.impl.ValidationFactoryImpl;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.ElementDefinition;


public final class ValidatorCacheFactory<N, A>
{
	private final AtomBridge<A> atomBridge;
	private final VxValidatorCacheFactory<A> factory;
	private final NameSource nameBridge = new NameSource();

	public ValidatorCacheFactory(final TypedContext<N, A> pcx)
	{
		final VxValidationHost<A> vxHost = new ValidationHost<N, A>(pcx);
		this.factory = new ValidationFactoryImpl<A>(vxHost);
		this.atomBridge = pcx.getAtomBridge();
	}

	public ValidatorCache<N, A> newValidatorCache()
	{
		final VxValidatorCache<A> validation = factory.newValidatorCache();

		return new ValidatorCacheImpl<N, A>(validation, atomBridge, nameBridge);
	}

	public ValidatorCache<N, A> newValidatorCache(final ElementDefinition<A> elementDeclaration)
	{
		final VxValidatorCache<A> validation = factory.newValidatorCache(elementDeclaration);

		return new ValidatorCacheImpl<N, A>(validation, atomBridge, nameBridge);
	}

	public ValidatorCacheFactory<N, A> schemaDocumentLocationStrategy(final GxSchemaDocumentLocationStrategy schemaDocumentLocationStrategy)
	{
		factory.setSchemaDocumentLocationStrategy(new GxSchemaDocumentLocationStrategyAdapter(schemaDocumentLocationStrategy));
		return this;
	}
}
