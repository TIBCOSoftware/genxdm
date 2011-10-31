package org.genxdm.transform;

import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.FragmentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XdmResult<N>
    implements Result
{
    
    public static final String FEATURE = "org.genxdm.transform";
    
    public XdmResult(final FragmentBuilder<N> builder)
    {
        this.builder = PreCondition.assertNotNull(builder, "builder");
    }
    
    public FragmentBuilder<N> getBuilder()
    {
        return builder;
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
