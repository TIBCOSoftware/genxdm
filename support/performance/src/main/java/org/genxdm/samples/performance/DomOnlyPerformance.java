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
package org.genxdm.samples.performance;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.genxdm.bridge.dom.DomSupport;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 * This sample illustrates a simple, serialization.  
 * 
 * @param <N> the node type
 * @author jbaysdon
 */
public class DomOnlyPerformance extends BasePerformance
{
	DocumentBuilderFactory m_dbf;
	DocumentBuilder m_builder;
	LSSerializer m_writer;
	Node m_testNode;
	
	/**
	 * @param args args[0] is the location of the test properties file
	 */
	static public void main(String[] args)
	{
		try {
			if(args.length < 1)
			{
				throw new IllegalArgumentException("Test properites filename must be specified on command line.");
			}
			else
			{
				DomOnlyPerformance sample = new DomOnlyPerformance(args[0]);
				sample.runPerfTest();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public DomOnlyPerformance(String propsFile)
	{
		super(propsFile);
	}
	@Override
	void setupTesting() {}
	
	@Override
	void setupParsing() {
	    m_dbf = DocumentBuilderFactory.newInstance();
    	m_dbf.setNamespaceAware(true);
        try {
			m_builder = m_dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	void setupSerialization() {
        System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMImplementationSourceImpl");
		DOMImplementationRegistry registry;
		try {
			registry = DOMImplementationRegistry.newInstance();
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
		m_writer = impl.createLSSerializer();
	}
	@Override
	void setupNavigation() {}
	@Override
	void setupValidation() {}
	@Override
	void parse(String docFilePath) {
        try {
			m_testNode = m_builder.parse(docFilePath);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	void serialize() {
        m_writer.writeToString(m_testNode);
	}
	@Override
	void navigate(TaskTimer ttNavigate) {
		long attributes = 0;
		long comments = 0;
		long documents = 0;
		long elements = 0;
		long namespaces = 0;
		long processingInstructions = 0;
		long texts = 0;
		long other = 0;
		
		Node node = m_testNode;
		ttNavigate.startTimer();
        while (node!=null) {
	        switch(node.getNodeType())
	        {
			case Node.ATTRIBUTE_NODE:
				if(DomSupport.isNamespace(node))
					namespaces++;
				else
					attributes++;
				break;
			case Node.COMMENT_NODE:
				comments++;
				break;
			case Node.DOCUMENT_NODE:
				documents++;
				break;
			case Node.ELEMENT_NODE:
				elements++;
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				processingInstructions++;
				break;
			case Node.TEXT_NODE:
				texts++;
				break;
	        default:
	        	other++;
	        	break;
	        }
	        if(node.hasChildNodes())
	        {
		        node = node.getFirstChild();
	        } 
	        else {
	        	while (node.getNextSibling() == null && node != m_testNode)
	        	{
	        		node = node.getParentNode();
	        	}
		        node = node.getNextSibling();
		      } 
	    } 
        ttNavigate.stopTimer();
       	if(attributes > 0) ttNavigate.addNote("att      count = " + Long.toString(attributes));
       	if(comments > 0) ttNavigate.addNote("comment  count = " + Long.toString(comments));
       	if(documents > 0) ttNavigate.addNote("document count = " + Long.toString(documents));
       	if(elements > 0) ttNavigate.addNote("element  count = " + Long.toString(elements));
       	if(namespaces > 0) ttNavigate.addNote("ns       count = " + Long.toString(namespaces));
       	if(processingInstructions > 0) ttNavigate.addNote("pi       count = " + Long.toString(processingInstructions));
       	if(texts > 0) ttNavigate.addNote("text     count = " + Long.toString(texts));
       	if(other > 0) ttNavigate.addNote("other    count = " + Long.toString(other));
	}
	@Override
	void validate(TaskTimer ttTimer) {
		throw new UnsupportedOperationException("DOM validation available.");
	}
}
