/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
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
package org.genxdm.processor.convert;

import java.util.List;

import org.genxdm.Cursor;
import org.genxdm.ProcessingContext;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.FragmentBuilder;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.types.AtomBridge;

public class Converter<N, A>
{
    public Converter(final ProcessingContext<N> context)
    {
        this.context = PreCondition.assertNotNull(context, "context");
    }

    public ProcessingContext<N> getProcessingContext()
    {
        return context;
    }
    
    /** Convert a node from one tree model to another, preserving type information if possible.
     * 
     * If both the processing context that owns this processor, and the supplied processing
     * context are typed, then a typed conversion will be attempted.  Otherwise, the conversion
     * will use base, text-only functionality.
     * 
     * @param <Ntrg> the target bridge's node handle
     * @param <Atrg> the target bridge's atom handle
     * @param source the source node (root of a tree/subtree) to convert
     * @param targetContext context associated with the target bridge
     * @return a node handle for the new tree
     * @throws GenXDMException if a problem in conversion is encountered
     */
    public <Ntrg, Atrg> Ntrg convert(N source, ProcessingContext<Ntrg> targetContext)
        throws GenXDMException
    {
        // if both contexts are typed, then do a typed-typed conversion.
        TypedContext<N, A> sourceTypedContext = context.getTypedContext(null);
        if (sourceTypedContext != null)
        {
            TypedContext<Ntrg, Atrg> targetTypedContext = targetContext.getTypedContext(null);
            if (targetTypedContext != null)
            {
                return convert(source, targetTypedContext.newSequenceBuilder(), targetTypedContext.getAtomBridge());
            }
        }
        // otherwise, do a text-only conversion.
        return convert(source, targetContext.newFragmentBuilder());
    }
    
    /** Do a simple conversion, tree model to tree model.
     * 
     * @param <Ntrg> the target node handle
     * @param source the source node (root of a tree/subtree)
     * @param builder a FragmentBuilder supplied by the target bridge
     * @return a target node corresponding to the source
     * @throws GenXDMException if a problem in conversion is encountered
     */
    public <Ntrg> Ntrg convert(N source, FragmentBuilder<Ntrg> builder)
        throws GenXDMException
    {
        builder.reset();
        // I like cursor.  :-)  besides, we use the model below.
        Cursor<N> cursor = context.newCursor(source);
        cursor.write(builder);
        List<Ntrg> nodeList = builder.getNodes();
        if (nodeList.size() != 1)
        {
            // never happen?  one in should mean one out ...
            throw new GenXDMException("Not a tree.");
        }
        return nodeList.get(0);
    }
    
    /** Convert from a type-enhanced model to another type-enhanced model.
     * 
     * The supplied SequenceBuilder will be wrapped in a SequenceConversionFilter,
     * which does the atom to atom conversion, using supplied atom bridges.
     * 
     * This conversion will fail if the context for this 
     * 
     * @param <Ntrg> the target node handle
     * @param <Atrg> the target atom handle
     * @param source the source node (root of a tree/subtree)
     * @param builder a SequenceBuilder supplied by the target bridge
     * @param targetBridge an AtomBridge supplied by the target bridge
     * @return a target node corresponding to the source node
     * @throws GenXDMException if a problem in conversion or casting is encountered
     */
    public <Ntrg, Atrg> Ntrg convert(final N source, SequenceBuilder<Ntrg, Atrg> builder,
                                     final AtomBridge<Atrg> targetBridge)
        throws GenXDMException
    {
        PreCondition.assertNotNull(context.getTypedContext(null), "context is typed");
        TypedContext<N, A> tc = context.getTypedContext(null);
        builder.reset();

        TypedModel<N, A> model = tc.getModel();
        SequenceConversionFilter<A, Atrg> filter = new SequenceConversionFilter<A, Atrg>(builder, tc.getAtomBridge(), targetBridge);
        model.stream(source, filter);

        List<Ntrg> nodeList = builder.getNodes();
        if (nodeList.size() != 1)
        {
            // never happen?  one in should mean one out ...
            throw new GenXDMException("Not a tree.");
        }
        return nodeList.get(0);
    }

    private final ProcessingContext<N> context;
}
