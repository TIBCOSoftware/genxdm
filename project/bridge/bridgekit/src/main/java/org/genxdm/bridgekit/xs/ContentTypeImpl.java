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
package org.genxdm.bridgekit.xs;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.constraints.ModelGroupUse;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.ContentTypeKind;
import org.genxdm.xs.types.SimpleType;

public final class ContentTypeImpl<A> implements ContentType<A>
{
	private ModelGroupUse<A> contentModel;
	private ContentTypeKind kind;
	private SimpleType<A> simpleType;

	public ContentTypeImpl()
	{
		this.kind = ContentTypeKind.Empty;
		this.simpleType = null;
		this.contentModel = null;
	}

	public ContentTypeImpl(final boolean mixed, final ModelGroupUse<A> contentModel)
	{
		this.kind = mixed ? ContentTypeKind.Mixed : ContentTypeKind.ElementOnly;
		this.simpleType = null;
		this.contentModel = PreCondition.assertArgumentNotNull(contentModel, "contentModel");
	}

	public ContentTypeImpl(final SimpleType<A> simpleType)
	{
		this.kind = ContentTypeKind.Simple;
		this.simpleType = PreCondition.assertArgumentNotNull(simpleType, "simpleType");
		this.contentModel = null;
	}

	public void copyTo(final ContentTypeImpl<A> destination)
	{
		destination.kind = kind;
		destination.simpleType = simpleType;
		destination.contentModel = contentModel;
	}

	public ModelGroupUse<A> getContentModel() throws AssertionError
	{
		PreCondition.assertTrue(isMixed() || isElementOnly(), "isMixed() || isElementOnly()");
		return contentModel;
	}

	public ContentTypeKind getKind()
	{
		return PreCondition.assertArgumentNotNull(kind, "kind");
	}

	public SimpleType<A> getSimpleType() throws AssertionError
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
