/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.bridgekit.misc;

import java.util.Map;

/**
 * A restricted implementation of {@link Map.Entry} that prevents
 * the {@link Map.Entry} contract from being broken.
 *
 * @since 3.0
 */
public final class DefaultMapEntry<K, V> implements Map.Entry<K, V> {

    /**
     * Constructs a new entry from the specified <code>KeyValue</code>.
     *
     * @param pair  the pair to copy, must not be null
     * @throws NullPointerException if the entry is null
     */
    public DefaultMapEntry(final KeyValue<K, V> pair) {
        this(pair.getKey(), pair.getValue());
    }

    /**
     * Constructs a new entry from the specified <code>Map.Entry</code>.
     *
     * @param entry  the entry to copy, must not be null
     * @throws NullPointerException if the entry is null
     */
    public DefaultMapEntry(final Map.Entry<K, V> entry) {
        this(entry.getKey(), entry.getValue());
    }

    /**
     * Constructs a new pair with the specified key and given value.
     *
     * @param key  the key for the entry, may be null
     * @param value  the value for the entry, may be null
     */
    protected DefaultMapEntry(K key, V value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key from the pair.
     *
     * @return the key 
     */
    public K getKey() {
        return key;
    }

    /**
     * Gets the value from the pair.
     *
     * @return the value
     */
    public V getValue() {
        return value;
    }

    /**
     * Gets a debugging String view of the pair.
     * 
     * @return a String view of the entry
     */
    @Override
    public String toString() {
        return new StringBuilder()
            .append(getKey())
            .append('=')
            .append(getValue())
            .toString();
    }
    // Map.Entry interface
    //-------------------------------------------------------------------------
    /** 
     * Sets the value stored in this <code>Map.Entry</code>.
     * <p>
     * This <code>Map.Entry</code> is not connected to a Map, so only the
     * local data is changed.
     *
     * @param value  the new value
     * @return the previous value
     */
    public V setValue(V value) {
        V answer = this.value;
        this.value = value;
        return answer;
    }

    /**
     * Compares this <code>Map.Entry</code> with another <code>Map.Entry</code>.
     * <p>
     * Implemented per API documentation of {@link java.util.Map.Entry#equals(Object)}
     * 
     * @param obj  the object to compare to
     * @return true if equal key and value
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map.Entry == false) {
            return false;
        }
        Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
        return
            (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey())) &&
            (getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
    }

    /**
     * Gets a hashCode compatible with the equals method.
     * <p>
     * Implemented per API documentation of {@link java.util.Map.Entry#hashCode()}
     * 
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return (getKey() == null ? 0 : getKey().hashCode()) ^
               (getValue() == null ? 0 : getValue().hashCode()); 
    }

    /** The key */
    protected K key;
    /** The value */
    protected V value;

}
