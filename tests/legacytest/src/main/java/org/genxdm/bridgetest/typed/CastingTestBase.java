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
package org.genxdm.bridgetest.typed;

import org.genxdm.bridgetest.GxTestBase;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.types.AtomBridge;

// TODO: perhaps we could do something useful, hmmm?
public abstract class CastingTestBase<N, A> 
    extends GxTestBase<N>
{
	public void testProlog()
	{
        final TypedContext<N, A> pcx = newProcessingContext().getTypedContext();

		final AtomBridge<A> atomBridge = pcx.getAtomBridge();

		assertNotNull(atomBridge);
	}
}