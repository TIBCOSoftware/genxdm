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
package org.gxml.samples.conversion;

import org.gxml.bridge.cx.base.XmlNodeContext;
import org.gxml.bridge.cx.tree.XmlNode;
import org.gxml.bridge.dom.DomProcessingContext;
import org.gxml.base.ProcessingContext;
import org.w3c.dom.Node;

public class Cx2DomSampleConverter {
	
	/**
	 * Creates Cx & DOM processing contexts for conversion example.
	 * @param args args[0] is filepath to input document
	 */
	static public void main(String[] args)
	{
		ProcessingContext<XmlNode> cxContext = new XmlNodeContext();
		ProcessingContext<Node> domContext = new DomProcessingContext();
		
		try {
			SampleConverter.convertSample(args[0], cxContext, domContext);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
