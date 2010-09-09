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
package org.gxml.xs.components;

import javax.xml.namespace.QName;

/**
 * For deterimining whether a schema component exists.
 */
public interface SmComponentDetector
{
	/**
	 * Determines whether the specified attribute declaration exists.
	 */
	boolean hasAttribute(QName name);

	/**
	 * Determines whether the specified attribute group definition exists.
	 */
	boolean hasAttributeGroup(QName name);

	/**
	 * Determines whether the specified Complex type definition exists.
	 */
	boolean hasComplexType(QName name);

	/**
	 * Determines whether the specified element declaration exists.
	 */
	boolean hasElement(QName name);

	/**
	 * Determines whether the specified identity-constraint definition exists.
	 */
	boolean hasIdentityConstraint(QName name);

	/**
	 * Determines whether the specified model group definition exists.
	 */
	boolean hasModelGroup(QName name);

	/**
	 * Determines whether the specified notation declaration exists.
	 */
	boolean hasNotation(QName name);

	/**
	 * Determines whether the specified Simple type definition exists.
	 */
	boolean hasSimpleType(QName name);

	/**
	 * Determines whether the specified type definition exists.
	 */
	boolean hasType(QName name);
}
