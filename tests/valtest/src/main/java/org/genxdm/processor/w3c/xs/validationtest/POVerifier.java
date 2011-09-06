package org.genxdm.processor.w3c.xs.validationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.genxdm.Model;
import org.genxdm.NodeKind;
import org.genxdm.typed.TypedModel;

public class POVerifier
{

    static <N> void verifyUntyped(N untyped, Model<N> model)
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
        
        child = model.getFirstChildElement(docElement);
        assertNotNull(child);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(child));
        assertEquals("shipTo", model.getLocalName(child));
        target = model.getAttribute(child, "", "country");
        assertNotNull(target);
        assertEquals(NodeKind.ATTRIBUTE, model.getNodeKind(target));
        
        grandChild = model.getFirstChildElement(child);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("name", model.getLocalName(grandChild));
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("street", model.getLocalName(grandChild));
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("city", model.getLocalName(grandChild));

        grandChild = model.getNextSiblingElement(grandChild);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("state", model.getLocalName(grandChild));
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("zip", model.getLocalName(grandChild));
        
        child = model.getNextSiblingElement(child);
        assertNotNull(child);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(child));
        assertEquals("billTo", model.getLocalName(child));
        target = model.getAttribute(child, "", "country");
        assertNotNull(target);
        assertEquals(NodeKind.ATTRIBUTE, model.getNodeKind(target));

        grandChild = model.getFirstChildElement(child);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("name", model.getLocalName(grandChild));
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("street", model.getLocalName(grandChild));
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("city", model.getLocalName(grandChild));

        grandChild = model.getNextSiblingElement(grandChild);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("state", model.getLocalName(grandChild));
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("zip", model.getLocalName(grandChild));
                
        child = model.getNextSiblingElement(child);
        assertNotNull(child);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(child));
        assertEquals("comment", model.getLocalName(child));
        
        child = model.getNextSiblingElement(child);
        assertNotNull(child);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(child));
        assertEquals("items", model.getLocalName(child));
        
        grandChild = model.getFirstChildElement(child);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("item", model.getLocalName(grandChild));
        
        target = model.getAttribute(grandChild, "", "partNum");
        assertNotNull(target);
        assertEquals(NodeKind.ATTRIBUTE, model.getNodeKind(target));
        
        target = model.getFirstChildElement(grandChild);
        assertNotNull(target);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(target));
        assertEquals("productName", model.getLocalName(target));
        
        target = model.getNextSiblingElement(target);
        assertNotNull(target);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(target));
        assertEquals("quantity", model.getLocalName(target));
        
        target = model.getNextSiblingElement(target);
        assertNotNull(target);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(target));
        assertEquals("USPrice", model.getLocalName(target));
        
        target = model.getNextSiblingElement(target);
        assertNotNull(target);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(target));
        assertEquals("comment", model.getLocalName(target));
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertNotNull(grandChild);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(grandChild));
        assertEquals("item", model.getLocalName(grandChild));

        target = model.getAttribute(grandChild, "", "partNum");
        assertNotNull(target);
        assertEquals(NodeKind.ATTRIBUTE, model.getNodeKind(target));
        
        target = model.getFirstChildElement(grandChild);
        assertNotNull(target);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(target));
        assertEquals("productName", model.getLocalName(target));
        
        target = model.getNextSiblingElement(target);
        assertNotNull(target);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(target));
        assertEquals("quantity", model.getLocalName(target));
        
        target = model.getNextSiblingElement(target);
        assertNotNull(target);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(target));
        assertEquals("USPrice", model.getLocalName(target));
        
        target = model.getNextSiblingElement(target);
        assertNotNull(target);
        assertEquals(NodeKind.ELEMENT, model.getNodeKind(target));
        assertEquals("shipDate", model.getLocalName(target));
    }
    
    static <N, A> void verifyTyped(N typed, TypedModel<N, A> model)
    {
        // we've already verified the structure in verifyUntyped.
        // here, just verify the types associated with each node.
        N docElement = null;
        N child = null;
        N grandChild = null;
        N target = null;

        assertEquals(NodeKind.DOCUMENT, model.getNodeKind(typed));
        docElement = model.getFirstChildElement(typed);
        assertEquals("purchaseOrder", model.getLocalName(docElement));
        assertEquals("PurchaseOrderType", model.getTypeName(docElement).getLocalPart());
        
        target = model.getAttribute(docElement, "", "orderDate");
        assertEquals("date", model.getTypeName(target).getLocalPart());
        
        child = model.getFirstChildElement(docElement);
        assertEquals("shipTo", model.getLocalName(child));
        assertEquals("USAddress", model.getTypeName(child).getLocalPart());
        target = model.getAttribute(child, "", "country");
        assertEquals("NMTOKEN", model.getTypeName(target).getLocalPart());
        
        grandChild = model.getFirstChildElement(child);
        assertEquals("name", model.getLocalName(grandChild));
        assertEquals("string", model.getTypeName(grandChild).getLocalPart());
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertEquals("street", model.getLocalName(grandChild));
        assertEquals("string", model.getTypeName(grandChild).getLocalPart());
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertEquals("city", model.getLocalName(grandChild));
        assertEquals("string", model.getTypeName(grandChild).getLocalPart());

        grandChild = model.getNextSiblingElement(grandChild);
        assertEquals("state", model.getLocalName(grandChild));
        assertEquals("string", model.getTypeName(grandChild).getLocalPart());
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertEquals("zip", model.getLocalName(grandChild));
        assertEquals("decimal", model.getTypeName(grandChild).getLocalPart());
        
        child = model.getNextSiblingElement(child);
        assertEquals("billTo", model.getLocalName(child));
        assertEquals("USAddress", model.getTypeName(child).getLocalPart());
        target = model.getAttribute(child, "", "country");
        assertEquals("NMTOKEN", model.getTypeName(target).getLocalPart());

        grandChild = model.getFirstChildElement(child);
        assertEquals("name", model.getLocalName(grandChild));
        assertEquals("string", model.getTypeName(grandChild).getLocalPart());
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertEquals("street", model.getLocalName(grandChild));
        assertEquals("string", model.getTypeName(grandChild).getLocalPart());
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertEquals("city", model.getLocalName(grandChild));
        assertEquals("string", model.getTypeName(grandChild).getLocalPart());

        grandChild = model.getNextSiblingElement(grandChild);
        assertEquals("state", model.getLocalName(grandChild));
        assertEquals("string", model.getTypeName(grandChild).getLocalPart());
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertEquals("zip", model.getLocalName(grandChild));
        assertEquals("decimal", model.getTypeName(grandChild).getLocalPart());
                
        child = model.getNextSiblingElement(child);
        assertEquals("comment", model.getLocalName(child));
        assertEquals("string", model.getTypeName(child).getLocalPart());
        
        child = model.getNextSiblingElement(child);
        assertEquals("items", model.getLocalName(child));
        assertEquals("Items", model.getTypeName(child).getLocalPart());
        
        grandChild = model.getFirstChildElement(child);
        assertEquals("item", model.getLocalName(grandChild));
        // TODO
        // we can't predict the name.  it's anonymous.  we need the
        // types bridge to assert that it is anonymous, i think.
        String itemTypeName = model.getTypeName(grandChild).getLocalPart();
        
        target = model.getAttribute(grandChild, "", "partNum");
        assertEquals("SKU", model.getTypeName(target).getLocalPart());
        
        target = model.getFirstChildElement(grandChild);
        assertEquals("productName", model.getLocalName(target));
        assertEquals("string", model.getTypeName(target).getLocalPart());
        
        target = model.getNextSiblingElement(target);
        assertEquals("quantity", model.getLocalName(target));
        // TODO
        // anonymous simple type; its base type is integer
        
        target = model.getNextSiblingElement(target);
        assertEquals("USPrice", model.getLocalName(target));
        assertEquals("decimal", model.getTypeName(target).getLocalPart());
        
        target = model.getNextSiblingElement(target);
        assertEquals("comment", model.getLocalName(target));
        assertEquals("string", model.getTypeName(target).getLocalPart());
        
        grandChild = model.getNextSiblingElement(grandChild);
        assertEquals("item", model.getLocalName(grandChild));
        assertEquals(itemTypeName, model.getTypeName(grandChild).getLocalPart());

        target = model.getAttribute(grandChild, "", "partNum");
        assertEquals("SKU", model.getTypeName(target).getLocalPart());

        target = model.getFirstChildElement(grandChild);
        assertEquals("productName", model.getLocalName(target));
        assertEquals("string", model.getTypeName(target).getLocalPart());
        
        target = model.getNextSiblingElement(target);
        assertEquals("quantity", model.getLocalName(target));
        // TODO see above
        
        target = model.getNextSiblingElement(target);
        assertEquals("USPrice", model.getLocalName(target));
        assertEquals("decimal", model.getTypeName(target).getLocalPart());
        
        target = model.getNextSiblingElement(target);
        assertEquals("shipDate", model.getLocalName(target));
        assertEquals("date", model.getTypeName(target).getLocalPart());
    }
}
