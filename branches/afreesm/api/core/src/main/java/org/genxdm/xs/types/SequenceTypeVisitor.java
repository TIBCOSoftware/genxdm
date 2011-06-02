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

import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.ElementDefinition;

/**
 * Visitor pattern for {@link SequenceType}.
 * 
 * @param <A>
 *            The atom handle.
 */
public interface SequenceTypeVisitor<A>
{
    void visit(AttributeDefinition<A> schemaAttribute);

    void visit(AttributeNodeType<A> attributeType);

    void visit(ChoiceType<A> choiceType);

    void visit(CommentNodeType<A> commentNodeType);

    void visit(ComplexType<A> atomicType);

    void visit(ComplexUrType<A> complexUrType);

    void visit(ConcatType<A> concatType);

    void visit(DocumentNodeType<A> documentNodeType);

    void visit(ElementDefinition<A> schemaElement);

    void visit(ElementNodeType<A> elementNodeType);

    void visit(EmptyType<A> emptyType);

    void visit(InterleaveType<A> interleaveType);

    void visit(ListSimpleType<A> atomicType);

    void visit(MultiplyType<A> multiplyType);

    void visit(NamespaceNodeType<A> namespaceNodeType);

    void visit(NodeUrType<A> nodeType);

    void visit(NoneType<A> noneType);

    void visit(ProcessingInstructionNodeType<A> processingInstructionNodeType);

    void visit(SimpleType<A> simpleType);

    void visit(SimpleUrType<A> simpleUrType);

    void visit(TextNodeType<A> textNodeType);

    void visit(UnionSimpleType<A> unionType);
}
