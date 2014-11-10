/**
 * Copyright (c) 2012 TIBCO Software Inc.
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
package org.genxdm.xs.components;

import javax.xml.namespace.QName;

/** Foreign attributes may be found on most schema elements; they are preserved in the
 * schema model as a collection of attributes.
 *
 * A "foreign attribute" is an attribute in namespaces other than the schema namespace (and friends).
 * These attributes are not validated (they are 'skipped' in schema parlance).
 */
public interface ForeignAttributes
{
     /**
     * Returns the foreign attribute names.
     * 
     * @return an iterable over the QNames of the foreign attributes found on this schema abstraction;
     * never null, but may be empty.
     */
    Iterable<QName> getForeignAttributeNames();
    
    /**
     * Returns a value of a foreign attribute
     * 
     * @param name The name of the attribute of interest. Must not be null.
     * @return The value associated with the attribute of the given name, if any; null if
     * no such attribute exists.
     */
    String getForeignAttributeValue(QName name);
    
    // TODO : add support for QNames in content by providing a getNamespaceContext() method
    // or the equivalent (GenXDM's namespace resolver?).
}
