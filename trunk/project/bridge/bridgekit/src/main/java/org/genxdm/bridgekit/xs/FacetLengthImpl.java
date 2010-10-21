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
package org.genxdm.bridgekit.xs;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.exceptions.FacetException;
import org.genxdm.xs.exceptions.FacetLengthException;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.facets.SmLength;
import org.genxdm.xs.facets.SmLengthFacetUOM;

public final class FacetLengthImpl<A> extends FacetLengthCommonImpl<A> implements SmLength<A>
{
	private final int length;

	public FacetLengthImpl(final int length, final boolean isFixed, final AtomBridge<A> atomBridge)
	{
		super(isFixed, atomBridge, SmFacetKind.Length);
		PreCondition.assertTrue(length >= 0, "length >= 0");
		this.length = length;
	}

	protected void checkLength(final int length, final SmLengthFacetUOM uom) throws FacetException
	{
		if (length != this.length)
		{
			throw new FacetLengthException(this, length);
		}
	}

	public int getValue()
	{
		return length;
	}
}
