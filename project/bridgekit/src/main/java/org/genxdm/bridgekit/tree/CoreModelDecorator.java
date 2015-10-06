/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.bridgekit.tree;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.namespace.QName;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.bridgekit.atoms.XsiNil;
import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentHandler;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

/** Provides type names for bridges that do not provide facilities for direct annotation.
 * 
 */
public final class CoreModelDecorator<N, A> 
    implements TypedModel<N, A>, TypeAnnotator<N>
{
    public CoreModelDecorator(final Model<N> model, final AtomBridge<A> atomBridge, final SchemaComponentCache cache)
    {
        this.model = PreCondition.assertNotNull(model, "model");
        this.atomBridge = PreCondition.assertNotNull(atomBridge, "atomBridge");
        this.schemas = PreCondition.assertNotNull(cache, "schemas");
        // may not want initialcapacity here
        mapOfTypesMaps = new WeakHashMap<Object, Map<Object, QName>>(initialCapacity);
    }

    @Override
    public int compare(final N one, final N two)
    {
        return model.compare(one, two);
    }

    @Override
    public Iterable<N> getAncestorAxis(final N origin)
    {
        return model.getAncestorAxis(origin);
    }

    @Override
    public Iterable<N> getAncestorOrSelfAxis(N origin)
    {
        return model.getAncestorOrSelfAxis(origin);
    }

    @Override
    public N getAttribute(final N parent, final String namespaceURI, final String localName)
    {
        return model.getAttribute(parent, namespaceURI, localName);
    }
    
    @Override
    public Iterable<N> getAttributeAxis(final N origin, final boolean inherit)
    {
        return model.getAttributeAxis(origin, inherit);
    }

    @Override
    public Iterable<QName> getAttributeNames(final N node, final boolean orderCanonical)
    {
        return model.getAttributeNames(node, orderCanonical);
    }

    @Override
    public String getAttributeStringValue(final N parent, final String namespaceURI, final String localName)
    {
        return model.getAttributeStringValue(parent, namespaceURI, localName);
    }

    @Override
    public Iterable<? extends A> getAttributeValue(final N parent, final String namespaceURI, final String localName)
    {
        PreCondition.assertNotNull(parent, "node");
        final N attribute = getAttribute(parent, namespaceURI, localName);
        if (attribute != null)
            return getValue(attribute);
        return null;
    }

    @Override
    public QName getAttributeTypeName(final N parent, final String namespaceURI, final String localName)
    {
        PreCondition.assertNotNull(parent, "node");
        final N attribute = getAttribute(parent, namespaceURI, localName);
        if (attribute != null)
            return getTypeName(attribute);
        return NativeType.UNTYPED_ATOMIC.toQName();
    }

    @Override
    public URI getBaseURI(final N node)
    {
        return model.getBaseURI(node);
    }

    @Override
    public Iterable<N> getChildAxis(final N origin)
    {
        return model.getChildAxis(origin);
    }

    @Override
    public Iterable<N> getChildElements(final N origin)
    {
        return model.getChildElements(origin);
    }

    @Override
    public Iterable<N> getChildElementsByName(final N origin, final String namespaceURI, final String localName)
    {
        return model.getChildElementsByName(origin, namespaceURI, localName);
    }

    @Override
    public Iterable<N> getDescendantAxis(final N origin)
    {
        return model.getDescendantAxis(origin);
    }

    @Override
    public Iterable<N> getDescendantOrSelfAxis(final N origin)
    {
        return model.getDescendantOrSelfAxis(origin);
    }

    @Override
    public URI getDocumentURI(final N node)
    {
        return model.getDocumentURI(node);
    }
    
    @Override
   public N getElementById(final N context, final String id)
    {
        return model.getElementById(context, id);
    }

    @Override
    public N getFirstChild(final N origin)
    {
        return model.getFirstChild(origin);
    }

    @Override
    public N getFirstChildElement(final N origin)
    {
        return model.getFirstChildElement(origin);
    }

    @Override
    public N getFirstChildElementByName(final N origin, final String namespaceURI, final String localName)
    {
        return model.getFirstChildElementByName(origin, namespaceURI, localName);
    }

    @Override
    public Iterable<N> getFollowingAxis(final N origin)
    {
        return model.getFollowingAxis(origin);
    }

    @Override
    public Iterable<N> getFollowingSiblingAxis(final N origin)
    {
        return model.getFollowingSiblingAxis(origin);
    }

    @Override
    public N getLastChild(final N origin)
    {
        return model.getLastChild(origin);
    }

    @Override
    public String getLocalName(final N node)
    {
        return model.getLocalName(node);
    }

    @Override
    public Iterable<N> getNamespaceAxis(final N node, final boolean inherit)
    {
        return model.getNamespaceAxis(node, inherit);
    }

    @Override
    public Iterable<NamespaceBinding> getNamespaceBindings(final N node)
    {
        return model.getNamespaceBindings(node);
    }
    
    @Override
    public String getNamespaceForPrefix(final N node, final String prefix)
    {
        return model.getNamespaceForPrefix(node, prefix);
    }

    @Override
    public Iterable<String> getNamespaceNames(final N node, final boolean orderCanonical)
    {
        return model.getNamespaceNames(node, orderCanonical);
    }

    @Override
    public String getNamespaceURI(final N node)
    {
        return model.getNamespaceURI(node);
    }

    @Override
    public N getNextSibling(final N origin)
    {
        return model.getNextSibling(origin);
    }

    @Override
    public N getNextSiblingElement(final N node)
    {
        return model.getNextSiblingElement(node);
    }

    @Override
    public N getNextSiblingElementByName(final N node, final String namespaceURI, final String localName)
    {
        return model.getNextSiblingElementByName(node, namespaceURI, localName);
    }
    
    public Object getNodeId(final N node)
    {
        return model.getNodeId(node);
    }

    @Override
   public NodeKind getNodeKind(final N node)
    {
        return model.getNodeKind(node);
    }

    @Override
    public N getParent(final N origin)
    {
        return model.getParent(origin);
    }

    @Override
   public Iterable<N> getPrecedingAxis(N origin)
    {
        return model.getPrecedingAxis(origin);
    }

    @Override
    public Iterable<N> getPrecedingSiblingAxis(final N origin)
    {
        return model.getPrecedingSiblingAxis(origin);
    }

    @Override
    public String getPrefix(final N node)
    {
        return model.getPrefix(node);
    }

    @Override
    public N getPreviousSibling(final N origin)
    {
        return model.getPreviousSibling(origin);
    }

    @Override
    public N getRoot(final N node)
    {
        return model.getRoot(node);
    }

    @Override
    public String getStringValue(final N node)
    {
        return model.getStringValue(node);
    }

    @Override
    public Iterable<? extends A> getValue(final N node)
    {
        PreCondition.assertNotNull(node, "node");
        switch (getNodeKind(node))
        {
            case ELEMENT:
            {
                if (XsiNil.isNilledElement(this, node, atomBridge))
                    return new UnaryIterable<A>(null);
                // otherwise, fall through to the next section, which is both
                // elements and attributes. clear?
            }
            case ATTRIBUTE:
            {
                final QName typeName = getTypeName(node);
                final Type type = schemas.getComponentProvider().getTypeDefinition(typeName);
                if (type instanceof SimpleType)
                {
                    final SimpleType simpleType = (SimpleType)type;
                    final String stringValue = getStringValue(node);
                    try
                    {
                        return simpleType.validate(stringValue, atomBridge);
                    }
                    catch (final DatatypeException e)
                    {
                        throw new GenXDMException(e);
                    }
                }
                final String stringValue = getStringValue(node);
                return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(stringValue));
            }
            case COMMENT:
            case NAMESPACE:
            case PROCESSING_INSTRUCTION:
            {
                return atomBridge.wrapAtom(atomBridge.createString(getStringValue(node)));
            }
            case DOCUMENT:
            case TEXT:
            default:
            {
                return atomBridge.wrapAtom(atomBridge.createUntypedAtomic(getStringValue(node)));
            }
        }
    }

    @Override
    public QName getTypeName(N node)
    {
//System.out.println("Types map contains " + typesMap.size() + " entries");
        PreCondition.assertNotNull(node, "node");
        QName defaultType = null;
        switch (getNodeKind(node))
        {
            case ELEMENT:
            {
                defaultType = NativeType.UNTYPED.toQName();
//System.out.println("Getting type name for element {" + getNamespaceURI(node) + "}" + getLocalName(node));
//System.out.println("Node id is " + getNodeId(node));
            }
            case ATTRIBUTE:
            {
                if (defaultType == null) //{ System.out.println("Getting type name for attribute " + getNodeId(node)); 
                    defaultType = NativeType.UNTYPED_ATOMIC.toQName(); //}
                N doc = getRoot(node);
                Object documentId = (doc == null) ? dummyId : getNodeId(doc);
                if (documentId == null)
                    documentId = dummyId;
                Map<Object, QName> localTypesMap = mapOfTypesMaps.get(documentId);
                QName type = null;
                if (localTypesMap != null)
                    type = localTypesMap.get(getNodeId(node));
                if (type == null)
                    type = defaultType;
                return type;
            }
            case TEXT:
            {
                return NativeType.UNTYPED_ATOMIC.toQName();
            }
            // these four fall through to return null
            case COMMENT:
            case NAMESPACE:
            case PROCESSING_INSTRUCTION:
            case DOCUMENT:
        }
        return null;
    }

    @Override
    public boolean hasAttributes(final N node)
    {
        return model.hasAttributes(node);
    }

    @Override
    public boolean hasChildren(final N node)
    {
        return model.hasChildren(node);
    }

    @Override
    public boolean hasNamespaces(final N node)
    {
        return model.hasNamespaces(node);
    }

    @Override
    public boolean hasNextSibling(final N node)
    {
        return model.getNextSibling(node) != null;
    }

    @Override
    public boolean hasParent(final N node)
    {
        return model.hasParent(node);
    }

    @Override
    public boolean hasPreviousSibling(final N node)
    {
        return model.getPreviousSibling(node) != null;
    }

    @Override
    public boolean isAttribute(final N node)
    {
        return model.getNodeKind(node) == NodeKind.ATTRIBUTE;
    }

    @Override
    public boolean isElement(final N node)
    {
        return model.isElement(node);
    }
    
    @Override
    public boolean isId(final N node)
    {
        return model.isId(node);
    }
    
    @Override
    public boolean isIdRefs(final N node)
    {
        return model.isIdRefs(node);
    }

    @Override
    public boolean isNamespace(final N node)
    {
        return model.getNodeKind(node) == NodeKind.NAMESPACE;
    }

    @Override
    public boolean isText(final N node)
    {
        return model.getNodeKind(node) == NodeKind.TEXT;
    }

    @Override
    public boolean matches(final N node, final NodeKind kind, final String namespaceURI, final String localName)
    {
        return model.matches(node, kind, namespaceURI, localName);
    }

    @Override
    public boolean matches(final N node, final String namespaceURI, final String localName)
    {
        return model.matches(node, namespaceURI, localName);
    }

    @Override
    public void stream(final N node, final ContentHandler handler) throws GenXDMException
    {
        model.stream(node, handler);
    }

    @Override
    public void stream(final N node, final SequenceHandler<A> handler, boolean bogus) throws GenXDMException
    {
        PreCondition.assertNotNull(node, "node");
        switch (getNodeKind(node))
        {
            case ELEMENT:
            {
                final QName type = getTypeName(node);

                handler.startElement(getNamespaceURI(node), getLocalName(node), getPrefix(node), type);
                try
                {
                    if (hasNamespaces(node))
                    {
                        for (NamespaceBinding ns : getNamespaceBindings(node))
                        {
                            handler.namespace(ns.getPrefix(), ns.getNamespaceURI());
                        }
                    }
                    if (hasAttributes(node))
                    {
                        for (N attr : getAttributeAxis(node, false))
                        {
                            stream(attr, handler, bogus);
                        }
                    }
                    for (N child : getChildAxis(node))
                    {
                        stream(child, handler, false);
                    }
                }
                finally
                {
                    handler.endElement();
                }
            }
            break;
            case ATTRIBUTE:
            {
                handler.attribute(getNamespaceURI(node), getLocalName(node), getPrefix(node), iterableToList(getValue(node)), getTypeName(node));
            }
            break;
            case TEXT:
            {
                boolean typed = false;
                if (hasParent(node))
                {
                    final QName typeName = getTypeName(getParent(node));
                    if (typeName != null)
                    {
                        final Type type = schemas.getComponentProvider().getTypeDefinition(typeName);
                        if (type instanceof SimpleType)
                        {
                            handler.text(iterableToList(getValue(getParent(node))));
                            typed = true;
                        }
                    }
                }
                if (!typed)
                    handler.text(getStringValue(node));
            }
            break;
            case DOCUMENT:
            {
                handler.startDocument(getDocumentURI(node), null);
                try
                {
                    for (N child : getChildAxis(node))
                    {
                        stream(child, handler, bogus);
                    }
                }
                finally
                {
                    handler.endDocument();
                }
            }
            break;
            case NAMESPACE:
            {
                // this won't ever happen for non-namespace-axis-supporting models.
                handler.namespace(getPrefix(node), getNamespaceURI(node));
            }
            break;
            case COMMENT:
            {
                handler.comment(getStringValue(node));
            }
            break;
            case PROCESSING_INSTRUCTION:
            {
                handler.processingInstruction(getLocalName(node), getStringValue(node));
            }
            break;
            default:
            {
                throw new AssertionError(getNodeKind(node));
            }
        }
    }
    
    @Override 
    public void annotate(N document, Object nodeId, QName type)
    {
//System.out.println("Adding annotation for type " + type.toString());
//System.out.println("Node id is " + nodeId.toString());
        PreCondition.assertNotNull(nodeId, "nodeId");
        Object documentId = (document == null) ? dummyId : getNodeId(document);
        Map<Object, QName> localTypesMap = mapOfTypesMaps.get(documentId);
        if (localTypesMap == null)
        {
            localTypesMap = new WeakHashMap<Object, QName>(initialCapacity);
            mapOfTypesMaps.put(documentId, localTypesMap);
        }
        localTypesMap.put(nodeId, type);
    }
    
    private List<? extends A> iterableToList(Iterable<? extends A> able)
    {
        List<A> list = new ArrayList<A>();
        for (A a : able)
        {
            list.add(a);
        }
        return list;
    }
    
    public static void setInitialCapacity(int cap)
    {
        initialCapacity = cap;
    }
    
    private static int initialCapacity = 16;

    private final Model<N> model;
    private final AtomBridge<A> atomBridge;
    private final SchemaComponentCache schemas;
    private final Map<Object, Map<Object, QName>> mapOfTypesMaps;
    private final Object dummyId = new Object();

}
