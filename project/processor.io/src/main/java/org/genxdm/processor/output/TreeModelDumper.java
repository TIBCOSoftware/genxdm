package org.genxdm.processor.output;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.exceptions.AtomCastException;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.types.NativeType;

public class TreeModelDumper
{
    /** Recommended entry point, with complete content.
     * 
     * @param origin the node representing the origin of the tree or subtree to be displayed; may not be null
     * @param tcx the typed context used to acquire information about the tree; may not be null
     * @param writer the target for output; may not be null
     */
    public static final <N, A> void displayTree(final N origin, final TypedContext<N, A> tcx, final Writer writer)
    {
        displayTree(PreCondition.assertNotNull(origin, "origin"),
                    PreCondition.assertNotNull(tcx, "typed context").getModel(),
                    tcx.getAtomBridge(),
                    PreCondition.assertNotNull(writer, "writer"), null, 0);
    }
    
    /** Variant entry point intended primarily for use with System.err/out
     *
     * Arguments are the same as those for the recommended type-aware entry point,
     * except that a PrintStream replaces the Writer.
     */
    public static final <N, A> void displayTree(final N origin, final TypedContext<N, A> tcx, final PrintStream stream)
    {
        displayTree(PreCondition.assertNotNull(origin, "origin"),
                    PreCondition.assertNotNull(tcx, "typed context").getModel(),
                    tcx.getAtomBridge(), null,
                    PreCondition.assertNotNull(stream, "stream"), 0);
    }
    
    /** Variant entry point for use only with untyped trees (no type-related information)
     * 
     * @param origin the node representing the origin of the tree or subtree to be displayed; may not be null
     * @param model the model used to acquire information about the tree; may not be null
     * @param writer the target for output; may not be null
     */
    public static final <N> void displayTree(final N origin, final Model<N> model, final Writer writer)
    {
        displayTree(PreCondition.assertNotNull(origin, "origin"),
                    PreCondition.assertNotNull(model, "model"),
                    PreCondition.assertNotNull(writer, "writer"),
                    null, 0);
    }
    
    /** Variant entry point intended primarily for use with System.err/out
    *
    * Arguments are the same as those for the recommended untyped entry point,
    * except that a PrintStream replaces the Writer.
    */
    public static final <N> void displayTree(final N origin, final Model<N> model, final PrintStream stream)
    {
        displayTree(PreCondition.assertNotNull(origin, "origin"),
                    PreCondition.assertNotNull(model, "model"), null,
                    PreCondition.assertNotNull(stream, "stream"), 0);
    }
    
