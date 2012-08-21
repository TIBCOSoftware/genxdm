package org.genxdm.processor.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.genxdm.Model;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.exceptions.XdmMarshalException;
import org.genxdm.processor.output.ContentHandlerOnXmlStreamWriter;

/** This is a simple mixin, though that isn't really possible in Java.
 *
 * It's the base class for the document handlers here; it implements the
 * shared write methods.
 */
abstract class DefaultSerializer<N>
{
    protected DefaultSerializer(final Model<N> model)
    {
        this.model = model;
        opf = XMLOutputFactory.newInstance();
    }
    
    protected DefaultSerializer(final XMLOutputFactory factory, final Model<N> model)
    {
        this.model = model;
        if (factory == null)
            opf = XMLOutputFactory.newInstance();
        else
            opf = factory;
    }
            
    
    public void write(final OutputStream byteStream, final N source, String encoding)
        throws IOException, XdmMarshalException
    {
        PreCondition.assertNotNull(byteStream, "byteStream");
        PreCondition.assertNotNull(source, "source");
        Charset cs = null;
        if (encoding != null)
        {
            if (Charset.isSupported(encoding))
            {
                cs = Charset.forName(encoding);
            }
        }
        if (cs == null)
        {
            cs = Charset.forName("UTF-8");
        }
        OutputStreamWriter writer = new OutputStreamWriter(byteStream, cs);
        write(writer, source);
        writer.flush();
    }
    
    public void write(Writer characterStream, N source)
        throws IOException, XdmMarshalException
    {
        PreCondition.assertNotNull(characterStream, "characterStream");
        PreCondition.assertNotNull(source, "source");
        try
        {
            XMLStreamWriter stream = opf.createXMLStreamWriter(characterStream);
            ContentHandlerOnXmlStreamWriter adapter = new ContentHandlerOnXmlStreamWriter(stream);
            //ContentWriter adapter = new ContentWriter(characterStream);
            model.stream(source, adapter);
            stream.flush();
        }
        catch (XMLStreamException xse)
        {
            throw new XdmMarshalException(xse);
        }
    }

    protected final XMLOutputFactory opf;
    protected final Model<N> model;

}
