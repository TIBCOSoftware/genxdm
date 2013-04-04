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
package org.genxdm.processor.w3c.xs.xmlrep;

import org.genxdm.xs.components.SchemaParticle;

public final class XMLCardinality
{
    private static final int UNBOUNDED = Integer.MAX_VALUE;

    /**
     * A cardinality of min=1, max=unbounded, known=true.
     */
    public static final XMLCardinality AT_LEAST_ONE = new XMLCardinality(1, UNBOUNDED, true);
    /**
     * A cardinality of min=1, max=1, known=true.
     */
    public static final XMLCardinality EXACTLY_ONE = new XMLCardinality(1, 1, true);

    /**
     * Above this number, the implementation rounds maximum values up to unbounded.
     */
    private static final int MAX_OCCUR_CUTOFF_LEVEL = 10;

    /**
     * A cardinality of min=0, max=0, known=true.
     */
    public static final XMLCardinality NONE = new XMLCardinality(0, 0, true);

    /**
     * A cardinality of min=0, max=1, known=true.
     */
    public static final XMLCardinality OPTIONAL = new XMLCardinality(0, 1, true);

    /**
     * A cardinality of min=0, max=unbounded, known=true.
     */
    public static final XMLCardinality REPEATING = new XMLCardinality(0, UNBOUNDED, true);

    /**
     * A cardinality of min=0, max=1, known=false.
     */
    public static final XMLCardinality UNKNOWN_OPTIONAL = new XMLCardinality(0, 1, false);

    /**
     * A cardinality of min=0, max=unbounded, known=true.
     */
    public static final XMLCardinality UNKNOWN_REPEATING = new XMLCardinality(0, UNBOUNDED, false);

    /**
     * Creates a cardinality with min and max, and where known is true.
     * 
     * @param min
     *            The minOccurs
     * @param max
     *            The maxOccurs
     * @return An XOccurrence with min and max set, and with known as true.
     */
    public static XMLCardinality create(final int min, final int max)
    {
        return create(min, max, true);
    }

    /**
     * Creates a cardinality with min, max and known.
     * 
     * @param min
     *            The minOccurs
     * @param max
     *            The maxOccurs
     * @param known
     *            Is the cardinality an guess or known for sure.
     * @return A cardinality with min and max set, and with known as true.
     */
    public static XMLCardinality create(final int min, final int max, final boolean known)
    {
        if (known)
        {
            if (min == 0 && max == 0)
            {
                return NONE;
            }
            if (min == 1 && max == 1)
            {
                return EXACTLY_ONE;
            }
            if (min == 0 && max == 1)
            {
                return OPTIONAL;
            }
            if (min == 0 && max == UNBOUNDED)
            {
                return REPEATING;
            }
            if (min == 1 && max == UNBOUNDED)
            {
                return AT_LEAST_ONE;
            }
        }
        if (min > max)
        {
            throw new IllegalArgumentException("Min can't be > max");
        }
        if (min == UNBOUNDED)
        {
            throw new IllegalArgumentException("Min can't be unbounded");
        }
        return new XMLCardinality(min, max, known);
    }

    /**
     * Creates a cardinality with the min and max taken from the particle. <br>
     * Known is set to true.
     * 
     * @param particle
     *            The particle whose cardinality is to be taken.
     * @return The cardinality corresponding to the particle's cardinality.
     */
    public static  XMLCardinality create(final SchemaParticle particle)
    {
    	if(particle.isMaxOccursUnbounded())
    	{
    		return create(particle.getMinOccurs(), UNBOUNDED, true);
    	}
        return create(particle.getMinOccurs(), particle.getMaxOccurs(), true);
    }

    private final boolean m_known; // are m_min & m_max known for sure or just a 'guess'

    private final int m_max; // max occurs

    private final int m_min; // min occurs

