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
package org.genxdm.processor.w3c.xs.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.resolve.CatalogResolver;

enum DefaultCatalogResolver implements CatalogResolver
{
    SINGLETON;

    public InputStream resolveInputStream(final URI uri) throws IOException
    {
        PreCondition.assertArgumentNotNull(uri, "uri");

        final URL toRetrieve;

        if (!uri.isAbsolute()) // assume local file
        {
            final File canonFile = new File(uri.toString()).getCanonicalFile();
            toRetrieve = canonFile.toURI().toURL();
        }
        else
        {
            toRetrieve = uri.toURL();
        }

        if (toRetrieve == null)
        {
            throw new FileNotFoundException(uri.toString());
        }

        final InputStream stream = toRetrieve.openStream();
        if (stream == null)
        {
            throw new FileNotFoundException(toRetrieve.toString());
        }
        return stream;
    }
}
