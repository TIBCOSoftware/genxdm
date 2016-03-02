package org.genxdm.bridge.axiom;

import java.lang.ref.WeakReference;

import org.apache.axiom.om.OMDocument;
import org.genxdm.exceptions.PreCondition;

public class DocumentIdentity
{
    DocumentIdentity(OMDocument document)
    {
        this.document = new WeakReference<OMDocument>(PreCondition.assertNotNull(document, "document"));
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof DocumentIdentity) 
            return hashCode() == o.hashCode();
        return document.get().equals(o);
    }
    
    @Override
    public int hashCode()
    {
        return document.get().hashCode();
    }
    private final WeakReference<OMDocument> document;
}
