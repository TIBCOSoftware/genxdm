package org.genxdm.bridgekit.filters;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.io.SequenceFilter;

public class FilteredSequenceBuilder<N, A>
    implements SequenceBuilder<N, A>
{

    public FilteredSequenceBuilder(SequenceFilter<A> filter, SequenceBuilder<N, A> builder)
    {
        this.filter = PreCondition.assertNotNull(filter);
        this.builder = PreCondition.assertNotNull(builder);
        filter.setOutputSequenceHandler(builder);
    }
    
    @Override
    public void attribute(String namespaceURI, String localName, String prefix, List<? extends A> data, QName type)
        throws GenXDMException
    {
        filter.attribute(namespaceURI, localName, prefix, data, type);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix, QName type)
        throws GenXDMException
    {
        filter.startElement(namespaceURI, localName, prefix, type);
    }

    @Override
    public void text(List<? extends A> data)
        throws GenXDMException
    {
        filter.text(data);
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix,
            String value, DtdAttributeKind type)
        throws GenXDMException
    {
            filter.attribute(namespaceURI, localName, prefix, value, type);
    }

    @Override
    public void comment(String value)
        throws GenXDMException
    {
        filter.comment(value);
    }

    @Override
    public void endDocument()
        throws GenXDMException
    {
        filter.endDocument();
    }

    @Override
    public void endElement()
        throws GenXDMException
    {
        filter.endElement();
    }

    @Override
    public void namespace(String prefix, String namespaceURI)
        throws GenXDMException
    {
        filter.namespace(prefix, namespaceURI);
    }

    @Override
    public void processingInstruction(String target, String data)
        throws GenXDMException
    {
        filter.processingInstruction(target, data);
    }

    @Override
    public void startDocument(URI documentURI, String docTypeDecl)
        throws GenXDMException
    {
        filter.startDocument(documentURI, docTypeDecl);
    }

    @Override
    public void startElement(String namespaceURI, String localName,
            String prefix)
        throws GenXDMException
    {
        filter.startElement(namespaceURI, localName, prefix);
    }

    @Override
    public void text(String data)
        throws GenXDMException
    {
        filter.text(data);
    }

    @Override
    public void close()
        throws IOException
    {
        filter.close();
    }

    @Override
    public void flush()
        throws IOException
    {
        filter.flush();
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

    @Override
    public void reset()
    {
        builder.reset();
    }

    private final SequenceBuilder<N, A> builder;
    private final SequenceFilter<A> filter;
}
