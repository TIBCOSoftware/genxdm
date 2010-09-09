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
package org.gxml.bridge.cx.tests;

import org.gxml.bridgetest.TimingTestBase;
import org.gxml.bridge.cx.base.XmlNodeContext;
import org.gxml.bridge.cx.tree.XmlNode;
import org.gxml.names.NameSource;

public final class CxTimingTest 
    extends TimingTestBase<XmlNode> 
{
	public XmlNodeContext newProcessingContext()
	{
	    return new XmlNodeContext();
	}

    protected NameSource newNameBridge()
    {
        return new NameSource();
    }
}