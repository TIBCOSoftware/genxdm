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
package org.genxdm.processor.w3c.xs.exception;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.resolve.LocationInSchema;


@SuppressWarnings("serial")
public final class SmRedefineTypeSelfReferenceException extends SrcRedefinitionException
{
	private final QName expectName;
	private final QName actualName;

	public SmRedefineTypeSelfReferenceException(final QName expectName, final QName actualName, final LocationInSchema location)
	{
		super(PART_TYPE_SELF_REFERENCE, location);
		this.expectName = PreCondition.assertArgumentNotNull(expectName, "expectName");
		this.actualName = PreCondition.assertArgumentNotNull(actualName, "actualName");
	}

	public QName getExpectName()
	{
		return expectName;
	}

	public QName getActualName()
	{
		return actualName;
	}
}
