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
package org.genxdm.bridge.axiom;

import java.net.URI;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.genxdm.bridgekit.misc.StringToURIParser;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.mutable.NodeFactory;

public class AxiomFactory
    implements NodeFactory<Object>
{
    
    public AxiomFactory(OMFactory delegate)
    {
        omFactory = delegate;
    }

    public OMAttribute createAttribute(String namespaceURI, String localName, String prefix, String value)
    {
        PreCondition.assertNotNull(prefix, "prefix");
        return omFactory.createOMAttribute(localName, omFactory.createOMNamespace(checkNamespace(namespaceURI), prefix), value);
    }

    public OMComment createComment(String data)
    {
        return omFactory.createOMComment(null, data);
    }

    public OMDocument createDocument(final URI uri, final String docTypeDecl)
    {
        return omFactory.createOMDocument();
    }

    public OMElement createElement(String namespaceURI, String localName, String prefix)
    {
        PreCondition.assertNotNull(prefix, "prefix");
        OMNamespace ns = omFactory.createOMNamespace(checkNamespace(namespaceURI), prefix);
        return omFactory.createOMElement(localName, ns);
    }

    public OMProcessingInstruction createProcessingInstruction(String target, String data)
    {
        return omFactory.createOMProcessingInstruction(null, target, data);
    }

    public OMText createText(String value)
    {
        return omFactory.createOMText(value);
    }
    
    private String checkNamespace(String original)
    {
        if ((original == null) || (original.length() == 0))
            return "";
        String ns = namespaces.get(original);
        if (ns != null)
            return ns;
        ns = StringToURIParser.parse(original).toString();
        namespaces.put(original, ns);
        return ns;
    }

    final OMFactory omFactory;
    private Map<String, String> namespaces = new WeakHashMap<String, String>();
}
