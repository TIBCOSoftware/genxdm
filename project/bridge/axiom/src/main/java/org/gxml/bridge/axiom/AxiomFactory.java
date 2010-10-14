/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.gxml.bridge.axiom;

import java.net.URI;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.genxdm.base.mutable.NodeFactory;

public class AxiomFactory
    implements NodeFactory<Object>
{
    
    public AxiomFactory(OMFactory delegate)
    {
        omFactory = delegate;
    }

    public Object createAttribute(Object owner, String namespaceURI,
            String localName, String prefix, String value)
    {
        // TODO Auto-generated method stub
        return omFactory.createOMAttribute(localName, omFactory.createOMNamespace(namespaceURI, prefix), value);
    }

    public Object createComment(Object owner, String data)
    {
        return omFactory.createOMComment((OMContainer) owner, data);
    }

    public Object createDocument(final URI uri, final String docTypeDecl)
    {
        return omFactory.createOMDocument();
    }

    public Object createElement(Object owner, String namespaceURI,
            String localName, String prefix)
    {
    	OMContainer parent = AxiomSupport.staticDowncastContainer(owner);
    	OMNamespace ns = omFactory.createOMNamespace(namespaceURI, prefix);
        return omFactory.createOMElement(localName, ns, parent);
    }

    public Object createNamespace(Object owner, String prefix,
            String namespaceURI)
    {
        return omFactory.createOMNamespace(namespaceURI, prefix);
    }

    public Object createProcessingInstruction(Object owner, String target,
            String data)
    {
        return omFactory.createOMProcessingInstruction((OMContainer) owner, target, data);
    }

    public Object createText(Object owner, String value)
    {
        return omFactory.createOMText((OMContainer) owner, value);
    }

    private final OMFactory omFactory;
}
