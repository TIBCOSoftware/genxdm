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
package org.genxdm.xs;

import javax.xml.namespace.QName;

import org.genxdm.names.NameSource;
import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.types.AttributeNodeType;
import org.genxdm.xs.types.CommentNodeType;
import org.genxdm.xs.types.DocumentNodeType;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.EmptyType;
import org.genxdm.xs.types.NamespaceNodeType;
import org.genxdm.xs.types.NoneType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.ProcessingInstructionNodeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.TextNodeType;

/**
 * The metadata interface provided by the schema model implementation.
 * 
 * @param 
 *            The atom handle.
 */
public interface SchemaTypeBridge extends Schema
{
    SequenceType atomSet(SequenceType type);

    SequenceType attributeAxis(SequenceType contextType);

    AttributeNodeType attributeType(QName name, SequenceType type);

    AttributeNodeType attributeWild(SequenceType type);

    SequenceType childAxis(SequenceType contextType);

    SequenceType choice(SequenceType one, SequenceType two);

    CommentNodeType commentType();

    SequenceType concat(SequenceType one, SequenceType two);

    DocumentNodeType documentType(SequenceType contextType);

    ElementNodeType elementType(QName name, SequenceType type, boolean nillable);

    ElementNodeType elementWild(SequenceType type, boolean nillable);

    EmptyType emptyType();

    QName getName(SequenceType type);

    SequenceType interleave(SequenceType one, SequenceType two);

    PrimeType itemType();

    SequenceType multiply(SequenceType argument, Quantifier multiplier);

    NamespaceNodeType namespaceType();

    PrimeType nodeType();

    NoneType noneType();

    NoneType noneType(QName errorCode);

    SequenceType oneOrMore(SequenceType type);

    SequenceType optional(SequenceType type);

    ProcessingInstructionNodeType processingInstructionType(String name);

    boolean sameAs(SequenceType one, SequenceType two);

    boolean subtype(SequenceType lhs, SequenceType rhs);

    TextNodeType textType();

    SequenceType zeroOrMore(SequenceType type);
}
