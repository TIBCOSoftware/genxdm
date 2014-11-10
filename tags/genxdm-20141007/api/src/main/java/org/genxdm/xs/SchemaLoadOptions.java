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
package org.genxdm.xs;

import java.util.HashMap;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;

/**
 * Defines arguments that condition the loading of a schema.
 * These options are defined to be name-value pairs, where the
 * name is represented as a QName, and the option is a String.
 */
public final class SchemaLoadOptions
{
    private HashMap<QName, String> m_options = new HashMap<QName, String>();

    public String getOption(final QName name)
    {
        PreCondition.assertArgumentNotNull(name, "name");
        return m_options.get(name);
    }

    public String setOption(final QName name, final String value)
    {
        PreCondition.assertArgumentNotNull(name, "name");
        PreCondition.assertArgumentNotNull(value, "value");
        return m_options.put(name, value);
    }

    public Iterable<QName> getOptionNames()
    {
        return m_options.keySet();
    }
}
