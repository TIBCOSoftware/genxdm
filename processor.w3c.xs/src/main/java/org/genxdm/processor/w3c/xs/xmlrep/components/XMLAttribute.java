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
package org.genxdm.processor.w3c.xs.xmlrep.components;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.xmlrep.XMLScope;
import org.genxdm.processor.w3c.xs.xmlrep.XMLTypeRef;
import org.genxdm.processor.w3c.xs.xmlrep.util.SrcFrozenLocation;

public final class XMLAttribute extends XMLDeclaration
{
    public XMLAttribute(final QName name, final XMLScope scope, final XMLTypeRef anySimpleType, final SrcFrozenLocation location)
    {
        super(PreCondition.assertArgumentNotNull(name, "name"), scope, anySimpleType, location);
    }

    public XMLAttribute(final QName name, final XMLScope scope, final XMLTypeRef anySimpleType)
    {
        super(PreCondition.assertArgumentNotNull(name, "name"), scope, anySimpleType);
    }
}
