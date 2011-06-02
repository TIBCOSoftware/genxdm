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
package org.genxdm.xs.components;

import javax.xml.namespace.QName;

import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.AtomicUrType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ComplexUrType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.Type;

/**
 * A collection of schema components accessible through their expanded-QName.
 */
public interface ComponentProvider<A> extends ComponentDetector
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
    AtomicType<A> getAtomicType(QName name);

    AtomicType<A> getAtomicType(NativeType name);

    AtomicUrType<A> getAtomicUrType();

    /**
     * Returns the {@link org.genxdm.xs.components.AttributeDefinition} if it exists by the given name, otherwise <code>null</code>. If an
     * error occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the attribute declaration to be retrieved.
     * @return The {@link org.genxdm.xs.components.AttributeDefinition} identified by the name, or <code>null</code> if it does not exist.
     */
    AttributeDefinition<A> getAttributeDeclaration(QName name);

    /**
     * Returns the {@link AttributeGroupDefinition} if it exists by the given name, otherwise <code>null</code>. If an error
     * occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Attribute Group Definition to be retrieved.
     * @return The {@link org.genxdm.xs.components.AttributeGroupDefinition} identified by the name, or <code>null</code> if it does not
     *         exist.
     */
    AttributeGroupDefinition<A> getAttributeGroup(QName name);

    /**
     * Returns the {@link ComplexType} if it exists by the given name, otherwise <code>null</code>. If an error
     * occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Complex type definition to be retrieved.
     * @return The {@link org.genxdm.xs.types.ComplexType} identified by the name, or <code>null</code> if it does not exist.
     */
    ComplexType<A> getComplexType(QName name);

    ComplexUrType<A> getComplexUrType();

    /**
     * Returns the {@link org.genxdm.xs.components.ElementDefinition} if it exists by the given name, otherwise <code>null</code>. If an
     * error occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the element declaration to be retrieved.
     * @return The {@link org.genxdm.xs.components.ElementDefinition} identified by the name, or <code>null</code> if it does not exist.
     */
    ElementDefinition<A> getElementDeclaration(QName name);

    /**
     * Returns the {@link IdentityConstraint} if it exists by the given name, otherwise <code>null</code>. If an error
     * occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Identity-constraint definition to be retrieved.
     * @return The {@link IdentityConstraint} identified by the name, or <code>null</code> if it does not exist.
     */
    IdentityConstraint<A> getIdentityConstraint(QName name);

    /**
     * Returns the {@link org.genxdm.xs.components.ModelGroup} if it exists by the given name, otherwise <code>null</code>. If an
     * error occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Model Group Definition to be retrieved.
     * @return The {@link org.genxdm.xs.components.ModelGroup} identified by the name, or <code>null</code> if it does not exist.
     */
    ModelGroup<A> getModelGroup(QName name);

    /**
     * Returns the {@link org.genxdm.xs.components.NotationDefinition} if it exists by the given name, otherwise <code>null</code>. If an
     * error occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Notation declaration to be retrieved.
     * @return The {@link org.genxdm.xs.components.NotationDefinition} identified by the name, or <code>null</code> if it does not exist.
     */
    NotationDefinition<A> getNotationDeclaration(QName name);

    /**
     * Returns the {@link org.genxdm.xs.types.SimpleType} if it exists by the given name, otherwise <code>null</code>. If an
     * error occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Simple type definition to be retrieved.
     * @return The {@link org.genxdm.xs.types.SimpleType} identified by the name, or <code>null</code> if it does not exist.
     */
    SimpleType<A> getSimpleType(QName name);

    SimpleType<A> getSimpleType(NativeType name);

    SimpleUrType<A> getSimpleUrType();

    /**
     * Returns the {@link org.genxdm.xs.types.Type} if it exists by the given name, otherwise <code>null</code>. If an error
     * occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the type to be retrieved.
     * @return The {@link org.genxdm.xs.types.Type} identified by the name, or <code>null</code> if it does not exist.
     */
    Type<A> getTypeDefinition(QName name);

    /**
     * Returns the specified type definition.
     * 
     * @param nativeType
     *            The name of the native type definition.
     */
    Type<A> getTypeDefinition(NativeType nativeType);
}
