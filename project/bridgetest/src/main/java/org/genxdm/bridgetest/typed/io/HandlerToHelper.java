package org.genxdm.bridgetest.typed.io;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.content.BinaryContentHelperToEventQueue;
import org.genxdm.creation.Attrib;
import org.genxdm.creation.BinaryContentHelper;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;

// this is a first, because we're reversing the direction of work.
// our content helpers are supposed to be developer-friendly wrappers
// around the much more computer-oriented (computer-friendly?) handlers.
// here, we want to convert handlers to helpers, so that they can be
// fired from the helper to a handler. kind of a nonsensical way to
// test things, but we had to test it with something, and this reduces
// the number of things that can break to itself and the helper impl
// that it's testing. We're implicitly implementing the promoter interface
// (but still without promoting promoter, so actually re-defining it again)
// here as well, so that we can do blob testing, but first we get the
// non-blob testing working.
public class HandlerToHelper<N, A>
    implements ContentHandler
{
    public HandlerToHelper(final BinaryContentHelper targ)
    {
//        m_generator = PreCondition.assertNotNull(gen, "generator");
        m_helper = PreCondition.assertNotNull(targ, "helper");
    }
    
    //  in case we need one of these to test queueing
    public BinaryContentHelperToEventQueue<A> newQueue(final Map<String, String> bindings)
    {
        // precondition assertnotnull for bindings?
        return new BinaryContentHelperToEventQueue<A>(bindings); 
    }
    
    // next two are from the non-public Promoter interface.
    // if we have an untyped tree in memory, there's no reason to use these. But for people creating
    // content, we prolly ought to think about having these, or having the example of implementing
    // them, even without the interface extension in place.
    public void binaryAttribute(final String namespace, final String name, final String prefix, final byte [] data)
    {
        Attrib att = m_helper.newBinaryAttribute(namespace, name, data);
        if (pendingElement != null)
            pendingElement.addAttribute(new QName(namespace, name), att);
        else
            throw new GenXDMException("Attribute {"+namespace+"}"+name+" without an element!");
    }

    public void binaryText(final byte [] data)
    {
        if (pendingElement != null)
            pendingElement.setData(data);
        else
            throw new GenXDMException("Binary data without an element to contain it!");
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type) 
        throws GenXDMException
    {
        Attrib att = m_helper.newAttribute(namespaceURI, localName, value);
        if (pendingElement != null)
            pendingElement.addAttribute(new QName(namespaceURI, localName), att);
        else
            throw new GenXDMException("Attribute {"+namespaceURI+"}"+localName+" without an element!");
    }

    @Override
    public void comment(String value) 
        throws GenXDMException
    {
        // throw it away
        // don't flush pending yet
    }

    @Override
    public void endDocument() 
        throws GenXDMException
    {
        if (pendingElement != null)
            throw new GenXDMException("End of document with at least one element not closed");
        m_helper.end();
    }

    @Override
    public void endElement() 
        throws GenXDMException
    {
        if (pendingElement != null)
            sendStartElement(false);
        else // endElement() with no pendingELement is typical of reaching the end of a sequence of child elements
        {
            m_helper.endComplex();
            if (elementDepth > 0)
                elementDepth--;
            else throw new GenXDMException("Depth of complex element nesting mismatch, too many endElement()s?");
        }
    }

    @Override
    public void namespace(String prefix, String namespaceURI) 
        throws GenXDMException
    {
        if (pendingElement != null)
            pendingElement.addNamespace(prefix, namespaceURI);
        else
            throw new GenXDMException("Namespace binding xmlns:"+prefix+"+\""+namespaceURI+"\" without an element to provide context!");
    }

    @Override
    public void processingInstruction(String target, String data) 
        throws GenXDMException
    {
        // throw it away
        // don't flush pending yet
    }

    @Override
    public void startDocument(URI documentURI, String docTypeDecl) 
        throws GenXDMException
    {
        m_helper.start(); // simplest way of dealing with startDocument
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix) 
        throws GenXDMException
    {
//System.out.println("startElement("+namespaceURI+", "+localName+", "+prefix+")");
        if (pendingElement != null)
            sendStartElement(true);
        pendingElement = new StartElementEvent(namespaceURI, localName);
    }

    @Override
    public void text(String data) 
        throws GenXDMException
    {
        if (pendingElement !=null)
            pendingElement.setText(data);
        else
        {
            // finesse outside-of-element whitespace stuff
            if (!data.trim().isEmpty())
                throw new GenXDMException("Text \""+data+"\" without an element to contain it!");
        }
    }
    
    @Override
    public void close()
    {
        // don't bother
    }
    
    @Override
    public void flush()
    {
        // no
    }

    // slightly misnamed, does it need to be fixed?
    // this can only be called from startElement() or endElement().
    // from startElement hasChild == true; this flushes a complex startelement, nulls
    // pending element, and clears the path for the rest of the complex element's content 
    private void sendStartElement(boolean hasChild)
    {
 //System.out.println("sendStartElement("+hasChild+")");
        if (pendingElement != null) // we have something to create!
        {
//System.out.println("Pending element: {"+pendingElement.namespace+"}"+pendingElement.name+(hasChild?"(has child)":""));
            if (pendingElement.isBinary())
            {
                if (pendingElement.hasComplexity())
                    m_helper.binaryExElement(pendingElement.namespace, pendingElement.name, pendingElement.namespaces, pendingElement.attributes == null ? null : pendingElement.attributes.values(), pendingElement.data);
                else
                    m_helper.binaryElement(pendingElement.namespace, pendingElement.name, pendingElement.data);
            }
            else if (pendingElement.isSimple())
            {
                if (pendingElement.hasComplexity())
                    m_helper.simplexElement(pendingElement.namespace, pendingElement.name, pendingElement.namespaces, pendingElement.attributes == null ? null : pendingElement.attributes.values(), pendingElement.text);
                else
                    m_helper.simpleElement(pendingElement.namespace, pendingElement.name, pendingElement.text);
            }
            else if (hasChild) // startComplex
            {
                if (pendingElement.hasComplexity())
                    m_helper.startComplex(pendingElement.namespace, pendingElement.name, pendingElement.namespaces, pendingElement.attributes == null ? null : pendingElement.attributes.values());
                else
                    m_helper.startComplex(pendingElement.namespace, pendingElement.name);
                elementDepth++; // this happens here and not in startElement because startElement don't know nothing about its content
            }
            else // called from endElement(), an empty simple/complex element. treat as simple, because it requires less mucking about
            {
                if (pendingElement.hasComplexity())
                    m_helper.simplexElement(pendingElement.namespace, pendingElement.name, pendingElement.namespaces, pendingElement.attributes == null ? null : pendingElement.attributes.values(), pendingElement.text);
            }
            
            // since it wasn't null, it needs to become null
            pendingElement = null;
        }
        // else there's not a thing to do
    }
 
    static class StartElementEvent
    {
        StartElementEvent(final String ns, final String ln)
        {
            namespace = ns == null ? "" : ns;
            name = PreCondition.assertNotNull(ln, "local name");
        }
        
        boolean isSimple()
        {
            return (text != null);
        }
        
        boolean isBinary()
        {
            return (data != null);
        }
        
        boolean isEmpty()
        {
            return ( (text == null) && (data == null) );
        }
        
        boolean hasComplexity()
        {
            return ( (attributes != null) || (namespaces != null) );
        }
        
        void addNamespace(final String name, final String namespace)
        {
            if (namespaces == null)
                namespaces = new HashMap<String, String>();
            namespaces.put(name, namespace);
        }
        
        void addAttribute(final QName key, final Attrib value)
        {
            if (attributes == null)
                attributes = new HashMap<QName, Attrib>();
            attributes.put(key, value);
        }
        
        void setText(final String value)
        {
            // defeat pretty printing, but this may cause problems for empty elements?
            if (!value.trim().isEmpty())
                text = value;
        }
        
        void setData(final byte [] content)
        {
            data = content;
        }
        
        String namespace;
        String name;
        String text;
        byte [] data;
        Map<QName, Attrib> attributes;
        Map<String, String> namespaces;
    }

    private int elementDepth = 0; // only increments for complex,not simple/simplex
    private StartElementEvent pendingElement;

//    private final ContentGenerator m_generator;
    private final BinaryContentHelper m_helper;
}