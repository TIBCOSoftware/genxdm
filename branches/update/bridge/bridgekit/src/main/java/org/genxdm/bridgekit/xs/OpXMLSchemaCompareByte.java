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

import org.genxdm.typed.types.AtomBridge;

final class OpXMLSchemaCompareByte<A> implements ValueComparator<A>
{
	private final OpXMLSchemaCompare opcode;
	private final byte operandRHS;
	private final AtomBridge<A> atomBridge;

	public OpXMLSchemaCompareByte(final OpXMLSchemaCompare opcode, final A rhsAtom, final AtomBridge<A> atomBridge)
	{
		this.opcode = opcode;
		this.operandRHS = atomBridge.getByte(rhsAtom);
		this.atomBridge = atomBridge;
	}

	public boolean compare(final A lhsAtom)
	{
		final byte operandLHS = atomBridge.getByte(lhsAtom);

		switch (opcode)
		{
			case Gt:
			{
				return operandLHS > operandRHS;
			}
			case Ge:
			{
				return operandLHS >= operandRHS;
			}
			case Lt:
			{
				return operandLHS < operandRHS;
			}
			case Le:
			{
				return operandLHS <= operandRHS;
			}
			default:
			{
				throw new AssertionError(opcode);
			}
		}
	}
}
