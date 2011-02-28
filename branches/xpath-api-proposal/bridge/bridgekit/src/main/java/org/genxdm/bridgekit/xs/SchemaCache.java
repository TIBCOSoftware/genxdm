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
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.AtomicUrType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ComplexUrType;
import org.genxdm.xs.types.DocumentNodeType;
import org.genxdm.xs.types.ElementNodeType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SequenceType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;

/**
 * Keep private. This can go away.
 */
interface SchemaCache<A> extends SequenceTypeFactory<A>
{
	DocumentNodeType<A> documentType(SequenceType<A> contentType);

	ElementNodeType<A> elementWild(SequenceType<A> type, boolean nillable);

	AtomBridge<A> getAtomBridge();

	AtomicType<A> getAtomicType(QName name);

	AtomicType<A> getAtomicType(NativeType nativeType);

	AtomicUrType<A> getAtomicUrType();

	AttributeDefinition<A> getAttributeDeclaration(QName name);

	AttributeGroupDefinition<A> getAttributeGroup(QName name);

	ComplexType<A> getComplexType(QName name);

	ComplexUrType<A> getComplexUrType();

	ElementDefinition<A> getElementDeclaration(QName name);

	IdentityConstraint<A> getIdentityConstraint(QName name);

	ModelGroup<A> getModelGroup(QName name);

	NameSource getNameBridge();

	SimpleType<A> getSimpleType(QName name);

	SimpleType<A> getSimpleType(NativeType nativeType);

	SimpleUrType<A> getSimpleUrType();

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