    private static final <N, A> void displayTree(final N node, final TypedModel<N, A> model, final AtomBridge<A> atoms, final Writer writer, final PrintStream stream, final int indent)
    {
        final NodeKind kind = model.getNodeKind(node);
        switch (kind)
        {
            case DOCUMENT :
                // no fecking indent for document!
                splat(CURL_START+"Kind: "+kind+" Children "+BOX_START, writer, stream);
                for (final N child : model.getChildAxis(node))
                    displayTree(child, model, atoms, writer, stream, indent+1);
                splat(BOX_END+CURL_END, writer, stream);
                break;
            case ELEMENT :
                final QName type = model.getTypeName(node); 
                splat(indent(indent, writer, stream)+CURL_START+"Kind: "+kind+" Namespace: "+model.getNamespaceURI(node)+" Name: "+model.getLocalName(node)+" Prefix hint: "+model.getPrefix(node), writer, stream);
                if (model.hasNamespaces(node))
                {
                    splat(indent(indent+1, writer, stream)+"Namespaces "+BOX_START, writer, stream);
                    for (final N namespace : model.getNamespaceAxis(node, false))
                        displayTree(namespace, model, atoms, writer, stream, indent+2);
                    splat(indent(indent+1, writer, stream)+BOX_END, writer, stream);
                }
                if (model.hasAttributes(node))
                {
                    splat(indent(indent+1, writer, stream)+"Attributes "+BOX_START, writer, stream);
                    for (final N attribute : model.getAttributeAxis(node, false))
                        displayTree(attribute, model, atoms, writer, stream, indent+2);
                    splat(indent(indent+1, writer, stream)+BOX_END, writer, stream);
                }
                final boolean isUntyped = (type.equals(NativeType.UNTYPED_ATOMIC.toQName()) ||
                                           type.equals(NativeType.UNTYPED.toQName())) ? true : false;
                if (!isUntyped) // ugh. means typed
                {
                    final List<? extends A> value = (List<? extends A>)model.getValue(node);
                    final NativeType nType;
                    try
                    {
                        nType = atoms.getNativeType(atoms.unwrapAtom(value));
                    }
                    catch (AtomCastException ace)
                    {
                        // this is never thrown in unwraps
                        ace.printStackTrace();
                        throw new GenXDMException("Unwrap exception: "+ace.getMessage(), ace);
                    }
                    splat(indent(indent+1, writer, stream)+" Value (canonicalized): "+atoms.getC14NString(value)+" NativeType: "+nType+" Type name: {"+type.getNamespaceURI()+"}"+type.getLocalPart(), writer, stream);
                }
                if (model.hasChildren(node))
                {
                    boolean wroteContainer = false;
                    if (isUntyped) // write unconditionally if untyped
                    {
                        splat(indent(indent+1, writer, stream)+"Children "+BOX_START, writer, stream);
                        wroteContainer = true;
                    }
                    for (final N child : model.getChildAxis(node))
                    {
                        if (isUntyped)
                            displayTree(child, model, atoms, writer, stream, indent+2);
                        else // typed: we should have already written the sole text node
                        {
                            if (!(model.getNodeKind(child) == NodeKind.TEXT))
                            {
                                splat(indent(indent+1, writer, stream)+"Children "+BOX_START, writer, stream);
                                wroteContainer = true;
                                displayTree(child, model, atoms, writer, stream, indent+2);
                            }
                        }
                    }
                    if (wroteContainer)
                        splat(indent(indent+1, writer, stream)+BOX_END, writer, stream);
                }
                splat(indent(indent, writer, stream)+CURL_END, writer, stream);
                break;
            case NAMESPACE :
                splat(indent(indent, writer, stream)+CURL_START+"Kind: "+kind+" Prefix: "+model.getLocalName(node)+" Namespace: "+model.getStringValue(node)+CURL_END, writer, stream); 
                break;
            case ATTRIBUTE :
                final List<? extends A> value = (List<? extends A>)model.getValue(node);
                final NativeType nType;
                try
                {
                    nType = atoms.getNativeType(atoms.unwrapAtom(value));
                }
                catch (AtomCastException ace)
                {
                    // this is never thrown in unwraps
                    ace.printStackTrace();
                    throw new GenXDMException("Unwrap exception: "+ace.getMessage(), ace);
                }
                final QName typeName = model.getTypeName(node);
                splat(indent(indent, writer, stream)+CURL_START+"Kind: "+kind+" Namespace: "+model.getNamespaceURI(node)+" Name: "+model.getLocalName(node)+" Prefix hint: "+model.getPrefix(node)+" Value (canonicalized): "+atoms.getC14NString(value)+" NativeType: "+nType+" Type name: {"+typeName.getNamespaceURI()+"}"+typeName.getLocalPart()+CURL_END, writer, stream);
                break;
            case TEXT :
            case COMMENT :
                splat(indent(indent, writer, stream)+CURL_START+"Kind: "+kind+" Data: "+model.getStringValue(node)+CURL_END, writer, stream);
                break;
            case PROCESSING_INSTRUCTION :
                splat(indent(indent, writer, stream)+CURL_START+"Kind: "+kind+" Target: "+model.getLocalName(node)+" Data: "+model.getStringValue(node)+CURL_END, writer, stream);
                break;
            default :
                throw new GenXDMException("Unknown NodeKind: "+kind);
        }
        if (writer != null)
            finish(writer);
    }
    
