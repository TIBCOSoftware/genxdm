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
package org.gxml.bridgekit.xs;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.constraints.SmModelGroupUse;
import org.genxdm.xs.types.SmContentType;
import org.genxdm.xs.types.SmContentTypeKind;
import org.genxdm.xs.types.SmSimpleType;

public final class ContentTypeImpl<A> implements SmContentType<A>
{
	private SmModelGroupUse<A> contentModel;
	private SmContentTypeKind kind;
	private SmSimpleType<A> simpleType;

	public ContentTypeImpl()
	{
		this.kind = SmContentTypeKind.Empty;
		this.simpleType = null;
		this.contentModel = null;
	}

	public ContentTypeImpl(final boolean mixed, final SmModelGroupUse<A> contentModel)
	{
		this.kind = mixed ? SmContentTypeKind.Mixed : SmContentTypeKind.ElementOnly;
		this.simpleType = null;
		this.contentModel = PreCondition.assertArgumentNotNull(contentModel, "contentModel");
	}

	public ContentTypeImpl(final SmSimpleType<A> simpleType)
	{
		this.kind = SmContentTypeKind.Simple;
		this.simpleType = PreCondition.assertArgumentNotNull(simpleType, "simpleType");
		this.contentModel = null;
	}

	public void copyTo(final ContentTypeImpl<A> destination)
	{
		destination.kind = kind;
		destination.simpleType = simpleType;
		destination.contentModel = contentModel;
	}

	public SmModelGroupUse<A> getContentModel() throws AssertionError
	{
		PreCondition.assertTrue(isMixed() || isElementOnly(), "isMixed() || isElementOnly()");
		return contentModel;
	}

	public SmContentTypeKind getKind()
	{
		return PreCondition.assertArgumentNotNull(kind, "kind");
	}

	public SmSimpleType<A> getSimpleType() throws AssertionError
	{
		PreCondition.assertTrue(isSimple(), "isSimple()");
		return simpleType;
	}

	public boolean isComplex()
	{
		return kind.isComplex();
	}

	public boolean isElementOnly()
	{
		return kind.isElementOnly();
	}

	public boolean isEmpty()
	{
		return kind.isEmpty();
	}

	public boolean isMixed()
	{
		return kind.isMixed();
	}

	public boolean isSimple()
	{
		return kind.isSimple();
	}
}
