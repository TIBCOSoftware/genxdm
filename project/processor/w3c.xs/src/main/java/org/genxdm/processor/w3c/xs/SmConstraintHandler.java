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
package org.genxdm.processor.w3c.xs;

import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmAttributeGroup;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.components.SmNotation;
import org.genxdm.xs.components.SmParticle;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmSimpleType;

public interface SmConstraintHandler<A>
{
	void error(SmSimpleType<A> simpleType, SmException exception) throws SmAbortException;

	void error(SmComplexType<A> complexType, SmException exception) throws SmAbortException;

	void error(SmAttribute<A> attribute, SmException exception) throws SmAbortException;

	void error(SmElement<A> element, SmException exception) throws SmAbortException;

	void error(SmModelGroup<A> modelGroup, SmException exception) throws SmAbortException;

	void error(SmAttributeGroup<A> attributeGroup, SmException exception) throws SmAbortException;

	void error(SmIdentityConstraint<A> constraint, SmException exception) throws SmAbortException;

	void error(SmNotation<A> notation, SmException exception) throws SmAbortException;

	void error(SmParticle<A> particle, SmException exception) throws SmAbortException;
}
