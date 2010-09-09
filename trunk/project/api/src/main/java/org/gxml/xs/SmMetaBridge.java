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
package org.gxml.xs;

import javax.xml.namespace.QName;

import org.gxml.names.NameSource;
import org.gxml.typed.types.AtomBridge;
import org.gxml.xs.enums.SmQuantifier;
import org.gxml.xs.types.SmAttributeNodeType;
import org.gxml.xs.types.SmCommentNodeType;
import org.gxml.xs.types.SmDocumentNodeType;
import org.gxml.xs.types.SmElementNodeType;
import org.gxml.xs.types.SmEmptyType;
import org.gxml.xs.types.SmNamespaceNodeType;
import org.gxml.xs.types.SmNoneType;
import org.gxml.xs.types.SmPrimeType;
import org.gxml.xs.types.SmProcessingInstructionNodeType;
import org.gxml.xs.types.SmSequenceType;
import org.gxml.xs.types.SmTextNodeType;

/**
 * The metadata interface provided by the schema model implementation.
 * 
 * @param <A>
 *            The atom handle.
 */
public interface SmMetaBridge<A> extends SmSchema<A>
{
	SmSequenceType<A> atomSet(SmSequenceType<A> type);

	SmSequenceType<A> attributeAxis(SmSequenceType<A> contextType);

	SmAttributeNodeType<A> attributeType(QName name, SmSequenceType<A> type);

	SmAttributeNodeType<A> attributeWild(SmSequenceType<A> type);

	SmSequenceType<A> childAxis(SmSequenceType<A> contextType);

	SmSequenceType<A> choice(SmSequenceType<A> one, SmSequenceType<A> two);

	SmCommentNodeType<A> commentType();

	SmSequenceType<A> concat(SmSequenceType<A> one, SmSequenceType<A> two);

	SmDocumentNodeType<A> documentType(SmSequenceType<A> contextType);

	SmElementNodeType<A> elementType(QName name, SmSequenceType<A> type, boolean nillable);

	SmElementNodeType<A> elementWild(SmSequenceType<A> type, boolean nillable);

	SmEmptyType<A> emptyType();

	AtomBridge<A> getAtomBridge();

	QName getName(SmSequenceType<A> type);

	NameSource getNameBridge();

	SmSequenceType<A> interleave(SmSequenceType<A> one, SmSequenceType<A> two);

	SmPrimeType<A> itemType();

	SmSequenceType<A> multiply(SmSequenceType<A> argument, SmQuantifier multiplier);

	SmNamespaceNodeType<A> namespaceType();

	SmPrimeType<A> nodeType();

	SmNoneType<A> noneType();

	SmNoneType<A> noneType(QName errorCode);

	SmSequenceType<A> oneOrMore(SmSequenceType<A> type);

	SmSequenceType<A> optional(SmSequenceType<A> type);

	SmProcessingInstructionNodeType<A> processingInstructionType(String name);

	boolean sameAs(SmSequenceType<A> one, SmSequenceType<A> two);

	boolean subtype(SmSequenceType<A> lhs, SmSequenceType<A> rhs);

	SmTextNodeType<A> textType();

	SmSequenceType<A> zeroOrMore(SmSequenceType<A> type);
}
