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

import org.genxdm.xs.types.SequenceType;

/**
 * Visitor pattern for types.
 * 
 * @param <A>
 *            The atom handle.
 */
public interface MetaVisitor<A>
{
	void atomicType(SequenceType<A> node, QName name, SequenceType<A> baseType);

	void atomicUrType(SequenceType<A> node);

	void attributeType(SequenceType<A> node, QName name, SequenceType<A> type);

	void choiceType(SequenceType<A> node, SequenceType<A> lhs, SequenceType<A> rhs);

	void commentType(SequenceType<A> node);

	void complexType(SequenceType<A> node, QName name, SequenceType<A> baseType);

	void complexUrType(SequenceType<A> node);

	void concatType(SequenceType<A> node, SequenceType<A> lhs, SequenceType<A> rhs);

	void documentType(SequenceType<A> node, SequenceType<A> contentType);

	void elementType(SequenceType<A> node, QName name, SequenceType<A> type, boolean nillable);

	void emptyType(SequenceType<A> node);

	void interleaveType(SequenceType<A> node, SequenceType<A> lhs, SequenceType<A> rhs);

	void multiplyType(SequenceType<A> node, SequenceType<A> argument, Quantifier multiplier);

	void namespaceType(SequenceType<A> node);

	void noneType(SequenceType<A> node);

	void processingInstructionType(SequenceType<A> node, String name);

	void schemaAttributeType(SequenceType<A> node, QName name);

	void schemaElementType(SequenceType<A> type, QName name);

	void simpleUrType(SequenceType<A> node);

	void textType(SequenceType<A> node);
}
