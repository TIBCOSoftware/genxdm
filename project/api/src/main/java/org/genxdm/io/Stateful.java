package org.genxdm.io;

public interface Stateful
{
    /**
     * Reset whatever state this artifact holds, so that it can be reused
     * as if it was just initialized.
     */
    void reset();
}
