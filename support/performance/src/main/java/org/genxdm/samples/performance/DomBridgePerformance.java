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

import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.w3c.dom.Node;

public class DomBridgePerformance extends BridgePerformance<Node, XmlAtom> {

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
				DomBridgePerformance sample = new DomBridgePerformance(args[0]);
				sample.runPerfTest();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public DomBridgePerformance(String propsFile)
	{
		super(propsFile);
	}
    public final DomProcessingContext newProcessingContext()
    {
        return new DomProcessingContext();
    }
}
