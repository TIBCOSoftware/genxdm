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
package org.genxdm.apitest.xs;

import org.genxdm.xs.enums.KeeneQuantifier;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

// NOTE: disabled until we figure out why we have two of the same
// thing. see issue 39.

public final class QuantifierTestCase// extends TestCase
{
    @Test
    public void testDoNothing() {}
//	public void testToString()
//	{
//		assertEqualsQuan("none", KeeneQuantifier.NONE);
//		assertEqualsQuan("empty", KeeneQuantifier.EMPTY);
//		assertEqualsQuan("1", KeeneQuantifier.EXACTLY_ONE);
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE);
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE);
//		assertEqualsQuan("?", KeeneQuantifier.OPTIONAL);
//	}
//
//	public void testIsNone()
//	{
//		assertEquals(true, KeeneQuantifier.NONE.isNone());
//		assertEquals(false, KeeneQuantifier.EMPTY.isNone());
//		assertEquals(false, KeeneQuantifier.EXACTLY_ONE.isNone());
//		assertEquals(false, KeeneQuantifier.ONE_OR_MORE.isNone());
//		assertEquals(false, KeeneQuantifier.ZERO_OR_MORE.isNone());
//		assertEquals(false, KeeneQuantifier.OPTIONAL.isNone());
//	}
//
//	public void testIsEmpty()
//	{
//		assertEquals(false, KeeneQuantifier.NONE.isEmpty());
//		assertEquals(true, KeeneQuantifier.EMPTY.isEmpty());
//		assertEquals(false, KeeneQuantifier.EXACTLY_ONE.isEmpty());
//		assertEquals(false, KeeneQuantifier.ONE_OR_MORE.isEmpty());
//		assertEquals(false, KeeneQuantifier.ZERO_OR_MORE.isEmpty());
//		assertEquals(false, KeeneQuantifier.OPTIONAL.isEmpty());
//	}
//
//	public void testSum()
//	{
//		// none is the killer for the sum operation.
//		assertEqualsQuan("none", KeeneQuantifier.NONE.sum(KeeneQuantifier.NONE));
//		assertEqualsQuan("none", KeeneQuantifier.NONE.sum(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("none", KeeneQuantifier.NONE.sum(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("none", KeeneQuantifier.NONE.sum(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("none", KeeneQuantifier.NONE.sum(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("none", KeeneQuantifier.NONE.sum(KeeneQuantifier.ZERO_OR_MORE));
//
//		// empty is the identity for the sum operation.
//		assertEqualsQuan("none", KeeneQuantifier.EMPTY.sum(KeeneQuantifier.NONE));
//		assertEqualsQuan("empty", KeeneQuantifier.EMPTY.sum(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("1", KeeneQuantifier.EMPTY.sum(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("?", KeeneQuantifier.EMPTY.sum(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("+", KeeneQuantifier.EMPTY.sum(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.EMPTY.sum(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("none", KeeneQuantifier.EXACTLY_ONE.sum(KeeneQuantifier.NONE));
//		assertEqualsQuan("1", KeeneQuantifier.EXACTLY_ONE.sum(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("+", KeeneQuantifier.EXACTLY_ONE.sum(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("+", KeeneQuantifier.EXACTLY_ONE.sum(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("+", KeeneQuantifier.EXACTLY_ONE.sum(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("+", KeeneQuantifier.EXACTLY_ONE.sum(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("none", KeeneQuantifier.OPTIONAL.sum(KeeneQuantifier.NONE));
//		assertEqualsQuan("?", KeeneQuantifier.OPTIONAL.sum(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("+", KeeneQuantifier.OPTIONAL.sum(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("*", KeeneQuantifier.OPTIONAL.sum(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("+", KeeneQuantifier.OPTIONAL.sum(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.OPTIONAL.sum(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("none", KeeneQuantifier.ONE_OR_MORE.sum(KeeneQuantifier.NONE));
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE.sum(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE.sum(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE.sum(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE.sum(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE.sum(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("none", KeeneQuantifier.ZERO_OR_MORE.sum(KeeneQuantifier.NONE));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.sum(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("+", KeeneQuantifier.ZERO_OR_MORE.sum(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.sum(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("+", KeeneQuantifier.ZERO_OR_MORE.sum(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.sum(KeeneQuantifier.ZERO_OR_MORE));
//	}
//
//	public void testChoice()
//	{
//		// none is the identity for the choice operation.
//		assertEqualsQuan("none", KeeneQuantifier.NONE.choice(KeeneQuantifier.NONE));
//		assertEqualsQuan("empty", KeeneQuantifier.NONE.choice(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("1", KeeneQuantifier.NONE.choice(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("?", KeeneQuantifier.NONE.choice(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("+", KeeneQuantifier.NONE.choice(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.NONE.choice(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("none", KeeneQuantifier.EMPTY.choice(KeeneQuantifier.NONE));
//		assertEqualsQuan("empty", KeeneQuantifier.EMPTY.choice(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("?", KeeneQuantifier.EMPTY.choice(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("?", KeeneQuantifier.EMPTY.choice(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("*", KeeneQuantifier.EMPTY.choice(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.EMPTY.choice(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("1", KeeneQuantifier.EXACTLY_ONE.choice(KeeneQuantifier.NONE));
//		assertEqualsQuan("?", KeeneQuantifier.EXACTLY_ONE.choice(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("1", KeeneQuantifier.EXACTLY_ONE.choice(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("?", KeeneQuantifier.EXACTLY_ONE.choice(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("+", KeeneQuantifier.EXACTLY_ONE.choice(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.EXACTLY_ONE.choice(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("?", KeeneQuantifier.OPTIONAL.choice(KeeneQuantifier.NONE));
//		assertEqualsQuan("?", KeeneQuantifier.OPTIONAL.choice(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("?", KeeneQuantifier.OPTIONAL.choice(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("?", KeeneQuantifier.OPTIONAL.choice(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("*", KeeneQuantifier.OPTIONAL.choice(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.OPTIONAL.choice(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE.choice(KeeneQuantifier.NONE));
//		assertEqualsQuan("*", KeeneQuantifier.ONE_OR_MORE.choice(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE.choice(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("*", KeeneQuantifier.ONE_OR_MORE.choice(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE.choice(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.ONE_OR_MORE.choice(KeeneQuantifier.ZERO_OR_MORE));
//
//		// * is the killer for the choice operation.
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.choice(KeeneQuantifier.NONE));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.choice(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.choice(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.choice(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.choice(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.choice(KeeneQuantifier.ZERO_OR_MORE));
//	}
//
//	public void testProduct()
//	{
//		// none is the killer for the product operation.
//		assertEqualsQuan("none", KeeneQuantifier.NONE.product(KeeneQuantifier.NONE));
//		assertEqualsQuan("none", KeeneQuantifier.NONE.product(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("none", KeeneQuantifier.NONE.product(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("none", KeeneQuantifier.NONE.product(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("none", KeeneQuantifier.NONE.product(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("none", KeeneQuantifier.NONE.product(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("none", KeeneQuantifier.EMPTY.product(KeeneQuantifier.NONE));
//		assertEqualsQuan("empty", KeeneQuantifier.EMPTY.product(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("empty", KeeneQuantifier.EMPTY.product(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("empty", KeeneQuantifier.EMPTY.product(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("empty", KeeneQuantifier.EMPTY.product(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("empty", KeeneQuantifier.EMPTY.product(KeeneQuantifier.ZERO_OR_MORE));
//
//		// 1 is the identity for the product operation.
//		assertEqualsQuan("none", KeeneQuantifier.EXACTLY_ONE.product(KeeneQuantifier.NONE));
//		assertEqualsQuan("empty", KeeneQuantifier.EXACTLY_ONE.product(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("1", KeeneQuantifier.EXACTLY_ONE.product(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("?", KeeneQuantifier.EXACTLY_ONE.product(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("+", KeeneQuantifier.EXACTLY_ONE.product(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.EXACTLY_ONE.product(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("none", KeeneQuantifier.OPTIONAL.product(KeeneQuantifier.NONE));
//		assertEqualsQuan("empty", KeeneQuantifier.OPTIONAL.product(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("?", KeeneQuantifier.OPTIONAL.product(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("?", KeeneQuantifier.OPTIONAL.product(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("*", KeeneQuantifier.OPTIONAL.product(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.OPTIONAL.product(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("none", KeeneQuantifier.ZERO_OR_MORE.product(KeeneQuantifier.NONE));
//		assertEqualsQuan("empty", KeeneQuantifier.ONE_OR_MORE.product(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE.product(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("*", KeeneQuantifier.ONE_OR_MORE.product(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("+", KeeneQuantifier.ONE_OR_MORE.product(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.ONE_OR_MORE.product(KeeneQuantifier.ZERO_OR_MORE));
//
//		assertEqualsQuan("none", KeeneQuantifier.ZERO_OR_MORE.product(KeeneQuantifier.NONE));
//		assertEqualsQuan("empty", KeeneQuantifier.ZERO_OR_MORE.product(KeeneQuantifier.EMPTY));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.product(KeeneQuantifier.EXACTLY_ONE));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.product(KeeneQuantifier.OPTIONAL));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.product(KeeneQuantifier.ONE_OR_MORE));
//		assertEqualsQuan("*", KeeneQuantifier.ZERO_OR_MORE.product(KeeneQuantifier.ZERO_OR_MORE));
//	}
//
//	private void assertEqualsQuan(final String expectCard, final KeeneQuantifier actual)
//	{
//		final String actualCard = actual.toString();
//		if (!expectCard.equals(actualCard))
//		{
//			System.out.println("expectCard=" + expectCard);
//			System.out.println("actualCard=" + actualCard);
//		}
//		assertEquals(expectCard, actualCard);
//	}
}
