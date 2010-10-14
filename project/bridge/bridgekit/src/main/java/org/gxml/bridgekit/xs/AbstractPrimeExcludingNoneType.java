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

import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.types.SmPrimeType;

abstract class AbstractPrimeExcludingNoneType<A> extends AbstractType<A> implements SmPrimeType<A>
{
	public final boolean isNone()
	{
		return false;
	}

	public final SmQuantifier quantifier()
	{
		return SmQuantifier.EXACTLY_ONE;
	}
}
