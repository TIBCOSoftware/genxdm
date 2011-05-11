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
package org.genxdm.processor.w3c.xs.impl;

import javax.xml.namespace.QName;

import org.genxdm.xs.types.EmptyType;
import org.genxdm.xs.types.NamespaceNodeType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.Type;

/**
 * Keep private. This will go away.
 */
interface SmSequenceTypeFactory<A>
{
	PrimeType<A> comment();

	EmptyType<A> empty();

	Type<A> getTypeDefinition(QName dataType);

	Type<A> getTypeDefinition(NativeType nativeType);

	PrimeType<A> item();

	SequenceType<A> itemSet();

	NamespaceNodeType<A> namespace();

	PrimeType<A> node();

	PrimeType<A> processingInstruction(String name);

	PrimeType<A> text();
}
