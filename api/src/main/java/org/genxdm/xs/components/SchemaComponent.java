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
package org.genxdm.xs.components;

import javax.xml.namespace.QName;

import org.genxdm.xs.enums.ScopeExtent;

/**
 * The common interface for all schema components (things that are named).
 */
public interface SchemaComponent<A>
{
	/**
	 * The {name} and {target namespace} properties.
	 */
	QName getName();

	/**
	 * The {name} property.
	 */
	String getLocalName();

	/**
	 * The {target namespace} property.
	 */
	String getTargetNamespace();

	/**
	 * The {scope} property of this component.
	 */
	ScopeExtent getScopeExtent();

	/**
	 * A component is anonymous if it does not explicitly have a name in a schema document.
	 * 
	 * A component may be assigned a processing-context unique name if it is anonymous.
	 */
	boolean isAnonymous();
}