    /**
     * Internal constructor.
     * 
     * @param min
     *            The minOccurs
     * @param max
     *            The maxOccurs
     * @param known
     *            Is the cardinality an guess or known for sure.
     */
    private XMLCardinality(final int min, final int max, final boolean known)
    {
        m_min = min;
        m_max = max;
        m_known = known;
    }

    /**
     * Does a cardinality addition (sum) based on the xquery-formal-semantics definition of quantifier sum. <br/>
     * This implementation is more general in that XQuery only recognizes ?,+,*, and required, but this implementation
     * represents holds a min and max occurrence. To get the exact equivalent of the xquery semantics, call
     * {@link #reduceToPrimitive()} on the result.
     * 
     * @param cardinality
     *            The cardinality to add to this.
     * @return The sum of the two cardinalities.
     */
    public XMLCardinality addTo(final XMLCardinality cardinality)
    {
        int min1 = getMinOccurs();
        int min2 = cardinality.getMinOccurs();
        int max1 = getMaxOccurs();
        int max2 = cardinality.getMaxOccurs();
        int min;
        int max;
        boolean known = isKnown() && cardinality.isKnown();
        if (min1 > MAX_OCCUR_CUTOFF_LEVEL || min2 > MAX_OCCUR_CUTOFF_LEVEL || min1 + min2 > MAX_OCCUR_CUTOFF_LEVEL)
        {
            min = MAX_OCCUR_CUTOFF_LEVEL; // don't go any greater than MAX_OCCUR_CUTOFF_LEVEL for min
        }
        else
        {
            min = min1 + min2;
        }
        if (max1 > MAX_OCCUR_CUTOFF_LEVEL || max2 > MAX_OCCUR_CUTOFF_LEVEL || max1 + max2 > MAX_OCCUR_CUTOFF_LEVEL || max1 == UNBOUNDED || max2 == UNBOUNDED)
        {
            max = UNBOUNDED;
        }
        else
        {
            max = max1 + max2;
        }
        return XMLCardinality.create(min, max, known);
    }

    /**
     * Does a cardinality choice based on the xquery-formal-semantics definition of quantifier choice. <br/>
     * This implementation is more general in that XQuery only recognizes ?,+,*, and required, but this implementation
     * represents holds a min and max occurrence. To get the exact equivalent of the xquery semantics, call
     * {@link #reduceToPrimitive()} on the result. <br/>
     * ChoiceBy corresponds to the overall cardinality resulting from type choices, for example (A? | B+) --- here the
     * overall cardinality is * (see below). <br/>
     * Example results
     * <code>{@link #AT_LEAST_ONE} choice with {@link #OPTIONAL} is {@link #REPEATING} because the cardinality min is
     * either 1 or 0, so we must be conservative and pick 0, and the cardinality max is either 1 or unbounded, so again,
     * we must be conservative and pick unbounded. This leaves 0,unbounded which is repeating.
     * 
     * @param cardinality
     *            The cardinality to 'choice' to this.
     * @return The 'choice' of the two cardinalities.
     */
    public XMLCardinality choiceBy(final XMLCardinality cardinality)
    {
        int newmin;
        if (m_min >= MAX_OCCUR_CUTOFF_LEVEL && cardinality.m_min >= MAX_OCCUR_CUTOFF_LEVEL)
        {
            newmin = MAX_OCCUR_CUTOFF_LEVEL; // limit to a reasonable level.
        }
        else
        {
            newmin = Math.min(m_min, cardinality.m_min);
        }
        newmin = Math.min(newmin, MAX_OCCUR_CUTOFF_LEVEL); // limit to a reasonable level.
        int newmax;
        if (m_max >= MAX_OCCUR_CUTOFF_LEVEL && cardinality.m_max >= MAX_OCCUR_CUTOFF_LEVEL)
        {
            newmax = UNBOUNDED;
        }
        else
        {
            newmax = Math.max(m_max, cardinality.m_max);
        }
        if (newmax > MAX_OCCUR_CUTOFF_LEVEL)
        { // after a point, just call it unbounded.
            newmax = UNBOUNDED;
        }
        boolean newKnown = m_known && cardinality.m_known;
        return XMLCardinality.create(newmin, newmax, newKnown);
    }

