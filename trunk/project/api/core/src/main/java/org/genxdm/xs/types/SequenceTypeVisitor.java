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
 */
public interface SequenceTypeVisitor
{
    void visit(AttributeDefinition schemaAttribute);

    void visit(AttributeNodeType attributeType);

    void visit(ChoiceType choiceType);

    void visit(CommentNodeType commentNodeType);

    void visit(ComplexType atomicType);

    void visit(ComplexUrType complexUrType);

    void visit(ConcatType concatType);

    void visit(DocumentNodeType documentNodeType);

    void visit(ElementDefinition schemaElement);

    void visit(ElementNodeType elementNodeType);

    void visit(EmptyType emptyType);

    void visit(InterleaveType interleaveType);

    void visit(ListSimpleType atomicType);

    void visit(MultiplyType multiplyType);

    void visit(NamespaceNodeType namespaceNodeType);

    void visit(NodeUrType nodeType);

    void visit(NoneType noneType);

    void visit(ProcessingInstructionNodeType processingInstructionNodeType);

    void visit(SimpleType simpleType);

    void visit(SimpleUrType simpleUrType);

    void visit(TextNodeType textNodeType);

    void visit(UnionSimpleType unionType);
}
