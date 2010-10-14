/**
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

public interface TypeInformer<A>
{

    /**
     * Returns the type name of the attribute node with the specified expanded-QName.
     * <p>
     * This is equivalent to retrieving the attribute node and then its type name.
     * </p>
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.
     * @param localName
     *            The local-name part of the attribute name.
     */
    QName getAttributeTypeName(String namespaceURI, String localName);

    Iterable<? extends A> getAttributeValue(String namespaceURI, String localName);

    /**
     * Gets the type name of an element or attribute node. <br/>
     * Returns <code>null</code> for all other node kinds. <br/>
     * Corresponds to the dm:type-name accessor in the XDM.
     * 
     */
    QName getTypeName();

    /**
     * @return the dm:typed-value property of the XDM.
     */
    Iterable<? extends A> getValue();

}
