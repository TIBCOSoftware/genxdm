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

import java.util.NoSuchElementException;

/** 
 * Provides an implementation of an empty map iterator.
 *
 * @since 3.1
 */
public class EmptyMapIterator<K, V> implements MapIterator<K, V>, ResettableIterator<K> 
{

    /**
     * Singleton instance of the iterator.
     * @since 3.1
     */
    public static final MapIterator<Object, Object> INSTANCE = new EmptyMapIterator<Object, Object>();

    /**
     * Get a typed instance of the iterator.
     * @param <K> the key type
     * @param <V> the value type
     * @return {@link MapIterator}<K, V>
     */
    @SuppressWarnings("unchecked")
    public static <K, V> MapIterator<K, V> emptyMapIterator() {
        return (MapIterator<K, V>) INSTANCE;
    }

    /**
     * Constructor.
     */
    protected EmptyMapIterator() {
        super();
    }

    public boolean hasNext() {
        return false;
    }

    public K next() {
        throw new NoSuchElementException("Iterator contains no elements");
    }

    public boolean hasPrevious() {
        return false;
    }

    public K previous() {
        throw new NoSuchElementException("Iterator contains no elements");
    }

    public int nextIndex() {
        return 0;
    }

    public int previousIndex() {
        return -1;
    }

    public void add(K obj) {
        throw new UnsupportedOperationException("add() not supported for empty Iterator");
    }

    public void set(K obj) {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public void remove() {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public void reset() {
        // do nothing
    }
    public K getKey() {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public V getValue() {
        throw new IllegalStateException("Iterator contains no elements");
    }

    public V setValue(V value) {
        throw new IllegalStateException("Iterator contains no elements");
    }
}
