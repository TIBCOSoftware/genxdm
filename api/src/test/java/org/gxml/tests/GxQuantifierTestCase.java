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
package org.gxml.tests;

import junit.framework.TestCase;

import org.gxml.typed.types.Quantifier;

public class GxQuantifierTestCase extends TestCase
{
	/**
	 * {@link Quantifier#contains} is used to supports static type analysis.
	 */
	public void testContains()
	{
		assertTrue(Quantifier.NONE.contains(Quantifier.NONE));
		assertFalse(Quantifier.NONE.contains(Quantifier.EXACTLY_ONE));
		assertFalse(Quantifier.NONE.contains(Quantifier.ONE_OR_MORE));
		assertFalse(Quantifier.NONE.contains(Quantifier.EMPTY));
		assertFalse(Quantifier.NONE.contains(Quantifier.OPTIONAL));
		assertFalse(Quantifier.NONE.contains(Quantifier.ZERO_OR_MORE));

		assertTrue(Quantifier.EXACTLY_ONE.contains(Quantifier.NONE));
		assertTrue(Quantifier.EXACTLY_ONE.contains(Quantifier.EXACTLY_ONE));
		assertFalse(Quantifier.EXACTLY_ONE.contains(Quantifier.ONE_OR_MORE));
		assertFalse(Quantifier.EXACTLY_ONE.contains(Quantifier.EMPTY));
		assertFalse(Quantifier.EXACTLY_ONE.contains(Quantifier.OPTIONAL));
		assertFalse(Quantifier.EXACTLY_ONE.contains(Quantifier.ZERO_OR_MORE));

		assertTrue(Quantifier.ONE_OR_MORE.contains(Quantifier.NONE));
		assertTrue(Quantifier.ONE_OR_MORE.contains(Quantifier.EXACTLY_ONE));
		assertTrue(Quantifier.ONE_OR_MORE.contains(Quantifier.ONE_OR_MORE));
		assertFalse(Quantifier.ONE_OR_MORE.contains(Quantifier.EMPTY));
		assertFalse(Quantifier.ONE_OR_MORE.contains(Quantifier.OPTIONAL));
		assertFalse(Quantifier.ONE_OR_MORE.contains(Quantifier.ZERO_OR_MORE));

		assertTrue(Quantifier.EMPTY.contains(Quantifier.NONE));
		assertFalse(Quantifier.EMPTY.contains(Quantifier.EXACTLY_ONE));
		assertFalse(Quantifier.EMPTY.contains(Quantifier.ONE_OR_MORE));
		assertTrue(Quantifier.EMPTY.contains(Quantifier.EMPTY));
		assertFalse(Quantifier.EMPTY.contains(Quantifier.OPTIONAL));
		assertFalse(Quantifier.EMPTY.contains(Quantifier.ZERO_OR_MORE));

		assertTrue(Quantifier.OPTIONAL.contains(Quantifier.NONE));
		assertTrue(Quantifier.OPTIONAL.contains(Quantifier.EXACTLY_ONE));
		assertFalse(Quantifier.OPTIONAL.contains(Quantifier.ONE_OR_MORE));
		assertTrue(Quantifier.OPTIONAL.contains(Quantifier.EMPTY));
		assertTrue(Quantifier.OPTIONAL.contains(Quantifier.OPTIONAL));
		assertFalse(Quantifier.OPTIONAL.contains(Quantifier.ZERO_OR_MORE));

		assertTrue(Quantifier.ZERO_OR_MORE.contains(Quantifier.NONE));
		assertTrue(Quantifier.ZERO_OR_MORE.contains(Quantifier.EXACTLY_ONE));
		assertTrue(Quantifier.ZERO_OR_MORE.contains(Quantifier.ONE_OR_MORE));
		assertTrue(Quantifier.ZERO_OR_MORE.contains(Quantifier.EMPTY));
		assertTrue(Quantifier.ZERO_OR_MORE.contains(Quantifier.OPTIONAL));
		assertTrue(Quantifier.ZERO_OR_MORE.contains(Quantifier.ZERO_OR_MORE));
	}

