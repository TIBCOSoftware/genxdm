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

public final class OpXMLSchemaCompareDouble<A> implements ValueComparator<A>
{
	private final OpXMLSchemaCompare m_opcode;
	private final double doubleR;
	private final AtomBridge<A> atomBridge;

	public OpXMLSchemaCompareDouble(final OpXMLSchemaCompare opcode, final A rhsAtom, final AtomBridge<A> atomBridge)
	{
		this.m_opcode = opcode;
		this.doubleR = atomBridge.getDouble(rhsAtom);
		this.atomBridge = atomBridge;

	}

	public boolean compare(final A lhs)
	{
		final double doubleL = atomBridge.getDouble(lhs);

		switch (m_opcode)
		{
			case Gt:
			{
				return doubleL > doubleR;
			}
			case Ge:
			{
				return doubleL >= doubleR;
			}
			case Lt:
			{
				return doubleL < doubleR;
			}
			case Le:
			{
				return doubleL <= doubleR;
			}
			default:
			{
				throw new AssertionError(m_opcode);
			}
		}
	}
}
