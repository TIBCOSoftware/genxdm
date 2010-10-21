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
package org.genxdm.xs.types;

import org.genxdm.xs.SchemaTypeBridge;

/**
 * The following enumerated constants are used by {@link SchemaTypeBridge} to classify the sequence type instance.
 */
public enum SmPrimeTypeKind
{
	/**
	 * Represents any kind of item.
	 */
	ITEM,
	/**
	 * Represents an atomic type. i.e. anything derived from xs:anyAtomicType.
	 */
	ATOM,
	/**
	 * Represents any kind of node.
	 */
	NODE,
	/**
	 * Represents a document node.
	 */
	DOCUMENT,
	/**
	 * Represents a comment node, i.e. the type <code>comment()</code> as defined in XQuery.
	 */
	COMMENT,
	/**
	 * Represents an element node, i.e. the type <code>element(...)</code> as defined in XQuery.
	 */
	ELEMENT,
	/**
	 * Represents an element node whose type annotation matches a schema type, i.e. the type
	 * <code>schema-element(...)</code> as defined in XQuery.
	 */
	SCHEMA_ELEMENT,
	/**
	 * Represents an attribute node, i.e. the type <code>attribute(...)</code> as defined in XQuery.
	 */
	ATTRIBUTE,
	/**
	 * Represents an attribute node whose type annotation matches a schema type, i.e. the type
	 * <code>schema-attribute(...)</code> as defined in XQuery.
	 */
	SCHEMA_ATTRIBUTE,
	/**
	 * Represents a processing instruction node, i.e. the type <code>processing-instruction(...)</code> as defined in
	 * XQuery.
	 */
	PROCESSING_INSTRUCTION,
	/**
	 * Represents a text node, i.e. the type <code>text()</code> as defined in XQuery.
	 */
	TEXT,
	/**
	 * Represents a namespace node.
	 */
	NAMESPACE,
	/**
	 * Represents the absence of a type, i.e. an error.
	 */
	NONE,
	/**
	 * Represents the type indicating an empty sequence.
	 */
	EMPTY,
	/**
	 * Represents a choice between two types. i.e. <code>type-1 | type-2</code>.
	 */
	CHOICE,
	/**
	 * Represents the xs:anyType Complex Ur-type.
	 */
	ANY_TYPE,
	/**
	 * Represents the xs:anySimpleType Simple Ur-type.
	 */
	ANY_SIMPLE_TYPE,
	/**
	 * Represents the xs:anyAtomicType Atomic Ur-type.
	 */
	ANY_ATOMIC_TYPE,
	/**
	 * Represents a user-defined complex type. i.e. anything derived from xs:anyType.
	 */
	COMPLEX
}
