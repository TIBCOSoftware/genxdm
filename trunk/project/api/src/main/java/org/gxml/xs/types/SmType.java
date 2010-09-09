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
package org.gxml.xs.types;

import java.util.Set;

import javax.xml.namespace.QName;

import org.gxml.xs.components.SmComponent;
import org.gxml.xs.enums.SmDerivationMethod;

/**
 * Represents all types in a schema, both simple types and complex types.
 */
public interface SmType<A> extends SmComponent<A>, SmSequenceType<A>
{
	boolean derivedFrom(String namespace, String name, Set<SmDerivationMethod> derivationMethods);

	boolean derivedFromType(SmType<A> ancestorType, Set<SmDerivationMethod> derivationMethods);

	/**
	 * Returns the {base type definition} of this type. This may be a simple type or a complex type.
	 */
	SmType<A> getBaseType();

	/**
	 * Returns the {derivation method} property of this type from its base type.
	 */
	SmDerivationMethod getDerivationMethod();

	/**
	 * Returns the {final} property. Applies to both simple types and complex types. This is a design-time constraint on
	 * types. For simple types, this is a subset of {list, union, restriction}. For complex types, this is a subset of
	 * {extension, restriction}.
	 */
	Set<SmDerivationMethod> getFinal();

	/**
	 * The {name} property, which is in fact the local-name part of an expanded-QName.
	 */
	String getLocalName();

	/**
	 * The {name} and {target namespace} properties.
	 */
	QName getName();

	/**
	 * The {target namespace} property.
	 */
	String getTargetNamespace();

	/**
	 * Returns the {abstract} property of this type. <br/>
	 * Determines whether object of this type can be instantiated. An abstract type can only be used to derive subtypes.
	 */
	boolean isAbstract();

	boolean isAnonymous();

	/**
	 * Returns whether this type is a simple type with a variety of atomic.
	 */
	boolean isAtomicType();

	/**
	 * Returns whether this type is the Atomic Ur-Type.
	 */
	boolean isAtomicUrType();

	/**
	 * Returns whether this type is the Complex Ur-Type.
	 */
	boolean isComplexUrType();

	/**
	 * Determines whether a particular derivation method is final.
	 * 
	 * @param derivation
	 *            The derivation method.
	 */
	boolean isFinal(SmDerivationMethod derivation);

	/**
	 * Returns whether this type is a built-in type.
	 */
	boolean isNative();

	/**
	 * Returns whether this type is the Simple Ur-Type.
	 */
	boolean isSimpleUrType();
}
