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
package org.genxdm.bridgekit.xs.operations;

import org.genxdm.typed.types.AtomBridge;

public final class OpXMLSchemaCompareInt<A> implements ValueComparator<A>
{
    private final OpXMLSchemaCompare m_opcode;
    private final int intR;
    private final AtomBridge<A> atomBridge;

    public OpXMLSchemaCompareInt(final OpXMLSchemaCompare opcode, final A rhsAtom, final AtomBridge<A> atomBridge)
    {
        this.m_opcode = opcode;
        this.intR = atomBridge.getInt(rhsAtom);
        this.atomBridge = atomBridge;
    }

    public boolean compare(final A lhsAtom)
    {
        final int intL = atomBridge.getInt(lhsAtom);

        switch (m_opcode)
        {
            case Gt:
            {
                return intL > intR;
            }
            case Ge:
            {
                return intL >= intR;
            }
            case Lt:
            {
                return intL < intR;
            }
            case Le:
            {
                return intL <= intR;
            }
            default:
            {
                throw new AssertionError(m_opcode);
            }
        }
    }
}
