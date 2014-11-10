package org.genxdm.processor.io.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.genxdm.NodeSource;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.io.FragmentBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/** A SAX ContentHandler implementation that drives a FragmentBuilder to
 * construct a tree in memory.
 * 
 * @param <N> the node abstraction.
 */
public class NodeBuilderContentHandler<N>
    implements ContentHandler, NodeSource<N>
{  // TODO: also implement LexicalHandler? adds comments, but also CDATA and DTD
    /** Initialize the contenthandler.
     * 
     * @param builder the FragmentBuilder to be used in construction;
     * may not be null.
     */
    public NodeBuilderContentHandler(FragmentBuilder<N> builder)
    {
        this.builder = PreCondition.assertNotNull(builder, "builder");
    }

    @Override
    public void setDocumentLocator(Locator locator)
    {
        this.locator = locator;
    }

    @Override
    public void startDocument()
        throws SAXException
    {
        builder.startDocument(null, null);
    }

    @Override
    public void endDocument()
        throws SAXException
    {
        builder.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException
    {
        namespaces.put(prefix, uri);
        // TODO: set up prefix mappings for use in startElement?
    }

    @Override
    public void endPrefixMapping(String prefix)
        throws SAXException
    {
        // do nothing?
        // TODO: tear down prefix mappings?
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts)
        throws SAXException
    {
        flushText();
        String ns, name, prefix;
        if ( (uri == null) || (localName == null) ) // ick
        {
            if (qName == null)
                throw new SAXException("Unnamed element.");
            else
            {
                // TODO revisit
                // assume a simple name, for now.
                ns = prefix = "";
                name = qName;
            }
        }
        else
        {
            ns = uri;
            name = localName;
            if (qName != null)
            {
                // TODO extract the prefix
                prefix = "";
            }
            else
                prefix = ""; // TODO prefix lookup from namespace
        }
        builder.startElement(ns, name, prefix);
        for (String p : namespaces.keySet())
        {
            builder.namespace(p, namespaces.get(p));
        }
        namespaces.clear();
        for (int i = 0 ; i < atts.getLength() ; i++)
        {
            // TODO extract prefix from QName
            // TODO determine attribute kind
            builder.attribute(atts.getURI(i), atts.getLocalName(i),
                              "", atts.getValue(i), DtdAttributeKind.CDATA);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
        throws SAXException
    {
        flushText();
        builder.endElement();
    }

    @Override
    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        buffer.append(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException
    {
        buffer.append(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data)
        throws SAXException
    {
        flushText();
        builder.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name)
        throws SAXException
    {
        // ignore
    }

    @Override
    public List<N> getNodes()
    {
        return builder.getNodes();
    }

    @Override
    public N getNode()
    {
        return builder.getNode();
    }
    
    private void flushText()
    {
        if (buffer.length() == 0)
            return;
        builder.text(buffer.toString());
        buffer.delete(0, buffer.length());
    }
    
    private final FragmentBuilder<N> builder;
    private final Map<String, String> namespaces = new HashMap<String, String>();
    private final StringBuilder buffer = new StringBuilder();
    
    private Locator locator;

}
