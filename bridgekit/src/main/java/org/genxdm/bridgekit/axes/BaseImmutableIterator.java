/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.bridgekit.axes;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class BaseImmutableIterator<T> implements Iterator<T> {

    /**
     * Set up the iterator to return the first result.
     * 
     * @param firstResult The first result to return - subsequent results returned from this.
     */
    protected BaseImmutableIterator(T firstResult) {
        m_next = firstResult;
    }
    
    protected BaseImmutableIterator() {
        m_next = null;
    }
    
    /**
     * Meant to be called by subclasses that want to set the first result after the base
     * class has been constructed.
     * 
     * @param firstResult
     */
    protected void setFirstResult(T firstResult) {
        m_next = firstResult;
    }
    
    @Override
    public boolean hasNext() {
        return (null != m_next);
    }

    /**
     * Subclasses override this method to get from the current result to the next
     * result.
     * 
     * @param current Whatever the current result is.
     * 
     * @return The next result .
     */
    protected abstract T next(T current);
    
    @Override
    public T next() {
        if (m_next != null) {
            final T last = m_next;

            m_next = next(m_next);

            return last;
        }
        else {
            // The iteration has no more elements.
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private T m_next;
}
