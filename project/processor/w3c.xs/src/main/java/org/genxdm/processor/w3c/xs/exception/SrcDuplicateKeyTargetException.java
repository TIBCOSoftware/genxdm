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

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.xs.resolve.LocationInSchema;


@SuppressWarnings("serial")
public final class SrcDuplicateKeyTargetException extends CvcIdentityConstraintException
{
	@SuppressWarnings("unchecked")
	private final List fields;

	@SuppressWarnings("unchecked")
	public SrcDuplicateKeyTargetException(final QName constraintName, final List fields, final LocationInSchema location)
	{
		super(constraintName, PART_TODO, location);
		this.fields = fields;
	}

	@Override
	public String getMessage()
	{
		return "Duplicate key " + fields + " for identity constraint " + getConstraintName() + " at " + getLocation() + ".";
	}
}
