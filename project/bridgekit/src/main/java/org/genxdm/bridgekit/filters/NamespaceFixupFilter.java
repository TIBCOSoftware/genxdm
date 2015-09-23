package org.genxdm.bridgekit.filters;

import org.genxdm.bridgekit.filters.AbstractNamespaceFixupHandler.Attr;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.ContentFilter;
import org.genxdm.io.ContentHandler;

/** Implements ContentFilter to perform namespace checking with fixups, so
 * that namespace-ill-formed sequences of calls can produce a conformant
 * XDM.
 *
 */
public class NamespaceFixupFilter
    extends AbstractNamespaceFixupHandler
    implements ContentFilter
{
    
    @Override
    public void setOutputContentHandler(ContentHandler handler)
    {
        output = PreCondition.assertNotNull(handler);
    }

    @SuppressWarnings("unchecked")
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
