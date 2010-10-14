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
package org.genxdm.typed.types;

import javax.xml.namespace.QName;

import org.genxdm.xs.types.SmSequenceType;

/**
 * Visitor pattern for types.
 * 
 * @param <A>
 *            The atom handle.
 */
public interface MetaVisitor<A>
{
	void atomicType(SmSequenceType<A> node, QName name, SmSequenceType<A> baseType);

	void atomicUrType(SmSequenceType<A> node);

	void attributeType(SmSequenceType<A> node, QName name, SmSequenceType<A> type);

	void choiceType(SmSequenceType<A> node, SmSequenceType<A> lhs, SmSequenceType<A> rhs);

	void commentType(SmSequenceType<A> node);

	void complexType(SmSequenceType<A> node, QName name, SmSequenceType<A> baseType);

	void complexUrType(SmSequenceType<A> node);

	void concatType(SmSequenceType<A> node, SmSequenceType<A> lhs, SmSequenceType<A> rhs);

	void documentType(SmSequenceType<A> node, SmSequenceType<A> contentType);

	void elementType(SmSequenceType<A> node, QName name, SmSequenceType<A> type, boolean nillable);

	void emptyType(SmSequenceType<A> node);

	void interleaveType(SmSequenceType<A> node, SmSequenceType<A> lhs, SmSequenceType<A> rhs);

	void multiplyType(SmSequenceType<A> node, SmSequenceType<A> argument, Quantifier multiplier);

	void namespaceType(SmSequenceType<A> node);

	void noneType(SmSequenceType<A> node);

	void processingInstructionType(SmSequenceType<A> node, String name);

	void schemaAttributeType(SmSequenceType<A> node, QName name);

	void schemaElementType(SmSequenceType<A> type, QName name);

	void simpleUrType(SmSequenceType<A> node);

	void textType(SmSequenceType<A> node);
}
