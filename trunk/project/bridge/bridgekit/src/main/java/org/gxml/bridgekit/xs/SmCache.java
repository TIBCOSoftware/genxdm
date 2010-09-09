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
package org.gxml.bridgekit.xs;

import javax.xml.namespace.QName;

import org.gxml.names.NameSource;
import org.gxml.typed.types.AtomBridge;
import org.gxml.xs.components.SmAttribute;
import org.gxml.xs.components.SmAttributeGroup;
import org.gxml.xs.components.SmElement;
import org.gxml.xs.components.SmModelGroup;
import org.gxml.xs.constraints.SmIdentityConstraint;
import org.gxml.xs.types.SmAtomicType;
import org.gxml.xs.types.SmAtomicUrType;
import org.gxml.xs.types.SmComplexType;
import org.gxml.xs.types.SmComplexUrType;
import org.gxml.xs.types.SmDocumentNodeType;
import org.gxml.xs.types.SmElementNodeType;
import org.gxml.xs.types.SmNativeType;
import org.gxml.xs.types.SmSequenceType;
import org.gxml.xs.types.SmSimpleType;
import org.gxml.xs.types.SmSimpleUrType;

/**
 * Keep private. This can go away.
 */
interface SmCache<A> extends SmSequenceTypeFactory<A>
{
	SmDocumentNodeType<A> documentType(SmSequenceType<A> contentType);

	SmElementNodeType<A> elementWild(SmSequenceType<A> type, boolean nillable);

	AtomBridge<A> getAtomBridge();

	SmAtomicType<A> getAtomicType(QName name);

	SmAtomicType<A> getAtomicType(SmNativeType nativeType);

	SmAtomicUrType<A> getAtomicUrType();

	SmAttribute<A> getAttributeDeclaration(QName name);

	SmAttributeGroup<A> getAttributeGroup(QName name);

	SmComplexType<A> getComplexType(QName name);

	SmComplexUrType<A> getComplexUrType();

	SmElement<A> getElementDeclaration(QName name);

	SmIdentityConstraint<A> getIdentityConstraint(QName name);

	SmModelGroup<A> getModelGroup(QName name);

	NameSource getNameBridge();

	SmSimpleType<A> getSimpleType(QName name);

	SmSimpleType<A> getSimpleType(SmNativeType nativeType);

	SmSimpleUrType<A> getSimpleUrType();

	boolean hasAttribute(QName name);

	boolean hasAttributeGroup(QName name);

	boolean hasComplexType(QName name);

	boolean hasElement(QName name);

	boolean hasIdentityConstraint(QName name);

	boolean hasModelGroup(QName name);

	boolean hasSimpleType(QName name);

	boolean hasType(QName name);

	/**
	 * Allocates a new unique name for an "anonymous" type.
	 */
	QName generateUniqueName();
}
