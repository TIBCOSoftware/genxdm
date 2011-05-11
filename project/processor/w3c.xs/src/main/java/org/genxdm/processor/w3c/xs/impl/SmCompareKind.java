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
package org.genxdm.processor.w3c.xs.impl;

/**
 * Enumeration representing possible results of comparisons.
 */
enum SmCompareKind
{
	/**
	 * compare(a,b) => {@link #BEFORE} iff a is before b in the ordering.
	 */
	BEFORE,
	/**
	 * compare(a,b) => {@link #EQUAL} iff a is equal to b in the ordering.
	 */
	EQUAL,
	/**
	 * compare(a,b) => {@link #AFTER} iff a is after b in the ordering.
	 */
	AFTER,
	/**
	 * compare(a,b) => {@link #INDETERMINATE} iff the ordering of a and b cannot be determined.
	 */
	INDETERMINATE;

	/**
	 * Returns <code>true</code> if the ordering can be determined, otherwise <code>false</code>.
	 * <table border="1">
	 * <tr>
	 * <th>{@link SmCompareKind}</th>
	 * <th>{@link #isDeterminate}</th>
	 * </tr>
	 * <tr>
	 * <td>{@link #BEFORE}</td>
	 * <td><code>true</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #EQUAL}</td>
	 * <td><code>true</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #AFTER}</td>
	 * <td><code>true</code></td>
	 * </tr>
	 * <tr>
	 * <td>{@link #INDETERMINATE}</td>
	 * <td><code>false</code></td>
	 * </tr>
	 * </table>
	 */
	public boolean isDeterminate()
	{
		return (this != INDETERMINATE);
	}

	/**
	 * Returns an integer value according to the following table:
	 * <table border="1">
	 * <tr>
	 * <th>{@link SmCompareKind}</th>
	 * <th>signum</th>
	 * </tr>
	 * <tr>
	 * <td>{@link #BEFORE}</td>
	 * <td>-1</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #EQUAL}</td>
	 * <td>0</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #AFTER}</td>
	 * <td>+1</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #INDETERMINATE}</td>
	 * <td>throws {@link AssertionError}</td>
	 * </tr>
	 * </table>
	 */
	public int signum()
	{
		if (isDeterminate())
		{
			return ordinal() - 1;
		}
		else
		{
			throw new AssertionError(INDETERMINATE);
		}
	}

	/**
	 * Converts a signum value into an enumeration value according to the following table:
	 * <table border="1">
	 * <tr>
	 * <th>lookup</th>
	 * <th>signum</th>
	 * </tr>
	 * <tr>
	 * <td>{@link #BEFORE}</td>
	 * <td>negative</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #EQUAL}</td>
	 * <td>zero (0)</td>
	 * </tr>
	 * <tr>
	 * <td>{@link #AFTER}</td>
	 * <td>positive</td>
	 * </tr>
	 * </table>
	 */
	public static SmCompareKind lookup(final int signum)
	{
		if (signum > 0)
		{
			return AFTER;
		}
		else if (signum < 0)
		{
			return BEFORE;
		}
		else
		{
			return EQUAL;
		}
	}
}
