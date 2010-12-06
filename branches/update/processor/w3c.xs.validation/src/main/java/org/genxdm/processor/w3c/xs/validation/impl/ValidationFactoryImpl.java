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

import org.genxdm.processor.w3c.xs.validation.api.VxSchemaDocumentLocationStrategy;
import org.genxdm.processor.w3c.xs.validation.api.VxValidationHost;
import org.genxdm.processor.w3c.xs.validation.api.VxValidatorCache;
import org.genxdm.processor.w3c.xs.validation.api.VxValidatorCacheFactory;
import org.genxdm.xs.components.ElementDefinition;


public final class ValidationFactoryImpl<A> implements VxValidatorCacheFactory<A>
{
	private final VxValidationHost<A> host;
	private VxSchemaDocumentLocationStrategy sdl;

	public ValidationFactoryImpl(final VxValidationHost<A> host)
	{
		this.host = PreCondition.assertArgumentNotNull(host, "host");
	}

	public VxValidatorCache<A> newValidatorCache()
	{
		return new ValidationCache<A>(null, host, sdl);
	}

	public VxValidatorCache<A> newValidatorCache(final ElementDefinition<A> elementDeclaration)
	{
		PreCondition.assertArgumentNotNull(elementDeclaration, "elementDeclaration");
		return new ValidationCache<A>(elementDeclaration, host, sdl);
	}

	public VxValidatorCacheFactory<A> setSchemaDocumentLocationStrategy(final VxSchemaDocumentLocationStrategy schemaDocumentLocationStrategy)
	{
		this.sdl = schemaDocumentLocationStrategy;
		return this;
	}
}
