package org.genxdm.bridge.cx.typed;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.bridge.cx.tree.XmlAttributeNode;
import org.genxdm.bridge.cx.tree.XmlElementNode;
import org.genxdm.bridge.cx.tree.XmlLeafNode;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridge.cx.tree.XmlNodeMutator;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.validation.ChildNode;
import org.genxdm.bridgekit.validation.StartTag;
import org.genxdm.bridgekit.validation.TextNode;
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
        for (ChildNode<XmlNode> target : startTags)
        {
            // if the first one is a text node, is that an error?
            // do we at least need to discard it?
            //if (target.isText()) {}
            if (target.isStartTag())
            {
                StartTag<XmlNode> candidate = (StartTag<XmlNode>)target;
                if (candidate.element.getNamespaceURI().equals(namespaceURI) &&
                    candidate.element.getLocalName().equals(localName) )
                {
                    ((XmlElementNode)candidate.element).setTypeName(type);
                    startTag = candidate;
                    startTags.remove(target);
                    found = true;
                    break;
                }
            }
        }
//if (found)
//  System.out.println("Got element " + localName + " with type " + type.getLocalPart());
        if (!found)
            throw new GenXDMException("validation error; can't find the element to annotate");
    }

    @Override
    public void namespace(String prefix, String namespaceURI)
        throws GenXDMException
    {
        boolean matched = false;
        if ( (startTag != null) && (startTag.bindings != null) )
        {
            for (String candidate : startTag.bindings.keySet())
            {
                if (candidate.equals(prefix))
                {
                    String ns = startTag.bindings.get(candidate); 
                    if (ns.equals(namespaceURI))
                    {
                        matched = true;
                        break;
                    }
                    // oops. got the prefix here, but it's a different namespace.
                    // trust the validator? that would mean: remove the original
                    // binding, and add this new one.
                    // for now, toss cookies
                    throw new IllegalStateException("prefix " + prefix + " for namespace " + namespaceURI + " already bound to " + ns); 
                }
            }
        }
        if (!matched && (startTag != null) )
        {
            // add the binding to the start tag
            XmlNodeMutator mutant = new XmlNodeMutator();
            mutant.insertNamespace(startTag.element, prefix, namespaceURI);
        } // else things are very, very strange; we have a namespace event without a start tag
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, List<? extends XmlAtom> data, QName type)
        throws GenXDMException
    {
        boolean found = false;
//System.out.println("Got attribute " + localName + " with type " + type.getLocalPart() + " and value " + data);
        if ( (startTag != null) && (startTag.attributes != null) )
        {
            for (XmlNode attribute : startTag.attributes)
            {
                if (attribute.getNamespaceURI().equals(namespaceURI) &&
                    attribute.getLocalName().equals(localName) )
                {
                    ((XmlLeafNode)attribute).setTypeName(type);
                    ((XmlLeafNode)attribute).setValue(data);
                    found = true;
                    break;
                }
            }
        }
        if (!found)
        {
            XmlNodeMutator mutant = new XmlNodeMutator();
            // add it
            XmlAttributeNode attribute = (XmlAttributeNode)mutant.getFactory(startTag.element).createAttribute(namespaceURI, localName, prefix, "");
            mutant.insertAttribute(startTag.element, attribute);
            attribute.setTypeName(type);
            attribute.setValue(data);
        }
    }

    @Override
    public void attribute(String namespaceURI, String localName, String prefix, String value, DtdAttributeKind type)
        throws GenXDMException
    {
        // this one *does* get called.
        // it might be defaulted, too, though prolly not. defaulted has to be added.
        // just ignore the ones that are already there.
//System.out.println("Got untyped attribute " + localName + " with value " + value);
        boolean found = false;
        if ( (startTag != null) && (startTag.attributes != null) )
        {
            for (XmlNode attribute : startTag.attributes)
            {
                if (attribute.getNamespaceURI().equals(namespaceURI) &&
                    attribute.getLocalName().equals(localName) )
                {
                    found = true; // found it: ignore
                    break;
                }
            }
        }
        if (!found && (startTag != null) )
        {
            XmlNodeMutator mutant = new XmlNodeMutator();
            // add it
            XmlAttributeNode attribute = (XmlAttributeNode)mutant.getFactory(startTag.element).createAttribute(namespaceURI, localName, prefix, value);
            mutant.insertAttribute(startTag.element, attribute);
        }
    }

    @Override
    public void text(String data)
        throws GenXDMException
    {
        // this one *also* does get called.
//System.out.println("Got untyped text \"" + data + "\"");
        // Note: empty nodes or nodes containing whitespace only are not in
        // the queue. if we get this call and it's ignorable whitespace,
        // drop it silently.
        // if it's *not* ignorable whitespace, it's *still* been validated
        // so remove the first ChildNode that is text from startTags
        if (!data.trim().isEmpty())
        {
            for (ChildNode<XmlNode> target : startTags)
            {
                if (target.isText())
                {
                    startTags.remove(target);
                    break;
                }
            }
        }
    }

    @Override
    public void text(List<? extends XmlAtom> data)
        throws GenXDMException
    {
        // this is a non-element child node event
        startTag = null; // we could do a verification of correctness, that everything's been touched
        // if the supplied data is an empty string, it won't be in the list.
        // shortcircuit the return.
        if (context.getAtomBridge().getC14NString(data).trim().isEmpty())
            return;
        boolean found = false;
        for (ChildNode<XmlNode> target : startTags)
        {
            // we assume that the first text node is the one we want.
            // the only way to "match" would be to re-validate and compare the values.
            // that's pretty much not going to happen.
            if (target.isText())
            {
                TextNode<XmlNode> text = (TextNode<XmlNode>)target;
                ((XmlLeafNode)text.content).setValue(data);
                startTags.remove(target);
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
        // this is a non-element child node event
        startTag = null; // we could do a verification of correctness, that everything's been touched
        // otherwise, ignore; pi-s don't validate.
    }

    @Override
    public void comment(String value)
        throws GenXDMException
    {
        // this is a non-element child node event
        startTag = null; // we could do a verification of correctness, that everything's been touched
        // otherwise, ignore; comment-s don't validate
    }

    @Override
    public void endElement()
        throws GenXDMException
    {
        // this is a non-element child node event
        startTag = null; // we could do a verification of correctness, that everything's been touched
        // otherwise, we've already handled everything.
    }

    @Override
    public void endDocument()
        throws GenXDMException
    {
        // ignore. startTag had *better* already be null, for instance,
        // and startTags should be empty. we could verify those things.
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
        throws GenXDMException
    {
        // by my reading of VxValidatorOutput, the adapter that drives the
        // sequencehandler in the validator, this should never be called.
//System.out.println("Got untyped element " + localName);
        throw new UnsupportedOperationException("untyped start-element");
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
        switch (getNodeKind())
        {
            case ELEMENT:
            {
                StartTag<XmlNode> tag = new StartTag<XmlNode>(node);
                startTags.add(tag);
                handler.startElement(getNamespaceURI(), getLocalName(), getPrefix(), getTypeName());
                for (NamespaceBinding namespace : getNamespaceBindings())
                {
                    if (tag.bindings == null)
                        tag.bindings = new HashMap<String, String>();
                    tag.bindings.put(namespace.getPrefix(), namespace.getNamespaceURI());
                    handler.namespace(namespace.getPrefix(), namespace.getNamespaceURI());
                }
                for (QName att : getAttributeNames(true))
                {
                    moveToAttribute(att.getNamespaceURI(), att.getLocalPart());
                    if (tag.attributes == null)
                        tag.attributes = new ArrayList<XmlNode>();
                    tag.attributes.add(node);
                    handler.attribute(getNamespaceURI(), getLocalName(), getPrefix(), (List<? extends XmlAtom>)getValue(), getTypeName());
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
                if (!getStringValue().trim().isEmpty())
                    startTags.add(new TextNode<XmlNode>(node));
                handler.text((List<? extends XmlAtom>)getValue());
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
                handler.comment(getStringValue());
                break;
            }
            case PROCESSING_INSTRUCTION:
            {
                handler.processingInstruction(getLocalName(), getStringValue());
                break;
            }
            default:
            {
                throw new AssertionError(getNodeKind());
            }
        }
    }

    // the notion here is that we don't need to synchronize, because
    // we add a node to this list, invoke the validator, and the validator
    // will either fire back, or will queue, and then fire back a sequence
    // of events. but we are initialized, and the validator is single-threaded,
    // so there are no opportunities to concurrently modify.
    private final List<ChildNode<XmlNode>> startTags = new ArrayList<ChildNode<XmlNode>>();
    // this gets set to the StartTag<XmlNode> from startTags that matches inside startElement,
    // at which time that StartTag is also removed from the startTags list.
    // we then handle attributes and namespaces by looking at this thing.
    // when we get endElement, or any child node event, we reset this to null.
    private StartTag<XmlNode> startTag;
}
