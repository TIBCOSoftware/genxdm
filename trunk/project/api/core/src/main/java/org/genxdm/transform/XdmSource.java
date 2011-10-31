package org.genxdm.transform;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.genxdm.Model;
import org.genxdm.exceptions.PreCondition;
import org.w3c.dom.Node;

public class XdmSource<N>
    implements Source
{
    
    public static final String FEATURE = "org.genxdm.transform";
    
    public XdmSource(final N node, final Model<N> model)
    {
        this.model = PreCondition.assertNotNull(model, "model");
        this.node = PreCondition.assertNotNull(node, "node");
    }
    
    public Model<N> getModel()
    {
        return model;
    }
    
    public N getNode()
    {
        return node;
    }
    
    @Override
    public String getSystemId()
    {
        return systemId;
    }
    
    @Override
    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

    public static XdmSource<Node> XdmSourceFromDomSource(final DOMSource ds, final Model<Node> model)
    {
        return new XdmSource<Node>(ds.getNode(), model);
    }

    private final Model<N> model;
    private final N node;
    private String systemId;
}
