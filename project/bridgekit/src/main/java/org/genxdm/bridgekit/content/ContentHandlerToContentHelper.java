package org.genxdm.bridgekit.content;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.genxdm.creation.Attrib;
import org.genxdm.creation.ContentHelper;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;

public class ContentHandlerToContentHelper
    implements ContentHandler
{
    public ContentHandlerToContentHelper(ContentHelper helper)
    {
        contentHelper = PreCondition.assertNotNull(helper, "helper");
    }

    @Override
    public void close()
        throws IOException
    {
    }

    @Override
    public void flush()
        throws IOException
    {
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GenXDMException
    {
        if (name != null)
        {
            if (attributes == null)
                attributes = new HashSet<Attrib>();
            attributes.add(contentHelper.newAttribute(namespaceURI, localName, value));
        }
    }

    @Override
    public void comment(String value)
        throws GenXDMException
    {
        childBoundary(null);
        contentHelper.comment(value);
    }

    @Override
    public void endDocument()
        throws GenXDMException
    {
        contentHelper.end();
    }

    @Override
    public void endElement()
        throws GenXDMException
    {
        if (endedSimple)
            endedSimple = false;
        else contentHelper.endComplex();
    }

    @Override
    public void namespace(String prefix, String namespaceURI)
        throws GenXDMException
    {
        if (name != null)
        {
            if (bindings == null)
                bindings = new HashMap<String, String>();
            bindings.put(prefix, namespaceURI);
        }
    }

    @Override
    public void processingInstruction(String target, String data)
        throws GenXDMException
    {
        childBoundary(null);
        contentHelper.pi(target, data);
    }

    @Override
    public void startDocument(URI documentURI, String docTypeDecl)
        throws GenXDMException
    {
        contentHelper.start();
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
        throws GenXDMException
    {
        childBoundary(null);
        namespace = namespaceURI;
        name = localName;
    }

    @Override
    public void text(String data)
        throws GenXDMException
    {
        if (name != null)
            childBoundary(data);
        // otherwise, assume it's ignorable whitespace?
    }
    
    private void childBoundary(String text)
    {
        if (name != null)
        {
            if (text != null)
            {
                contentHelper.simplexElement(namespace, name, bindings, attributes, text);
                endedSimple = true;
            }
            else
                contentHelper.startComplex(namespace, name, bindings, attributes);
            name = null;
            namespace = null;
            bindings = null;
            attributes = null;
        }
    }
    
    private String name = null;
    private String namespace = null;
    private Map<String, String> bindings = null;
    private Set<Attrib> attributes = null;
    
    private boolean endedSimple = false;

    private final ContentHelper contentHelper;
}
