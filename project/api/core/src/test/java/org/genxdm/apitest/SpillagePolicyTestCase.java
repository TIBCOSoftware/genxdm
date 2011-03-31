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
