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
package org.genxdm.bridgekit.xs;

import javax.xml.namespace.QName;

import org.genxdm.names.NameSource;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.types.SmAtomicType;
import org.genxdm.xs.types.SmAtomicUrType;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmComplexUrType;
import org.genxdm.xs.types.SmDocumentNodeType;
import org.genxdm.xs.types.SmElementNodeType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSequenceType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmSimpleUrType;

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

	AttributeDefinition<A> getAttributeDeclaration(QName name);

	AttributeGroupDefinition<A> getAttributeGroup(QName name);

	SmComplexType<A> getComplexType(QName name);

	SmComplexUrType<A> getComplexUrType();

	ElementDefinition<A> getElementDeclaration(QName name);

	SmIdentityConstraint<A> getIdentityConstraint(QName name);

	ModelGroup<A> getModelGroup(QName name);

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
