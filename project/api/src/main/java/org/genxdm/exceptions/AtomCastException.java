/*
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
package org.genxdm.exceptions;

import javax.xml.namespace.QName;

import org.genxdm.xs.types.NativeType;

/**
 * Exception arising during the casting or parsing of atomic values.
 * <p/>Parsing is to be regarded as a cast from xs:untypedAtomic.</p>
 * 
 */
@SuppressWarnings("serial")
public final class AtomCastException extends Exception
{
    private final QName errorCode;
    private final QName sourceType;
    private final String sourceValue;
    private final QName targetType;

    public AtomCastException(final String sourceValue, final QName targetType, final QName errorCode)
    {
        this(sourceValue, NativeType.UNTYPED_ATOMIC.toQName(), targetType, errorCode);
    }

    public AtomCastException(final String sourceValue, final QName sourceType, final QName targetType, final QName errorCode)
    {
        this.sourceValue = PreCondition.assertNotNull(sourceValue, "sourceValue");
        this.sourceType = PreCondition.assertNotNull(sourceType, "sourceType");
        this.targetType = PreCondition.assertNotNull(targetType, "targetType");
        this.errorCode = PreCondition.assertNotNull(errorCode, "errorCode");
    }

    public AtomCastException(final String sourceValue, final QName targetType, final QName errorCode, final Throwable cause)
    {
        this(sourceValue, NativeType.UNTYPED_ATOMIC.toQName(), targetType, errorCode, cause);
    }

    public AtomCastException(final String sourceValue, final QName sourceType, final QName targetType, final QName errorCode, final Throwable cause)
    {
        super(cause);
        this.sourceValue = PreCondition.assertNotNull(sourceValue, "sourceValue");
        this.sourceType = PreCondition.assertNotNull(sourceType, "sourceType");
        this.targetType = PreCondition.assertNotNull(targetType, "targetType");
        this.errorCode = PreCondition.assertNotNull(errorCode, "errorCode");
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
