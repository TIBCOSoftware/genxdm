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
package org.genxdm.processor.w3c.xs.exception.sm;

import java.net.URI;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.resolve.LocationInSchema;


@SuppressWarnings("serial")
public final class SmNoSchemaForNamespaceException extends SmLocationException
{
    private final String namespaceURI;

    public SmNoSchemaForNamespaceException(final String namespaceURI, final LocationInSchema location)
    {
        super(ValidationOutcome.TODO, "1", location);
        this.namespaceURI = PreCondition.assertArgumentNotNull(namespaceURI, "namespaceURI");
    }

    public String getNamespaceUri()
    {
        return namespaceURI;
    }

    @Override
    public String getMessage()
    {
        return "No schema for namespace " + namespaceURI + ".";
    }
}
