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
package org.genxdm.bridgekit.atoms;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.types.NativeType;

public class XmlQName 
    extends XmlAbstractAtom
{
    public XmlQName(final String namespaceURI, final String localName, final String prefix)
    {
        this.namespaceURI = PreCondition.assertNotNull(namespaceURI);
        this.localName = PreCondition.assertNotNull(localName);
        this.prefix = prefix;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof XmlQName)
            return equalsName((XmlQName)obj);
        return false;
    }

    public boolean equalsName(final XmlQName other)
    {
        return namespaceURI.equals(other.namespaceURI) && localName.equals(other.localName);
    }

    public String getC14NForm()
    {
        final int prefixLength = prefix.length();
        if (localName != null)
        {
            if (prefixLength > 0)
            {
                final int localNameLength = localName.length();
                return new StringBuilder(prefixLength + 1 + localNameLength).append(prefix).append(":").append(localName).toString();
            }
            return localName;
        }
        else
        {
            if (prefixLength > 0)
                return prefix.concat(":*");
            return "*";
        }
    }

    public String getLocalName()
    {
        return localName;
    }

    public String getNamespaceURI()
    {
        return namespaceURI;
    }

    public NativeType getNativeType()
    {
        return NativeType.QNAME;
    }

    public String getPrefix()
    {
        return prefix;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = prime + ((namespaceURI == null) ? 0 : namespaceURI.hashCode());
        result = prime * result + ((localName == null) ? 0 : localName.hashCode());
        return result; 
    }

    public boolean isWhiteSpace()
    {
        return false;
    }

    private final String localName;
    private final String namespaceURI;
    private final String prefix;
}
