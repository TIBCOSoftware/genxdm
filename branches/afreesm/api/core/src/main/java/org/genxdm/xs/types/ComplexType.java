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
package org.genxdm.xs.types;

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.xs.components.SchemaWildcard;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.enums.DerivationMethod;

/**
 * A Complex Type Definition.
 * 
 * @param <A>
 *            The atom handle.
 */
public interface ComplexType<A> extends ComplexMarkerType<A>
{
    /**
     * Returns the {attribute uses} property for a complex type.
     */
    Map<QName, AttributeUse<A>> getAttributeUses();

    /**
     * Returns the {attribute wildcard} property for a complex type.
     */
    SchemaWildcard<A> getAttributeWildcard();

    /**
     * Returns the {content type} property.
     */
    ContentType<A> getContentType();

    /**
     * Returns the {prohibited substitutions} property. This is a run-time constraint on the types. A subset of
     * {extension, restriction}.
     */
    Set<DerivationMethod> getProhibitedSubstitutions();
}
