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
package org.genxdm.bridgetest;

import javax.xml.namespace.QName;

import org.genxdm.ProcessingContext;
import org.genxdm.names.NameSource;
import org.genxdm.xs.types.NativeType;

// TODO: either move the atom bridge test to a new typedtiming, or make this a typed test.
public abstract class TimingTestBase<N> 
    extends GxTestBase<N>
{
	/**
	 * Allow disabling so as not to impact other unit tests.
	 */
	private boolean ENABLED = false;
	private static final long ATOMBRIDGE_MAX_LOAD_TIME = 1000;
	private static final long NAMEBRIDGE_MAX_LOAD_TIME = 400;
	private static final long PCX_MAX_LOAD_TIME = 2000;
	private static final int REPETITIONS = 10000;
	private static final int CALL_REPETITIONS = 100000;

//	protected abstract AtomBridge<A> newAtomBridge();

	protected abstract NameSource newNameBridge();

//	public void testAtomBridgeLoadTime()
//	{
//		if (ENABLED)
//		{
//			final long start = System.currentTimeMillis();
//			for (int i = 0; i < REPETITIONS; i++)
//			{
//				@SuppressWarnings("unused")
//				final AtomBridge<A> nameBridge = newAtomBridge();
//			}
//			final long end = System.currentTimeMillis();
//			final long time = end - start;
//			System.out.println(getClass().getName() + " GxAtomBridge: " + Long.toString(time) + "ms for " + Integer.toString(REPETITIONS));
//			assertTrue("GxAtomBridge load time exceeds performance requirements.", time < ATOMBRIDGE_MAX_LOAD_TIME);
//		}
//	}

	public void testNameBridgeLoadTime()
	{
		if (ENABLED)
		{
			final long start = System.currentTimeMillis();
			for (int i = 0; i < REPETITIONS; i++)
			{
				@SuppressWarnings("unused")
				final NameSource nameBridge = newNameBridge();
			}
			final long end = System.currentTimeMillis();
			final long time = end - start;
			System.out.println(getClass().getName() + " GxNameBridge: " + Long.toString(time) + "ms for " + Integer.toString(REPETITIONS));
			assertTrue("GxNameBridge load time exceeds performance requirements.", time < NAMEBRIDGE_MAX_LOAD_TIME);
		}
	}

	public void testProcessingContextLoadTime()
	{
		if (ENABLED)
		{
			final long start = System.currentTimeMillis();
			for (int i = 0; i < REPETITIONS; i++)
			{
				@SuppressWarnings("unused")
				final ProcessingContext<N> pcx = newProcessingContext();
			}
			final long end = System.currentTimeMillis();
			final long time = end - start;
			System.out.println(getClass().getName() + " GxProcessingContext: " + Long.toString(time) + "ms for " + Integer.toString(REPETITIONS));
			assertTrue("GxProcessingContext load time exceeds performance requirements.", time < PCX_MAX_LOAD_TIME);
		}
	}

	public void testNameBridge0001()
	{
		if (ENABLED)
		{
			final NameSource nameBridge = new NameSource();
			final long start = System.currentTimeMillis();
			for (int i = 0; i < CALL_REPETITIONS; i++)
			{
				for (final NativeType nativeType : NativeType.values())
				{
					final QName name = nameBridge.nativeType(nativeType);
					assertNotNull(name);
					assertNotNull(name.getNamespaceURI());
					assertNotNull(name.getLocalPart());
				}
			}
			final long end = System.currentTimeMillis();
			final long time = end - start;
			System.out.println(getClass().getName() + " .nativeType(NativeType): " + Long.toString(time) + "ms for " + Integer.toString(CALL_REPETITIONS));
			// assertTrue("GxProcessingContext load time exceeds performance requirements.", time < PCX_MAX_LOAD_TIME);
		}
	}

	public void testNameBridge0002()
	{
		if (ENABLED)
		{
			final long start = System.currentTimeMillis();
			for (int i = 0; i < CALL_REPETITIONS; i++)
			{
				for (final NativeType nativeType : NativeType.values())
				{
					final QName name = nativeType.toQName();
					assertNotNull(name);
					assertNotNull(name.getNamespaceURI());
					assertNotNull(name.getLocalPart());
				}
			}
			final long end = System.currentTimeMillis();
			final long time = end - start;
			System.out.println(getClass().getName() + " .nativeType(NativeType): " + Long.toString(time) + "ms for " + Integer.toString(CALL_REPETITIONS));
			// assertTrue("GxProcessingContext load time exceeds performance requirements.", time < PCX_MAX_LOAD_TIME);
		}
	}
}
