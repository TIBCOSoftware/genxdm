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
package org.gxml.xs.components;

import javax.xml.namespace.QName;

import org.gxml.xs.constraints.SmIdentityConstraint;
import org.gxml.xs.types.SmAtomicType;
import org.gxml.xs.types.SmAtomicUrType;
import org.gxml.xs.types.SmComplexType;
import org.gxml.xs.types.SmComplexUrType;
import org.gxml.xs.types.SmNativeType;
import org.gxml.xs.types.SmSimpleType;
import org.gxml.xs.types.SmSimpleUrType;
import org.gxml.xs.types.SmType;

/**
 * A collection of schema components accessible through their expanded-QName.
 */
public interface SmComponentProvider<A> extends SmComponentDetector
{
	QName generateUniqueName();

	/**
	 * Returns the atomic type if it exists by the given name, otherwise <code>null</code>. If an error occurs, an
	 * implementation-defined unchecked exception will be thrown.
	 * 
	 * @param name
	 *            The name of the Atomic type definition to be retrieved.
	 * @return The atomic type identified by the name, or <code>null</code> if it does not exist.
	 */
	SmAtomicType<A> getAtomicType(QName name);

	SmAtomicType<A> getAtomicType(SmNativeType name);

	SmAtomicUrType<A> getAtomicUrType();

	/**
	 * Returns the {@link org.gxml.xs.components.SmAttribute} if it exists by the given name, otherwise <code>null</code>. If an
	 * error occurs, an implementation-defined unchecked exception will be thrown.
	 * 
	 * @param name
	 *            The name of the attribute declaration to be retrieved.
	 * @return The {@link org.gxml.xs.components.SmAttribute} identified by the name, or <code>null</code> if it does not exist.
	 */
	SmAttribute<A> getAttributeDeclaration(QName name);

	/**
	 * Returns the {@link SmAttributeGroup} if it exists by the given name, otherwise <code>null</code>. If an error
	 * occurs, an implementation-defined unchecked exception will be thrown.
	 * 
	 * @param name
	 *            The name of the Attribute Group Definition to be retrieved.
	 * @return The {@link org.gxml.xs.components.SmAttributeGroup} identified by the name, or <code>null</code> if it does not
	 *         exist.
	 */
	SmAttributeGroup<A> getAttributeGroup(QName name);

	/**
	 * Returns the {@link SmComplexType} if it exists by the given name, otherwise <code>null</code>. If an error
	 * occurs, an implementation-defined unchecked exception will be thrown.
	 * 
	 * @param name
	 *            The name of the Complex type definition to be retrieved.
	 * @return The {@link org.gxml.xs.types.SmComplexType} identified by the name, or <code>null</code> if it does not exist.
	 */
	SmComplexType<A> getComplexType(QName name);

	SmComplexUrType<A> getComplexUrType();

	/**
	 * Returns the {@link org.gxml.xs.components.SmElement} if it exists by the given name, otherwise <code>null</code>. If an
	 * error occurs, an implementation-defined unchecked exception will be thrown.
	 * 
	 * @param name
	 *            The name of the element declaration to be retrieved.
	 * @return The {@link org.gxml.xs.components.SmElement} identified by the name, or <code>null</code> if it does not exist.
	 */
	SmElement<A> getElementDeclaration(QName name);

	/**
	 * Returns the {@link SmIdentityConstraint} if it exists by the given name, otherwise <code>null</code>. If an error
	 * occurs, an implementation-defined unchecked exception will be thrown.
	 * 
	 * @param name
	 *            The name of the Identity-constraint definition to be retrieved.
	 * @return The {@link SmIdentityConstraint} identified by the name, or <code>null</code> if it does not exist.
	 */
	SmIdentityConstraint<A> getIdentityConstraint(QName name);

	/**
	 * Returns the {@link org.gxml.xs.components.SmModelGroup} if it exists by the given name, otherwise <code>null</code>. If an
	 * error occurs, an implementation-defined unchecked exception will be thrown.
	 * 
	 * @param name
	 *            The name of the Model Group Definition to be retrieved.
	 * @return The {@link org.gxml.xs.components.SmModelGroup} identified by the name, or <code>null</code> if it does not exist.
	 */
	SmModelGroup<A> getModelGroup(QName name);

	/**
	 * Returns the {@link org.gxml.xs.components.SmNotation} if it exists by the given name, otherwise <code>null</code>. If an
	 * error occurs, an implementation-defined unchecked exception will be thrown.
	 * 
	 * @param name
	 *            The name of the Notation declaration to be retrieved.
	 * @return The {@link org.gxml.xs.components.SmNotation} identified by the name, or <code>null</code> if it does not exist.
	 */
	SmNotation<A> getNotationDeclaration(QName name);

	/**
	 * Returns the {@link org.gxml.xs.types.SmSimpleType} if it exists by the given name, otherwise <code>null</code>. If an
	 * error occurs, an implementation-defined unchecked exception will be thrown.
	 * 
	 * @param name
	 *            The name of the Simple type definition to be retrieved.
	 * @return The {@link org.gxml.xs.types.SmSimpleType} identified by the name, or <code>null</code> if it does not exist.
	 */
	SmSimpleType<A> getSimpleType(QName name);

	SmSimpleType<A> getSimpleType(SmNativeType name);

	SmSimpleUrType<A> getSimpleUrType();

	/**
	 * Returns the {@link org.gxml.xs.types.SmType} if it exists by the given name, otherwise <code>null</code>. If an error
	 * occurs, an implementation-defined unchecked exception will be thrown.
	 * 
	 * @param name
	 *            The name of the type to be retrieved.
	 * @return The {@link org.gxml.xs.types.SmType} identified by the name, or <code>null</code> if it does not exist.
	 */
	SmType<A> getTypeDefinition(QName name);

	/**
	 * Returns the specified type definition.
	 * 
	 * @param nativeType
	 *            The name of the native type definition.
	 */
	SmType<A> getTypeDefinition(SmNativeType nativeType);
}
