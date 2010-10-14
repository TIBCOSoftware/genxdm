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

import org.genxdm.typed.types.AtomBridge;

final class OpXMLSchemaCompareFloat<A> implements SmValueComp<A>
{
	private final OpXMLSchemaCompare m_opcode;
	private final float floatR;
	private final AtomBridge<A> atomBridge;

	public OpXMLSchemaCompareFloat(final OpXMLSchemaCompare opcode, final A rhsAtom, final AtomBridge<A> atomBridge)
	{
		this.m_opcode = opcode;
		this.floatR = atomBridge.getFloat(rhsAtom);
		this.atomBridge = atomBridge;

	}

	public boolean compare(final A lhs)
	{
		final float floatL = atomBridge.getFloat(lhs);

		switch (m_opcode)
		{
			case Gt:
			{
				return floatL > floatR;
			}
			case Ge:
			{
				return floatL >= floatR;
			}
			case Lt:
			{
				return floatL < floatR;
			}
			case Le:
			{
				return floatL <= floatR;
			}
			default:
			{
				throw new AssertionError(m_opcode);
			}
		}
	}
}