	/**
	 * {@link Quantifier#sum} is used to supports static type analysis.
	 */
	public void testSum()
	{
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.EMPTY.sum(Quantifier.EMPTY));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.EMPTY.sum(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.EMPTY.sum(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.EMPTY.sum(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.EMPTY.sum(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.EXACTLY_ONE.sum(Quantifier.EMPTY));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.EXACTLY_ONE.sum(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.EXACTLY_ONE.sum(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.EXACTLY_ONE.sum(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.EXACTLY_ONE.sum(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.OPTIONAL.sum(Quantifier.EMPTY));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.OPTIONAL.sum(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.OPTIONAL.sum(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.OPTIONAL.sum(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.OPTIONAL.sum(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ONE_OR_MORE.sum(Quantifier.EMPTY));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ONE_OR_MORE.sum(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ONE_OR_MORE.sum(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ONE_OR_MORE.sum(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ONE_OR_MORE.sum(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.sum(Quantifier.EMPTY));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ZERO_OR_MORE.sum(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.sum(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ZERO_OR_MORE.sum(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.sum(Quantifier.ZERO_OR_MORE));
	}

	/**
	 * {@link Quantifier#choice} is used to supports static type analysis.
	 */
	public void testChoice()
	{
		assertEquals(Quantifier.EMPTY, Quantifier.EMPTY.choice(Quantifier.EMPTY));
		assertEquals(Quantifier.OPTIONAL, Quantifier.EMPTY.choice(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.OPTIONAL, Quantifier.EMPTY.choice(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.EMPTY.choice(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.EMPTY.choice(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.OPTIONAL, Quantifier.EXACTLY_ONE.choice(Quantifier.EMPTY));
		assertEquals(Quantifier.EXACTLY_ONE, Quantifier.EXACTLY_ONE.choice(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.OPTIONAL, Quantifier.EXACTLY_ONE.choice(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.EXACTLY_ONE.choice(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.EXACTLY_ONE.choice(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.OPTIONAL, Quantifier.OPTIONAL.choice(Quantifier.EMPTY));
		assertEquals(Quantifier.OPTIONAL, Quantifier.OPTIONAL.choice(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.OPTIONAL, Quantifier.OPTIONAL.choice(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.OPTIONAL.choice(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.OPTIONAL.choice(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ONE_OR_MORE.choice(Quantifier.EMPTY));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ONE_OR_MORE.choice(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ONE_OR_MORE.choice(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ONE_OR_MORE.choice(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ONE_OR_MORE.choice(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.choice(Quantifier.EMPTY));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.choice(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.choice(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.choice(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.choice(Quantifier.ZERO_OR_MORE));
	}

	/**
	 * {@link Quantifier#product} is used to supports static type analysis.
	 */
	public void testProduct()
	{
		assertEquals(Quantifier.EXACTLY_ONE, Quantifier.EXACTLY_ONE.product(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.OPTIONAL, Quantifier.EXACTLY_ONE.product(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.EXACTLY_ONE.product(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.EXACTLY_ONE.product(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.OPTIONAL, Quantifier.OPTIONAL.product(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.OPTIONAL, Quantifier.OPTIONAL.product(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.OPTIONAL.product(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.OPTIONAL.product(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ONE_OR_MORE.product(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ONE_OR_MORE.product(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ONE_OR_MORE, Quantifier.ONE_OR_MORE.product(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ONE_OR_MORE.product(Quantifier.ZERO_OR_MORE));

		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.product(Quantifier.EXACTLY_ONE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.product(Quantifier.OPTIONAL));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.product(Quantifier.ONE_OR_MORE));
		assertEquals(Quantifier.ZERO_OR_MORE, Quantifier.ZERO_OR_MORE.product(Quantifier.ZERO_OR_MORE));
	}
}
