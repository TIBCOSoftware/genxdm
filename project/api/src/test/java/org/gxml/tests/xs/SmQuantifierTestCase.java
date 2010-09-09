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
package org.gxml.tests.xs;

import junit.framework.TestCase;

import org.gxml.xs.enums.SmQuantifier;

public final class SmQuantifierTestCase extends TestCase
{
	public void testToString()
	{
		assertEqualsQuan("none", SmQuantifier.NONE);
		assertEqualsQuan("empty", SmQuantifier.EMPTY);
		assertEqualsQuan("1", SmQuantifier.EXACTLY_ONE);
		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE);
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE);
		assertEqualsQuan("?", SmQuantifier.OPTIONAL);
	}

	public void testIsNone()
	{
		assertEquals(true, SmQuantifier.NONE.isNone());
		assertEquals(false, SmQuantifier.EMPTY.isNone());
		assertEquals(false, SmQuantifier.EXACTLY_ONE.isNone());
		assertEquals(false, SmQuantifier.ONE_OR_MORE.isNone());
		assertEquals(false, SmQuantifier.ZERO_OR_MORE.isNone());
		assertEquals(false, SmQuantifier.OPTIONAL.isNone());
	}

	public void testIsEmpty()
	{
		assertEquals(false, SmQuantifier.NONE.isEmpty());
		assertEquals(true, SmQuantifier.EMPTY.isEmpty());
		assertEquals(false, SmQuantifier.EXACTLY_ONE.isEmpty());
		assertEquals(false, SmQuantifier.ONE_OR_MORE.isEmpty());
		assertEquals(false, SmQuantifier.ZERO_OR_MORE.isEmpty());
		assertEquals(false, SmQuantifier.OPTIONAL.isEmpty());
	}

	public void testSum()
	{
		// none is the killer for the sum operation.
		assertEqualsQuan("none", SmQuantifier.NONE.sum(SmQuantifier.NONE));
		assertEqualsQuan("none", SmQuantifier.NONE.sum(SmQuantifier.EMPTY));
		assertEqualsQuan("none", SmQuantifier.NONE.sum(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("none", SmQuantifier.NONE.sum(SmQuantifier.OPTIONAL));
		assertEqualsQuan("none", SmQuantifier.NONE.sum(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("none", SmQuantifier.NONE.sum(SmQuantifier.ZERO_OR_MORE));

		// empty is the identity for the sum operation.
		assertEqualsQuan("none", SmQuantifier.EMPTY.sum(SmQuantifier.NONE));
		assertEqualsQuan("empty", SmQuantifier.EMPTY.sum(SmQuantifier.EMPTY));
		assertEqualsQuan("1", SmQuantifier.EMPTY.sum(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("?", SmQuantifier.EMPTY.sum(SmQuantifier.OPTIONAL));
		assertEqualsQuan("+", SmQuantifier.EMPTY.sum(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.EMPTY.sum(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("none", SmQuantifier.EXACTLY_ONE.sum(SmQuantifier.NONE));
		assertEqualsQuan("1", SmQuantifier.EXACTLY_ONE.sum(SmQuantifier.EMPTY));
		assertEqualsQuan("+", SmQuantifier.EXACTLY_ONE.sum(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("+", SmQuantifier.EXACTLY_ONE.sum(SmQuantifier.OPTIONAL));
		assertEqualsQuan("+", SmQuantifier.EXACTLY_ONE.sum(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("+", SmQuantifier.EXACTLY_ONE.sum(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("none", SmQuantifier.OPTIONAL.sum(SmQuantifier.NONE));
		assertEqualsQuan("?", SmQuantifier.OPTIONAL.sum(SmQuantifier.EMPTY));
		assertEqualsQuan("+", SmQuantifier.OPTIONAL.sum(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("*", SmQuantifier.OPTIONAL.sum(SmQuantifier.OPTIONAL));
		assertEqualsQuan("+", SmQuantifier.OPTIONAL.sum(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.OPTIONAL.sum(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("none", SmQuantifier.ONE_OR_MORE.sum(SmQuantifier.NONE));
		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE.sum(SmQuantifier.EMPTY));
		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE.sum(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE.sum(SmQuantifier.OPTIONAL));
		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE.sum(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE.sum(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("none", SmQuantifier.ZERO_OR_MORE.sum(SmQuantifier.NONE));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.sum(SmQuantifier.EMPTY));
		assertEqualsQuan("+", SmQuantifier.ZERO_OR_MORE.sum(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.sum(SmQuantifier.OPTIONAL));
		assertEqualsQuan("+", SmQuantifier.ZERO_OR_MORE.sum(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.sum(SmQuantifier.ZERO_OR_MORE));
	}

	public void testChoice()
	{
		// none is the identity for the choice operation.
		assertEqualsQuan("none", SmQuantifier.NONE.choice(SmQuantifier.NONE));
		assertEqualsQuan("empty", SmQuantifier.NONE.choice(SmQuantifier.EMPTY));
		assertEqualsQuan("1", SmQuantifier.NONE.choice(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("?", SmQuantifier.NONE.choice(SmQuantifier.OPTIONAL));
		assertEqualsQuan("+", SmQuantifier.NONE.choice(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.NONE.choice(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("none", SmQuantifier.EMPTY.choice(SmQuantifier.NONE));
		assertEqualsQuan("empty", SmQuantifier.EMPTY.choice(SmQuantifier.EMPTY));
		assertEqualsQuan("?", SmQuantifier.EMPTY.choice(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("?", SmQuantifier.EMPTY.choice(SmQuantifier.OPTIONAL));
		assertEqualsQuan("*", SmQuantifier.EMPTY.choice(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.EMPTY.choice(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("1", SmQuantifier.EXACTLY_ONE.choice(SmQuantifier.NONE));
		assertEqualsQuan("?", SmQuantifier.EXACTLY_ONE.choice(SmQuantifier.EMPTY));
		assertEqualsQuan("1", SmQuantifier.EXACTLY_ONE.choice(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("?", SmQuantifier.EXACTLY_ONE.choice(SmQuantifier.OPTIONAL));
		assertEqualsQuan("+", SmQuantifier.EXACTLY_ONE.choice(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.EXACTLY_ONE.choice(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("?", SmQuantifier.OPTIONAL.choice(SmQuantifier.NONE));
		assertEqualsQuan("?", SmQuantifier.OPTIONAL.choice(SmQuantifier.EMPTY));
		assertEqualsQuan("?", SmQuantifier.OPTIONAL.choice(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("?", SmQuantifier.OPTIONAL.choice(SmQuantifier.OPTIONAL));
		assertEqualsQuan("*", SmQuantifier.OPTIONAL.choice(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.OPTIONAL.choice(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE.choice(SmQuantifier.NONE));
		assertEqualsQuan("*", SmQuantifier.ONE_OR_MORE.choice(SmQuantifier.EMPTY));
		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE.choice(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("*", SmQuantifier.ONE_OR_MORE.choice(SmQuantifier.OPTIONAL));
		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE.choice(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.ONE_OR_MORE.choice(SmQuantifier.ZERO_OR_MORE));

		// * is the killer for the choice operation.
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.choice(SmQuantifier.NONE));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.choice(SmQuantifier.EMPTY));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.choice(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.choice(SmQuantifier.OPTIONAL));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.choice(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.choice(SmQuantifier.ZERO_OR_MORE));
	}

	public void testProduct()
	{
		// none is the killer for the product operation.
		assertEqualsQuan("none", SmQuantifier.NONE.product(SmQuantifier.NONE));
		assertEqualsQuan("none", SmQuantifier.NONE.product(SmQuantifier.EMPTY));
		assertEqualsQuan("none", SmQuantifier.NONE.product(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("none", SmQuantifier.NONE.product(SmQuantifier.OPTIONAL));
		assertEqualsQuan("none", SmQuantifier.NONE.product(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("none", SmQuantifier.NONE.product(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("none", SmQuantifier.EMPTY.product(SmQuantifier.NONE));
		assertEqualsQuan("empty", SmQuantifier.EMPTY.product(SmQuantifier.EMPTY));
		assertEqualsQuan("empty", SmQuantifier.EMPTY.product(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("empty", SmQuantifier.EMPTY.product(SmQuantifier.OPTIONAL));
		assertEqualsQuan("empty", SmQuantifier.EMPTY.product(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("empty", SmQuantifier.EMPTY.product(SmQuantifier.ZERO_OR_MORE));

		// 1 is the identity for the product operation.
		assertEqualsQuan("none", SmQuantifier.EXACTLY_ONE.product(SmQuantifier.NONE));
		assertEqualsQuan("empty", SmQuantifier.EXACTLY_ONE.product(SmQuantifier.EMPTY));
		assertEqualsQuan("1", SmQuantifier.EXACTLY_ONE.product(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("?", SmQuantifier.EXACTLY_ONE.product(SmQuantifier.OPTIONAL));
		assertEqualsQuan("+", SmQuantifier.EXACTLY_ONE.product(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.EXACTLY_ONE.product(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("none", SmQuantifier.OPTIONAL.product(SmQuantifier.NONE));
		assertEqualsQuan("empty", SmQuantifier.OPTIONAL.product(SmQuantifier.EMPTY));
		assertEqualsQuan("?", SmQuantifier.OPTIONAL.product(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("?", SmQuantifier.OPTIONAL.product(SmQuantifier.OPTIONAL));
		assertEqualsQuan("*", SmQuantifier.OPTIONAL.product(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.OPTIONAL.product(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("none", SmQuantifier.ZERO_OR_MORE.product(SmQuantifier.NONE));
		assertEqualsQuan("empty", SmQuantifier.ONE_OR_MORE.product(SmQuantifier.EMPTY));
		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE.product(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("*", SmQuantifier.ONE_OR_MORE.product(SmQuantifier.OPTIONAL));
		assertEqualsQuan("+", SmQuantifier.ONE_OR_MORE.product(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.ONE_OR_MORE.product(SmQuantifier.ZERO_OR_MORE));

		assertEqualsQuan("none", SmQuantifier.ZERO_OR_MORE.product(SmQuantifier.NONE));
		assertEqualsQuan("empty", SmQuantifier.ZERO_OR_MORE.product(SmQuantifier.EMPTY));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.product(SmQuantifier.EXACTLY_ONE));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.product(SmQuantifier.OPTIONAL));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.product(SmQuantifier.ONE_OR_MORE));
		assertEqualsQuan("*", SmQuantifier.ZERO_OR_MORE.product(SmQuantifier.ZERO_OR_MORE));
	}

	private void assertEqualsQuan(final String expectCard, final SmQuantifier actual)
	{
		final String actualCard = actual.toString();
		if (!expectCard.equals(actualCard))
		{
			System.out.println("expectCard=" + expectCard);
			System.out.println("actualCard=" + actualCard);
		}
		assertEquals(expectCard, actualCard);
	}
}
