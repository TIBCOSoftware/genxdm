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

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.genxdm.Model;
import org.genxdm.exceptions.PreCondition;
import org.w3c.dom.Node;

/** Implement source for all supported GenXDM bridges (current and future).
 * 
 * @param <N> the node abstraction.
 */
public class XdmSource<N>
    implements Source
{
    
    public static final String FEATURE = "org.genxdm.transform";
    
    public XdmSource(final N node, final Model<N> model)
    {
        this.model = PreCondition.assertNotNull(model, "model");
        this.node = PreCondition.assertNotNull(node, "node");
    }
    
    /** Get the model associated with this Source.  Normally
     * called by the processor.
     * 
     * @return the Model with which the Source was instantiated.
     */
    public Model<N> getModel()
    {
        return model;
    }
    
    /** Get the &lt;N>ode associated with this Source.  Normally
     * called by the processor.
     * 
     * @return the N with which the Source was instantiated.
     */
    public N getNode()
    {
        return node;
    }
    
    /** Get the SystemID for this Source.
     * 
     * @return the system ID, if there is one; null otherwise.
     */
    @Override
    public String getSystemId()
    {
        return systemId;
    }
    
    /** Set the SystemID for this Source.
     *
     * @param systemID the system ID
     */
    @Override
    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

    /** A factory method useful for turning a (supplied) DOMSource into an
     * XdmSource specialized for the DOM.
     * 
     * <p>This permits a processor that understands GenXDM, but not DOM, to
     * process an incoming DOM tree, using the GenXDM DOM bridge.</p>
     * 
     * @param ds the initialized DOMSource, which may not be null.
     * @param model a DOM Model, which may not be null.
     * @return an XdmSource specialized for the DOM and initialized with the
     * content of the DOMSource.
     */
    public static XdmSource<Node> XdmSourceFromDomSource(final DOMSource ds, final Model<Node> model)
    {
        return new XdmSource<Node>(ds.getNode(), model);
    }

    private final Model<N> model;
    private final N node;
    private String systemId;
}
