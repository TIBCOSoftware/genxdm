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

import org.genxdm.Model;
import org.genxdm.Precursor;
import org.genxdm.nodes.Traverser;
import org.genxdm.nodes.TraversingInformer;

/**
 * Useful base class for implementing a {@link TraversingInformer} when starting with
 * a {@link Model} and nodes (&lt;N&gt;)
 *
 * @param <N> The type of the underlying node.
 */
public class TraversingInformerOnModel<N> extends InformerOnModel<N> implements TraversingInformer {

    public TraversingInformerOnModel(N node, Model<N> model) {
        super(node, model);
    }

    @Override
    public Precursor newPrecursor() {
        return new CursorOnModel<N>(node, model);
    }

    @Override
    public Traverser traverseAncestorAxis() {
        return new TraverserOnIterator<N>(model, model.getAncestorAxis(node).iterator());
    }

    @Override
    public Traverser traverseAncestorOrSelfAxis() {
        return new TraverserOnIterator<N>(model, model.getAncestorOrSelfAxis(node).iterator());
    }

    @Override
    public Traverser traverseAttributeAxis(boolean inherit) {
        return new TraverserOnIterator<N>(model, model.getAttributeAxis(node, inherit).iterator());
    }

    @Override
    public Traverser traverseChildAxis() {
        return new TraverserOnIterator<N>(model, model.getChildAxis(node).iterator());
    }

    @Override
    public Traverser traverseChildElements() {
        return new TraverserOnIterator<N>(model, model.getChildElements(node).iterator());
    }

    @Override
    public Traverser traverseChildElementsByName(String namespaceURI, String localName) {
        return new TraverserOnIterator<N>(model, model.getChildElementsByName(node, namespaceURI, localName).iterator());
    }

    @Override
    public Traverser traverseDescendantAxis() {
        return new TraverserOnIterator<N>(model, model.getDescendantAxis(node).iterator());
    }

    @Override
    public Traverser traverseDescendantOrSelfAxis() {
        return new TraverserOnIterator<N>(model, model.getDescendantOrSelfAxis(node).iterator());
    }

    @Override
    public Traverser traverseFollowingAxis() {
        return new TraverserOnIterator<N>(model, model.getFollowingAxis(node).iterator());
    }

    @Override
    public Traverser traverseFollowingSiblingAxis() {
        return new TraverserOnIterator<N>(model, model.getFollowingSiblingAxis(node).iterator());
    }

    @Override
    public Traverser traverseNamespaceAxis(boolean inherit) {
        return new TraverserOnIterator<N>(model, model.getNamespaceAxis(node, inherit).iterator());
    }

    @Override
    public Traverser traversePrecedingAxis() {
        return new TraverserOnIterator<N>(model, model.getPrecedingAxis(node).iterator());
    }

    @Override
    public Traverser traversePrecedingSiblingAxis() {
        return new TraverserOnIterator<N>(model, model.getPrecedingSiblingAxis(node).iterator());
    }

    @Override
    public int compareTo(TraversingInformer o) {
        if (! (o instanceof TraversingInformerOnModel)) {
            throw new IllegalArgumentException("Attempting to compare different classes of TraversingInformer.");
        }
        
        @SuppressWarnings({ "rawtypes", "unchecked" })
        TraversingInformerOnModel<N> otherTiom = (TraversingInformerOnModel) o;
        
        if (model.getClass() != otherTiom.model.getClass()) {
            throw new IllegalArgumentException("Attempting to compare different models.");
        }
        
        return model.compare(node, otherTiom.node);
    }

}