    private static final <N> void displayTree(final N node, final Model<N> model, final Writer writer, final PrintStream stream, final int indent)
    {
        final NodeKind kind = model.getNodeKind(node);
        switch (kind)
        {
            case DOCUMENT :
                // no fecking indent for document!
                splat(CURL_START+"Kind: "+kind+" Children "+BOX_START, writer, stream);
                for (final N child : model.getChildAxis(node))
                    displayTree(child, model, writer, stream, indent+1);
                splat(BOX_END+CURL_END, writer, stream);
                break;
            case ELEMENT :
                splat(indent(indent, writer, stream)+CURL_START+"Kind: "+kind+" Namespace: "+model.getNamespaceURI(node)+" Name: "+model.getLocalName(node)+" Prefix hint: "+model.getPrefix(node), writer, stream);
                if (model.hasNamespaces(node))
                {
                    splat(indent(indent+1, writer, stream)+"Namespaces "+BOX_START, writer, stream);
                    for (final N namespace : model.getNamespaceAxis(node, false))
                        displayTree(namespace, model, writer, stream, indent+2);
                    splat(indent(indent+1, writer, stream)+BOX_END, writer, stream);
                }
                if (model.hasAttributes(node))
                {
                    splat(indent(indent+1, writer, stream)+"Attributes "+BOX_START, writer, stream);
                    for (final N attribute : model.getAttributeAxis(node, false))
                        displayTree(attribute, model, writer, stream, indent+2);
                    splat(indent(indent+1, writer, stream)+BOX_END, writer, stream);
                }
                if (model.hasChildren(node))
                {
                    splat(indent(indent+1, writer, stream)+"Children "+BOX_START, writer, stream);
                    for (final N child : model.getChildAxis(node))
                        displayTree(child, model, writer, stream, indent+2);
                    splat(indent(indent+1, writer, stream)+BOX_END, writer, stream);
                }
                splat(indent(indent, writer, stream)+CURL_END, writer, stream);
                break;
            case NAMESPACE :
                splat(indent(indent, writer, stream)+CURL_START+"Kind: "+kind+" Prefix: "+model.getLocalName(node)+" Namespace: "+model.getStringValue(node)+CURL_END, writer, stream); 
                break;
            case ATTRIBUTE :
                splat(indent(indent, writer, stream)+CURL_START+"Kind: "+kind+" Namespace: "+model.getNamespaceURI(node)+" Name: "+model.getLocalName(node)+" Prefix hint: "+model.getPrefix(node)+" Value: "+model.getStringValue(node)+CURL_END, writer, stream);
                break;
            case TEXT :
            case COMMENT :
                splat(indent(indent, writer, stream)+CURL_START+"Kind: "+kind+" Data: "+model.getStringValue(node)+CURL_END, writer, stream);
                break;
            case PROCESSING_INSTRUCTION :
                splat(indent(indent, writer, stream)+CURL_START+"Kind: "+kind+" Target: "+model.getLocalName(node)+" Data: "+model.getStringValue(node)+CURL_END, writer, stream);
                break;
            default :
                throw new GenXDMException("Unknown NodeKind: "+kind);
        }
        if (writer != null)
            finish(writer);
    }
    
    private static final String indent(final int indent, final Writer writer, final PrintStream stream)
    {
        final String tabEights;
        final String tabUnits;
        final int eights = indent / 8;
        final int units = indent % 8;
        
        switch (eights)
        {
            case 1 : tabEights = TAB8_1; break;
            case 2 : tabEights = TAB8_2; break;
            case 3 : tabEights = TAB8_3; break;
            case 4 : tabEights = TAB8_4; break;
            case 5 : tabEights = TAB8_5; break;
            case 6 : tabEights = TAB8_6; break;
            case 7 : tabEights = TAB8_7; break;
            default : tabEights = NONE;
        }
        
        switch (units)
        {
            case 1 : tabUnits = TAB_1; break;
            case 2 : tabUnits = TAB_2; break;
            case 3 : tabUnits = TAB_3; break;
            case 4 : tabUnits = TAB_4; break;
            case 5 : tabUnits = TAB_5; break;
            case 6 : tabUnits = TAB_6; break;
            case 7 : tabUnits = TAB_7; break;
            default : tabUnits = NONE;
        }
        
        return tabEights+tabUnits;
    }
    
    private static final void splat(final String string, final Writer writer, final PrintStream stream)
    {
        if (writer != null)
        {
            try
            {
                writer.write(string+NL);
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
                throw new GenXDMException("Serialization error: "+ioe.getMessage(), ioe);
            }
        }
        else
            stream.println(string);
    }
    
    private static final void finish(final Writer writer)
    {
        try
        {
            writer.flush();
            writer.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            throw new GenXDMException("Serialization error: "+ioe.getMessage(), ioe);
        }
    }
    
    private static final String CURL_START = "{ ";
    private static final String CURL_END = " }";
    private static final String BOX_START = "[ ";
    private static final String BOX_END = " ]";
    private static final String NL = "\n";

    private static final String NONE = "";
    private static final String TAB = "    ";
    
    private static final String TAB_1 = TAB;
    private static final String TAB_2 = TAB + TAB;
    private static final String TAB_3 = TAB + TAB_2;
    private static final String TAB_4 = TAB_2 + TAB_2;
    private static final String TAB_5 = TAB_2 + TAB_3;
    private static final String TAB_6 = TAB_3 + TAB_3;
    private static final String TAB_7 = TAB_3 + TAB_4;

    private static final String TAB8_1 = TAB_4 + TAB_4;
    private static final String TAB8_2 = TAB8_1 + TAB8_1;
    private static final String TAB8_3 = TAB8_1 + TAB8_2;
    private static final String TAB8_4 = TAB8_2 + TAB8_2;
    private static final String TAB8_5 = TAB8_2 + TAB8_3;
    private static final String TAB8_6 = TAB8_3 + TAB8_3;
    private static final String TAB8_7 = TAB8_3 + TAB8_4;
}
