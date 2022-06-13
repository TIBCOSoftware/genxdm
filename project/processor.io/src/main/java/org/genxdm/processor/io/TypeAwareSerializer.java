package org.genxdm.processor.io;

import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.exceptions.XdmMarshalException;
import org.genxdm.io.ContentStreamer;
import org.genxdm.processor.output.SequenceHandlerOnXmlStreamWriter;
import org.genxdm.typed.types.AtomBridge;

/** This is a simple mixin, though that isn't really possible in Java.
 *
 * It's a derivation of the DefaultSerializer base class. All it really
 * does is to add an AtomBridge to the constructor, and changes out the
 * ContenHandlerOnXmlStreamWriter in favor of SequenceHandlerOnXmlStreamWriter.
 * These changes give us type-aware streaming that should (in initial impl)
 * reduce the memory impact of Binary Large Objects (future development could
 * add configurability for serialization of other types as well).
 */
abstract class TypeAwareSerializer<N, A>
    extends DefaultSerializer<N>
{
    protected TypeAwareSerializer(final XMLOutputFactory factory, final ContentStreamer<N> model, final AtomBridge<A> bridge)
    {
        super(factory, model);
        this.bridge = PreCondition.assertNotNull(bridge, "bridge");
    }

    // all but one of the write() methods from DefaultSerializer are used as-is;
    // they call this one:
    @Override
    public void write(Writer characterStream, N source)
        throws IOException, XdmMarshalException
    {
        PreCondition.assertNotNull(characterStream, "characterStream");
        PreCondition.assertNotNull(source, "source");
        try
        {
            XMLStreamWriter stream = opf.createXMLStreamWriter(characterStream);
            SequenceHandlerOnXmlStreamWriter<A> adapter = new SequenceHandlerOnXmlStreamWriter<A>(stream, bridge);
//            ContentHandlerOnXmlStreamWriter adapter = new ContentHandlerOnXmlStreamWriter(stream);
            model.stream(source, adapter);
            stream.flush();
        }
        catch (XMLStreamException xse)
        {
            throw new XdmMarshalException(xse);
        }
    }

    protected final AtomBridge<A> bridge;
}
