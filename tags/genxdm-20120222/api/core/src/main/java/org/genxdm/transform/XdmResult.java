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
package org.genxdm.transform;

import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.FragmentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/** Implement Result for all supported GenXDM bridges (current and future).
 * 
 * @param <N> the node abstraction.
 */
public class XdmResult<N>
    implements Result
{
    
    public static final String FEATURE = "org.genxdm.transform";
    
    public XdmResult(final FragmentBuilder<N> builder)
    {
        this.builder = PreCondition.assertNotNull(builder, "builder");
    }
    
    /** Get the {@link FragmentBuilder} for this result.
     * 
     * @return the builder (never null).
     */
    public FragmentBuilder<N> getBuilder()
    {
        return builder;
    }

    /** Get the systemID set for this Result.
     * 
     * @return the system ID, if there is one; null if not.
     */
    @Override
    public String getSystemId()
    {
        return systemId;
    }

    /** Set the systemID for this Result.
     * 
     */
    @Override
    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }
    
    /** After processing, insert the (DOM) XdmResult into the (original)
     * DOMResult.  This is only relevant if one has been initially supplied
     * a DOMResult.  The code simply finds the insertion point (if there is
     * one) and inserts the results from the FragmentBuilder into the tree
     * at the appropriate point.
     * 
     * <p>This permits an application to supply a DOMResult, a GenXDM processor
     * to actually populate a DOM-specialized XdmResult (assuming that an
     * intermediary intercepts the DOMResult and creates the DOM-specialized
     * XdmResult), and then, before the result is returned, to transfer the
     * generated information from the XdmResult to the DOMResult.</p>
     * 
     * @param dr the DOMResult that is to be populated or modified; may not be null.
     * @param builder the FragmentBuilder (from a processed XdmResult that was supplied
     * a FragmentBuilder&lt;Node> in the first place), never null.
     */
    public static void insertXdmDomFragmentIntoDomResult(DOMResult dr, FragmentBuilder<Node> builder)
    {
        List<Node> nodes = builder.getNodes();
        if (nodes.isEmpty())
            return;
        final Node parent = dr.getNode();
        final Document owner = (parent == null) ? null :
                               ( (parent.getNodeType() == Node.DOCUMENT_NODE) ? (Document)parent : parent.getOwnerDocument() );
        final Node next = dr.getNextSibling();
        if (next != null)
        {
            for (Node node : nodes)
            {
                parent.insertBefore(node.getOwnerDocument() == owner ? node : owner.importNode(node, true), next);
            }
        }
        else if (parent != null)
        {
            for (Node node : nodes)
            {
                parent.appendChild(node.getOwnerDocument() == owner ? node : owner.importNode(node, true));
            }
        }
        else
        {
            dr.setNode(nodes.get(0));
        }
    }

    private final FragmentBuilder<N> builder;
    private String systemId;
}
