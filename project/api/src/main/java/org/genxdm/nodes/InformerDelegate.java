/*
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
package org.genxdm.nodes;

import java.net.URI;

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.names.NamespaceBinding;

/**
 * Standard "delegate" pattern for implementing an {@link Informer} by delegating
 * to an existing instance of one.
 */
public class InformerDelegate implements Informer {

    public InformerDelegate(Informer inf) {
        informer = inf;
    }
    
    public void setInformer(Informer newInformer) {
        informer = newInformer;
    }
    
    public Informer getInformer() {
        return informer;
    }
    
    @Override
    public Iterable<QName> getAttributeNames(boolean orderCanonical) {
        return informer.getAttributeNames(orderCanonical);
    }

    @Override
    public String getAttributeStringValue(String namespaceURI, String localName) {
        return informer.getAttributeStringValue(namespaceURI, localName);
    }

    @Override
    public int getLineNumber() {
        return informer.getLineNumber();
    }

    @Override
    public String getLocalName() {
        return informer.getLocalName();
    }

    @Override
    public String getNamespaceForPrefix(String prefix) {
        return informer.getNamespaceForPrefix(prefix);
    }

    @Override
    public Iterable<String> getNamespaceNames(boolean orderCanonical) {
        return informer.getNamespaceNames(orderCanonical);
    }

    @Override
    public String getNamespaceURI() {
        return informer.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return informer.getPrefix();
    }

    @Override
    public String getStringValue() {
        return informer.getStringValue();
    }

    @Override
    public boolean hasAttributes() {
        return informer.hasAttributes();
    }

    @Override
    public boolean hasNamespaces() {
        return informer.hasNamespaces();
    }

    @Override
    public URI getBaseURI() {
        return informer.getBaseURI();
    }

    @Override
    public URI getDocumentURI() {
        return informer.getDocumentURI();
    }

    @Override
    public Iterable<NamespaceBinding> getNamespaceBindings() {
        return informer.getNamespaceBindings();
    }

    @Override
    public Object getNodeId() {
        return informer.getNodeId();
    }

    @Override
    public NodeKind getNodeKind() {
        return informer.getNodeKind();
    }

    @Override
    public boolean hasChildren() {
        return informer.hasChildren();
    }

    @Override
    public boolean hasNextSibling() {
        return informer.hasNextSibling();
    }

    @Override
    public boolean hasParent() {
        return informer.hasParent();
    }

    @Override
    public boolean hasPreviousSibling() {
        return informer.hasPreviousSibling();
    }

    @Override
    public boolean isAttribute() {
        return informer.isAttribute();
    }

    @Override
    public boolean isElement() {
        return informer.isElement();
    }

    @Override
    public boolean isId() {
        return informer.isId();
    }

    @Override
    public boolean isIdRefs() {
        return informer.isIdRefs();
    }

    @Override
    public boolean isNamespace() {
        return informer.isNamespace();
    }

    @Override
    public boolean isText() {
        return informer.isText();
    }

    @Override
    public boolean matches(NodeKind nodeKind, String namespaceURI, String localName) {
        return informer.matches(nodeKind, namespaceURI, localName);
    }

    @Override
    public boolean matches(String namespaceURI, String localName) {
        return informer.matches(namespaceURI, localName);
    }

    private Informer informer;

}
