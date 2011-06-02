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

public interface TypedNodeInformer<N, A>
{

    /**
     * Returns the type name of the attribute node with the specified expanded-QName.
     * <p>
     * This is equivalent to retrieving the attribute node and then its type name.
     * </p>
     * 
     * @param parent
     *            The node that is the parent of the attribute node.
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.
     * @param localName
     *            The local-name part of the attribute name.
     */
    QName getAttributeTypeName(N parent, String namespaceURI, String localName);

    /**
     * Returns the dm:typed-value of the attribute node with the specified expanded-QName.
     * <p>
     * This is equivalent to retrieving the attribute node and then its typed value.
     * </p>
     * 
     * @param parent
     *            The node that is the parent of the attribute node.
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.
     * @param localName
     *            The local-name part of the attribute name.
     */
    Iterable<? extends A> getAttributeValue(N parent, String namespaceURI, String localName);

    /**
     * Gets the type name of an element or attribute node. <br/>
     * Returns <code>null</code> for all other node kinds. <br/>
     * Corresponds to the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-type-name">
     * dm:type-name</a> accessor in the XDM.
     * 
     * @param node
     *            The node for which the type name is required.
     *            
     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-type-name
     */
    QName getTypeName(N node);

    /**
     * Returns the <a href="http://www.w3.org/TR/xpath-datamodel/#acc-summ-typed-value">
     * dm:typed-value</a> property of the node.
     * <p>
     * Applies to all node kinds.
     * </p>
     * <p>
     * If the node argument is <code>null</code>, then <code>null</code> is returned.
     * </p>
     * 
     * @param node
     *            The node for which dm:typed-value is required.
     * 
     * @see http://www.w3.org/TR/xpath-datamodel/#acc-summ-typed-value
     */
    Iterable<? extends A> getValue(N node);

}
