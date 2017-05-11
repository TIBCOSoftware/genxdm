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
package org.genxdm.bridgekit.tree;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceHandler;

/**
 * Implementation of a {@link GuCursor} when only a model is available.
 * 
 * This is likely to be less efficient than implementing a custom cursor.
 * 
 * @param <N>
 *            The node implementation type.
 */
public class CursorOnTypedModel<N, A> 
    extends CursorOnModel<N> 
    implements TypedCursor<N, A>
{
    public CursorOnTypedModel(final N node, final TypedModel<N, A> model)
    {
        super(node, model);
        this.tmodel = (TypedModel<N, A>)model;
    }

    @Override
    public QName getAttributeTypeName(String namespaceURI, String localName)
    {
        return tmodel.getAttributeTypeName(node, namespaceURI, localName);
    }
    
    @Override
    public Iterable<? extends A> getAttributeValue(final String namespaceURI, final String localName)
    {
        return tmodel.getAttributeValue(node, namespaceURI, localName);
    }

    @Override
    public QName getTypeName()
    {
        return tmodel.getTypeName(node);
    }
    
    @Override
    public Iterable<? extends A> getValue()
    {
        return tmodel.getValue(node);
    }

    @Override
    public void write(SequenceHandler<A> handler, boolean bogus)
        throws GenXDMException
    {
        tmodel.stream(node, handler);
    }

    private final TypedModel<N, A> tmodel;
}
