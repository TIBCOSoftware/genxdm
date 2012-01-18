/*
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
package org.genxdm.bridge.dom.enhanced;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.genxdm.bridge.dom.DomConstants;
import org.genxdm.bridge.dom.DomFragmentBuilder;
import org.genxdm.bridge.dom.DomSupport;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.exceptions.GenXDMException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.Emulation;
import org.w3c.dom.Node;

final class DomSequenceBuilder
    extends DomFragmentBuilder
    implements SequenceBuilder<Node, XmlAtom>
{
    public DomSequenceBuilder(DocumentBuilderFactory dbf, final TypedContext<Node, XmlAtom> pcx)
    {
        super(dbf);
        this.atomBridge = PreCondition.assertArgumentNotNull(pcx).getAtomBridge();
    }

    public void attribute(final String namespaceURI, final String localName, final String prefix, final List<? extends XmlAtom> data, final QName type) throws GenXDMException
    {
        if (m_depth > 0)
        {
            final Node attribute = DomSupport.setAttributeUntyped(m_current, namespaceURI, localName, prefix, Emulation.C14N.atomsToString(data, atomBridge));
            setAnnotationType(attribute, type);
        }
        else
        {
            startNodeProcessing();
            m_current = DomSupport.createAttributeUntyped(getOwner(), namespaceURI, localName, prefix, Emulation.C14N.atomsToString(data, atomBridge));
            setAnnotationType(m_current, type);
            endNodeProcessing();
        }
    }

    public void startElement(final String namespaceURI, final String localName, final String prefix, final QName type) throws GenXDMException
    {
        startElement(namespaceURI, localName, prefix);
        setAnnotationType(m_current, type);
    }

    public void text(final List<? extends XmlAtom> value) throws GenXDMException
    {
        text(atomBridge.getC14NString(value));
    }

    private void setAnnotationType(final Node node, final QName type)
    {
        // TODO: we could, potentially, store DTD types even in untyped API
        // to do so, though, we have to figure out how to define a QName for the DtdAttributeKind enumeration.
        if (DomSupport.supportsCoreLevel3(node))
        {
            try
            {
                node.setUserData(DomConstants.UD_ANNOTATION_TYPE, type, null);
            }
            catch (final AbstractMethodError e)
            {
                // LOG.warn("setAnnotationType", e);
            }
        }
        // TODO: Log something for DOM w/o Level 3 support?
        // LOG.warn("DOM does not support DOM CORE version 3.0: setUserData");
    }

    private final AtomBridge<XmlAtom> atomBridge;
}
