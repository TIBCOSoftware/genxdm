/**
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.genxdm.bridge.cx.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.atoms.XmlUntypedAtomic;
import org.genxdm.bridgekit.misc.UnaryIterable;
import org.genxdm.bridgekit.xs.SchemaTypeBridgeFactory;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.names.NamespaceBinding;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.Type;

// since there are only two container nodes, it follows that there are five leaf nodes:
// attribute, namespace, text, comment, pi
public class XmlLeafNode
    extends XmlNode
{
    protected XmlLeafNode(final NodeKind nodeKind, final Type<XmlAtom> type, final List<? extends XmlAtom> data)
    {
        super(nodeKind);
        if (type == null)
            this.type = UNTYPED_ATOMIC_TYPE;
        else
            this.type = type;
        this.atoms = Collections.unmodifiableList(new ArrayList<XmlAtom>(PreCondition.assertNotNull(data, "data")));
    }
    
    protected XmlLeafNode(final NodeKind nodeKind, final String value)
    {
        super(nodeKind);
        this.type = null;
        this.atoms = Collections.unmodifiableList(new ArrayList<XmlAtom>(new XmlUntypedAtomic( (value == null) ? "" : value )));
    }

    public XmlAttributeNode getAttribute(String namespaceURI, String localName)
    {
        return null;
    }

    public String getAttributeStringValue(String namespaceURI, String localName)
    {
        return null;
    }

    public QName getAttributeTypeName(String namespaceURI, String localName)
    {
        return null; 
    }

    public Iterable<? extends XmlAtom> getAttributeValue(String namespaceURI, String localName)
    {
        return null;
    }

    public XmlNode getFirstChild()
    {
        return null;
    }

    public XmlElementNode getFirstChildElement()
    {
        return null;
    }

    public XmlElementNode getFirstChildElementByName(String namespaceURI, String localName)
    {
        return null;
    }

    public XmlNode getLastChild()
    {
        return null;
    }

    public Iterable<NamespaceBinding> getNamespaceBindings()
    {
        return new UnaryIterable<NamespaceBinding>(null);
    }

    public String getNamespaceForPrefix(String prefix)
    {
        if (parent != null)
            return parent.getNamespaceForPrefix(prefix);
        else
        {
            if ( (prefix == null) && (prefixHint == null) )
                return this.namespaceURI;
            if ( (prefix != null) && prefix.equals(prefixHint) )
                return this.namespaceURI;
        }
        return null;
    }

    public Iterable<String> getNamespaceNames(boolean orderCanonical)
    {
        return new UnaryIterable<String>(null);
    }

    public String getStringValue()
    {
        // we're doing this in an odd order, so that it will do the simple thing
        // when it ought to (there's no value, or no type), and then will *try*
        // to do the complicated thing (get an atom bridge and convert to string),
        // but if that doesn't work, then go back to the simple thing.
        
        // if there is no value specified, then return the empty string.
        if (atoms == null)
            return "";
        // if there is no type specified, we are either an untyped leaf,
        // or we're operating in the untyped world, where everything is untyped atomic.
        if (type == UNTYPED_ATOMIC_TYPE)
        {
            return getUntypedValue();
        }
        // if a type is specified, then this is the typed world,
        // so let's hope that we have a document and that it has a bridge.
        if (getRoot().getNodeKind() == NodeKind.DOCUMENT)
        {
            if (((XmlRootNode)getRoot()).atomBridge != null)
                return ((XmlRootNode)getRoot()).atomBridge.getC14NString(atoms);
        }
        return getUntypedValue();
    }
    
    public Type<XmlAtom> getType()
    {
        return type;
    }

    public QName getTypeName()
    {
        if (type != null)
            return type.getName();
        return null;
    }

    public Iterable<? extends XmlAtom> getValue()
    {
        return atoms;
    }

    public boolean hasAttributes()
    {
        return false;
    }

    public boolean hasChildren()
    {
        return false;
    }

    public boolean hasNamespaces()
    {
        return false;
    }

    public boolean isAttribute()
    {
        return false;
    }

    public boolean isElement()
    {
        return false;
    }

    public boolean isId()
    {
        return false;
    }

    public boolean isIdRefs()
    {
        return false;
    }

    public boolean isNamespace()
    {
        return false;
    }

    public boolean isText()
    {
        return false;
    }
    
    String setValue(String value)
    {
        String ret = getUntypedValue();
        setValue(new ArrayList<XmlAtom>(new XmlUntypedAtomic( (value == null) ? "" : value )) );
        return ret;
    }
    
    Iterable<? extends XmlAtom> setValue(final List<? extends XmlAtom> data)
    {
        List<XmlAtom> ret = atoms;
        atoms = Collections.unmodifiableList(new ArrayList<XmlAtom>(PreCondition.assertNotNull(data, "data")));
        return ret;
    }
    
    private String getUntypedValue()
    {
        if ( (atoms == null) || (atoms.size() < 1) )
            return "";
        else if (atoms.size() == 1)
            return atoms.get(0).getC14NForm();
        else
        {
            final int size = atoms.size();
            final StringBuilder sb = new StringBuilder();
            sb.append(atoms.get(0).getC14NForm());
            for (int i = 1; i < size; i++)
            {
                sb.append(" ");
                sb.append(atoms.get(i).getC14NForm());
            }
            return sb.toString();
        }
    }

    protected List<XmlAtom> atoms;
    protected final Type<XmlAtom> type;
    private static final Type<XmlAtom> UNTYPED_ATOMIC_TYPE = new SchemaTypeBridgeFactory<XmlAtom>(new XmlAtomBridge(null, new NameSource())).newMetaBridge().getAtomicType(NativeType.UNTYPED_ATOMIC);
}
