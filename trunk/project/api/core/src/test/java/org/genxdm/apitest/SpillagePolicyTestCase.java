/*
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.genxdm.apitest;

import org.genxdm.exceptions.SpillagePolicy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SpillagePolicyTestCase
{

    @Test
    public void lookups()
    {
        // do the right thing checks capacity and raises errors.
        assertEquals(SpillagePolicy.DO_THE_RIGHT_THING, SpillagePolicy.lookup(true, true));
        // sweep under the rug checks capacity, and ignores errors.
        assertEquals(SpillagePolicy.SWEEP_UNDER_THE_RUG, SpillagePolicy.lookup(true, false));
        // no checking + error raising causes an assertion error.  interesting ... denial of service avenue?
        // no checking, no errors raised is hope for the best.
        assertEquals(SpillagePolicy.HOPE_FOR_THE_BEST, SpillagePolicy.lookup(false, false));
    }
    
    @Test
    public void checksAndRaises()
    {
        assertTrue(SpillagePolicy.DO_THE_RIGHT_THING.checkCapacity());
        assertTrue(SpillagePolicy.DO_THE_RIGHT_THING.raiseError());
        assertTrue(SpillagePolicy.SWEEP_UNDER_THE_RUG.checkCapacity());
        assertFalse(SpillagePolicy.SWEEP_UNDER_THE_RUG.raiseError());
        assertFalse(SpillagePolicy.HOPE_FOR_THE_BEST.checkCapacity());
        assertFalse(SpillagePolicy.HOPE_FOR_THE_BEST.raiseError());
    }
}
