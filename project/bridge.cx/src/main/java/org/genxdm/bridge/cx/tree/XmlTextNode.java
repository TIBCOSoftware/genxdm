/*
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

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.NodeKind;
import org.genxdm.bridgekit.atoms.XmlAtom;

public final class XmlTextNode
    extends XmlLeafNode
{

    XmlTextNode(List<? extends XmlAtom> data)
    {
        super(NodeKind.TEXT, null, data);
    }
    
    XmlTextNode(String value)
    {
        super(NodeKind.TEXT, value);
    }
    
    public QName getTypeName()
    {
        if ( (parent != null) && parent.isElement() )
            return ((XmlElementNode)parent).getTypeName();
        return null;
    }

    public boolean isId()
    {
        return parent.isId();
    }

    public boolean isIdRefs()
    {
        return parent.isIdRefs();
    }

    public boolean isText()
    {
        return true;
    }
}
