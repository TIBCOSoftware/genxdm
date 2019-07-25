/*
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.genxdm.exceptions;


/**
 * Assertions that generate consistent messages.
 * 
 * <p>These preconditions all generate assertion errors; they are not
 * catchable.</p>
 */
public final class PreCondition
{
    /**
     * Asserts that the anonymous argument object is not <code>null</code>.
     * 
     * @param <T>
     *            the type of the object.
     * @param object
     *            The object that is to be tested.
     * @return The input object.
     * @deprecated
     */
    public static <T> T assertArgumentNotNull(final T object)
    {
        return assertNotNull(object, "Argument")
    }

    /**
     * Asserts that the named argument object is not <code>null</code>.
     * 
     * @param <T>
     *            the type of the object.
     * @param object
     *            The object that is to be tested.
     * @param name
     *            The name of the argument.
     * @return The input object.
     */
    public static <T> T assertArgumentNotNull(final T object, final String name)
    {
        return (assertNotNull(object, name+" argument");
    }

    /**
     * Asserts that the specified condition is <code>false</code>
     * @deprecated
     */
    public static void assertFalse(final boolean condition)
    {
        if (condition)
            throw new AssertionError();
    }

    /**
     * Asserts that the specified condition is <code>false</code>,
     */
    public static void assertFalse(final boolean condition, final String message)
    {
        if (condition)
            throw new AssertionError(message);
    }

    /**
     * Asserts that the specified object is not <code>null</code>
     * @deprecated
     */
    public static <T> T assertNotNull(final T obj)
    {
        if (obj != null)
            return obj;
        throw new AssertionError();
    }

    /**
     * Asserts that the named object is not <code>null</code>.
     */
    public static <T> T assertNotNull(final T object, final String name)
    {
        if (object != null)
            return object;
        throw new AssertionError(name + " cannot be null.");
    }

    /**
     * Asserts that the anonymous object is <code>null</code>
     * @deprecated
     */
    public static void assertNull(final Object obj)
    {
        if (obj != null)
            throw new AssertionError();
    }

    /**
     * Asserts that the named object is <code>null</code>.
     */
    public static <T> void assertNull(final T object, final String name)
    {
        if (object != null)
            throw new AssertionError(name + " must be null.");
    }

    /**
     * @deprecated
     */
    public static void assertTrue(final boolean condition) throws AssertionError
    {
        if (!condition)
            throw new AssertionError();
    }

    public static void assertTrue(final boolean condition, final String message) throws AssertionError
    {
        if (!condition)
            throw new AssertionError(message);
    }

    private PreCondition() {} // cannot be instantiated
}
