package org.genxdm.processor.w3c.xs.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.Cursor;
import org.genxdm.exceptions.PreCondition;

final class CursoryInputStream
    extends InputStream
{
    /** Initialize a reader from a cursor
     * 
     * @param cursor the cursor over which the reader operates, initialized
     * to the desired starting position; may not be null.
     */
    public CursoryInputStream(Cursor cursor, String encoding)
    {
        this.cursor = PreCondition.assertNotNull(cursor, "cursor");
        this.encoding = ( (encoding == null) || (encoding.trim().length() == 0) ) ? "UTF-8" : encoding; 
        originId = cursor.getNodeId();
    }

    @Override
    public int read()
        throws IOException
    {
        if ( (buffer == null) || (pointer >= (buffer.length - 1)) )
            lurch();
        if ( (buffer == null) || ( pointer == -1) )
            return -1;
        last = pointer; // it's not that i don't trust the jvm to do the right thing, it's just that i don't.
        pointer++;
        return buffer[last];
    }

    @Override
    public void close()
        throws IOException
    {
        // do nothing
    }
    
    /** Take the next step into the tree.
     * 
     * @throws IOException
     */
    private void lurch()
        throws IOException
    {
        // we know that we haven't processed the current position yet,
        // and that we should be on a child node.
        NodeKind kind = cursor.getNodeKind();
        switch (kind)
        {
            case DOCUMENT : // can only happen on startup
            {
                // nothing here to see; move along to the first child.
                if (cursor.moveToFirstChild())
                    lurch();
                // otherwise, we've still got an empty builder
                break;
            }
            case COMMENT :
            {
                builder.append("<!--" + cursor.getStringValue() + "-->");
                if (cursor.hasNextSibling())
                    cursor.moveToNextSibling();
                else
                    pop();
                break;
            }
            case PROCESSING_INSTRUCTION :
            {
                builder.append("<?" + cursor.getLocalName() + " " + cursor.getStringValue() + "?>");
                if (cursor.hasNextSibling())
                    cursor.moveToNextSibling();
                else
                    pop();
                break;
            }
            case TEXT :
            {
                builder.append(encoder.encodePCData(cursor.getStringValue()));
                if (cursor.hasNextSibling())
                    cursor.moveToNextSibling();
                else
                    pop();
                break;
            }
            case ELEMENT :
            {
                builder.append("<" + getQName(cursor.getPrefix(), cursor.getLocalName()));
                if (cursor.hasNamespaces())
                {
                    for (String prefix : cursor.getNamespaceNames(false))
                        builder.append(" xmlns" + (prefix.equals("") ? "" : ":" + prefix) + "=\"" + encoder.encodeCData(cursor.getNamespaceForPrefix(prefix)) + "\"");
                }
                if (cursor.hasAttributes())
                {
                    for (QName attribute : cursor.getAttributeNames(false))
                        builder.append(" " + getQName(attribute.getPrefix(), attribute.getLocalPart()) + "=\"" + encoder.encodeCData(cursor.getAttributeStringValue(attribute.getNamespaceURI(), attribute.getLocalPart())) + "\"");
                }
                boolean moved = false;
                if (!cursor.hasChildren())
                    builder.append("/>");
                else
                {
                    builder.append(">");
                    moved = cursor.moveToFirstChild();
                }
                if (!moved)
                {
                    if (cursor.hasNextSibling())
                        cursor.moveToNextSibling();
                    else
                        pop();
                }
                break;
            }
            // TODO: wait, maybe these two can be legal? just very short ...
            case NAMESPACE : // can only happen on startup; broken
            case ATTRIBUTE : // can only happen on startup; broken
            default :
            {
                throw new IOException("Invalid state");
            }
        }
        if (builder.length() > 0)
        {
            buffer = builder.toString().getBytes(encoding);
            pointer = 0;
            builder.delete(0, builder.length());
        }
        else
        {
            buffer = null;
            pointer = -1;
        }
    }
    
    /** Step up, insuring that we don't step backwards.
     * 
     */
    private void pop()
    {
        // we are inside a child node. there is no next sibling.
        // we want to move up to the parent, and write its close tag.
        // then we have to find the next thing to move to.
        cursor.moveToParent();
        if (cursor.getNodeKind() == NodeKind.DOCUMENT)
            return; // there's no more pop possible; we're done
        Object currentId = cursor.getNodeId();
        builder.append("</" + getQName(cursor.getPrefix(), cursor.getLocalName()) + ">");
        if (currentId.equals(originId))
            return; // we're back to where we started; *don't* continue.
        if (cursor.hasNextSibling())
            cursor.moveToNextSibling();
        else
            pop();
    }

    private String getQName(String prefix, String localName)
    {
        if ( (prefix != null) && !prefix.equals("") )
            return prefix + ":" + localName;
        return localName;
    }

    private final Cursor cursor;
    private final String encoding;
    private final Object originId;
    private final StringBuilder builder = new StringBuilder();
    private final XmlEncoder encoder = new XmlEncoder();
    
    private int pointer = -1;
    private int last = -1;
    private byte [] buffer;

}
