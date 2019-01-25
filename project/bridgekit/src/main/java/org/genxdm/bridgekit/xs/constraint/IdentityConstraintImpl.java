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
package org.genxdm.bridgekit.xs.constraint;

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.xs.ForeignAttributesImpl;
import org.genxdm.bridgekit.xs.ForeignAttributesSink;
import org.genxdm.bridgekit.xs.complex.NamedComponentImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.constraints.IdentityConstraintKind;
import org.genxdm.xs.constraints.RestrictedXPath;
import org.genxdm.xs.enums.ScopeExtent;

public final class IdentityConstraintImpl 
    extends NamedComponentImpl 
    implements IdentityConstraint, ForeignAttributesSink
{

    public IdentityConstraintImpl(final QName name, final IdentityConstraintKind category, final RestrictedXPath selector, final List<RestrictedXPath> fields, final IdentityConstraint keyConstraint)
    {
        super(name, false, ScopeExtent.Global);
        m_name = PreCondition.assertArgumentNotNull(name, "name");
        m_category = PreCondition.assertArgumentNotNull(category, "category");
        m_selector = PreCondition.assertArgumentNotNull(selector, "selector");
        m_fields = PreCondition.assertArgumentNotNull(fields, "fields");
        m_keyConstraint = keyConstraint;
        m_selectsContainer = evaluateSelector(m_selector);
    }

    @Override
    public IdentityConstraintKind getCategory()
    {
        return m_category;
    }

    @Override
    public List<RestrictedXPath> getFields()
    {
        return m_fields;
    }

    @Override
    public IdentityConstraint getKeyConstraint()
    {
        return m_keyConstraint;
    }

    @Override
    public RestrictedXPath getSelector()
    {
        return m_selector;
    }

    public boolean sameAs(final IdentityConstraint constraint)
    {
        PreCondition.assertArgumentNotNull(constraint, "constraint");
        if (this == constraint)
            return true;
        return getName().equals(constraint.getName());
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append(m_category);
        sb.append(":");
        sb.append(m_name);
        return sb.toString();
    }

    @Override
    public Iterable<QName> getForeignAttributeNames()
    {
        return forAtts.getForeignAttributeNames();
    }

    @Override
    public String getForeignAttributeValue(QName name)
    {
        return forAtts.getForeignAttributeValue(name);
    }

    @Override
    public void putForeignAttribute(QName name, String value)
    {
        forAtts.putForeignAttribute(name, value);
    }

    @Override
    public boolean selectsContainer()
    {
        return m_selectsContainer;
    }
    
    private boolean evaluateSelector(RestrictedXPath selector)
    {
        // the DefaultRestrictedXPathImpl is associated with its alternatives
        // (which should be unusual for the case of an xpath selector identifying
        // the context node, but it is *conceivable*) via the getAlternate()
        // mechanism, which then allows us to examine each to see if it contains
        // a sole context-node alternative. we return true if *any* branch is
        // sole context-node, because we want to fire the extra startElement event.
        do {
            if ((selector.getStepLength() == 1) // only contains one step
                && selector.isContextNode(0)) // that step is self::node() or . ; delegate determination to the xpath parser/xpath impl
                return true;
            selector = selector.getAlternate(); // next selector
        } while (selector != null); // stop when we get a null selector

        return false; // return false unconditionally if we haven't found a selector xpath alternate that matches context node only. 
    }

    private ForeignAttributesImpl forAtts = new ForeignAttributesImpl();
    private final IdentityConstraintKind m_category;
    private final List<RestrictedXPath> m_fields;
    private final IdentityConstraint m_keyConstraint;
    private final QName m_name;
    private final RestrictedXPath m_selector;
    private final boolean m_selectsContainer;
}
