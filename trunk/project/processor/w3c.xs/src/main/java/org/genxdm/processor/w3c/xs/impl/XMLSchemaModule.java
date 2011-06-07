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

import java.net.URI;
import java.util.EnumSet;
import java.util.HashSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.sm.SmDuplicateIdentityConstraintException;
import org.genxdm.xs.enums.DerivationMethod;


/**
 * The physical schema module.
 */
final class XMLSchemaModule
{
	private enum ModuleKind
	{
		Import, Include, Redefine
	}

	boolean attributeQualified = false;
	final EnumSet<DerivationMethod> blockDefault = EnumSet.noneOf(DerivationMethod.class);

	boolean elementQualified = false;

	final EnumSet<DerivationMethod> finalDefault = EnumSet.noneOf(DerivationMethod.class);

	private final EnumSet<ModuleKind> m_flags = EnumSet.noneOf(ModuleKind.class);
	String m_id;
	/**
	 * The following is used to check for uniqueness of identity constraint names.
	 */
	final HashSet<QName> m_identityConstraints = new HashSet<QName>();

	/**
	 * The following is used to check for uniqueness of id attributes.
	 */
	final HashSet<String> m_ids = new HashSet<String>();

	String m_lang;
	private final XMLSchemaModule m_parentModule;
	private final URI m_schemaLocation;
	private final URI m_systemId;

	private URI m_targetNamespace;

	String m_version;

	public XMLSchemaModule(final XMLSchemaModule parentModule, final URI schemaLocation, final URI systemId)
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
				return m_targetNamespace.toString();
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

	public URI getSchemaLocation()
	{
		return m_schemaLocation;
	}

	public URI getSystemId()
	{
		return m_systemId;
	}

	public URI getTargetNamespace()
	{
		return m_targetNamespace;
	}

	public boolean isChameleon()
	{
		if (null == m_targetNamespace)
		{
			return isInclude() || isRedefine();
		}
		else
		{
			// The targetNamespace attribute has been defined.
			return false;
		}
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

	public void setTargetNamespace(final URI targetNamespace)
	{
		m_targetNamespace = PreCondition.assertArgumentNotNull(targetNamespace, "targetNamespace");
	}
}
