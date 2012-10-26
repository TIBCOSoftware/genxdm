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
package org.genxdm.processor.w3c.xs.xmlrep;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;

/**
 * A reference to either a global type identified by name or a local type (anonymous) identified by an object reference.
 */
public final class XMLTypeRef
{
    private final XMLType localType;
    private final QName name;

    public XMLTypeRef(final QName name)
    {
        this.name = PreCondition.assertArgumentNotNull(name, "name");
        this.localType = null;
    }

    public XMLTypeRef(final XMLType localType)
    {
        this.name = null;
        this.localType = PreCondition.assertArgumentNotNull(localType, "localType");
        PreCondition.assertTrue(localType.getScope().isLocal());
    }

    public XMLType getLocal()
    {
        return localType;
    }

    public QName getName()
    {
        return PreCondition.assertNotNull(name, "name");
    }

    public boolean isComplexUrType()
    {
        if (null != name)
        {
            return XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(name.getNamespaceURI()) && "anyType".equals(name.getLocalPart());
        }
        else
        {
            return false;
        }
    }

    public boolean isGlobal()
    {
        return (null != name);
    }
}
