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
package org.genxdm.xs.types;

import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmElement;

/**
 * Visitor pattern for {@link SmSequenceType}.
 * 
 * @param <A>
 *            The atom handle.
 */
public interface SmSequenceTypeVisitor<A>
{
	void visit(SmAttribute<A> schemaAttribute);

	void visit(SmAttributeNodeType<A> attributeType);

	void visit(SmChoiceType<A> choiceType);

	void visit(SmCommentNodeType<A> commentNodeType);

	void visit(SmComplexType<A> atomicType);

	void visit(SmComplexUrType<A> complexUrType);

	void visit(SmConcatType<A> concatType);

	void visit(SmDocumentNodeType<A> documentNodeType);

	void visit(SmElement<A> schemaElement);

	void visit(SmElementNodeType<A> elementNodeType);

	void visit(SmEmptyType<A> emptyType);

	void visit(SmInterleaveType<A> interleaveType);

	void visit(SmListSimpleType<A> atomicType);

	void visit(SmMultiplyType<A> multiplyType);

	void visit(SmNamespaceNodeType<A> namespaceNodeType);

	void visit(SmNodeUrType<A> nodeType);

	void visit(SmNoneType<A> noneType);

	void visit(SmProcessingInstructionNodeType<A> processingInstructionNodeType);

	void visit(SmSimpleType<A> simpleType);

	void visit(SmSimpleUrType<A> simpleUrType);

	void visit(SmTextNodeType<A> textNodeType);

	void visit(SmUnionSimpleType<A> unionType);
}
