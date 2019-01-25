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

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.xs.components.SchemaComponent;
import org.genxdm.xs.enums.ScopeExtent;

/**
 * Describes key/keyref/unique constraints on the schema.
 */
public interface IdentityConstraint extends SchemaComponent
{
    /**
     * Returns this constraint's {identity-constraint category} property. This is one of key, keyref or unique.
     */
    IdentityConstraintKind getCategory();

    /**
     * Returns the {fields} property of the constraint, a non-empty list of restricted XPath expressions.
     */
    List<RestrictedXPath> getFields();

    /**
     * In the case of keyref constraints, returns the corresponding key constraint with {identity-constraint category}
     * equal to key or unique.
     * 
     * @return a IdentityConstraint or null
     */
    IdentityConstraint getKeyConstraint();

    /** The name of this constraint (from the name attribute)
     * 
     */
    QName getName();

    /** ScopeExtent has values global or local
     * 
     */
    ScopeExtent getScopeExtent();

    /**
     * Returns the {selector} restricted XPath of the constraint.
     */
    RestrictedXPath getSelector();
    
    /** True if the selector XPath for this constraint selects only the context node
     * 
     * That is, if the key contains approximately: &lt;selector xpath="." /&gt;
     * it returns true, and otherwise returns false.
     */
    boolean selectsContainer();
}
