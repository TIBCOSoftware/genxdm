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

import java.math.BigInteger;

import org.genxdm.typed.types.AtomBridge;
import org.genxdm.exceptions.AtomCastException;
import org.genxdm.xs.types.NativeType;

public final class OpXMLSchemaCompareIntegerRestricted<A> implements ValueComparator<A>
{
    private final OpXMLSchemaCompare opcode;
    private final BigInteger operandRHS;
    private final AtomBridge<A> atomBridge;

    public OpXMLSchemaCompareIntegerRestricted(final OpXMLSchemaCompare opcode, final A rhsAtom, final NativeType nativeType, final AtomBridge<A> atomBridge)
    {
        this.opcode = opcode;
        this.atomBridge = atomBridge;
        operandRHS = atomBridge.getInteger(rhsAtom);
    }

    public boolean compare(final A lhsAtom) throws AtomCastException
    {
        final BigInteger operandLHS = atomBridge.getInteger(lhsAtom);
        switch (opcode)
        {
            case Gt:
            {
                return operandLHS.compareTo(operandRHS) > 0;
            }
            case Ge:
            {
                return operandLHS.compareTo(operandRHS) >= 0;
            }
            case Lt:
            {
                return operandLHS.compareTo(operandRHS) < 0;
            }
            case Le:
            {
                return operandLHS.compareTo(operandRHS) <= 0;
            }
            default:
            {
                throw new AssertionError(opcode);
            }
        }
    }
}
