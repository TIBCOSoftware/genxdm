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
package org.gxml.names;

import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.gxml.exceptions.IllegalNullArgumentException;
import org.gxml.xs.types.SmNativeType;

/**
 * Provides lookups for certain well-known names.
 */
public class NameSource
{
    public NameSource()
    {
        initialize();
    }

    /**
     * Returns a symbol equivalent to the empty string.
     */
    public final String empty()
    {
        return NULL_NS_URI;
    }

    /**
     * Returns a prefix String by recognizing the namespaceURI symbol. <br/>
     * The return value should conform to the following table: <br/>
     * <table border='1'>
     * <tr>
     * <th>namespaceURI</th>
     * <th>mayUseDefaultMapping</th>
     * <th>prefix</th>
     * </tr>
     * <tr>
     * <td>""</td>
     * <td><code>true</code></td>
     * <td>""</td>
     * </tr>
     * <tr>
     * <td>"http://www.w3.org/XML/1998/namespace"</td>
     * <td>ignored</td>
     * <td>"xml"</td>
     * </tr>
     * <tr>
     * <td>"http://www.w3.org/2000/xmlns/"</td>
     * <td>ignored</td>
     * <td>"xmlns"</td>
     * </tr>
     * <tr>
     * <td>otherwise</td>
     * <td>ignored</td>
     * <td><code>null</code></td>
     * </tr>
     * </table>
     */
    public final String getPrefix(final String namespaceURI, final boolean mayUseDefaultMapping)
    {
        if (null != namespaceURI)
        {
            if (NULL_NS_URI == namespaceURI)
            {
                if (mayUseDefaultMapping)
                {
                    return XMLConstants.DEFAULT_NS_PREFIX;
                }
                else
                {
                    return null;
                }
            }
            else if (XML_NS_URI == namespaceURI)
            {
                return XMLConstants.XML_NS_PREFIX;
            }
            else if (XMLNS_ATTRIBUTE_NS_URI == namespaceURI)
            {
                return XMLConstants.XMLNS_ATTRIBUTE;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    protected void initialize()
    {
        for (final SmNativeType nativeType : SmNativeType.values())
        {
            final QName name = new QName(W3C_XML_SCHEMA_NS_URI, nativeType.getLocalName());
            nameToNative.put(name, nativeType);
            nativeToName.put(nativeType, name);
        }
    }

    /**
     * Determines whether the specified symbol is equivalent to an empty string.
     */
    public final boolean isEmpty(final String symbol)
    {
        if (null != symbol)
        {
            return NULL_NS_URI == symbol;
        }
        else
        {
            return false;
        }
    }

    public final boolean isW3cXmlSchemaNamespaceURI(final String namespaceURI)
    {
        if (null != namespaceURI)
        {
            return W3C_XML_SCHEMA_NS_URI == namespaceURI;
        }
        else
        {
            return false;
        }
    }

    /**
     * Determines whether the specified symbol is the "http://www.w3.org/XML/1998/namespace" namespace symbol.
     * <p>
     * This is a convenience for method, so <code>isXmlNamespaceURI(x)</code> is the same as <code>equal(symbolize("http://www.w3.org/XML/1998/namespace"),x)</code>.
     * </p>
     */
    public final boolean isXmlNamespaceURI(final String symbol)
    {
        if (null != symbol)
        {
            return XML_NS_URI == symbol;
        }
        else
        {
            return false;
        }
    }

    /**
     * Given a name, return a corresponding {@link SmNativeType}. <br/>
     * If the name is not a built-in type, returns <code>null</code>.
     */
    public final SmNativeType nativeType(final QName name)
    {
        return nameToNative.get(name);
    }

    /**
     * Given an {@link SmNativeType}, lookup a corresponding name.
     */
    public final QName nativeType(final SmNativeType nativeType)
    {
        IllegalNullArgumentException.check(nativeType, "nativeType");
        final QName name = nativeToName.get(nativeType);
        assert (name != null) : "forget to initialize me?";
        return name;
    }

    /**
     * Return "http://www.w3.org/2001/XMLSchema".
     */
    public final String W3C_XML_SCHEMA_NS_URI()
    {
        return W3C_XML_SCHEMA_NS_URI;
    }

    private final HashMap<QName, SmNativeType> nameToNative = new HashMap<QName, SmNativeType>();
    private final HashMap<SmNativeType, QName> nativeToName = new HashMap<SmNativeType, QName>();
    private String NULL_NS_URI = XMLConstants.NULL_NS_URI;
    private String W3C_XML_SCHEMA_NS_URI = XMLConstants.W3C_XML_SCHEMA_NS_URI;
    private String XML_NS_URI = XMLConstants.XML_NS_URI;
    private String XMLNS_ATTRIBUTE_NS_URI = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

}
