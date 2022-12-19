package org.genxdm.bridgekit.content;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
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
    TypePromoter(final SequenceHandler<A> out, final AtomBridge<A> atoms, final ComponentProvider components)
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
        else // p(arent)Type is not null
        {
            ElementDefinition element = m_provider.getElementDeclaration(elementQName);
            if (pType instanceof ComplexType)// ought to be; it contains this element
            {
                // we don't mind the check above, because it catches
                // attempts to put things in the wrong place. however,
                // if we already have a global element, don't look for
                // a local here. we're protecting against the existence of local elements
                if (element == null)
                {
                    ComplexType cType = (ComplexType)pType;
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
            else // can't actually be null, but I'm not turning off warnings for the whole method because eclipse can't figure that out
                throw new IllegalStateException("Illegal element content {"+namespaceURI+"}"+localName+" inside an element of non-complex type {"+(type==null?"null":type.getName().getNamespaceURI())+"}"+(type==null?"null":type.getName().getLocalPart()));
        }
        // fully resolved here, call the typed version with the type we've collected.
        // and save the actual type, because apparently some can't be looked up by name? wut?
        m_lastActualTypeDiscovered = type;
        startElement(namespaceURI, localName, prefix, type.getName());
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, final String prefix, final QName type) throws GenXDMException
    {
        // this can be called directly with the xsi type override, otherwise is called internally.
        PreCondition.assertNotNull(type, "{"+namespaceURI+"}"+localName+" type");
        Type actualType = m_provider.getTypeDefinition(type);
        if (actualType == null)
            actualType = m_lastActualTypeDiscovered;
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
            throw new GenXDMException("Illegal invocation of binary-element for element "+getCurrentElementName()+" : type "+type.getName()+" is not simple");
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
    
    private void closeStartTag()
    {
        // summary: 1: are there no attributes? then checkformissing, with insertion or exception possible
        // 2: we have attributes, are none allowed? exception
        // 3: have attributes, some are allowed, check each one
        // 4: is this attribute allowed? no, exception.
        // 5: this attribute allowed here, assign a type and promote the value to it (exception can happen
        // 6: after handling all of the specified attributes, check if some 'attribute uses' remain, and
        //    if so, checkformissing, again, with that subset, insertion or exception possible.
        // do we have any attributes?
        if (m_attributes == null)
        {
            if ((m_attributeUses != null) && !m_attributeUses.isEmpty())
                checkForMissingAttributes(m_attributeUses);
        }
        if ((m_attributes != null) && !m_attributes.isEmpty())
        {
            // compare attributes (all of them at once!) with attribute uses.
            // first, if none are allowed, throw an exception.
            if (m_attributeUses == null)
                throw new GenXDMException("Element "+getCurrentElementName()+" contains "+m_attributes.size()+"attributes when type "+getCurrentElementType().getName()+" allows none");
            // make a shallow copy, and check that it contains something
            // we can modify the shallow copy by removing attribute uses, but not the original.
            Map<QName, AttributeUse> unusedSoFar = new HashMap<QName, AttributeUse>(m_attributeUses);
            //if (unusedSoFar.isEmpty()) // we have no allowed attributes, but at least one is here. it will throw
            // an exception in the for loop for m_attributes.
            for (final Attrib a: m_attributes)
            {
                final boolean isBinary = a instanceof BinaryAttrib;
                final QName key = new QName(a.getNamespace(), a.getName());
                final AttributeUse use = unusedSoFar.get(key);
                if (use == null) // this attribute not allowed in this element
                {
                    if (isBinary) // protection against blowing the heap/stack with a BLOB in an error message
                        throw new GenXDMException("In element "+getCurrentElementName()+" no declaration found for attribute "+key+" with binary content");
                    // implicit else
                    throw new GenXDMException("In element "+getCurrentElementName()+" no declaration found for attribute "+key+" with value '"+a.getValue()+"'");
                }
                // use is non-null
                final SimpleType aType = use.getAttribute().getType();
                if (isBinary)
                {
                    // this style can't throw DTE
                    final BinaryAttr b = (BinaryAttr)a;
                    attribute(b.getNamespace(), b.getName(), b.getPrefix(), promote(aType.getNativeType(), b.getData()), aType.getName());
                }
                else
                {
                    try
                    {
                        final Attr aa = (Attr)a; // this gives us back access to the prefix
                        attribute(aa.getNamespace(), aa.getName(), aa.getPrefix(), promote(aType, aa.getValue()), aType.getName());
                    }
                    catch (DatatypeException dte)
                    {
                        throw new GenXDMException("In element "+getCurrentElementName()+" attribute "+key+" has invalid content '"+a.getValue()+"' for declared type "+aType.getName(), dte);
                    }
                } // else (non-binary)
                // got to here, so remove the attribute just processed:
                unusedSoFar.remove(key);
            } // for loop
            if (!unusedSoFar.isEmpty())
            {
                // this is exactly the same problem as: m_attributeUses != null, m_attributes null or empty
                // something is required, possibly with a default value when missing. So insert or throw an
                // exception, but only for the unused subset, which unusedSoFar contains.
                // (note: default attributes inserted here will result in attribute appearing not in
                // canonical order; all of the specified attributes will appear first, then the attributes
                // inserted due to defaulting. we could fix this, but it's not worth the effort unless
                // someone reports it as a problem for them; in general we don't guarantee canonicalization
                // of xml attribute order (it's worth doing when it's cheap and easy, though)).
                checkForMissingAttributes(unusedSoFar);
            }
        } // if m_attributes non-null and not empty
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
    
    private void checkForMissingAttributes(Map<QName, AttributeUse> unusedUses) throws GenXDMException
    {
        // don't even call this if unusedUses is null or empty; caller's responsibility to check.
        for (Map.Entry<QName, AttributeUse> unused : unusedUses.entrySet())
        {
            // check defaults (and insert) first; only check required if not also defaulted.
            // any attribute defaulted (and unset to some other value): insert
            // prefix, if the attribute is qualified, is prolly wrong. fix this when there's an actual example
            // of failure. this should work for most (unprefixed) attributes (99.8% case).
            // if we had logging, here's where we would log insertion of a default attribute
            if (unused.getValue().getValueConstraint().getVariety().isDefault())
                            attribute(unused.getKey().getNamespaceURI(), unused.getKey().getLocalPart(), unused.getKey().getPrefix(),
                            unused.getValue().getValueConstraint().getValue(m_bridge), unused.getValue().getAttribute().getType().getName());
            // any attribute that is required, but missing: exception
            else if (unused.getValue().isRequired())
                throw new GenXDMException("In element "+getCurrentElementName()+" required attribute "+unused.getKey()+" is missing.");
            // the invisible common case here: an attribute is optional, and has no default value.
            // so it exists in attribute uses, but there is no attribute for this instance. quietly ignore it;
            // it's completely normal and requies no further attention.
        }
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

    // this is a workaround for a weird problem: if we use m_provider to get a Type (from an element decl, perhaps),
    // then call startElement(... QName type) by using startElement(... type.getName()), then retrieve using:
    // m_provider.getTypeDefinition(QName type), we seem to sometimes get null. So save the original, use it
    // if we have a null there. (this really shouldn't happen; anonymous means we generate a type name).
    private Type m_lastActualTypeDiscovered = null;
    private final Deque<Type> m_types = new ArrayDeque<Type>(); // the stack of types determined for each element
    private final Deque<QName> m_elements = new ArrayDeque<QName>(); // the stack of element names
    
    private SequenceHandler<A> m_target;
    private AtomBridge<A> m_bridge;
    private ComponentProvider m_provider;
    
}