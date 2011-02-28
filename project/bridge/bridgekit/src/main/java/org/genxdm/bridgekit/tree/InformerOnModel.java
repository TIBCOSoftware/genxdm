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
package org.genxdm.bridgekit.tree;

import java.net.URI;

import javax.xml.namespace.QName;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.nodes.Informer;

class InformerOnModel<N>
    implements Informer
{
    
    InformerOnModel(final N node, final Model<N> model)
    {
        this.node = PreCondition.assertNotNull(node, "node");
        this.model = PreCondition.assertNotNull(model, "model");
    }

    public Iterable<QName> getAttributeNames(boolean orderCanonical)
    {
        return model.getAttributeNames(node, orderCanonical);
    }

    public String getAttributeStringValue(String namespaceURI, String localName)
    {
        return model.getAttributeStringValue(node, namespaceURI, localName);
    }

    public URI getBaseURI()
    {
        return model.getBaseURI(node);
    }

    public URI getDocumentURI()
    {
        return model.getDocumentURI(node);
    }

    public int getLineNumber()
    {
        return -1;
    }

    public String getLocalName()
    {
        return model.getLocalName(node);
    }

    public Iterable<NamespaceBinding> getNamespaceBindings()
    {
        return model.getNamespaceBindings(node);
    }

    public String getNamespaceForPrefix(String prefix)
    {
        return model.getNamespaceForPrefix(node, prefix);
    }

    public Iterable<String> getNamespaceNames(boolean orderCanonical)
    {
        return model.getNamespaceNames(node, orderCanonical);
    }

    public String getNamespaceURI()
    {
        return model.getNamespaceURI(node);
    }
    
    public Object getNodeId()
    {
        return model.getNodeId(node);
    }
    
    public NodeKind getNodeKind()
    {
        return model.getNodeKind(node);
    }
    
    public String getPrefix()
    {
        return model.getPrefix(node);
    }

    public String getStringValue()
    {
        return model.getStringValue(node);
    }
    
    public boolean hasAttributes()
    {
        return model.hasAttributes(node);
    }

    public boolean hasChildren()
    {
        return model.hasChildren(node);
    }

    public boolean hasNamespaces()
    {
        return model.hasNamespaces(node);
    }

    public boolean hasNextSibling()
    {
        return model.hasNextSibling(node);
    }

    public boolean hasParent()
    {
        return model.hasParent(node);
    }

    public boolean hasPreviousSibling()
    {
        return model.hasPreviousSibling(node);
    }

    public boolean isAttribute()
    {
        return model.isAttribute(node);
    }

    public boolean isElement()
    {
        return model.isElement(node);
    }

    public boolean isId()
    {
        return model.isId(node);
    }

    public boolean isIdRefs()
    {
        return model.isIdRefs(node);
    }

    public boolean isNamespace()
    {
        return model.isNamespace(node);
    }

    public boolean isText()
    {
        return model.isText(node);
    }

    public boolean matches(NodeKind nodeKind, String namespaceURI, String localName)
    {
        return model.matches(node, nodeKind, namespaceURI, localName);
    }

    public boolean matches(String namespaceURI, String localName)
    {
        return model.matches(node, namespaceURI, localName);
    }

    protected final Model<N> model;
    protected N node;
}
