/*
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.genxdm.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.xml.namespace.QName;

import org.genxdm.names.NameSource;
import org.genxdm.xs.types.NativeType;
import org.junit.Test;

public class NameSourceTestCase
{
    
    @Test
    public void nativesAndNames()
    {
        NameSource source = NameSource.SINGLETON;

        for (final NativeType type : NativeType.values())
        {
            // explanation: nativeType(NativeType) returns a QName;
            // nativeType(QName) returns a NativeType.  We here test
            // that feeding the type to the namesource generates a
            // qname that when fed to the namesource produces the 
            // original native type.
            assertEquals(type, source.nativeType(source.nativeType(type)));
        }
        // nativeType(QName) may return null; it *should* if it gets a silly name
        assertNull(source.nativeType(new QName("", "xyzzy")));
    }
}
