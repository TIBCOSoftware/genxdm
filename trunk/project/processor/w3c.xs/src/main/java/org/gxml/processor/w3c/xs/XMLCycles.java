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
package org.gxml.processor.w3c.xs;

import java.util.Stack;

/**
 * Used to keep track of where we have been in order to detect cycles. <br/>
 * Rather than checking cycles as an independent step, cycle checking is integral to the conversion process. This should
 * avoid duplication of code while also making the conversion process robust.
 */
final class XMLCycles<A>
{
	final Stack<XMLAttributeGroup<A>> attributeGroups = new Stack<XMLAttributeGroup<A>>();
	final Stack<XMLAttribute<A>> attributes = new Stack<XMLAttribute<A>>();
	final Stack<XMLIdentityConstraint<A>> constraints = new Stack<XMLIdentityConstraint<A>>();
	final Stack<XMLElement<A>> elements = new Stack<XMLElement<A>>();
	final Stack<XMLModelGroup<A>> groups = new Stack<XMLModelGroup<A>>();
	final Stack<XMLType<A>> types = new Stack<XMLType<A>>();
}