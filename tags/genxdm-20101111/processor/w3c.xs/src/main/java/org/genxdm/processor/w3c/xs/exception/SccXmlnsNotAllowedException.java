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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.enums.ValidationOutcome;
import org.genxdm.xs.exceptions.ComponentConstraintException;


@SuppressWarnings("serial")
public final class SccXmlnsNotAllowedException extends ComponentConstraintException
{
	private final QName m_attributeName;

	public SccXmlnsNotAllowedException(final QName attributeName)
	{
		super(ValidationOutcome.SCC_xmlns_Not_Allowed, ""/* no part number */);
		m_attributeName = PreCondition.assertArgumentNotNull(attributeName, "attributeName");
	}

	public final QName getAttributeName()
	{
		return m_attributeName;
	}

	@Override
	public String getMessage()
	{
		return "The {name} or {target namespace} of the attribute declaration " + getAttributeName() + " must not match " + XMLConstants.XMLNS_ATTRIBUTE + " or " + XMLConstants.XMLNS_ATTRIBUTE_NS_URI + " respectively.";
	}
}
