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

import javax.xml.namespace.QName;

import org.genxdm.processor.w3c.xs.validation.api.VxMetaBridge;
import org.genxdm.typed.TypedContext;
import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.types.SmType;


final class ValidationMetaBridge<N, A> implements VxMetaBridge<A>
{
	private final TypedContext<N, A> pcx;

	ValidationMetaBridge(final TypedContext<N, A> pcx)
	{
		this.pcx = PreCondition.assertArgumentNotNull(pcx, "pcx");
	}

	public SmAttribute<A> getAttributeDeclaration(final QName attributeName)
	{
		return pcx.getAttributeDeclaration(attributeName);
	}

	public SmElement<A> getElementDeclaration(final QName elementName)
	{
		return pcx.getElementDeclaration(elementName);
	}

	public SmType<A> getTypeDefinition(final QName typeName)
	{
		// TODO: Why don't we have an SmNAtiveType?
		return pcx.getTypeDefinition(typeName);
	}

}
