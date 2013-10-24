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

import org.genxdm.typed.types.Quantifier;
import org.genxdm.xs.components.ForeignAttributes;

/**
 * Root interface for a hierarchy of sequence types.
 * 
 */
public interface SequenceType
    extends ForeignAttributes
{
    /**
     * Implementation of the visitor design pattern.
     * 
     * @param visitor
     *            The visitor of this type.
     */
    void accept(SequenceTypeVisitor visitor);

    /**
     * The prime method extracts all the item types from this type and combines them into a choice.
     */
    PrimeType prime();

    /**
     * The quantifier method approximates the possible number of items in this type with the occurrence indicators
     * supported by the [XPath/XQuery] type system (?, +, *).
     */
    Quantifier quantifier();
}
