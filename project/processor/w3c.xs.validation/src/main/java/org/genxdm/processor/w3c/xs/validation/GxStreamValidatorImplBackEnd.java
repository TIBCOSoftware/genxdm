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
package org.genxdm.processor.w3c.xs.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GxmlException;
import org.genxdm.processor.w3c.xs.validation.api.VxOutputHandler;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmType;


final class GxStreamValidatorImplBackEnd<A> implements VxOutputHandler<A>
{
	public A m_atom;
	private final AtomBridge<A> m_atomBridge;
	public List<? extends A> m_atoms;
	public final ArrayList<List<? extends A>> m_attributeAtoms = new ArrayList<List<? extends A>>();
	public final ArrayList<QName> m_attributeNames = new ArrayList<QName>();
	public final ArrayList<QName> m_attributeTypes = new ArrayList<QName>();
	public SmType<A> m_dataType;
	public String m_elementLN;
	public String m_elementNS;
	public String m_elementPH;
	public int m_eventCount;
	public final ArrayList<String> m_namespaceAlias = new ArrayList<String>();
	public final ArrayList<String> m_namespaceNames = new ArrayList<String>();

	public GxStreamValidatorImplBackEnd(final AtomBridge<A> atomBridge)
	{
		this.m_atomBridge = atomBridge;
	}

	public void attribute(final QName name, final List<? extends A> value, final SmSimpleType<A> type) throws IOException
	{
		m_attributeNames.add(name);
		m_attributeAtoms.add(value);
		m_attributeTypes.add(type.getName());
	}

	public void attribute(QName name, String value) throws IOException
	{
		throw new RuntimeException();
	}

	public void close() throws IOException
	{
	}

	public void comment(final String value) throws GxmlException
	{
		throw new RuntimeException();
	}

	public void endDocument()
	{
		// Ignore.
	}

	public void endElement()
	{
		m_eventCount++;
	}

	public void flush() throws IOException
	{
	}

	public void ignorableWhitespace(final String value) throws GxmlException
	{
	}

	public void namespace(final String prefix, final String namespaceURI)
	{
		m_namespaceAlias.add(prefix);
		m_namespaceNames.add(namespaceURI);
	}

	public void processingInstruction(final String target, final String data) throws GxmlException
	{
		throw new RuntimeException();
	}

	public void startDocument() throws GxmlException
	{
		// Ignore.
	}

	public void startElement(final QName name, final SmType<A> type) throws IOException
	{
		m_elementNS = name.getNamespaceURI();
		m_elementLN = name.getLocalPart();
		m_elementPH = name.getPrefix();
		m_dataType = type;
	}

	public void text(final List<? extends A> value) throws GxmlException
	{
		m_eventCount++;
		m_atom = null;
		m_atoms = value;

		// Optional reduction for testing purposes.
		final Iterator<? extends A> atoms = m_atoms.iterator();
		if (atoms.hasNext())
		{
			m_atom = atoms.next();
			if (atoms.hasNext())
			{
				m_atom = null;
			}
			else
			{
				m_atoms = null;
			}
		}
	}

	public void text(final String untypedAtomic) throws GxmlException
	{
		m_eventCount++;
		m_atom = m_atomBridge.createUntypedAtomic(untypedAtomic);
		m_atoms = null;
	}

	public void textAtom(final A data) throws GxmlException
	{
		m_eventCount++;
		m_atom = data;
		m_atoms = null;
	}
}
