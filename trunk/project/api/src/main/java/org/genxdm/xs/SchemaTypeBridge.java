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
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.enums.KeeneQuantifier;
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
 * @param <A>
 *            The atom handle.
 */
public interface SchemaTypeBridge<A> extends Schema<A>
{
	SequenceType<A> atomSet(SequenceType<A> type);

	SequenceType<A> attributeAxis(SequenceType<A> contextType);

	AttributeNodeType<A> attributeType(QName name, SequenceType<A> type);

	AttributeNodeType<A> attributeWild(SequenceType<A> type);

	SequenceType<A> childAxis(SequenceType<A> contextType);

	SequenceType<A> choice(SequenceType<A> one, SequenceType<A> two);

	CommentNodeType<A> commentType();

	SequenceType<A> concat(SequenceType<A> one, SequenceType<A> two);

	DocumentNodeType<A> documentType(SequenceType<A> contextType);

	ElementNodeType<A> elementType(QName name, SequenceType<A> type, boolean nillable);

	ElementNodeType<A> elementWild(SequenceType<A> type, boolean nillable);

	EmptyType<A> emptyType();

	AtomBridge<A> getAtomBridge();

	QName getName(SequenceType<A> type);

	NameSource getNameBridge();

	SequenceType<A> interleave(SequenceType<A> one, SequenceType<A> two);

	PrimeType<A> itemType();

	SequenceType<A> multiply(SequenceType<A> argument, KeeneQuantifier multiplier);

	NamespaceNodeType<A> namespaceType();

	PrimeType<A> nodeType();

	NoneType<A> noneType();

	NoneType<A> noneType(QName errorCode);

	SequenceType<A> oneOrMore(SequenceType<A> type);

	SequenceType<A> optional(SequenceType<A> type);

	ProcessingInstructionNodeType<A> processingInstructionType(String name);

	boolean sameAs(SequenceType<A> one, SequenceType<A> two);

	boolean subtype(SequenceType<A> lhs, SequenceType<A> rhs);

	TextNodeType<A> textType();

	SequenceType<A> zeroOrMore(SequenceType<A> type);
}
