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
package org.genxdm.names;

import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.types.NativeType;

/**
 * Provides lookups for certain well-known names.
 * 
 * Primary remaining utility: map from the schema model NativeType
 * abstraction to QName, and from QName to NativeType.  This is really
 * something that belongs in with types, but so it goes.  See the notes
 * on the "empty" method before moving and renaming this to something
 * more rational.
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
        // TODO: this method needs to be removed, but we currently have
        // over one hundred references to it.  We should instead be using
        // direct comparison with javax.xml.XMLConstants.NULL_NS_URI and
        // avoiding the extra method call.
        return XMLConstants.NULL_NS_URI;
    }

    /**
     * Given a name, return a corresponding {@link NativeType}. <br/>
     * If the name is not a built-in type, returns <code>null</code>.
     */
    public final NativeType nativeType(final QName name)
    {
        return nameToNative.get(name);
    }

    /**
     * Given an {@link NativeType}, lookup a corresponding name.
     */
    public final QName nativeType(final NativeType nativeType)
    {
        PreCondition.assertNotNull(nativeType, "nativeType");
        final QName name = nativeToName.get(nativeType);
        assert (name != null) : "forget to initialize me?";
        return name;
    }

    private void initialize()
    {
        for (final NativeType nativeType : NativeType.values())
        {
            final QName name = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, nativeType.getLocalName());
            nameToNative.put(name, nativeType);
            nativeToName.put(nativeType, name);
        }
    }

    private final HashMap<QName, NativeType> nameToNative = new HashMap<QName, NativeType>();
    private final HashMap<NativeType, QName> nativeToName = new HashMap<NativeType, QName>();

}
