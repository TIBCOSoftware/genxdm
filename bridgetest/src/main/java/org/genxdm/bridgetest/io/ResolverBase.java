/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
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
package org.genxdm.bridgetest.io;

import org.genxdm.bridgetest.TestBase;
import org.genxdm.io.Resolver;
import org.junit.Ignore;
import org.junit.Test;

/** Test the contract of Resolver and Resolved.
 *
 * @author aaletal@gmail.com
 */
abstract public class ResolverBase<N>
    extends TestBase<N>
{
    /** Bridges testing resolution must implement.
     */
    abstract public Resolver newResolver();
    
    /**
     * Contract: given a URI to a known-good location, verify that
     * a Resolved<InputStream> is returned with properly initialized fields.
     * Depends on sharedResolvedTest.
     */
    @Test
    @Ignore
    public void testResolveInputStream()
    {
        Resolver resolver = newResolver();
    }
    
    /**
     * Contract: given a URI to a known-good location, verify that
     * a Resolved<Reader> is returned with properly initialized fields.
     * Depends on sharedResolvedTest.
     */
    @Test
    @Ignore
    public void testResolveReader()
    {
        Resolver resolver = newResolver();
    }
    
    /**
     * Contract: verify that all public methods return non-null when the
     * instance is properly created. verify that the parameters passed are
     * the parameters retrieved. verify that the instance cannot be created
     * incomplete.
     */
    @Test
    @Ignore
    public void testInitializeResolved()
    {
    }
    
//    private <E> void sharedResolvedTest(Resolved<E> totest, URI loc, URI systemId, E resource)
//    {
//        assertNotNull("Resolved to test", totest);
//        assertNotNull("location URI", loc);
//        assertNotNull("system ID URI", systemId);
//        assertNotNull("resource", resource);
//        assertEquals("location eq totest.getLocation", loc, totest.getLocation());
//        assertEquals("systemId eq totest.getSystemId", systemId, totest.getSystemId());
//        assertEquals("resource eq totest.getResource", resource, totest.getResource());
//    }
}
