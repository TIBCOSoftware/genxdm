package org.genxdm.processor.w3c.xs.tests;

import javax.xml.namespace.QName;

import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ComponentKind;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ContentType;
import org.genxdm.xs.types.SimpleType;

public class Utilities
{
    static void displayComponents(ComponentBag components)
    {
        Iterable<AttributeDefinition> atts = components.getAttributes();
        System.out.println("Global Attributes: " + count(atts));
        for (AttributeDefinition att : atts)
            System.out.println(TAB + att.getName());
        Iterable<AttributeGroupDefinition> attGroups = components.getAttributeGroups();
        System.out.println("Attribute Groups: " + count(attGroups));
        for (AttributeGroupDefinition attGroup : attGroups)
            System.out.println(TAB + attGroup.getName());
        
        Iterable<ElementDefinition> elems = components.getElements();
        System.out.println("Elements: " + count(elems));
        for (ElementDefinition element : elems)
            System.out.println(TAB + element.getName());
        
        Iterable<ModelGroup> models = components.getModelGroups();
        System.out.println("Model Groups: " + count(models));
        for (ModelGroup model : models)
            System.out.println(TAB + model.getName());
        
        Iterable<SimpleType> simps = components.getSimpleTypes();
        System.out.println("Simple Types: " + count(simps));
        for (SimpleType simple : simps)
            System.out.println(TAB + simple.getName());
        Iterable<ComplexType> comps = components.getComplexTypes();
        System.out.println("Complex Types: " + count(comps));
        for (ComplexType comp : comps)
            System.out.println(TAB + comp.getName());

        Iterable<IdentityConstraint> ids = components.getIdentityConstraints();
        System.out.println("Identity Constraints: " + count(ids));
        for (IdentityConstraint id : ids)
            System.out.println(TAB + id.getName());
        Iterable<NotationDefinition> nots = components.getNotations();
        System.out.println("Notations: " + count(nots));
        for (NotationDefinition not : nots)
            System.out.println(TAB + not.getName());
    }
    
    static void displayComponent(ComponentProvider provider, ComponentKind kind, QName name)
    {
        switch (kind)
        {
            // TODO: incomplete
            case COMPLEX_TYPE :
            {
                ComplexType type = provider.getComplexType(name);
                System.out.println("Complex Type: {" + name.getNamespaceURI() + "}" + name.getLocalPart());
                System.out.println(TAB + "Content Type: " + displaySimplifiedContentType(type.getContentType()));
                break;
            }
            default :
                System.out.println("Sorry, display of " + kind + " hasn't been written. Write what you need, 'kay?");
        }
    }
    
    static String displaySimplifiedContentType(ContentType ct)
    {
        if (ct.isEmpty())
            return "EMPTY";
        else if (ct.isSimple())
        {
            QName t = ct.getSimpleType().getName();
            return "SIMPLE ({" + t.getNamespaceURI() + "}" + t.getLocalPart() + ")";
        }
        else if (ct.isComplex())
        {
            if (ct.isElementOnly())
                return "COMPLEX: ELEMENT ONLY"; // TODO: show the content model
            else if (ct.isMixed())
                return "COMPLEX: MIXED";
        }
        return "UNKNOWN";
    }

    static <E> int count(Iterable<E> it)
    {
        int i = 0;
        for (@SuppressWarnings("unused") E e : it) i++;
        return i;
    }
    
    private static final String TAB = "  ";
}
