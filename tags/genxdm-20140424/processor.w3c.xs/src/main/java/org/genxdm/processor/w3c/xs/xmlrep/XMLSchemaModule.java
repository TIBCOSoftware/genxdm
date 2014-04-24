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
package org.genxdm.processor.w3c.xs.xmlrep;

import java.net.URI;
import java.util.EnumSet;
import java.util.HashSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.sm.SmDuplicateIdentityConstraintException;
import org.genxdm.processor.w3c.xs.xmlrep.util.SrcFrozenLocation;
import org.genxdm.xs.enums.DerivationMethod;


/**
 * The physical schema module.
 */
public final class XMLSchemaModule
{
    enum ModuleKind
    {
        Import, Include, Redefine
    }

    public boolean attributeQualified = false;
    public boolean elementQualified = false;

    public final EnumSet<DerivationMethod> blockDefault = EnumSet.noneOf(DerivationMethod.class);
    public final EnumSet<DerivationMethod> finalDefault = EnumSet.noneOf(DerivationMethod.class);

    /**
     * The following is used to check for uniqueness of id attributes.
     */
    public final HashSet<String> m_ids = new HashSet<String>();
    public String m_id;
    public String m_version;
    public String m_lang;

    public XMLSchemaModule(final XMLSchemaModule parentModule, final String schemaLocation, final String systemId)
    {
        this.m_parentModule = parentModule;
        this.m_schemaLocation = schemaLocation;
        this.m_systemId = systemId;
    }

    public String computeTargetNamespace()
    {
        if (isChameleon())
        {
            return getContainingModule().computeTargetNamespace();
        }
        else
        {
            if (null != m_targetNamespace)
            {
                return m_targetNamespace;
            }
            else
            {
                return XMLConstants.NULL_NS_URI;
            }
        }
    }

    public XMLSchemaModule getContainingModule()
    {
        return m_parentModule;
    }

    public String getSchemaLocation()
    {
        return m_schemaLocation;
    }

    public String getSystemId()
    {
        return m_systemId;
    }

    public String getTargetNamespace()
    {
        return m_targetNamespace;
    }

    public boolean isChameleon()
    {
        return (m_targetNamespace == null) ? (isInclude() || isRedefine()) : false;
    }

    public boolean isImport()
    {
        return m_flags.contains(ModuleKind.Import);
    }

    public boolean isInclude()
    {
        return m_flags.contains(ModuleKind.Include);
    }

    public boolean isRedefine()
    {
        return m_flags.contains(ModuleKind.Redefine);
    }

    public void registerIdentityConstraintName(final QName name, final Location location) throws SmDuplicateIdentityConstraintException
    {
        if (m_identityConstraints.contains(name))
        {
            // The xs:QName value is taken from the Schema for Schemas.
            throw new SmDuplicateIdentityConstraintException(new QName("identityConstraint"), new SrcFrozenLocation(location));
        }
        else
        {
            m_identityConstraints.add(name);
        }
    }

    public void setImportFlag()
    {
        m_flags.add(ModuleKind.Import);
    }

    public void setIncludeFlag()
    {
        m_flags.add(ModuleKind.Include);
    }

    public void setRedefineFlag()
    {
        m_flags.add(ModuleKind.Redefine);
    }

    public void setTargetNamespace(final String targetNamespace)
    {
        m_targetNamespace = PreCondition.assertArgumentNotNull(targetNamespace, "targetNamespace");
    }

    private final EnumSet<ModuleKind> m_flags = EnumSet.noneOf(ModuleKind.class);
    /**
     * The following is used to check for uniqueness of identity constraint names.
     */
    private final HashSet<QName> m_identityConstraints = new HashSet<QName>();

    private final XMLSchemaModule m_parentModule;
    private final String m_schemaLocation;
    private final String m_systemId;

    private String m_targetNamespace;
}
