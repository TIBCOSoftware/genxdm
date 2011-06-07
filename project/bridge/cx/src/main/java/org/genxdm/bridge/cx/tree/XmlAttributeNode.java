/*
 * Copyright (c) 2010-2011 TIBCO Software Inc.
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

import java.util.List;

import javax.xml.XMLConstants;

import org.genxdm.NodeKind;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.io.DtdAttributeKind;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.Type;

public final class XmlAttributeNode
    extends XmlLeafNode
{
    XmlAttributeNode(final String namespace, final String localName, final String prefix, final DtdAttributeKind attType, final String value)
    {
        super(NodeKind.ATTRIBUTE, value);
        dtdType = attType;
        if (attType == null)
            dtdType = DtdAttributeKind.CDATA;
        this.namespaceURI = (namespace == null) ? "" : namespace;
        this.localName = localName;
        this.prefixHint = prefix;
        checkId();
    }
    
    XmlAttributeNode( final String namespace, final String localName, final String prefix, final Type type, final List<XmlAtom> data)
    {
        super(NodeKind.ATTRIBUTE, type, data);
        this.namespaceURI = (namespace == null) ? "" : namespace;
        this.localName = localName;
        this.prefixHint = prefix;
        checkId();
    }
    
    public boolean isAttribute()
    {
        return true;
    }

    public boolean isId()
    {
        if (dtdType == DtdAttributeKind.ID)
            return true;
        // true if namespace is xml and localname is id.
        if ( (namespaceURI != null) && namespaceURI.equals(XMLConstants.XML_NS_URI) &&
             localName.equals("id") )
            return true;
        // true if type is xs:ID
        if ( (type != null) && type.isNative() && type.isAtomicType() )
        {
            AtomicType atomicType = (AtomicType)type;
            if (atomicType.isID())
                return true;
        }
        return false;
    }

    public boolean isIdRefs()
    {
        if ( (dtdType == DtdAttributeKind.IDREF) ||
                (dtdType == DtdAttributeKind.IDREFS) )
               return true;
        // true if type is xs:IDREF or xs:IDREFS
        if ( type.isNative() && type.isAtomicType() )
        {
            AtomicType atomicType = (AtomicType)type;
            if (atomicType.isIDREF() || atomicType.isIDREFS())
                return true;
        }
        return false;
    }
    
    void checkId()
    {
        if (isId() && (getRoot().getNodeKind() == NodeKind.DOCUMENT) )
            ((XmlRootNode)getRoot()).addIdNode(this);
    }
    
    @Override
    void setParent(XmlContainerNode container)
    {
        super.setParent(container);
        checkId();
    }

    private DtdAttributeKind dtdType;
}
