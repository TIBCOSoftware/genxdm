/**
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.bridge.dom;

import org.genxdm.bridgekit.axes.BaseImmutableIterator;
import org.w3c.dom.Node;

/**
 * Traverses sibling elements looking for matches.
 */
public class NamedSiblingIterator extends BaseImmutableIterator<Node> {

	/**
	 * Construct the iterator that matches elements with the given name & namespace.
	 * 
	 * @param possibleFirst What node might be the first possible match?
	 * @param namespace	Namespace name to match, or null if any namespace should match.
	 * @param localName	Local name to match, or null if any local name will do.
	 */
	public NamedSiblingIterator(Node possibleFirst, String namespace, String localName) {
		super();
		m_namespace = namespace;
		m_localName = localName;
		setFirstResult( findNextMatch(possibleFirst, namespace, localName) );
	}
	
	public static boolean isMatch(Node node, String namespace, String localName) {
		
		if (node.getNodeType() == Node.ELEMENT_NODE) {
		    String ns = node.getNamespaceURI();
		    if (ns == null)
		        ns = "";
			if ( (localName == null || localName.equals(node.getLocalName())) &&
					(namespace == null || namespace.equals(ns) ) ){
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected Node next(Node current) {
		return findNextMatch( current.getNextSibling(), m_namespace, m_localName );
	}

	/**
	 * Finds the next match - always checks the passed node to see if it is a match,
	 * and then looks to the next sibling.
	 * 
	 * <p>Wrote this method this way so that it would be easy to think up and
	 * implement other "filters" that override findNextMatch.
	 * </p>
	 * 
	 * @param possibleMatch	Node that is a possible match 
	 * @return The next node that matches the criteria of the iterator, including possibly
	 * <code>null</code>.
	 */
	public static Node findNextMatch(Node possibleMatch, String namespace, String localName) {
		
		while (possibleMatch != null && !isMatch(possibleMatch, namespace, localName)) {
			possibleMatch = possibleMatch.getNextSibling();
		}
		
		return possibleMatch;
	}

	private String m_namespace;
	private String m_localName;
}
