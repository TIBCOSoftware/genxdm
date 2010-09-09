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
package org.gxml.processor.w3c.xs.exception;

import javax.xml.namespace.QName;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.resolve.SmLocation;

@SuppressWarnings("serial")
public final class SrcBaseContentTypeCannotBeSimpleException extends SmSourceComplexTypeException
{
	private final QName baseType;
	private final QName derivedType;

	public SrcBaseContentTypeCannotBeSimpleException(final QName derivedType, final QName baseType, final SmLocation location)
	{
		super(PART_BASE_CONTENT_CANNOT_BE_SIMPLE, location);
		this.derivedType = PreCondition.assertArgumentNotNull(derivedType, "derivedType");
		this.baseType = PreCondition.assertArgumentNotNull(baseType, "baseType");
	}

	public QName getBaseType()
	{
		return baseType;
	}

	public QName getDerivedType()
	{
		return derivedType;
	}

	@Override
	public String getMessage()
	{
		return "The complex type, '" + derivedType + "',  cannot be derived by extension from the complex type, '" + baseType + "', with simple content or simple type.";
	}
}