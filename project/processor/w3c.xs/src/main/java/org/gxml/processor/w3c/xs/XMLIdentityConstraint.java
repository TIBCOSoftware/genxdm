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

import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.gxml.exceptions.PreCondition;
import org.gxml.xs.constraints.SmIdentityConstraintKind;
import org.gxml.xs.constraints.SmRestrictedXPath;

final class XMLIdentityConstraint<A> extends XMLComponent<A>
{
	private final QName name;
	public SmIdentityConstraintKind category;
	public SmRestrictedXPath selector;
	public XMLIdentityConstraint<A> keyConstraint;
	public final LinkedList<SmRestrictedXPath> fields = new LinkedList<SmRestrictedXPath>();

	public XMLIdentityConstraint(final QName name, final XMLScope<A> global)
	{
		super(global);
		this.name = PreCondition.assertArgumentNotNull(name);
	}

	public XMLIdentityConstraint(final QName name, final XMLScope<A> global, final SrcFrozenLocation location)
	{
		super(global, location);
		this.name = PreCondition.assertArgumentNotNull(name);
	}

	public QName getName()
	{
		return name;
	}
}
