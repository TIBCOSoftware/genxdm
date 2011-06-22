package org.genxdm.processor.w3c.xs.validationtest;

import org.genxdm.Model;
import org.genxdm.exceptions.XdmMarshalException;
import org.genxdm.typed.TypedModel;

public class POVerifier
{

    static <N> void verifyUntypedTree(N untyped, Model<N> model)
        throws XdmMarshalException
    {
        // TODO: make sure that the tree exists, and hasn't gone weird.
    }
    
    static <N, A> void verifyTyped(N typed, TypedModel<N, A> model)
        throws XdmMarshalException
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
