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
package org.gxml.exceptions;

import javax.xml.namespace.QName;

import org.gxml.xs.types.SmNativeType;

/**
 * Exception arising during the casting or parsing of atomic values.
 * <p/>
 * Parsing is to be regarded as a cast from xs:untypedAtomic.
 * 
 * @author David Holmes
 */
@SuppressWarnings("serial")
public final class GxmlAtomCastException extends Exception
{
	private final QName errorCode;
	private final QName sourceType;
	private final String sourceValue;
	private final QName targetType;

	public GxmlAtomCastException(final String sourceValue, final QName targetType, final QName errorCode)
	{
		this(sourceValue, SmNativeType.UNTYPED_ATOMIC.toQName(), targetType, errorCode);
	}

	public GxmlAtomCastException(final String sourceValue, final QName sourceType, final QName targetType, final QName errorCode)
	{
		this.sourceValue = PreCondition.assertArgumentNotNull(sourceValue, "sourceValue");
		this.sourceType = PreCondition.assertArgumentNotNull(sourceType, "sourceType");
		this.targetType = PreCondition.assertArgumentNotNull(targetType, "targetType");
		this.errorCode = PreCondition.assertArgumentNotNull(errorCode, "errorCode");
	}

	public GxmlAtomCastException(final String sourceValue, final QName sourceType, final QName targetType, final QName errorCode, final Throwable cause)
	{
		super(cause);
		this.sourceValue = PreCondition.assertArgumentNotNull(sourceValue, "sourceValue");
		this.sourceType = PreCondition.assertArgumentNotNull(sourceType, "sourceType");
		this.targetType = PreCondition.assertArgumentNotNull(targetType, "targetType");
		this.errorCode = PreCondition.assertArgumentNotNull(errorCode, "errorCode");
	}

	public GxmlAtomCastException(final String sourceValue, final QName targetType, final QName errorCode, final Throwable cause)
	{
		this(sourceValue, SmNativeType.UNTYPED_ATOMIC.toQName(), targetType, errorCode, cause);
	}

	/**
	 * Returns the error code.
	 */
	public QName getErrorCode()
	{
		return errorCode;
	}

	@Override
	public String getMessage()
	{
		return "source=" + sourceType + "('" + sourceValue + "'), target='" + targetType + "', errorCode='" + errorCode + "'";
	}

	/**
	 * Returns the target data type name.
	 */
	public QName getSourceType()
	{
		return sourceType;
	}

	/**
	 * Returns the source value.
	 */
	public String getSourceValue()
	{
		return sourceValue;
	}

	/**
	 * Returns the target data type name.
	 */
	public QName getTargetType()
	{
		return targetType;
	}
}