    /**
     * Does a cardinality common-occurs based on the xquery-formal-semantics definition of quantifier common-occurs. <br/>
     * This implementation is more general in that XQuery only recognizes ?,+,*, and required, but this implementation
     * represents holds a min and max occurrence. To get the exact equivalent of the xquery semantics, call
     * {@link #reduceToPrimitive()} on the result. <br/>
     * common-occurs is essentially the result of an 'intersection' for cardinality, for example if: A? and A+ is true,
     * then A must be 1 (required). This is because the left hand side (A?) is either 0 or 1 occurs, but the right hand
     * side 1 or more, so the only possible cardinality remaining is 1. <br/>
     * This corresponds exactly to an 'instance-of' check, too, i.e. A? instanceof A+ is true only when the cardinality
     * is A. <br/>
     * Example: {@link #OPTIONAL} common occurs with {@link #AT_LEAST_ONE} yields {@link #EXACTLY_ONE}
     * 
     * @param cardinality
     *            The cardinality to add to this.
     * @return The common occurrence of the two cardinalities.
     */
    public XMLCardinality commonOccursWith(XMLCardinality cardinality)
    {
        int min1 = getMinOccurs();
        int min2 = cardinality.getMinOccurs();
        int max1 = getMaxOccurs();
        int max2 = cardinality.getMaxOccurs();
        int min;
        int max;
        boolean known = isKnown() && cardinality.isKnown();
        if (min1 > MAX_OCCUR_CUTOFF_LEVEL || min2 > MAX_OCCUR_CUTOFF_LEVEL || min1 + min2 > MAX_OCCUR_CUTOFF_LEVEL)
        {
            min = MAX_OCCUR_CUTOFF_LEVEL; // don't go any greater than MAX_OCCUR_CUTOFF_LEVEL for min
        }
        else
        {
            min = Math.max(min1, min2);
        }
        if (max1 == UNBOUNDED && max2 == UNBOUNDED)
        {
            max = UNBOUNDED;
        }
        else
        {
            max = Math.min(max1, max2);
        }
        if (min > max)
        {
            // not possible.
            return XMLCardinality.create(0, 0, known);
        }
        return XMLCardinality.create(min, max, known);
    }

    public boolean equals(Object val)
    {
        if (val instanceof XMLCardinality)
        {
            XMLCardinality xo = (XMLCardinality)val;
            return xo.m_min == m_min && xo.m_max == m_max && xo.m_known == m_known;
        }
        return false;
    }

    public int getMaxOccurs()
    {
        return m_max;
    }

    public int getMinOccurs()
    {
        return m_min;
    }

    public int hashCode()
    {
        return m_min + m_max;
    }

    public boolean isKnown()
    {
        return m_known;
    }

    /**
     * Does a cardinality product based on the xquery-formal-semantics definition of quantifier product. <br>
     * This implementation is more general in that XQuery only recognizes ?,+,*, and required, but this implementation
     * represents holds a min and max occurrence. To get the exact equivalent of the xquery semantics, call
     * {@link #reduceToPrimitive()} on the result. <br>
     * Example results <code>{@link #EXACTLY_ONE} multiply by {@link #OPTIONAL} is {@link #OPTIONAL} because the
     * cardinality min is the product of 1*0, which is 0, and the cardinality max is the product of 1*1, which is 1.
     * This leaves 0,1 which is optional.
     * 
     * @param cardinality
     *            The cardinality to multiply by this.
     * @return The product of the two cardinalities.
     */
    public XMLCardinality multiplyBy(final XMLCardinality cardinality)
    {
        int newmin;
        if (m_min >= MAX_OCCUR_CUTOFF_LEVEL && cardinality.m_min >= MAX_OCCUR_CUTOFF_LEVEL)
        {
            newmin = MAX_OCCUR_CUTOFF_LEVEL; // limit to a reasonable level.
        }
        else
        {
            newmin = m_min * cardinality.m_min;
        }
        newmin = Math.min(newmin, MAX_OCCUR_CUTOFF_LEVEL); // limit to a reasonable level.
        int newmax;
        if (m_max == UNBOUNDED && cardinality.m_max == UNBOUNDED)
        {
            newmax = UNBOUNDED;
        }
        else
        {
            newmax = m_max * cardinality.m_max;
            // special checks for multiply with unbounded:
            if (newmax != 0 && (m_max == UNBOUNDED || cardinality.m_max == UNBOUNDED))
            {
                newmax = UNBOUNDED;
            }
        }
        if (newmax > MAX_OCCUR_CUTOFF_LEVEL)
        { // after a point, just call it unbounded.
            newmax = UNBOUNDED;
        }
        boolean newKnown = m_known && cardinality.m_known;
        return XMLCardinality.create(newmin, newmax, newKnown);
    }

