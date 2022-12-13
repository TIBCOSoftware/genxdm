package org.genxdm.bridgekit.content;

import java.util.Comparator;

import org.genxdm.creation.Attrib;

public class AttribComparator
    implements Comparator<Attrib>
{

    public int compare(final Attrib att1, final Attrib att2)
    {
        final int comp = att1.getNamespace().compareTo(att2.getNamespace());
        return (comp != 0) ? comp : att1.getName().compareTo(att2.getName());
    }

}
