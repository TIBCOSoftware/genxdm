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

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.constraints.ValueConstraint;

/**
 * XML representation of a value constraint. <br/>
 * This will be validated against a {type definition} to produce an actual value for storage on the schema model.
 */
final class XMLValueConstraint
{
	public final ValueConstraint.Kind kind;
	private final QName m_attributeName;
	private final String m_value;
	private final SrcFrozenLocation m_location;

	public XMLValueConstraint(final ValueConstraint.Kind kind, final QName attributeName, final String value, final SrcFrozenLocation location)
	{
		this.kind = PreCondition.assertArgumentNotNull(kind, "kind");
		this.m_attributeName = PreCondition.assertArgumentNotNull(attributeName, "attributeName");
		this.m_value = PreCondition.assertArgumentNotNull(value, "value");
		this.m_location = PreCondition.assertArgumentNotNull(location, "location");
	}

	/**
	 * Returns the kind of {value constraint} property.
	 */
	public QName getAttributeName()
	{
		return m_attributeName;
	}

	/**
	 * Returns the value of {value constraint} property.
	 */
	public String getValue()
	{
		return m_value;
	}

	/**
	 * Returns the location of the attribute defining the value.
	 */
	public SrcFrozenLocation getLocation()
	{
		return m_location;
	}

	@Override
	public String toString()
	{
		return m_attributeName + "=" + m_value;
	}
}
