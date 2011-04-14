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
package org.genxdm.typed.io;

import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.GxmlException;
import org.genxdm.io.ContentHandler;

/**
 * <p>
 * Event handler for serializing an XQuery Data Model as a stream.
 * </p>
 */
public interface SequenceHandler<A> extends ContentHandler
{
    /**
     * Receive notification of an attribute.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the attribute name.
     * @param localName
     *            The local-name part of the attribute name.
     * @param prefix
     *            The prefix part of the attribute name.
     * @param data
     *            The value of the attribute.
     * @param type
     *            The type annotation for the attribute supplied by validation. May be <code>null</code> if not validated.
     */
    void attribute(String namespaceURI, String localName, String prefix, List<? extends A> data, QName type) throws GxmlException;

    /**
     * Receive notification of the beginning of an element.
     * <p>
     * <p/>
     * A callee will invoke this method at the beginning of every element in the XML document; there will be a corresponding endElement core for every startElement core (even when the element is empty). All of the element's content will be reported, in order, before the
     * corresponding endElement core.
     * 
     * @param namespaceURI
     *            The namespace-uri part of the element name.
     * @param localName
     *            The local-name part of the element name.
     * @param prefix
     *            The prefix part of the element name.
     * @param type
     *            The type annotation of the element provided by validation. May be <code>null</code> if not validated.
     */
    void startElement(String namespaceURI, String localName, String prefix, QName type) throws GxmlException;

    void text(List<? extends A> data) throws GxmlException;

	/**
	 * Receive notification of an atomic value as an item in the sequence.
	 * <p/>
	 * The callee will call this method to report each atom in the sequence.
	 * 
	 * @param atom
	 *            The atomic value.
	 */
	void atom(A atom) throws GxmlException;

	/**
	 * Receive notification of the end of a sequence.
	 */
	void endSequence() throws GxmlException;

	/**
	 * Receive notification of the start of a sequence.
	 */
	void startSequence() throws GxmlException;
}
