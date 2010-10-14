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
package org.gxml.processor.w3c.xs;

import org.genxdm.exceptions.PreCondition;

abstract class XMLFacet<A> extends XMLTag<A>
{
	private final XMLType<A> simpleType;

	public XMLFacet(final XMLType<A> simpleType, final SrcFrozenLocation location)
	{
		super(location);
		this.simpleType = PreCondition.assertArgumentNotNull(simpleType, "simpleType");
	}

	public final XMLType<A> getSimpleType()
	{
		return simpleType;
	}
}
