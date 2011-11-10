/*
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.genxdm.nodes;

import javax.xml.namespace.QName;

/**
 * A stateful abstraction positioned over a &lt;N>ode (its state), and potentially
 * annotated with type information and &lt;A>tomic values. 
 */
public interface TypeInformer<A>
{

    /**
     * Returns the type name of the attribute node with the specified expanded-QName.
     * <p>This is equivalent to moving to the attribute node and then retrieving its type name.
     * This method only works if the current state is an element that
     * has an attribute of the appropriate name.</p>
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.  Must not be null,
     *            but may be the empty string (and typically is).
     * @param localName
     *            The local-name part of the attribute name.  Must not be null.
     * @return the QName representing the type name for the designated attribute,
     * if that attribute exists in this location.  Null if the attribute does not
     * exist.  untyped-atomic if the attribute exists, but has no type annotation.
     * null if the cursor is not positioned over an element node.
     */
    QName getAttributeTypeName(String namespaceURI, String localName);

    /**
     * Returns the dm:typed-value of the attribute node with the specified expanded-QName.
     * <p>This is equivalent to moving to the attribute node and then retrieving its typed value.
     * This method only works if the current state is an element that
     * has an attribute of the appropriate name.</p>
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.  Must not be null,
     *            but may be the empty string (and typically is).
     * @param localName
     *            The local-name part of the attribute name.
     *            Must not be null.
     * @return a sequence of atoms, representing the typed value of the designated
     * attribute, if that attribute exists in this location.  Null if the attribute
     * does not exist.  untyped-atomic if the attribute exists, but is not validated.
     * null if the cursor is not positioned over an element node.
     */
    Iterable<? extends A> getAttributeValue(String namespaceURI, String localName);

    /**
     * Gets the type name of an element or attribute node.
     * <p>Returns <code>null</code> for all other node kinds.</p>
     * <p>Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-type-name">
     * dm:type-name</a> accessor in the XDM.</p>
     * 
     * @return the type name, if the current node context is an element or attribute;
     * otherwise null.  If the context node is an element or attribute, but is
     * not validated, returns an xs:untyped or xs:untyped-atomic.
     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-type-name
     */
    QName getTypeName();

    /**
     * Returns the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-typed-value">
     * dm:typed-value</a> property of the node.
     * 
     * <p>Applies to all node kinds.</p>
     * 
     * @return a sequence of atoms representing the typed-value of the supplied
     * node.  The typed-value of a node may be
     * rather complex (for documents and elements particularly), or very simple.
     * returns an xs:untyped-atomic (or string) if the node is not validated (or
     * for comment and processing instruction nodes).
     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-typed-value
     */
    Iterable<? extends A> getValue();// TODO: should throw exception if
    // called on an element with element-only content; required to 'raise an error'

}
