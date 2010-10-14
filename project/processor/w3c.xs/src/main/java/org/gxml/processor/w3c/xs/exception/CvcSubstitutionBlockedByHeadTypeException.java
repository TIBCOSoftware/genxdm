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
package org.gxml.processor.w3c.xs.exception;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.enums.SmOutcome;
import org.genxdm.xs.resolve.SmLocation;

@SuppressWarnings("serial")
public final class CvcSubstitutionBlockedByHeadTypeException extends SmLocationException
{
	private final SmElement<?> elementDeclaration;

	public CvcSubstitutionBlockedByHeadTypeException(final SmElement<?> elementDeclaration, final SmLocation location)
	{
		super(SmOutcome.SCC_Substitution_Group_OK_Transitive, "2.1", location);
		this.elementDeclaration = PreCondition.assertArgumentNotNull(elementDeclaration, "elementDeclaration");
	}

	public String getMessage()
	{
		return "Substitution using element " + elementDeclaration + " is blocked by the type of the substitution group.";
	}
}
