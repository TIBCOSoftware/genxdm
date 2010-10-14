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
package org.gxml.samples.performance;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.genxdm.Resolved;
import org.genxdm.Resolver;
import org.genxdm.base.Model;
import org.genxdm.base.ProcessingContext;
import org.genxdm.base.io.DocumentHandler;
import org.genxdm.bridgekit.ProcessingContextFactory;
import org.genxdm.exceptions.GxmlMarshalException;
import org.genxdm.processor.w3c.xs.W3cXmlSchemaParser;
import org.genxdm.processor.w3c.xs.validation.GxContentValidator;
import org.genxdm.processor.w3c.xs.validation.GxValidatorCache;
import org.genxdm.processor.w3c.xs.validation.ValidatorCacheFactory;
import org.genxdm.typed.TypedContext;
import org.genxdm.xs.SmMetaLoadArgs;
import org.genxdm.xs.components.SmComponentBag;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.exceptions.SmExceptionCatcher;

/**
 * This sample illustrates a simple, serialization.  
 * 
 * @param <N> the node type
 * @author jbaysdon
 */
public abstract class BridgePerformance<N, A> extends BasePerformance implements ProcessingContextFactory<N>
{
	N m_testNode;
	ProcessingContext<N> m_pcx;	
	Model<N> m_model;
    DocumentHandler<N> m_handler;
    GxContentValidator<A> m_validator;
    
	public BridgePerformance(String propFile)
	{
		super(propFile);
	}
	final void setupTesting()
	{
		m_pcx = newProcessingContext();
		m_model = m_pcx.getModel();
	    m_handler = m_pcx.newDocumentHandler();
	}
	final void setupParsing(){}
	final void setupSerialization(){}
	final void setupNavigation(){}
	final void setupValidation()
	{
		// Load a schema...
        final TypedContext<N, A> tpcx = m_pcx.getTypedContext();
        final String schemaFilePath = m_baseURI + "/" + m_schemaFile;
        try {
			loadSchema(tpcx, new URI(schemaFilePath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SmAbortException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
        
		// Create a validator...
		final ValidatorCacheFactory<N, A> vcf = new ValidatorCacheFactory<N, A>(tpcx);
		final GxValidatorCache<A> vc = vcf.newValidatorCache();
		m_validator = vc.newContentValidator();
	}
	final void parse(final String docFilePath)
	{
		try {
			m_testNode = m_handler.parse(new FileReader(docFilePath), null);
		} catch (GxmlMarshalException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	final void serialize()
	{
		try {
			final StringWriter iwriter = new StringWriter();
	        m_handler.write(iwriter, m_testNode);
		} catch (GxmlMarshalException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	final void navigate(TaskTimer ttTimer)
	{
		long attributes = 0;
		long comments = 0;
		long documents = 0;
		long elements = 0;
		long namespaces = 0;
		long processingInstructions = 0;
		long texts = 0;
		long other = 0;
		
		N node = m_testNode;
        ttTimer.startTimer();
	    while (node!=null) {
	        switch(m_model.getNodeKind(node))
	        {
			case ATTRIBUTE:
				attributes++;
				break;
			case COMMENT:
				comments++;
				break;
			case DOCUMENT:
				documents++;
				break;
			case ELEMENT:
				elements++;
				break;
			case NAMESPACE:
				namespaces++;
				break;
			case PROCESSING_INSTRUCTION:
				processingInstructions++;
				break;
			case TEXT:
				texts++;
				break;
	        default:
	        	other++;
	        	break;
	        }
	        if(m_model.hasChildren(node))
	        {
		        node = m_model.getFirstChild(node);
	        } 
	        else {
	        	while (m_model.getNextSibling(node) == null && node != m_testNode)
	        	{
	        		node = m_model.getParent(node);
	        	}
		        node = m_model.getNextSibling(node);
		      } 
	    } 
        ttTimer.stopTimer();
       	if(attributes > 0)             ttTimer.addNote("att      count = " + Long.toString(attributes));
       	if(comments > 0)               ttTimer.addNote("comment  count = " + Long.toString(comments));
       	if(documents > 0)              ttTimer.addNote("document count = " + Long.toString(documents));
       	if(elements > 0)               ttTimer.addNote("element  count = " + Long.toString(elements));
       	if(namespaces > 0)             ttTimer.addNote("ns       count = " + Long.toString(namespaces));
       	if(processingInstructions > 0) ttTimer.addNote("pi       count = " + Long.toString(processingInstructions));
       	if(texts > 0)                  ttTimer.addNote("text     count = " + Long.toString(texts));
       	if(other > 0)                  ttTimer.addNote("other    count = " + Long.toString(other));
	}
	final void validate(TaskTimer ttValidate)
	{
		try {
			final SmExceptionCatcher errors = new SmExceptionCatcher();
			m_validator.setExceptionHandler(errors);
			ttValidate.startTimer();
	        
			m_model.stream(m_testNode, true, m_validator);

	        ttValidate.stopTimer();
			if(!errors.isEmpty())
			{
				ttValidate.addNote(errors.size() + " validation errors.");
				int errorCnt = 0;
				for (SmException ex : errors)
				{
					if(errorCnt == 10)
					{
						ttValidate.addNote("\t" + "... that's all we're listing...");
						break;
					}
					errorCnt++;
					ttValidate.addNote("\t" + ex.getMessage());
				}
			}
		} catch (GxmlMarshalException e) {
			throw new RuntimeException(e);
		}
	}
	final void loadSchema(TypedContext<N, A> tpcx, URI uri) throws IOException, SmAbortException, URISyntaxException
	{
		Resolver resolver = new SampleResolver(new URI(m_baseURI));
		// Load a top-level schema into the processing context.
		final List<Resolved<InputStream>> resources = new LinkedList<Resolved<InputStream>>();
		resources.add(resolver.resolveInputStream(uri));

		final SmExceptionCatcher errors = new SmExceptionCatcher();
		final SmMetaLoadArgs args = new SmMetaLoadArgs();
		final W3cXmlSchemaParser<A> parser = new W3cXmlSchemaParser<A>(tpcx.getAtomBridge());

		for (final Resolved<InputStream> resource : resources)
		{
			SmComponentBag<A> scBag = parser.parse(resource.getLocation(), resource.getResource(), resource.getSystemId(), errors, args, tpcx);
			tpcx.register(scBag);
		}
	}
}
