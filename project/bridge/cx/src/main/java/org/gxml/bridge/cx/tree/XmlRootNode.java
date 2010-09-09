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
package org.gxml.bridge.cx.tree;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.gxml.bridgekit.atoms.XmlAtom;
import org.gxml.NodeKind;
import org.gxml.typed.types.AtomBridge;

public final class XmlRootNode
    extends XmlContainerNode
{
    XmlRootNode(final URI docURI, final String decl)
    {
        super(NodeKind.DOCUMENT, null);
        documentURI = docURI;
        docTypeDecl = decl;
        this.document = this;
        parseDocTypeDecl();
    }
    
    XmlRootNode()
    {
        this(null, null);
    }

    public URI getBaseURI()
    {
        return documentURI;
    }

    public URI getDocumentURI()
    {
        return documentURI;
    }
    
    public XmlElementNode getElementById(String id)
    {
        if ( (id == null) || (id.trim().length() == 0) )
            return null;
        // TODO: enhancement
        // collect a set of possible nodes, then compare by document order,
        // and return the (parent of the) first.  in most cases, there will only be one, 
        // anyway, so this adds weight and time to handle a corner case.
        for (XmlNode node : idNodes)
        {
            if (id.equals(node.getStringValue()))
            {
                return (XmlElementNode)node.parent;
            }
        }
        return null;
    }
    
    void setAtomBridge(AtomBridge<XmlAtom> bridge)
    {
        atomBridge = bridge;
    }
    
    void addIdNode(XmlNode node)
    {
        // we *could* check that the id is unique.  it's prolly better to handle
        // that in lookup, though, because it's a validation constraint, not a
        // well-formedness constraint, so we shouldn't barf.
        idNodes.add(node);
    }
    
    private void parseDocTypeDecl()
    {
        // TODO
        // check the doctypedecl; for now, we just want to see if there are
        // any id or idref attribute definitions.
        // it *may* be that the parser handles this for us, but ... is that
        // robust for programmatic construction?
        // bridgekit now has an internalsubsetparser in misc
    }

    protected final URI documentURI;
    protected final String docTypeDecl;
    protected AtomBridge<XmlAtom> atomBridge;
    private Set<XmlNode> idNodes = new HashSet<XmlNode>();
}
