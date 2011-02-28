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
package org.genxdm.xs.exceptions;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;

/**
 * Raised when a cast fails from a source value to a target type.
 */
@SuppressWarnings("serial")
public class AtomCastException extends Exception
{
	private final QName errorCode;
	private final String sourceValue;
	private final QName targetType;

	public AtomCastException(final String sourceValue, final QName targetType, final QName errorCode)
	{
		this.sourceValue = PreCondition.assertArgumentNotNull(sourceValue, "sourceValue");
		this.targetType = PreCondition.assertArgumentNotNull(targetType, "targetType");
		this.errorCode = PreCondition.assertArgumentNotNull(errorCode, "errorCode");
	}

	public AtomCastException(final String sourceValue, final QName targetType, final QName errorCode, final Throwable cause)
	{
		super(cause);
		this.sourceValue = PreCondition.assertArgumentNotNull(sourceValue, "sourceValue");
		this.targetType = PreCondition.assertArgumentNotNull(targetType, "targetType");
		this.errorCode = PreCondition.assertArgumentNotNull(errorCode, "errorCode");
	}

	public QName getErrorCode()
	{
		return errorCode;
	}

	@Override
	public String getMessage()
	{
		return "sourceValue=" + sourceValue + ", targetType='" + targetType + "', errorCode='" + errorCode + "'";
	}

	public String getSourceValue()
	{
		return sourceValue;
	}

	public QName getTargetType()
	{
		return targetType;
	}
}
