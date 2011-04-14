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

import org.genxdm.bridge.dom.DomFragmentBuilder;
import org.genxdm.bridge.dom.DomSupport;
import org.genxdm.exceptions.GxmlException;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.Emulation;
import org.w3c.dom.Node;

final class DomSequenceBuilder<A>
    extends DomFragmentBuilder
    implements SequenceBuilder<Node, A>
{
	public DomSequenceBuilder(DocumentBuilderFactory dbf, final TypedContext<Node, A> pcx)
	{
		super(dbf);
		this.m_atomBridge = PreCondition.assertArgumentNotNull(pcx).getAtomBridge();
	}

	public void attribute(final String namespaceURI, final String localName, final String prefix, final List<? extends A> data, final QName type) throws GxmlException
	{
		if (m_depth > 0)
		{
			final Node attribute = DomSupport.setAttribute(m_current, namespaceURI, localName, prefix, data, Emulation.C14N, m_atomBridge);
			DomSupport.setAnnotationType(attribute, type);
		}
		else
		{
			startNodeProcessing();
			m_current = DomSupport.createAttribute(getOwner(), namespaceURI, localName, prefix, data, Emulation.C14N, m_atomBridge);
			DomSupport.setAnnotationType(m_current, type);
			endNodeProcessing();
		}
	}

	public void startElement(final String namespaceURI, final String localName, final String prefix, final QName type) throws GxmlException
	{
	    startElement(namespaceURI, localName, prefix);
		DomSupport.setAnnotationType(m_current, type);
	}

	public void text(final List<? extends A> value) throws GxmlException
	{
		text(m_atomBridge.getC14NString(value));
	}

    public void atom(A atom)
        throws GxmlException
    {
        // TODO Auto-generated method stub
        
    }

    public void endSequence()
        throws GxmlException
    {
        // TODO Auto-generated method stub
        
    }

    public void startSequence()
        throws GxmlException
    {
        // TODO Auto-generated method stub
        
    }

    private final AtomBridge<A> m_atomBridge;
}
