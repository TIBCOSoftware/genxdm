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
import org.genxdm.processor.w3c.xs.validation.api.VxValidatorCache;
import org.genxdm.processor.w3c.xs.validation.api.VxValidatorCacheFactory;
import org.genxdm.xs.components.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;


public final class ValidationFactoryImpl implements VxValidatorCacheFactory
{
    private final ComponentProvider components;
    private VxSchemaDocumentLocationStrategy sdl;

	public ValidationFactoryImpl(final ComponentProvider cp)
	{
		this.components = PreCondition.assertArgumentNotNull(cp, "ComponentProvider");
	}

	public VxValidatorCache newValidatorCache()
	{
		return new ValidationCache(null, components, sdl);
	}

	public VxValidatorCache newValidatorCache(final ElementDefinition elementDeclaration)
	{
		PreCondition.assertArgumentNotNull(elementDeclaration, "elementDeclaration");
		return new ValidationCache(elementDeclaration, components, sdl);
	}

	public VxValidatorCacheFactory setSchemaDocumentLocationStrategy(final VxSchemaDocumentLocationStrategy schemaDocumentLocationStrategy)
	{
		this.sdl = schemaDocumentLocationStrategy;
		return this;
	}
}
