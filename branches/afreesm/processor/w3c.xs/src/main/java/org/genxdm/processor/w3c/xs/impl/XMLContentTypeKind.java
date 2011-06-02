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

/**
 * In the XML Representation we don't have Empty and we start out assuming ElementOnly
 * <br/>
 * Empty is a special case that is computed for the schema model.
 */
enum XMLContentTypeKind
{
    ElementOnly,
    Mixed,
    Simple;

    public boolean isSimple()
    {
        return (this == Simple);
    }

    public boolean isComplex()
    {
        return (this == ElementOnly) || (this == Mixed);
    }

    public boolean isElementOnly()
    {
        return (this == ElementOnly);
    }

    public boolean isMixed()
    {
        return (this == Mixed);
    }
}
