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
package org.genxdm.processor.w3c.xs.impl;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.facets.FacetKind;

final class XMLMinMaxFacet<A> extends XMLFacet<A>
{
	private final FacetKind m_kind;
	public final String elementName;
	public boolean fixed = false;
	public String value;

	public XMLMinMaxFacet(final FacetKind kind, final String elementName, final XMLType<A> simpleType, final SrcFrozenLocation location)
	{
		super(simpleType, location);
		this.m_kind = PreCondition.assertArgumentNotNull(kind);
		this.elementName = PreCondition.assertArgumentNotNull(elementName);
	}

	public FacetKind getOperator()
	{
		return m_kind;
	}
}
