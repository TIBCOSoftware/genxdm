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
package org.genxdm.xs.constraints;

/**
 * Indicates the type of an identity constraint (xs:unique, xs:key, xs:keyref).
 */
public enum IdentityConstraintKind
{
	/**
	 * The identity-constraint definition asserts uniqueness, with respect to the content identified by {selector}, of
	 * the tuples resulting from evaluation of the {fields} XPath expression(s).
	 */
	Unique,

	/**
	 * The identity-constraint definition asserts uniqueness as for <em>unique</em>. key further asserts that all
	 * selected content actually has such tuples.
	 */
	Key,

	/**
	 * The identity-constraint definition asserts a correspondence, with respect to the content identified by
	 * {selector}, of the tuples resulting from evaluation of the {fields} XPath expression(s), with those of the
	 * {referenced key}.
	 */
	KeyRef;

	public boolean isUnique()
	{
		return (this == Unique);
	}

	public boolean isKey()
	{
		return (this == Key);
	}

	public boolean isKeyRef()
	{
		return (this == KeyRef);
	}
}
