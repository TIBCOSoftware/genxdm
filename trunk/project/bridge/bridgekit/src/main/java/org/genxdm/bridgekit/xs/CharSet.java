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
package org.genxdm.bridgekit.xs;

final class CharSet
{
	/**
	 * The set members come in pairs, and the pair with the smallest value must come first, and the largest value must
	 * come last.
	 * 
	 * @param setMembers
	 *            The members, grouped as pairs of values indicating ranges.
	 */
	public CharSet(char[] setMembers)
	{
		m_low = setMembers[0];
		m_high = setMembers[setMembers.length - 1] + 1;

		int intCount = ((m_high - m_low) + (BITS_TO_USE - 1)) / BITS_TO_USE;

		m_set = new int[intCount];

		char rangeLow;
		char rangeHigh;

		int idx = 0;
		while (idx < setMembers.length)
		{
			rangeLow = setMembers[idx++];
			rangeHigh = setMembers[idx++];

			for (char chVal = rangeLow; chVal <= rangeHigh; chVal++)
			{
				addMember(chVal);
			}
		}
	}

	private void addMember(char ch)
	{
		int offset = ch - m_low;
		int index = offset / BITS_TO_USE;
		int bitIdx = offset % BITS_TO_USE;
		int bitMask = BIT_FIELDS[bitIdx];

		m_set[index] = m_set[index] | bitMask;
	}

	/**
	 * Returns true if the indicated character is a member of the character set.
	 * 
	 * @param ch
	 *            The character to test.
	 * @return <code>true</code> if the character is a member.
	 */
	public boolean isMember(char ch)
	{

		boolean member = false;

		if (ch >= m_low && ch < m_high)
		{
			int offset = ch - m_low;
			int index = offset / BITS_TO_USE;
			int bitIdx = offset % BITS_TO_USE;
			int bitMask = BIT_FIELDS[bitIdx];

			member = ((m_set[index] & bitMask) != 0);
		}

		return member;
	}

	private int m_low;

	private int m_high;

	private int[] m_set;

	private static final int BITS_TO_USE = 31;

	/**
	 * Used to quickly determine bit field values.
	 */
	private static final int[] BIT_FIELDS = new int[] { 0x00000001, 0x00000002, 0x00000004, 0x00000008, 0x00000010, 0x00000020, 0x00000040, 0x00000080, 0x00000100, 0x00000200, 0x00000400, 0x00000800, 0x00001000, 0x00002000, 0x00004000, 0x00008000, 0x00010000, 0x00020000, 0x00040000, 0x00080000, 0x00100000, 0x00200000, 0x00400000, 0x00800000, 0x01000000, 0x02000000, 0x04000000, 0x08000000, 0x10000000, 0x20000000, 0x40000000, 0x80000000 };
}