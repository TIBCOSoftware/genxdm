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
package org.genxdm.processor.w3c.xs.xmlrep;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.types.ContentTypeKind;

final class XMLContentType
{
    private ContentTypeKind m_kind;
    private XMLType m_simpleType;
    private XMLParticleWithModelGroupTerm m_contentModel;

    public XMLContentType()
    {
        m_kind = ContentTypeKind.Empty;
        m_simpleType = null;
        m_contentModel = null;
    }

    public XMLContentType(final XMLType simpleType)
    {
        m_kind = ContentTypeKind.Simple;
        m_simpleType = PreCondition.assertArgumentNotNull(simpleType, "simpleType");
        m_contentModel = null;
    }

    public XMLContentType(final boolean mixed, final XMLParticleWithModelGroupTerm contentModel)
    {
        m_kind = mixed ? ContentTypeKind.Mixed : ContentTypeKind.ElementOnly;
        m_simpleType = null;
        m_contentModel = PreCondition.assertArgumentNotNull(contentModel);
    }

    public ContentTypeKind getKind()
    {
        return PreCondition.assertArgumentNotNull(m_kind, "kind");
    }

    public boolean isEmpty()
    {
        return m_kind.isEmpty();
    }

    public boolean isSimple()
    {
        return m_kind.isSimple();
    }

    public boolean isComplex()
    {
        return m_kind.isComplex();
    }

    public boolean isMixed()
    {
        return m_kind.isMixed();
    }

    public boolean isElementOnly()
    {
        return m_kind.isElementOnly();
    }

    public XMLType getSimpleType()
    {
        PreCondition.assertTrue(isSimple(), "must be simple");
        return m_simpleType;
    }

    public XMLParticleWithModelGroupTerm getContentModel()
    {
        PreCondition.assertTrue(isMixed() || isElementOnly(), "must be mixed or element only");
        return m_contentModel;
    }

    public void copyTo(final XMLContentType destination)
    {
        destination.m_kind = m_kind;
        destination.m_simpleType = m_simpleType;
        destination.m_contentModel = m_contentModel;
    }
}
