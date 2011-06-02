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
import org.genxdm.xs.enums.WhiteSpacePolicy;

final class ForeignAtomManager<A>
{
	public final QName atomType;
	public final QName baseType;
	public final WhiteSpacePolicy ws;

	ForeignAtomManager(final QName atomType, final QName baseType, final WhiteSpacePolicy ws)
	{
		this.atomType = PreCondition.assertArgumentNotNull(atomType, "atomType");
		this.baseType = PreCondition.assertArgumentNotNull(baseType, "baseType");
		this.ws = ws;
	}
}
