package org.genxdm.bridge.cx.typed;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridge.cx.tree.XmlElementNode;
import org.genxdm.bridge.cx.tree.XmlLeafNode;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.validation.ValidatingCursor;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.io.ContentHandler;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.typed.io.SequenceHandler;

class TreeCursor
    extends TypedXmlNodeCursor
    implements ValidatingCursor<XmlNode, XmlAtom>
{
    TreeCursor(final TypedXmlNodeContext context, final XmlNode node)
    {
        super(context, node);
    }

    @Override
    public void write(SequenceHandler<XmlAtom> handler, boolean bogus)
        throws GenXDMException
    {
        visit(handler);
    }

    @Override
    public void write(ContentHandler writer)
        throws GenXDMException
    {
        // if this one is called, we're in trouble!
        throw new UnsupportedOperationException("call to untyped write");
    }

    @Override
    public void startDocument(URI documentURI, String docTypeDecl)
        throws GenXDMException
    {
        // nothing to do; there's nothing in fired.
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix, QName type)
        throws GenXDMException
    {
        boolean found = false;
        for (XmlNode target : fired)
        {
            if (target.getNamespaceURI().equals(namespaceURI) &&
                target.getLocalName().equals(localName) &&
                target.isElement() )
            {
                ((XmlElementNode)target).setTypeName(type);
                fired.remove(target);
                found = true;
                break;
            }
//System.out.println("Got element " + localName + " with type " + type.getLocalPart());
        }
        if (!found)
            throw new GenXDMException("validation error; can't find the element to annotate");
    }

    @Override
    public void namespace(String prefix, String namespaceURI)
        throws GenXDMException
    {
        // TODO: make sure that the process doesn't screw up bindings.
        // namespaces are not in the fired queue
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, List<? extends XmlAtom> data, QName type)
        throws GenXDMException
    {
        boolean found = false;
        for (XmlNode target : fired)
        {
            if (target.getNamespaceURI().equals(namespaceURI) &&
                target.getLocalName().equals(localName) &&
                target.isAttribute() )
            {
                ((XmlLeafNode)target).setTypeName(type);
                ((XmlLeafNode)target).setValue(data);
                fired.remove(target);
                found = true;
                break;
            }
//System.out.println("Got attribute " + localName + " with type " + type.getLocalPart() + " and value " + data);
        }
        if (!found)
        {
            // we *might* want to throw an exception. but i think we prolly want to
            // add a new attribute that has a default value, don't you?
            // but in that case, we have to find the element to put it on!
            throw new GenXDMException("validation error; can't find the attribute to annotate and update");
        }
    }

    @Override
    public void text(List<? extends XmlAtom> data)
        throws GenXDMException
    {
        boolean found = false;
        for (XmlNode target : fired)
        {
            if (target.isText()) // assume only one? not really safe; how do we do better?
            {
                // it *should* be safe, assuming that whitespace nodes can't be
                // passed. might fail with mixed content? but then we don't have
                // typed value ...
                // note that we *might* get ignorable whitespace in this call.
                // that's gonna fail ....
                // prolly need to compare original to new data?
                // but it has to be kinda fuzzy.
                ((XmlLeafNode)target).setValue(data);
                fired.remove(target);
                found = true;
//System.out.println("Got text \"" + data + "\"");
                break;
            }
        }
        if (!found)
            throw new GenXDMException("validation error; can't find the text node to update");
    }

    @Override
    public void processingInstruction(String target, String data)
        throws GenXDMException
    {
        // not in the fired queue. ignore it
    }

    @Override
    public void comment(String value)
        throws GenXDMException
    {
        // not in the fired queue. ignore it
    }

    @Override
    public void endElement()
        throws GenXDMException
    {
        // i don't think there's anything to be done.
        // stored as a node in startElement, and already processed?
    }

    @Override
    public void endDocument()
        throws GenXDMException
    {
        // anything?
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
        throws GenXDMException
    {
//System.out.println("Got untyped element " + localName);
        // validator should never call this one; throw a fit if it does.
        throw new UnsupportedOperationException("untyped start-element");
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GenXDMException
    {
//System.out.println("Got untyped attribute " + localName + " with value " + value);
        // validator should never call this one; throw a fit if it does.
        throw new UnsupportedOperationException("untyped attribute");
    }

    @Override
    public void text(String data)
        throws GenXDMException
    {
//System.out.println("Got untyped text \"" + data + "\"");
        // Note: empty nodes or nodes containing whitespace only are not in
        // the queue. if we get this call and it's ignorable whitespace,
        // drop it silently. otherwise: exception
        if (!data.trim().isEmpty())
            throw new UnsupportedOperationException("untyped text");
    }

    @Override
    public void close()
        throws IOException
    {
        // ignore
    }

    @Override
    public void flush()
        throws IOException
    {
        // ignore.
    }
    
    private void visit(SequenceHandler<XmlAtom> handler)
    {
        switch (node.getNodeKind())
        {
            case ELEMENT:
            {
                fired.add(node);
                handler.startElement(getNamespaceURI(), getLocalName(), getPrefix(), getTypeName());
                for (NamespaceBinding namespace : getNamespaceBindings())
                {
                    handler.namespace(namespace.getPrefix(), namespace.getNamespaceURI());
                }
                for (QName att : getAttributeNames(true))
                {
                    moveToAttribute(att.getNamespaceURI(), att.getLocalPart());
                    fired.add(node);
                    handler.attribute(node.getNamespaceURI(), node.getLocalName(), node.getPrefix(), (List<? extends XmlAtom>)node.getValue(), node.getTypeName());
                    moveToParent();
                }
                if (hasChildren())
                {
                    moveToFirstChild();
                    visit(handler);
                    while (hasNextSibling())
                    {
                        moveToNextSibling();
                        visit(handler);
                    }
                    moveToParent();
                }
                handler.endElement();
                break;
            }
            case ATTRIBUTE:
            {
                // doesn't happen; only happens inside element
                break;
            }
            case TEXT:
            {
                if (!node.getStringValue().trim().isEmpty())
                    fired.add(node);
                handler.text((List<? extends XmlAtom>)node.getValue());
                break;
            }
            case DOCUMENT:
            {
                handler.startDocument(node.getDocumentURI(), /*((XmlRootNode)node).docTypeDecl*/null);
                moveToFirstChild();
                visit(handler);
                while (hasNextSibling())
                {
                    moveToNextSibling();
                    visit(handler);
                }
                moveToParent();
                handler.endDocument();
                break;
            }
            case NAMESPACE:
            {
                // doesn't happen
                break;
            }
            case COMMENT:
            {
                handler.comment(node.getStringValue());
                break;
            }
            case PROCESSING_INSTRUCTION:
            {
                handler.processingInstruction(node.getLocalName(), node.getStringValue());
                break;
            }
            default:
            {
                throw new AssertionError(node.getNodeKind());
            }
        }
    }

    // the notion here is that we don't need to synchronize, because
    // we add a node to this list, invoke the validator, and the validator
    // will either fire back, or will queue, and then fire back a sequence
    // of events. but we are initialized, and the validator is single-threaded,
    // so there are no opportunities to concurrently modify.
    // NOTA BENE: we may want to consider the possibility of doing this differently,
    // by catching all the bits of a start tag together on the way in (and also
    // catching the text node of elements with simple content, except that that
    // kinda makes us want to be predictive, when this process is what gives us
    // the type that we would use to predict with).
    private final List<XmlNode> fired = new ArrayList<XmlNode>();
}
