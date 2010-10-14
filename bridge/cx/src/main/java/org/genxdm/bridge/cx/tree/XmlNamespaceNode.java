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
package org.genxdm.bridge.cx.tree;

import javax.xml.XMLConstants;

import org.genxdm.NodeKind;

public final class XmlNamespaceNode
    extends XmlLeafNode
{

    XmlNamespaceNode(final XmlRootNode document, final String prefix, final String value)
    {
        super(NodeKind.NAMESPACE, document, value);
        this.localName = (prefix == null) ? XMLConstants.DEFAULT_NS_PREFIX : prefix;
        // if the following two bits seem weird, it's because these are *namespaces*,
        // not attributes.  it seems very weird to me, but it's correct according to the namespaces
        // spec.  if you look at the same node *as an attribute* (which you can't do), then
        // it will tell you that it's got a different prefix and namespace.
        this.namespaceURI = XMLConstants.NULL_NS_URI;
        this.prefixHint = XMLConstants.DEFAULT_NS_PREFIX;
    }

    public boolean isNamespace()
    {
        return true;
    }
}
