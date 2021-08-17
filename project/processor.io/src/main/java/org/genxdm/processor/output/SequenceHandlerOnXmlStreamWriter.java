package org.genxdm.processor.output;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.genxdm.bridgekit.atoms.Base64BinarySupport;
import org.genxdm.bridgekit.atoms.HexBinarySupport;
import org.genxdm.exceptions.AtomCastException;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.types.NativeType;

// not really the best way to 
public class SequenceHandlerOnXmlStreamWriter<A> extends
        ContentHandlerOnXmlStreamWriter implements SequenceHandler<A> 
{

    public SequenceHandlerOnXmlStreamWriter(XMLStreamWriter output, AtomBridge<A> atomBridge) 
    {
        super(output);
        this.atomBridge = PreCondition.assertNotNull(atomBridge, "atomBridge");
    }
    
    // default for breakLines is true, default for lineEnding is \r\n.
    // only allowed values for lineEnding are \r\n and \n; discard silently otherwise
    public void setBase64Formatting(final boolean insertLineBreaks, final String lineEndChars)
    {
        breakLines = insertLineBreaks;
        if (BARELF.equals(lineEndChars))
            lineEnding = BARELF;
        else
            lineEnding = CRLF;
    }
    
    // TODO: add line breaking to hex?
    
    // TODO: allow configuration of the write buffer size? maybe in multiples
    // of 1KB chars at a time? right now it's just 1KB chars each time. This
    // should be *safe* for memory, which is why we're doing this at all. But
    // it might need tweaked for less memory safety and more speed.

    @Override
    public void attribute(String namespaceURI, String localName, String prefix,
                          List<? extends A> data, QName type) 
        throws GenXDMException 
    {
        super.attribute(namespaceURI, localName, prefix, atomBridge.getC14NString(data), (DtdAttributeKind)null);
    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String prefix, QName type) 
        throws GenXDMException 
    {
        super.startElement(namespaceURI, localName, prefix);
    }

    @Override
    public void text(List<? extends A> data) 
        throws GenXDMException 
    {
        // added code to handle binary types. this reduces the required memory
        // to write this stuff out from 3/3 + 8/3 (for base64: 3 characters
        // become 4 2-byte characters) or 1/1 + 4/1 (for hex: 1 character becomes
        // 2 2-byte characters) to original size of each array + 768+2048 bytes
        // for base64, 512+2048 bytes for hex, as each block that will produce
        // 1024 characters is processed in turn. We could prolly tune this by
        // allowing the size of the buffer to be managed.
        flushPending();
        if ((data == null) || data.isEmpty())
            return; // short-circuit for speed
        try
        {
            A first = atomBridge.unwrapAtom(data); // not null or empty: safe
            if (isBinary(first)) // is this lazy? there may be more than one? for binary? nah
            {
                final boolean b64 = atomBridge.getNativeType(first) == NativeType.BASE64_BINARY;
                final byte [] val = b64 ? atomBridge.getBase64Binary(first)
                                        : atomBridge.getHexBinary(first);
                final int blockSize = b64 ? B64_KB_CHAR : HEX_KB_CHAR;
                final int nblocks = (val.length / blockSize) + 1;
                for (int i = 0; i < nblocks; i++)
                {
                    boolean last = false;
                    if ((i+1) == nblocks)
                        last = true;
                    final byte [] block = Arrays.copyOfRange(val, i * blockSize, (last ? val.length : (i+1)*blockSize));
                    writeBlock(block, b64);
                }
            }
            else
                super.text(atomBridge.getC14NString(data));
        }
        catch (AtomCastException ace) // this one doesn't actually happen.
        {
            throw new GenXDMException(ace);
        }
        catch (XMLStreamException xse) // this one could, though.
        {
            throw new GenXDMException(xse);
        }
    }
    
    private boolean isBinary(final A atom)
    {
        boolean rv = false;
        if (atom != null)
        {
            NativeType at = atomBridge.getNativeType(atom);
            if ((at == NativeType.BASE64_BINARY) || (at == NativeType.HEX_BINARY))
              rv = true;
        }
        return rv;
    }
    
    private void writeBlock(final byte [] block, boolean isB64)
        throws XMLStreamException
    {
        if (isB64)
        {
            if (breakLines)
                output.writeCharacters(Base64BinarySupport.encodeBase64(block, lineEnding));
            else
                output.writeCharacters(Base64BinarySupport.encodeBase64(block, !breakLines)); // utility uses "dontbreaklines" which is hard to keep straight, so we do it straight here
        }
        else // is hex binary: no configuration, no lines, just a block of characters all one line.
            output.writeCharacters(HexBinarySupport.encodeHex(block));
    }
    
    final private AtomBridge<A> atomBridge;
    private String lineEnding = CRLF;
    private boolean breakLines = true;
    
    private static final int B64_KB_CHAR = 768; // number of bytes that will produce 1k output
    private static final int HEX_KB_CHAR = 512;
    private static final String CRLF = "\r\n";
    private static final String BARELF = "\n";
}