    /**
     * Returns the most specific one of the 5 'primitive' cardinalities; {@link #OPTIONAL}, {@link #EXACTLY_ONE},
     * {@link #REPEATING}, {@link #AT_LEAST_ONE}, or {@link #NONE} for this cardinality. <br>
     * So, for example, a cardinality of (2,2) would become + {@link #AT_LEAST_ONE}, a cardinality of (0,2) would become
     * {@link #REPEATING} , and a cardinality of (1,1) would be unchanged at {@link #EXACTLY_ONE}. Note that
     * {@link #isKnown} is ignored for this computation.
     * 
     * @return The closest primitive cardinality.
     */
    public XMLCardinality reduceToPrimitive()
    {
        if (m_min == 0)
        {
            if (m_max == 0)
            {
                return XMLCardinality.NONE;
            }
            if (m_max == 1)
            {
                return XMLCardinality.OPTIONAL;
            }
            // otherwise, it must be repeating:
            return XMLCardinality.REPEATING;
        }
        if (m_min == 1 && m_max == 1)
        { // check for exactly one:
            return XMLCardinality.EXACTLY_ONE;
        }
        // otherwise it must be at-least-one:
        return XMLCardinality.AT_LEAST_ONE;
    }

    /**
     * Differs from add because (+ - *) is *, not +.
     */
    public XMLCardinality subtract(final XMLCardinality occurrence)
    {
        int min1 = getMinOccurs();
        int min2 = occurrence.getMinOccurs();
        int max1 = getMaxOccurs();
        int max2 = occurrence.getMaxOccurs();
        int min;
        int max;
        boolean known = isKnown() && occurrence.isKnown();
        if (max2 == UNBOUNDED)
        {
            min = 0;
        }
        else
        {
            min = min1 - max2;
        }
        if (min < 0)
            min = 0;
        if (max1 == UNBOUNDED)
        {
            max = UNBOUNDED;
        }
        else
        {
            if (min2 > max1)
            {
                max = 0;
            }
            else
            {
                max = max1 - min2;
            }
        }
        return XMLCardinality.create(min, max, known);
    }

    /**
     * Formats in a debugger-friendly way. <br>
     * Returns a zero length string for min==1 && max==1, '(0)' for 0,0, '?' for 0,1, '*' for 0,unbounded and '+' for
     * 1,unbounded. For 3,4 it would return '(3,4)' For 23,23 it would return '(23)'
     */
    public String toString()
    {
        if (m_min == 0 && m_max == 0)
            return "(0)";
        if (m_min == 0 && m_max == 1)
            return "?";
        if (m_min == 1 && m_max == 1)
            return "";
        if (m_min == 0 && m_max == UNBOUNDED)
            return "*";
        if (m_min == 1 && m_max == UNBOUNDED)
            return "+";
        if (m_min == m_max)
            return "(" + m_min + ')';
        return "(" + m_min + ',' + (m_max == UNBOUNDED ? "*" : "" + m_max) + ')';
    }
}
