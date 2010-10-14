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
package org.genxdm.bridge.cx.base;

import org.genxdm.base.mutable.MutableContext;
import org.genxdm.base.mutable.MutableCursor;
import org.genxdm.base.mutable.MutableModel;
import org.genxdm.base.mutable.NodeFactory;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridge.cx.tree.XmlNodeFactory;
import org.genxdm.bridge.cx.tree.XmlNodeMutator;
import org.genxdm.exceptions.PreCondition;

public class XmlNodeMutableContext
    implements MutableContext<XmlNode>
{
    
    XmlNodeMutableContext(XmlNodeContext context)
    {
        this.context = PreCondition.assertNotNull(context, "context");
    }

    public MutableModel<XmlNode> getModel()
    {
        return model;
    }

    public NodeFactory<XmlNode> getNodeFactory()
    {
        return factory;
    }

    public XmlNodeContext getProcessingContext()
    {
        return context;
    }

    public MutableCursor<XmlNode> newCursor(XmlNode node)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private final XmlNodeFactory factory = new XmlNodeFactory();
    private final XmlNodeContext context;
    private final XmlNodeMutator model = new XmlNodeMutator();
}
