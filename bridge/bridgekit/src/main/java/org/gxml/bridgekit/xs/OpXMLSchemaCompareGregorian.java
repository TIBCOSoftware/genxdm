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
package org.gxml.bridgekit.xs;

import java.math.BigDecimal;

import javax.xml.datatype.DatatypeConstants;

import org.genxdm.typed.types.AtomBridge;

public final class OpXMLSchemaCompareGregorian<A> implements SmValueComp<A>
{
	private static int FOURTEEN_HOURS_IN_MINUTES = 840;

	private final OpXMLSchemaCompare m_opcode;
	private final Gregorian operandRHS;
	private final AtomBridge<A> atomBridge;

	public OpXMLSchemaCompareGregorian(final OpXMLSchemaCompare opcode, final A rhsAtom, final AtomBridge<A> atomBridge)
	{
		this.m_opcode = opcode;
		this.operandRHS = Gregorian.dateTime(rhsAtom, atomBridge);
		this.atomBridge = atomBridge;
	}

	public boolean compare(final A lhsAtom)
	{
		final Gregorian operandLHS = Gregorian.dateTime(lhsAtom, atomBridge);

		switch (m_opcode)
		{
			case Gt:
			{
				return compare(operandLHS, operandRHS, +1) > 0;
			}
			case Ge:
			{
				return compare(operandLHS, operandRHS, +1) >= 0;
			}
			case Lt:
			{
				return compare(operandLHS, operandRHS, -1) < 0;
			}
			case Le:
			{
				return compare(operandLHS, operandRHS, -1) <= 0;
			}
			default:
			{
				throw new RuntimeException(m_opcode.toString());
			}
		}
	}

	public static int compare(final Gregorian L, final Gregorian R, final int sign)
	{
		if (DatatypeConstants.FIELD_UNDEFINED != L.getGmtOffset())
		{
			if (DatatypeConstants.FIELD_UNDEFINED != R.getGmtOffset())
			{
				final BigDecimal thisTime = L.getTimeSpanSinceEpoch();
				final BigDecimal thatTime = R.getTimeSpanSinceEpoch();

				return thisTime.compareTo(thatTime);
			}
			else
			{
				final BigDecimal thisTime = L.getTimeSpanSinceEpoch();
				final BigDecimal thatTime = R.normalize(sign * -FOURTEEN_HOURS_IN_MINUTES).getTimeSpanSinceEpoch();

				return thisTime.compareTo(thatTime);
			}
		}
		else
		{
			if (DatatypeConstants.FIELD_UNDEFINED != R.getGmtOffset())
			{
				final BigDecimal thisTime = L.normalize(sign * +FOURTEEN_HOURS_IN_MINUTES).getTimeSpanSinceEpoch();
				final BigDecimal thatTime = R.getTimeSpanSinceEpoch();

				return thisTime.compareTo(thatTime);
			}
			else
			{
				final BigDecimal thisTime = L.normalize(0).getTimeSpanSinceEpoch();
				final BigDecimal thatTime = R.normalize(0).getTimeSpanSinceEpoch();

				return thisTime.compareTo(thatTime);
			}
		}
	}
}
