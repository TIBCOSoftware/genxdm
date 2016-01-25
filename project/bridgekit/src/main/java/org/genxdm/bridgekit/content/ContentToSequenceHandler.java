package org.genxdm.bridgekit.content;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentTypeKind;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

public class ContentToSequenceHandler<A>
    implements ContentHandler
{
    
    public ContentToSequenceHandler(SequenceHandler<A> output, ComponentProvider components, AtomBridge<A> atoms)
    {
        handler = PreCondition.assertNotNull(output, "sequence handler");
        provider = PreCondition.assertNotNull(components, "component provider");
        bridge = PreCondition.assertNotNull(atoms, "atom bridge");
    }

    @Override
    public void startDocument(URI documentURI, String docTypeDecl)
        throws GenXDMException
    {
        handler.startDocument(documentURI, docTypeDecl);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
        throws GenXDMException
    {
        // TODO : prolly too simple. this just goes for global decls,
        // based on the element qname.
        ElementDefinition decl = provider.getElementDeclaration(new QName(namespaceURI, localName, prefix));
        if (decl == null)
            throw new IllegalStateException("oops");
        elementTypes.add(decl);
        
        handler.startElement(namespaceURI, localName, prefix, decl.getType().getName());
    }

    @Override
    public void namespace(String prefix, String namespaceURI)
        throws GenXDMException
    {
        handler.namespace(prefix, namespaceURI);
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GenXDMException
    {
        // TODO Auto-generated method stub
        // biggest loss was this one, 'cause i don't have a clue
        // what we originally did here.
    }

    @Override
    public void comment(String value)
        throws GenXDMException
    {
        handler.comment(value);
    }

    @Override
    public void processingInstruction(String target, String data)
        throws GenXDMException
    {
        handler.processingInstruction(target, data);
    }

    @Override
    public void text(String data)
        throws GenXDMException
    {
        // TODO : too simple and incomplete to boot
        Type type = elementTypes.get(elementTypes.size() - 1).getType();
        if (type instanceof SimpleType)
        {
            try
            {
                List<A> actual = ((SimpleType)type).validate(data, bridge);
                handler.text(actual);
            }
            catch (DatatypeException de)
            {
                throw new GenXDMException(de);
            }
        }
        else
        {
            ContentTypeKind kind = ((ComplexType)type).getContentType().getKind(); 
            switch (kind)
            {
                default :
                    handler.text(data);
            }
        }
    }

    @Override
    public void endElement()
        throws GenXDMException
    {
        // TODO : too simple
        elementTypes.remove(elementTypes.size() - 1);
        handler.endElement();
    }

    @Override
    public void endDocument()
        throws GenXDMException
    {
        handler.endDocument();
    }

    @Override
    public void close()
        throws IOException
    {
        handler.close();
    }

    @Override
    public void flush()
        throws IOException
    {
        handler.flush();
    }

    private final SequenceHandler<A> handler;
    private final AtomBridge<A> bridge;
    private final ComponentProvider provider;
    
    private final List<ElementDefinition> elementTypes = new ArrayList<ElementDefinition>();
}
