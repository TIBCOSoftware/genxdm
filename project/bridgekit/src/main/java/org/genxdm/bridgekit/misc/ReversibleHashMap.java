package org.genxdm.bridgekit.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReversibleHashMap<K, V>
    implements ReversibleMap<K, V>
{
    public ReversibleHashMap() 
    { 
        kToV = new HashMap<K, V>();
        vToK = new HashMap<V, K>();
    }
    
    private ReversibleHashMap(ReversibleHashMap<V, K> source)
    {
        kToV = source.vToK;
        vToK = source.kToV;
    }

    @Override
    public int size()
    {
        return kToV.size();
    }

    @Override
    public boolean isEmpty()
    {
        return kToV.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return kToV.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return vToK.containsKey(value);
    }

    @Override
    public V get(Object key)
    {
        return kToV.get(key);
    }

    @Override
    public V put(K key, V value)
    {
// minor note: multiple eclipse crashes while writing this (very annoying)
// i'm not sure of my logic, so double-check later.
        // this has to guarantee sync. null is not accepted
        if ( (key == null) || (value == null) )
            return null;
        final V prevVal = kToV.get(key);
        if ( (prevVal != null) && !prevVal.equals(value) ) // changing a value
        { 
            if (vToK.containsKey(value)) // that value is already a key in the reverse map
                throw new IllegalArgumentException("ReversibleMap.put("+key+","+value+") would result in desync: value is already key in reverse map");
            // this means that you have to remove the old value first
        }
        final K prevKey = vToK.get(prevVal);
        if ( (prevKey == null) && (prevVal == null) ) // new entry, both sides
        {
            kToV.put(key, value);
            vToK.put(value, key);
        }
        else if ( key.equals(prevKey) && value.equals(prevVal) ) // no-op; same thing
        {
        }
        else // key is non-null, value is non-null
        {    // if key equals prevKey, prevVal is null; if key !equals prevKey, we already threw
            kToV.remove(prevKey);
            vToK.remove(prevVal);
            kToV.put(key, value);
            vToK.put(value, key);
        }
        return prevVal;
    }

    @Override
    public V remove(Object key)
    {
        V value = kToV.remove(key);
        if (value != null)
            vToK.remove(value);
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m)
    {
        throw new UnsupportedOperationException("Not doing this, sorry");
    }

    @Override
    public void clear()
    {
        kToV.clear();
        vToK.clear();
    }

    @Override
    public Set<K> keySet()
    {
        return kToV.keySet();
    }

    @Override
    public Set<V> values()
    {
        return vToK.keySet();
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet()
    {
        return kToV.entrySet();
    }

    @Override
    public ReversibleMap<V, K> reverse()
    {
        return new ReversibleHashMap<V, K>(this);
    }

    private final Map<K, V> kToV;
    private final Map<V, K> vToK;
}
