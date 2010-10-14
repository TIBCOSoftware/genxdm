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
package org.gxml.bridgekit.xs;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.exceptions.SmDatatypeException;
import org.genxdm.xs.types.SmSimpleType;

final class ISO8601
{
	enum State
	{
		START, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, FINISH;
	}

	public static <A> A parseGregorian(final String s, final AtomBridge<A> atomBridge, final SmSimpleType<A> type) throws SmDatatypeException
	{
		PreCondition.assertArgumentNotNull(s, "s");
		final int length = s.length();
		int i = 0;
		State state = State.START;
		while (i < length)
		{
			char ch = s.charAt(i++);
			switch (state)
			{
				case START:
				{
					switch (ch)
					{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
						{

						}
						break;
						case ':':
						{
							state = State.ONE;
						}
						break;
						case '-':
						{
							state = State.ONE;
						}
						break;
						default:
						{
							throw new SmDatatypeException(s, type);
							// throw new AssertionError(s + "[" + Integer.toString(i - 1) + "]");
						}
					}
				}
				break;
				case ONE:
				{
					switch (ch)
					{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
						{

						}
						break;
						case ':':
						{
							state = State.TWO;
						}
						break;
						case '-':
						{
							state = State.TWO;
						}
						break;
						default:
						{
							throw new SmDatatypeException(s, type);
							// throw new AssertionError(s + "[" + Integer.toString(i - 1) + "]");
						}
					}
				}
				break;
				case TWO:
				{
					switch (ch)
					{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
						{

						}
						break;
						case '.':
						{
							state = State.THREE;
						}
						break;
						case ':':
						{
							state = State.THREE;
						}
						break;
						case '+':
						{
							state = State.THREE;
						}
						break;
						case '-':
						{
							state = State.THREE;
						}
						break;
						case 'T':
						{
							state = State.THREE;
						}
						break;
						case 'Z':
						{
							state = State.THREE;
						}
						break;
						default:
						{
							throw new AssertionError(s + "[" + Integer.toString(i - 1) + "]");
						}
					}
				}
				break;
				case THREE:
				{
					switch (ch)
					{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
						{

						}
						break;
						case '-':
						{
							state = State.FOUR;
						}
						break;
						case ':':
						{
							state = State.FOUR;
						}
						break;
						case 'Z':
						{
							state = State.FOUR;
						}
						break;
						default:
						{
							throw new AssertionError(s + "[" + Integer.toString(i - 1) + "]");
						}
					}
				}
				break;
				case FOUR:
				{
					switch (ch)
					{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
						{

						}
						break;
						case ':':
						{
							state = State.FIVE;
						}
						break;
						default:
						{
							throw new SmDatatypeException(s, type);
							// throw new AssertionError(s + "[" + Integer.toString(i - 1) + "]");
						}
					}
				}
				break;
				case FIVE:
				{
					switch (ch)
					{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
						{

						}
						break;
						case '+':
						{
							// There are no fractional seconds, entering timezone.
							state = State.SIX;
						}
						break;
						case '-':
						{
							// There are no fractional seconds, entering timezone.
							state = State.SIX;
						}
						break;
						case '.':
						{
							state = State.SIX;
						}
						break;
						case 'Z':
						{
							state = State.SIX;
						}
						break;
						default:
						{
							throw new AssertionError(s + "[" + Integer.toString(i - 1) + "]");
						}
					}
				}
				break;
				case SIX:
				{
					switch (ch)
					{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
						{

						}
						break;
						case ':':
						{
							// Entering the mm portion of a timezone for xs:dateTime
							state = State.SEVEN;
						}
						break;
						case '-':
						{
							state = State.SEVEN;
						}
						break;
						default:
						{
							throw new AssertionError(s + "[" + Integer.toString(i - 1) + "]");
						}
					}
				}
				break;
				case SEVEN:
				{
					switch (ch)
					{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
						{

						}
						break;
						case ':':
						{
							state = State.FINISH;
						}
						break;
						default:
						{
							throw new AssertionError(s + "[" + Integer.toString(i - 1) + "]");
						}
					}
				}
				break;
				case FINISH:
				{
					switch (ch)
					{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
						{

						}
						break;
						default:
						{
							throw new AssertionError(s + "[" + Integer.toString(i - 1) + "]");
						}
					}
				}
				break;
				default:
				{
					throw new AssertionError(state);
				}
			}
		}
		if (i == length)
		{
			return null;
		}
		else
		{
			throw new SmDatatypeException(s, type);
		}
	}
}
