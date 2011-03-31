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
package org.genxdm.exceptions;

/**
 * Thrown to indicate that an argument which was <code>null</code> is illegal.
 * 
 * @author David Holmes
 */
@SuppressWarnings("serial")
public class IllegalNullArgumentException extends IllegalArgumentException
{
	/**
	 * Checks that the specified argument is not <code>null</code>.
	 * 
	 * @param <T>
	 *            The type of the argument.
	 * @param arg
	 *            The argument to be checked.
	 * @param argName
	 *            The name of the argument as it appears in the interface.
	 * @return The argument, if the argument is not <code>null</code>, otherwise nothing.
	 * @throws IllegalNullArgumentException
	 *             if the argument is null.
	 */
	public static <T> T check(final T arg, final String argName) throws IllegalNullArgumentException
	{
		if (argName != null)
		{
			if (arg != null)
			{
				return arg;
			}
			else
			{
				throw new IllegalNullArgumentException(argName);
			}
		}
		else
		{
			throw new IllegalNullArgumentException("argName");
		}
	}

	private static String messageArgumentCannotBeNull(final String argName)
	{
		assert (argName != null);
		return ((argName == null ? "Argument" : argName) + " cannot be null.");
	}

	/**
	 * <p>
	 * Instantiates with the given argument name.
	 * </p>
	 * 
	 * @param argName
	 *            the name of the argument that was <code>null</code>.
	 */
	public IllegalNullArgumentException(final String argName)
	{
		super(messageArgumentCannotBeNull(argName));
	}
}
