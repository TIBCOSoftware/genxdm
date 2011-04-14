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
package org.genxdm.processor.w3c.xs.validation.regex.api;

public interface RegExBridge<E, T>
{
    public static final int UNBOUNDED = Integer.MAX_VALUE;

    /**
     * returns whether this term contains a sequence of one or more subterms
     */
    boolean isSequence(E expression);

    /**
     * returns whether this term contains a choice of one or more subterms
     */
    boolean isChoice(E expression);

    /**
     * returns whether this term contains a set of one or more subterms
     */
    boolean isInterleave(E expression);

    /**
     * returns the subterms of this term.
     */
    Iterable<E> getSubTerms(E expression);

    /**
     * returns whether this term matches the given token.
     */
    boolean matches(E expression, T token);

    /**
     * Returns true if this term and the other term are
     * both leaf terms and there exist some tokens which
     * match both terms.  Used to check for determinism.
     */
    boolean intersects(E e1, E e2);

    /**
     * The minimum number of occurrences of this term.
     *
     * @return Integer.MAX_VALUE to indicate quantification is undefined.
     */
    int minOccurs(E expression);

    /**
     * returns the maximum number of occurrences of this term.
     *
     * @return RegExBridge.UNBOUNDED to indicate unlimited occurrences.
     */
    int maxOccurs(E expression);

    /**
     * returns the prime value of this expression.
     * <br/>
     * Informally the prime value of an expression is the expression with a cardinality of (1,1).
     */
    E prime(E expression);
}
