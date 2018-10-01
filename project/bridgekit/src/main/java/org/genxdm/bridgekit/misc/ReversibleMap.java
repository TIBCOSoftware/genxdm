package org.genxdm.bridgekit.misc;

import java.util.Map;
import java.util.Set;

public interface ReversibleMap<K, V>
    extends Map<K, V>
{
    ReversibleMap<V, K> reverse();
    
    @Override
    Set<V> values();
}
