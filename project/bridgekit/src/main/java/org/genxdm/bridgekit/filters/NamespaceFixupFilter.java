package org.genxdm.bridgekit.filters;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentFilter;
import org.genxdm.io.ContentHandler;
import org.genxdm.names.RegisteredPrefixProvider;

/** Implements ContentFilter to perform namespace checking with fixups, so
 * that namespace-ill-formed sequences of calls can produce a conformant
 * XDM.
 *
 */
public class NamespaceFixupFilter
    extends AbstractNamespaceFixupHandler
    implements ContentFilter
{
    public NamespaceFixupFilter()
    {
        this(null);
    }
    
    public NamespaceFixupFilter(RegisteredPrefixProvider provider)
    {
        super(provider);
    }
    
    @Override
    public void setOutputContentHandler(ContentHandler handler)
    {
        output = PreCondition.assertNotNull(handler);
    }

    @Override
    protected ContentHandler getOutputHandler()
    {
        return output;
    }

    @Override
    protected void outputAttribute(Attr a)
    {
        output.attribute(a.namespace, a.name, a.prefix, a.value, a.type);
    }

    private ContentHandler output;
}
