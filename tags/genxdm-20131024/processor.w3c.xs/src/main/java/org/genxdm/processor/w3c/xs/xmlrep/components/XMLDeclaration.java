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
import org.genxdm.processor.w3c.xs.xmlrep.XMLValueConstraint;
import org.genxdm.processor.w3c.xs.xmlrep.util.SrcFrozenLocation;

abstract class XMLDeclaration extends XMLComponent
{
    private final QName m_name;
    public XMLTypeRef typeRef;
    public XMLValueConstraint m_valueConstraint = null;

    public XMLDeclaration(final QName name, final XMLScope scope, final XMLTypeRef typeRef, final SrcFrozenLocation location)
    {
        super(scope, location);
        this.m_name = PreCondition.assertArgumentNotNull(name, "name");
        this.typeRef = PreCondition.assertArgumentNotNull(typeRef, "typeRef");
    }

    public XMLDeclaration(final QName name, final XMLScope scope, final XMLTypeRef typeRef)
    {
        super(scope);
        this.m_name = PreCondition.assertArgumentNotNull(name, "name");
        this.typeRef = PreCondition.assertArgumentNotNull(typeRef, "typeRef");
    }

    public QName getName()
    {
        return m_name;
    }
}
