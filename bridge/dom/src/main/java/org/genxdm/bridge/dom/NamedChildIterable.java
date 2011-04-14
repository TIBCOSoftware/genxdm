/*
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

import java.util.Iterator;

import org.w3c.dom.Node;

public class NamedChildIterable implements Iterable<Node> {

	public NamedChildIterable(Node parentNode, String namespace, String localName) {
	
		m_parentNode = parentNode;
		m_namespace = namespace;
		m_localName = localName;
	}
	
	@Override
	public Iterator<Node> iterator() {
		return new NamedSiblingIterator(m_parentNode.getFirstChild(), m_namespace, m_localName);
	}

	private Node m_parentNode;
	
	private String m_namespace;
	
	private String m_localName;
}
