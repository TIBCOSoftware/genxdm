package org.genxdm.bridgekit.content;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.NodeSource;
import org.genxdm.ProcessingContext;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.TypedContext;

/** A helper tool to locate the document element, given a NodeSource and a
 * context.
 */
public final class DocumentElement
{
    private DocumentElement() {} // prevent instantiation
    
    public static <N> N get(ProcessingContext<N> context, NodeSource<N> source)
    {
        PreCondition.assertNotNull(context, "context");
        PreCondition.assertNotNull(source, "source");
        N result = source.getNode();
        if (result != null)
        {
            Model<N> model = context.getModel();
            N document = null;
            if (model.getNodeKind(result) == NodeKind.DOCUMENT)
                document = result;
            else // not the document, so inside a document?
            {
                document = model.getRoot(result);
                if (model.getNodeKind(document) != NodeKind.DOCUMENT) //oops
                {
                    if (model.getNodeKind(document) == NodeKind.ELEMENT) // oh, one of those
                        return document; // actually root element without a document
                    else
                        document = null;
                }
            }
            if (document != null) // and we know it *is* a document 
                return model.getFirstChildElement(document); // doc element
        }
        return null; // nothing in the node source
    }
    
    public static <N, A> N get(TypedContext<N, A> context, NodeSource<N> source)
    {
        if (context != null)
            return get(context.getProcessingContext(), source);
        return null;
    }
}
