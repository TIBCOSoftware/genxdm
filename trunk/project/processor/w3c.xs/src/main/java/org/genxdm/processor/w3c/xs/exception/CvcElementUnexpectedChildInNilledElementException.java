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
package org.genxdm.processor.w3c.xs.exception;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.resolve.LocationInSchema;


@SuppressWarnings("serial")
public final class CvcElementUnexpectedChildInNilledElementException extends CvcElementException
{
	/**
	 * The child node could be an element indormation item or a chacater information item, so we report the parent name
	 * and location.
	 */
	public CvcElementUnexpectedChildInNilledElementException(final ElementDefinition elementName, final LocationInSchema location)
	{
		super(CvcElementException.PART_NO_CHILDREN, elementName, location);
	}

	@Override
	public String getMessage()
	{
		return "Element \"" + getElementDeclaration() + "\" cannot have element node child because \"" + new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil") + "\" is specified.";
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof CvcElementUnexpectedChildInNilledElementException)
		{
			final CvcElementUnexpectedChildInNilledElementException e = (CvcElementUnexpectedChildInNilledElementException)obj;
			return e.getElementDeclaration().equals(getElementDeclaration());
		}
		else
		{
			return false;
		}
	}
}
