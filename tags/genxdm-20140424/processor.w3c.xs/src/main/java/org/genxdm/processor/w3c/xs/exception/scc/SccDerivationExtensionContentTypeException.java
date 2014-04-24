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
package org.genxdm.processor.w3c.xs.exception.scc;

import javax.xml.namespace.QName;

@SuppressWarnings("serial")
public final class SccDerivationExtensionContentTypeException extends SccDerivationExtensionException
{
    public SccDerivationExtensionContentTypeException(final QName complexTypeName, final QName baseTypeName)
    {
        super(PART_WHEN_BASE_COMPLEX_TYPE_CONTENT_TYPE, complexTypeName);
        baseType = baseTypeName;
    }

    @Override
    public String getMessage()
    {
        return "The {content type} of the complex type " + getComplexTypeName() + " conflicts with the {content type} of the {base type definition} " + baseType + ".";
    }
    
    private QName baseType;
}
