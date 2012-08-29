/**
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
package org.genxdm.processor.w3c.xs.regex.api;

/**
 * Assertions that generate consistent messages.
 */
final class PreCondition
{
    /**
     * Asserts that the specified object is not <code>null</code>
     *
     * @param object The object that is to be tested.
     * @param name   The name of the argument.
     * @return The input object.
     * @throws IllegalArgumentException if any of the arguments are null.
     */
    public static <T> T assertArgumentNotNull(final T object, final String name)
    {
        if (null != object)
        {
            return object;
        }
        else
        {
            throw new IllegalArgumentException(name + " cannot be null.");
        }
    }

    /**
     * Asserts that the specified object is not <code>null</code>
     *
     * @param object The object that is to be tested.
     * @return The input object.
     * @throws IllegalArgumentException if any of the arguments are null.
     */
    public static <T> T assertArgumentNotNull(final T object)
    {
        if (null != object)
        {
            return object;
        }
        else
        {
            throw new IllegalArgumentException("Argument cannot be null.");
        }
    }

    /**
     * Asserts that the specified condition is <code>true</code>
     */
    public static void assertTrue(final boolean condition, final String message)
    {
        if (!condition)
        {
            throw new AssertionError(message + " must be true.");
        }
    }

    /**
     * Asserts that the specified condition is <code>true</code>
     */
    public static void assertTrue(final boolean condition)
    {
        if (!condition)
        {
            throw new AssertionError();
        }
    }

    /**
     * Asserts that the specified condition is <code>false</code>
     */
    public static void assertFalse(final boolean condition, final String message)
    {
        if (condition)
        {
            throw new AssertionError(message + " must be false.");
        }
    }

    /**
     * Asserts that the specified condition is <code>false</code>
     */
    public static void assertFalse(final boolean condition)
    {
        if (condition)
        {
            throw new AssertionError();
        }
    }

    /**
     * Asserts that the specified object is <code>null</code>
     */
    public static <T> void assertNull(final T object, final String name)
    {
        if (null != object)
        {
            throw new AssertionError(name + " must be null.");
        }
    }

    /**
     * Asserts that the specified object is <code>null</code>
     */
    public static void assertNull(final Object obj)
    {
        if (null != obj)
        {
            throw new AssertionError();
        }
    }

    /**
     * Asserts that the specified object is not <code>null</code>
     */
    public static <T> T assertNotNull(final T object, final String name)
    {
        if (null != object)
        {
            return object;
        }
        else
        {
            throw new AssertionError(name + " cannot be null.");
        }
    }

    /**
     * Asserts that the specified object is not <code>null</code>
     */
    public static <T> T assertNotNull(final T obj)
    {
        if (null != obj)
        {
            return obj;
        }
        else
        {
            throw new AssertionError();
        }
    }
}
