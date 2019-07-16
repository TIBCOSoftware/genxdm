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

import org.genxdm.processor.w3c.xs.validation.api.VxValidator;
import org.genxdm.processor.w3c.xs.validation.api.VxValidatorFactory;
import org.genxdm.processor.w3c.xs.validation.impl.ValidationFactoryImpl;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.io.SAXValidator;
import org.genxdm.typed.types.AtomBridge;

public final class ValidatorFactory<N, A>
    implements org.genxdm.typed.ValidatorFactory
{

    public ValidatorFactory(final TypedContext<N, A> pcx)
    {
        this.factory = new ValidationFactoryImpl();
        this.atomBridge = pcx.getAtomBridge();
    }

    @Override
    public ValidationHandler<A> newXdmContentValidator()
    {
        final VxValidator<A> kernel = factory.newValidator(atomBridge);
        return new XdmContentValidatorImpl<A>(kernel, atomBridge);
    }

    @Override
    public SAXValidator<A> newSAXContentValidator()
    {
        final VxValidator<A> kernel = factory.newValidator(atomBridge);
        return new SAXContentValidatorImpl<A>(kernel);
    }

    public ValidatorFactory<N, A> schemaDocumentLocationStrategy(final SchemaDocumentLocationStrategy schemaDocumentLocationStrategy)
    {
        factory.setSchemaDocumentLocationStrategy(new SchemaDocumentLocationStrategyAdapter(schemaDocumentLocationStrategy));
        return this;
    }

    private final AtomBridge<A> atomBridge;
    private final VxValidatorFactory factory;
}
