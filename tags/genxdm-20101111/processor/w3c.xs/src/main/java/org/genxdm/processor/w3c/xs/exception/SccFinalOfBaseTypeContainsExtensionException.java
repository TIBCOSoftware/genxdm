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

@SuppressWarnings("serial")
public final class SccFinalOfBaseTypeContainsExtensionException extends SccDerivationExtensionException
{
    public SccFinalOfBaseTypeContainsExtensionException(final String partNumber, final QName complexTypeName)
    {
        super(partNumber, complexTypeName);
    }

    @Override
    public String getMessage()
    {
        return "The {final} of the {base type definition} of the complex type " + getComplexTypeName() + " must not contain extension.";
    }
}
