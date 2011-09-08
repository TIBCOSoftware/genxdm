/*
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

import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
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
public interface ComponentProvider extends ComponentDetector
{
    QName generateUniqueName();

    /**
     * Returns the atomic type if it exists by the given name, otherwise <code>null</code>. If an error occurs, an
     * implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Atomic type definition to be retrieved; must not be null.
     * @return The atomic type identified by the name, or <code>null</code> if it does not exist.
     */
    AtomicType getAtomicType(QName name);

    /**
     * 
     * @param name the native type definition for the atomic type to be retrieved; must not be null.
     * @return the atomic type identified by the name, or null if it does not exist.
     */
    AtomicType getAtomicType(NativeType name);

    /**
     * 
     * @return the atomic ur-type; never null.
     */
    AtomicUrType getAtomicUrType();

    /**
     * Returns the {@link org.genxdm.xs.components.AttributeDefinition} if it exists by the given name, otherwise <code>null</code>. If an
     * error occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the attribute declaration to be retrieved; must not be null.
     * @return The {@link org.genxdm.xs.components.AttributeDefinition} identified by the name, or <code>null</code> if it does not exist.
     */
    AttributeDefinition getAttributeDeclaration(QName name);

    /**
     * Returns the {@link AttributeGroupDefinition} if it exists by the given name, otherwise <code>null</code>. If an error
     * occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Attribute Group Definition to be retrieved; must not be null.
     * @return The {@link org.genxdm.xs.components.AttributeGroupDefinition} identified by the name, or <code>null</code> if it does not
     *         exist.
     */
    AttributeGroupDefinition getAttributeGroup(QName name);

    /**
     * Returns the {@link ComplexType} if it exists by the given name, otherwise <code>null</code>. If an error
     * occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Complex type definition to be retrieved; must not be null.
     * @return The {@link org.genxdm.xs.types.ComplexType} identified by the name, or <code>null</code> if it does not exist.
     */
    ComplexType getComplexType(QName name);

    ComplexUrType getComplexUrType();

    /**
     * Returns the {@link org.genxdm.xs.components.ElementDefinition} if it exists by the given name, otherwise <code>null</code>. If an
     * error occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the element declaration to be retrieved; must not be null.
     * @return The {@link org.genxdm.xs.components.ElementDefinition} identified by the name, or <code>null</code> if it does not exist.
     */
    ElementDefinition getElementDeclaration(QName name);

    /**
     * Returns the {@link IdentityConstraint} if it exists by the given name, otherwise <code>null</code>. If an error
     * occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Identity-constraint definition to be retrieved; must not be null.
     * @return The {@link IdentityConstraint} identified by the name, or <code>null</code> if it does not exist.
     */
    IdentityConstraint getIdentityConstraint(QName name);

    /**
     * Returns the {@link org.genxdm.xs.components.ModelGroup} if it exists by the given name, otherwise <code>null</code>. If an
     * error occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Model Group Definition to be retrieved; must not be null.
     * @return The {@link org.genxdm.xs.components.ModelGroup} identified by the name, or <code>null</code> if it does not exist.
     */
    ModelGroup getModelGroup(QName name);

    /**
     * Returns the {@link org.genxdm.xs.components.NotationDefinition} if it exists by the given name, otherwise <code>null</code>. If an
     * error occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Notation declaration to be retrieved; must not be null.
     * @return The {@link org.genxdm.xs.components.NotationDefinition} identified by the name, or <code>null</code> if it does not exist.
     */
    NotationDefinition getNotationDeclaration(QName name);

    /**
     * Returns the {@link org.genxdm.xs.types.SimpleType} if it exists by the given name, otherwise <code>null</code>. If an
     * error occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the Simple type definition to be retrieved; must not be null.
     * @return The {@link org.genxdm.xs.types.SimpleType} identified by the name, or <code>null</code> if it does not exist.
     */
    SimpleType getSimpleType(QName name);

    SimpleType getSimpleType(NativeType name);

    SimpleUrType getSimpleUrType();

    /**
     * Returns the {@link org.genxdm.xs.types.Type} if it exists by the given name, otherwise <code>null</code>. If an error
     * occurs, an implementation-defined unchecked exception will be thrown.
     * 
     * @param name
     *            The name of the type to be retrieved; must not be null.
     * @return The {@link org.genxdm.xs.types.Type} identified by the name, or <code>null</code> if it does not exist.
     */
    Type getTypeDefinition(QName name);

    /**
     * Returns the specified type definition.
     * 
     * @param nativeType
     *            The name of the native type definition; must not be null.
     */
    Type getTypeDefinition(NativeType nativeType);
}
