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
package org.genxdm.bridgekit.tree;

import org.genxdm.Cursor;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;

/**
 * Standard "delegating" pattern for implementing a {@link TraversingInformer} by
 * delegating to an existing instance.
 */
public class TraversingInformerDelegate extends InformerDelegate implements TraversingInformer {

    public TraversingInformerDelegate(TraversingInformer delegate) {
        super(delegate);
        m_delegate = delegate;
    }

    public void setInformer(TraversingInformer newInformer) {
        super.setInformer(newInformer);
        m_delegate = newInformer;
    }
    
    @Override
    public TraversingInformer getInformer() {
        return m_delegate;
    }
    
    @Override
    public Traverser traverseAncestorAxis() {
        return m_delegate.traverseAncestorAxis();
    }

    @Override
    public Traverser traverseAncestorOrSelfAxis() {
        return m_delegate.traverseAncestorOrSelfAxis();
    }

    @Override
    public Traverser traverseAttributeAxis(boolean inherit) {
        return m_delegate.traverseAttributeAxis(inherit);
    }

    @Override
    public Traverser traverseChildAxis() {
        return m_delegate.traverseChildAxis();
    }

    @Override
    public Traverser traverseChildElements() {
        return m_delegate.traverseChildElements();
    }

    @Override
    public Traverser traverseChildElementsByName(String namespaceURI, String localName) {
        return m_delegate.traverseChildElementsByName(namespaceURI, localName);
    }

    @Override
    public Traverser traverseDescendantAxis() {
        return m_delegate.traverseDescendantAxis();
    }

    @Override
    public Traverser traverseDescendantOrSelfAxis() {
        return m_delegate.traverseDescendantOrSelfAxis();
    }

    @Override
    public Traverser traverseFollowingAxis() {
        return m_delegate.traverseFollowingAxis();
    }

    @Override
    public Traverser traverseFollowingSiblingAxis() {
        return m_delegate.traverseFollowingSiblingAxis();
    }

    @Override
    public Traverser traverseNamespaceAxis(boolean inherit) {
        return m_delegate.traverseNamespaceAxis(inherit);
    }

    @Override
    public Traverser traversePrecedingAxis() {
        return m_delegate.traversePrecedingAxis();
    }

    @Override
    public Traverser traversePrecedingSiblingAxis() {
        return m_delegate.traversePrecedingAxis();
    }

    @Override
    public int compareTo(TraversingInformer o) {
        return m_delegate.compareTo(o);
    }

    @Override
    public Cursor newCursor() {
        return m_delegate.newCursor();
    }

    private TraversingInformer m_delegate;
}
