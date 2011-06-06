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

import java.net.URI;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;

final class XMLNotation extends XMLComponent
{
	private final QName name;
	String publicId;
	URI systemId;

	public XMLNotation(final QName name, final XMLScope global, final SrcFrozenLocation location)
	{
		super(global, location);
		this.name = PreCondition.assertArgumentNotNull(name);
	}

	public QName getName()
	{
		return name;
	}
}
