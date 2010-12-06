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
package org.genxdm.typed.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * A bridge used for converting between (N,A) and the uniform (X) representation.
 * <p>
 * The variant representation is useful when specifying interfaces that can take any value from the XQuery Data Model.
 * </p>
 * 
 * @param <N>
 *            The node handle parameter.
 * @param <A>
 *            The atom handle parameter.
 * @param <X>
 *            The variant data model representation handle.
 */
public interface VariantBridge<N, A, X>
{
	/**
	 * Converts an atomic value to a variant value (X).
	 */
	X atom(A atom);

	/**
	 * Converts a list of atomic values to a variant value (X).
	 */
	X atomSet(List<? extends A> atoms);

	/**
	 * Converts the Java primitive for xs:boolean to a variant value (X).
	 */
	X booleanValue(Boolean booval);

	/**
	 * Converts the Java primitive for xs:decimal to a variant value (X).
	 */
	X decimalValue(BigDecimal decval);

	/**
	 * Converts the Java primitive for xs:double to a variant value (X).
	 */
	X doubleValue(Double dblval);

	/**
	 * Returns the variant value representation for the empty-sequence.
	 */
	X empty();

	/**
	 * Converts a variant value (X) known to be {@link VariantKind#ATOM} to an atom.
	 */
	A getAtom(X value);

	/**
	 * Converts a variant value (X) known to be {@link VariantKind#ATOMS} to a list of atoms.
	 */
	List<A> getAtomSet(X value);

	/**
	 * Converts a variant value (X) known to be {@link VariantKind#BOOLEAN} to a {@link Boolean}.
	 */
	Boolean getBoolean(X value);

	/**
	 * Converts a variant value (X) known to be {@link VariantKind#DECIMAL} to a {@link BigDecimal}.
	 */
	BigDecimal getDecimal(X value);

	/**
	 * Converts a variant value (X) known to be {@link VariantKind#DOUBLE} to a {@link Double}.
	 */
	Double getDouble(X value);

	/**
	 * Converts a variant value (X) known to be {@link VariantKind#INTEGER} to a {@link BigInteger}.
	 */
	BigInteger getInteger(X value);

	/**
	 * Converts a variant value (X) known to be {@link VariantKind#ITEM} to an item handle.
	 */
	Object getItem(X value);

	/**
	 * Converts a variant value (X) known to be {@link VariantKind#ITEMS} to a list of item handles.
	 */
	Iterable<Object> getItemSet(X value);

	/**
	 * Returns an enumeration representing the nature of a variant value allowing it to be correctly converted back to (I,N,A) representation.
	 */
	VariantKind getNature(X value);

	/**
	 * Converts a variant value (X) know to be {@link VariantKind#NODE} to a node handle.
	 */
	N getNode(X value);

	/**
	 * Converts a variant value (X) know to be {@link VariantKind#NODES} to a list of node handles.
	 */
	Iterable<N> getNodeSet(X value);

	/**
	 * Converts a variant value (X) know to be {@link VariantKind#STRING} to a {@link String}.
	 */
	String getString(X value);

	/**
	 * Converts the Java object for xs:integer to a variant value (X).
	 * <p>
	 * A <code>null</code> {@link BigInteger} value will be converted to an empty sequence.
	 * <p>
	 */
	X integerValue(BigInteger intval);

	/**
	 * Converts an item handle into a variant value (X).
	 */
	X item(Object item);

	/**
	 * Converts a list of item handles to a variant value (X).
	 */
	X itemSet(Iterable<Object> items);

	/**
	 * Converts a node handle into a variant value (X).
	 */
	X node(N node);

	/**
	 * Converts a list of node handles to a variant value (X).
	 */
	X nodeSet(Iterable<? extends N> nodes);

	/**
	 * Converts the Java object for xs:string to a variant value (X).
	 * <p>
	 * A <code>null</code> {@link String} value will be converted to an empty sequence.
	 * <p>
	 */
	X stringValue(String strval);

	/**
	 * Allocates an empty array of variant values (X).
	 * 
	 * @param size
	 *            The size of the array of values.
	 */
	X[] valueArray(int size);
}
