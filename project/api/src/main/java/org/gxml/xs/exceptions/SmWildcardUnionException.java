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
package org.gxml.xs.exceptions;

import org.gxml.xs.enums.SmOutcome;

@SuppressWarnings("serial")
abstract public class SmWildcardUnionException extends SmComponentConstraintException
{
	public static final String PART_UNION_NOT_EXPRESSIBLE = "5.3";

	public SmWildcardUnionException(final String partNumber)
	{
		super(SmOutcome.SCC_Attribute_Wildcard_Union, partNumber);
	}
}
