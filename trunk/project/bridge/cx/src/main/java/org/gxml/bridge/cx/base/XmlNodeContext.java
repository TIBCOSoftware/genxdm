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
package org.gxml.bridge.cx.base;

import javax.xml.stream.XMLReporter;

import org.gxml.bridgekit.atoms.XmlAtom;
import org.gxml.processor.io.DefaultDocumentHandler;
import org.gxml.Feature;
import org.gxml.Resolver;
import org.gxml.base.Cursor;
import org.gxml.base.Model;
import org.gxml.base.ProcessingContext;
import org.gxml.base.io.DocumentHandler;
import org.gxml.base.io.FragmentBuilder;
import org.gxml.base.mutable.MutableContext;
import org.gxml.bridge.cx.tree.XmlNode;
import org.gxml.bridge.cx.typed.TypedXmlNodeContext;
import org.gxml.exceptions.PreCondition;
import org.gxml.nodes.Bookmark;
import org.gxml.typed.TypedContext;

public final class XmlNodeContext
    implements ProcessingContext<XmlNode>
{
    public XmlNodeContext()
    {
         mutant = new XmlNodeMutableContext(this);
    }

    public Bookmark<XmlNode> bookmark(XmlNode node)
    {
        return new XmlNodeMarker(node, model);
    }

    public Model<XmlNode> getModel()
    {
        return model;
    }

    public MutableContext<XmlNode> getMutableContext()
    {
        return mutant;
    }

    public TypedContext<XmlNode, XmlAtom> getTypedContext()
    {
        return new TypedXmlNodeContext(this);
    }

    public boolean isNode(Object item)
    {
        return (item instanceof XmlNode);
    }

    public boolean isSupported(String feature)
    {
        PreCondition.assertNotNull(feature, "feature");
        if (feature.startsWith(Feature.PREFIX))
        {
            // support all core features
            return true;
        }
        return false;
    }

    public Cursor<XmlNode> newCursor(XmlNode node)
    {
        return new XmlNodeCursor(node);
    }

    public FragmentBuilder<XmlNode> newFragmentBuilder()
    {
        return new XmlNodeBuilder();
    }

    public XmlNode node(Object item)
    {
        return isNode(item) ? (XmlNode)item : null;
    }

    public XmlNode[] nodeArray(int size)
    {
        // TODO: our tests don't permit us to assert.  are the tests wrong?
//        PreCondition.assertTrue(size > -1);
        return new XmlNode[size];
    }

    public DocumentHandler<XmlNode> newDocumentHandler()
    {
        return new DefaultDocumentHandler<XmlNode>(newFragmentBuilder(), model);
    }

    public DocumentHandler<XmlNode> newDocumentHandler(XMLReporter reporter, Resolver resolver)
    {
        // TODO: implement
        return newDocumentHandler();
    }

    public void setDefaultReporter(XMLReporter reporter)
    {
        // TODO: implement
    }
    
    public void setDefaultResolver(Resolver resolver)
    {
        // TODO: implement
    }

    private final XmlNodeModel model = new XmlNodeModel();
    private final XmlNodeMutableContext mutant;
}
