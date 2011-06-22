package org.genxdm.processor.w3c.xs.validationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.typed.TypedModel;

public class POVerifier
{

    static <N> void verifyUntypedTree(N untyped, Model<N> model)
    {
        N docElement = null;
        N child = null;
        N grandChild = null;
        N target = null;

        assertEquals(NodeKind.DOCUMENT, model.getNodeKind(untyped));
        docElement = model.getFirstChildElement(untyped);
        assertNotNull(docElement);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(docElement));
        assertEquals("purchaseOrder", model.getLocalName(docElement));
        
        target = model.getAttribute(docElement, "", "orderDate");
        assertNotNull(target);
        assertEquals(NodeKind.ATTRIBUTE, model.getNodeKind(target));
        // TODO: make sure that the tree exists, and hasn't gone weird.
    }
    
    static <N, A> void verifyTyped(N typed, TypedModel<N, A> model)
    {
        // document element is named purchaseOrder, of type PurchaseOrderType
        // document element has an attribute orderDate of type date
        // shipTo @country [NMTOKEN] {fixed == US}
        // // name
        // // street
        // // city
        // // state
        // // zip [decimal] // snicker
        // billTo (same as shipTo)
        // comment ?
        // items
        // // item @partNum * [SKU : \d{3}-[A-Z]{2} ]
        // // // productName
        // // // quantity [positiveInteger maxexclusive 100]
        // // // comment ?
        // // // USPrice [decimal]
        // // // shipDate [date]
    }
}
