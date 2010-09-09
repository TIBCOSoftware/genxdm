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
package org.gxml.xs.constraints;

/**
 * A restricted XPath expression associated with W3C XMl Schema validation.
 * 
 */
public interface SmRestrictedXPath
{
	/**
	 * returns true if path started with ".//"
	 */
	boolean isRelocatable();

	/**
	 * returns true if path ended with "@something"
	 */
	boolean isAttribute();

	/**
	 * Gives the number of steps for this branch of the expression
	 */
	int getStepLength();

	/**
	 * Returns the index of the highest step. This depends upon the xpath expression itself.
	 */
	int getUBoundStep();

	/**
	 * @param index
	 *            zero-based, must be less than {@link #getStepLength}
	 * @return a namespace URI, or null for this step
	 */
	String getStepNamespace(int index);

	/**
	 * @param index
	 *            zero-based, must be less than {@link #getStepLength}
	 * @return an unqualified name, or WILDCARD
	 */
	String getStepLocalName(int index);

	boolean isWildcardNamespace(int index);

	boolean isWildcardLocalName(int index);

	/**
	 * Determines whether the step specified is "." which is shorthand for "self::node()". <br/>
	 * Any node will match this step.
	 */
	boolean isContextNode(int index);

	/**
	 * Return the next XPath in a succession of alternates.
	 */
	SmRestrictedXPath getAlternate();
}
