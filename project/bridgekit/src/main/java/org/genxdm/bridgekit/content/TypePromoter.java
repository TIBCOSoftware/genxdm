package org.genxdm.bridgekit.content;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.namespace.QName;

import org.genxdm.creation.Attrib;
import org.genxdm.creation.BinaryAttrib;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.io.Stateful;
import org.genxdm.typed.io.SequenceFilter;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.ParticleTerm;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.constraints.AttributeUse;
import org.genxdm.xs.constraints.ValueConstraint;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.ContentTypeKind;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

// package access
// this class is used by the typedcontenthelper to keep track of context so that we can
// find the correct types for elements and attributes. it's externalized from the original
// implementation so that it can also be applied to the branch copier, promoting the startelements
// and attributes that are fired through it.
class TypePromoter<A>
    implements SequenceFilter<A>, Promoter, Stateful
{
    TypePromoter(final SequenceHandler out, final AtomBridge atoms, final ComponentProvider components)
    {
        setOutputSequenceHandler(PreCondition.assertNotNull(out, "output target"));
        setAtomBridge(PreCondition.assertNotNull(atoms, "atom bridge"));
        m_provider = PreCondition.assertNotNull(components, "schema components");
        
    }
 
    @Override
    public void setOutputSequenceHandler(final SequenceHandler<A> output)
    {
        m_target = output;
    }

    @Override
    public void setAtomBridge(final AtomBridge<A> bridge)
    {
        m_bridge = bridge;
    }

    @Override
    public void setSchema(final SchemaComponentCache schema)
    {
        m_provider = schema.getComponentProvider();
    }

    @Override
    public void attribute(final String namespaceURI, final String localName, final String prefix, final String value, final DtdAttributeKind type) throws GenXDMException
    {
        if (m_attributes == null) // common: we don't create this until/unless it's needed
            m_attributes = new TreeSet<Attrib>(new AttribComparator());
        m_attributes.add(new Attr(namespaceURI, localName, prefix, value));
    }
    
    // interface is package
    public void binaryAttribute(final String namespaceURI, final String localName, final String prefix, final byte[] data)
    {
        if (m_attributes == null)
            m_attributes = new TreeSet<Attrib>(new AttribComparator());
        m_attributes.add(new BinaryAttr(namespaceURI, localName, prefix, data));
    }

    @Override
    public void attribute(final String namespaceURI, final String localName, final String prefix, final List<? extends A> data, final QName type) throws GenXDMException
    {
        m_target.attribute(namespaceURI, localName, prefix, data, type);
    }

    @Override
    public void comment(final String value) throws GenXDMException
    {
        closeStartTag();
        m_target.comment(value);
    }

    @Override
    public void endDocument() throws GenXDMException
    {
        m_target.endDocument();
    }

    @Override
    public void endElement() throws GenXDMException
    {
        closeStartTag();
        m_target.endElement();
        m_types.pop();
        m_elements.pop();
    }

    @Override
    public void namespace(final String prefix, final String namespaceURI) throws GenXDMException
    {
        m_target.namespace(prefix, namespaceURI);
    }

    @Override
    public void processingInstruction(final String target, final String data) throws GenXDMException
    {
        closeStartTag();
        m_target.processingInstruction(target, data);
    }

    @Override
    public void startDocument(final URI documentURI, final String docTypeDecl) throws GenXDMException
    {
        m_target.startDocument(documentURI, docTypeDecl);
    }

    // TODO this one loooks like it still has too much any it. It should be the standard entry, both from
    // the typedcontenthandler and from the typedqueuegenerator, but it doesn't look elegant and efficient.
    // that makes me uncertain that we have full coverage.
    @Override
    public void startElement(final String namespaceURI, final String localName, final String prefix) throws GenXDMException
    {
        // this is where we do the promotion for the element.
        // we have to retrieve the correct type name. we should then have a stack
        // of types that we are tracking.
        // retrieve the type. quick and stupid: use the element name
        final Type pType = getCurrentElementType(); // type of the parent of this element
        Type type = null;
        QName elementQName = new QName(namespaceURI, localName, prefix);// old form :nsStack.getPrefix(ns, bindings));
        if (pType == null) // true for the root element, hopefully *only*
        {
            // this is more or less correct.
            ElementDefinition element = m_provider.getElementDeclaration(elementQName);
            if (element == null) // nowhere else to look. die in flames
                throw new GenXDMException("Illegal start-complex invocation: element {"+namespaceURI+"}"+localName+" has no element declaration");
            type = element.getType();
        }
        else
        {
            ElementDefinition element = m_provider.getElementDeclaration(elementQName);
            if (pType instanceof ComplexType)// ought to be; it contains this element
            {
                // we don't mind the check above, because it catches
                // attempts to put things in the wrong place. however,
                // if we already have a global element, don't look for
                // a local.
                if (element == null)
                {
                    ComplexType cType = (ComplexType)type;
                    if (!cType.getContentType().getKind().isSimple())
                    {
                        ModelGroup group = cType.getContentType().getContentModel().getTerm();
                        element = locateElementDefinition(group, elementQName);
                        if (element == null) // nowhere else to look. die in flames
                            throw new GenXDMException("Illegal start-complex invocation: element {"+namespaceURI+"}"+localName+" has no global element declaration or local element declaration in the scope of its parent");
                    }
                    // else throw new ExceptionThisIsAnElementWhereAnAttributeShouldBe
                    // we'd throw that for empty or complex-with-simplecontent elements
                    // that have a child element inside (which is not valid).
                }
                type = element.getType();
            }
            else
                throw new IllegalStateException("Illegal element content {"+namespaceURI+"}"+localName+" inside an element of non-complex type {"+type.getName().getNamespaceURI()+"}"+type.getName().getLocalPart());
        }
        // fully resolved here, call the typed version with the type we've collected.
        startElement(namespaceURI, localName, prefix, type.getName());
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String prefix, final QName type) throws GenXDMException
    {
        // this can be called directly with the xsi type override, otherwise is called internally.
        PreCondition.assertNotNull(type, "{"+namespaceURI+"}"+localName+" type");
        Type actualType = m_provider.getTypeDefinition(type);
        if (actualType instanceof ComplexType) // do some more setup: attribute uses, if any, and attribute set container
        {
            final ComplexType cType = (ComplexType)actualType;
            m_attributeUses = cType.getAttributeUses();
        }
        else // simple type
        {
            m_attributeUses = null;
            m_attributes = null;
        }
        m_target.startElement(namespaceURI, localName, prefix, type);
        m_elements.push(new QName(namespaceURI, localName, prefix));
        m_types.push(actualType);
    }

    @Override
    public void text(final String value) throws GenXDMException
    {
        // now handle the content of the text node. it *may* be empty or null,
        // in which case, bypass all of this and don't even supply a text node.
        Type type = getCurrentElementType();
        if ( (value != null) && !value.trim().isEmpty() )
        {
            SimpleType simple = null;
            if (type instanceof ComplexType) // allowed, if simple content
            {
                ContentType contentType = ((ComplexType)type).getContentType();
                if (contentType.getKind() == ContentTypeKind.Simple)
                    simple = contentType.getSimpleType();
                else // empty or element-only, with a text node
                    throw new GenXDMException("Invalid content for element "+getCurrentElementName()+" value: '"+value+"'"+" with content type '"+contentType+"'");
            }
            else if (type instanceof SimpleType)
                simple = (SimpleType)type;
            // we should have actual content, either from the simple type branch
            // or the complex type simple content branch.
            try
            {
                text(promote(simple, value));
            }
            catch (Exception e)
            {
                throw new GenXDMException("Invalid content for element "+getCurrentElementName()+" value: '"+value+"' for type: "+type.getName(), e);
            }
        }
        else // value is null, empty, or whitespace only
        {
// TODO: we should check that empty content is allowed for this type.
// leave it for later, though.
            if (type instanceof ComplexType)
            {
                ContentType contentType = ((ComplexType)type).getContentType();
                if (contentType.getKind() == ContentTypeKind.Simple)
                {
                    // simple type check ; okay for string, uri, not for numeric, date/time, numeric, (boolean?)
                }
                //else okay:
                //empty or element-only required to be empty, do nothing.
            }
            else if (type instanceof SimpleType)
            {
                // simple type check ; okay for string, uri, not for numeric, date/time, numeric, (boolean?)
            }
        }
    }
    
    @Override
    public void binaryText(final byte [] data)
    {
        Type type = getCurrentElementType();
        SimpleType bType = (type instanceof SimpleType) ? (SimpleType)type : null;
        if (bType == null)
        {
            // type must be a ComplexType, since it isn't simple
            ContentType cType = ((ComplexType)type).getContentType();
            if (cType.getKind() == ContentTypeKind.Simple)
                bType = cType.getSimpleType();
        }
        if (bType == null)
            throw new GenXDMException("Illegal invocation of binary-element for element "+getCurrentElementName()+" : type "+bType.getName()+" is not simple");
        NativeType nType = bType.getNativeType();
        if ( (nType != NativeType.BASE64_BINARY) && (nType != NativeType.HEX_BINARY) )
            throw new GenXDMException("Illegal invocation of binary-element for element "+getCurrentElementName()+" : type "+nType.toQName()+" is simple but not binary");
        text(promote(nType, data));
    }
    
    
    @Override
    public void text(final List<? extends A> data) throws GenXDMException
    {
        closeStartTag();
        m_target.text(data);
    }
    
    @Override
    public void close() {}
    
    @Override
    public void flush() {}

    @Override
    public void reset()
    {
        //m_target.reset(); // don't have a reset in seqhandler?
        m_attributeUses = null;
        m_types.clear();
        m_elements.clear();
    }
    
    // this should get invoked after startElement() (typed version), after all namespace events,
    // after all attribute events (if any), at the first child (text, element, comment, pi, sometimes
    // also endElement (but not startdoc/enddoc). it processes all of the outstanding attribute events
    // at once, mostly.
    private void closeStartTag()
    {
        // do we have any attributes?
        if ((m_attributes != null) && !m_attributes.isEmpty())
        {
            // compare attributes (all of them at once!) with attribute uses.
            // first, if none are allowed, throw an exception.
            if (m_attributeUses == null)
                throw new GenXDMException("Element "+getCurrentElementName()+" contains "+m_attributes.size()+"attributes when type "+getCurrentElementType().getName()+" allows none");
            // next, if any are missing, and the use says that it has default value, insert it. 
// TODO: add missing attributes with fixed values (default attributes)
            // if any are missing and required, throw an exception
// TODO: handle missing attributes that are required.
        }
        else if (m_attributeUses != null)
        {
            // no attributes are available, but some may be required; check.
// TODO: handle missing attributes that are required.
        }
        for (final Attrib a : m_attributes)
        {
            final boolean isBinary = a instanceof BinaryAttrib;
            final QName key = new QName(a.getNamespace(), a.getName());
            final AttributeUse use = m_attributeUses.get(key);
            if (use == null)
            { // not found
                if (!isBinary)
                    throw new GenXDMException("In element "+getCurrentElementName()+ "no declaration found for attribute "+key+" with value "+a.getValue());
                throw new GenXDMException("In element "+getCurrentElementName()+ "no declaration found for attribute "+key+" with binary content");
            }
            final SimpleType aType = use.getAttribute().getType();
            if (isBinary)
            {
                final BinaryAttr b = (BinaryAttr)a;
                attribute(b.getNamespace(), b.getName(), b.getPrefix(), promote(aType.getNativeType(), b.getData()), aType.getName());
            }
            else // non-binary rocks :-)
            {
                try
                {
                    final Attr aa = (Attr)a; // recover the prefix
                    attribute(aa.getNamespace(), aa.getName(), aa.getPrefix(), promote(aType, aa.getValue()), aType.getName());
                }
                catch (DatatypeException dte)
                {
                    throw new GenXDMException("In element "+getCurrentElementName()+ " attribute "+key+" has invalid content '"+a.getValue()+"' for declared type "+aType.getName(), dte);
                }
            }
        }
    }

    // some very simple/stupid methods to tell us the semantics of what we're doing rather than the mechanics of it
    Type getCurrentElementType()
    {
        return m_types.peekFirst();
    }
    
    QName getCurrentElementName()
    {
        return m_elements.peekFirst();
    }
    
    private ElementDefinition locateElementDefinition(ModelGroup mg, QName target)
    {
        for (SchemaParticle particle : mg.getParticles())
        {
            ParticleTerm term = particle.getTerm();
            if (term instanceof ModelGroup)
            {
                ElementDefinition candidate = locateElementDefinition((ModelGroup)term, target);
                if (candidate != null)
                    return candidate;
            }
            else if (term instanceof ElementDefinition) // better be!
            {
                if ( ((ElementDefinition)term).getName().equals(target) )
                    return (ElementDefinition)term;
            }
            // only two possibilities covered; if we haven't returned a match,
            // fall through and return null.
        }
        return null;
    }
    
    List<? extends A> promote(final SimpleType stype, final String text) // too simple to bother calling?
        throws DatatypeException
    {
        return stype.validate(text, m_bridge);
    }
    
    List<? extends A> promote(final NativeType ntype, final byte [] data)
    {
        // avoid stringify+validate
        // if we get here, then we have either base64Binary or hexBinary.
        final List<A> content;
        if (ntype == NativeType.BASE64_BINARY) // usual case
            content = m_bridge.wrapAtom(m_bridge.createBase64Binary(data));
        else
            content = m_bridge.wrapAtom(m_bridge.createHexBinary(data));
        return content;
    }
    
    private SortedSet<Attrib> m_attributes = null;
    private Map<QName, AttributeUse> m_attributeUses = null; // set inside startElement if we have a complex type,
    // nulled out if it's not a complex type.

    private final Deque<Type> m_types = new ArrayDeque<Type>(); // the stack of types determined for each element
    private final Deque<QName> m_elements = new ArrayDeque<QName>(); // the stack of element names
    
    private SequenceHandler<A> m_target;
    private AtomBridge<A> m_bridge;
    private ComponentProvider m_provider;
    
}