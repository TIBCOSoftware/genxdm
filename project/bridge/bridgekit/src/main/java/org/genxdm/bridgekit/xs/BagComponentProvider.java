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
package org.genxdm.bridgekit.xs;

/** Provides access to named components, given a ComponentBag.
 * 
 * Note that this is not a dynamic adapter.  It is initialized with
 * a ComponentBag (and can be reinitialized), but if the bag on which
 * its state is based is changed, this provider will <em>not</em> change.
 * 
 * Note that this implementation <em>does</em> include the standard built-ins.
 */
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.AtomicUrType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ComplexUrType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.Type;

public class BagComponentProvider
    implements ComponentProvider
{
    public BagComponentProvider(final ComponentBag bag)
    {
        initialize(PreCondition.assertNotNull(bag, "bag"));
    }
    
    public void reinitialize(final ComponentBag bag)
    {
        PreCondition.assertNotNull(bag, "bag");
        attributeGroups.clear();
        attributes.clear();
        complexTypes.clear();
        elements.clear();
        identities.clear();
        modelGroups.clear();
        notations.clear();
        simpleTypes.clear();
        builtins();
        initialize(bag);
    }

    @Override
    public boolean hasAttribute(QName name)
    {
        return attributes.containsKey(name);
    }

    @Override
    public boolean hasAttributeGroup(QName name)
    {
        return attributeGroups.containsKey(name);
    }

    @Override
    public boolean hasComplexType(QName name)
    {
        return complexTypes.containsKey(name);
    }

    @Override
    public boolean hasElement(QName name)
    {
        return elements.containsKey(name);
    }

    @Override
    public boolean hasIdentityConstraint(QName name)
    {
        return identities.containsKey(name);
    }

    @Override
    public boolean hasModelGroup(QName name)
    {
        return modelGroups.containsKey(name);
    }

    @Override
    public boolean hasNotation(QName name)
    {
        return notations.containsKey(name);
    }

    @Override
    public boolean hasSimpleType(QName name)
    {
        return simpleTypes.containsKey(name);
    }

    @Override
    public boolean hasType(QName name)
    {
        return (hasSimpleType(name) || hasComplexType(name));
    }

    @Override
    public QName generateUniqueName()
    {
        // TODO: we really need a better way of generating unique names, dammit.
        return null;
    }

    @Override
    public AtomicType getAtomicType(QName name)
    {
        final SimpleType type = simpleTypes.get(name);
        if ( (type != null) && type.isAtomicType())
            return (AtomicType)type;
        return null;
    }

    @Override
    public AtomicType getAtomicType(NativeType name)
    {
        final Type type = getTypeDefinition(name);
        if (type.isAtomicType())
            return (AtomicType)type;
        return null;
    }

    @Override
    public AtomicUrType getAtomicUrType()
    {
        return BuiltInSchema.SINGLETON.ANY_ATOMIC_TYPE;
    }

    @Override
    public AttributeDefinition getAttributeDeclaration(QName name)
    {
        return attributes.get(name);
    }

    @Override
    public AttributeGroupDefinition getAttributeGroup(QName name)
    {
        return attributeGroups.get(name);
    }

    @Override
    public ComplexType getComplexType(QName name)
    {
        return complexTypes.get(name);
    }

    @Override
    public ComplexUrType getComplexUrType()
    {
        return BuiltInSchema.SINGLETON.ANY_COMPLEX_TYPE;
    }

    @Override
    public ElementDefinition getElementDeclaration(QName name)
    {
        return elements.get(name);
    }

    @Override
    public IdentityConstraint getIdentityConstraint(QName name)
    {
        return identities.get(name);
    }

    @Override
    public ModelGroup getModelGroup(QName name)
    {
        return modelGroups.get(name);
    }

    @Override
    public NotationDefinition getNotationDeclaration(QName name)
    {
        return notations.get(name);
    }

    @Override
    public SimpleType getSimpleType(QName name)
    {
        return simpleTypes.get(name);
    }

    @Override
    public SimpleType getSimpleType(NativeType name)
    {
        final Type type = getTypeDefinition(name);
        if (type instanceof SimpleType)
            return (SimpleType)type;
        return null;
    }

    @Override
    public SimpleUrType getSimpleUrType()
    {
        return BuiltInSchema.SINGLETON.ANY_SIMPLE_TYPE;
    }

    @Override
    public Type getTypeDefinition(QName name)
    {
        PreCondition.assertArgumentNotNull(name, "name");
        if (hasComplexType(name))
            return complexTypes.get(name);
        if (hasSimpleType(name))
            return simpleTypes.get(name);
        if (name.equals(BuiltInSchema.SINGLETON.ANY_ATOMIC_TYPE.getName()))
            return BuiltInSchema.SINGLETON.ANY_ATOMIC_TYPE;
        if (name.equals(BuiltInSchema.SINGLETON.ANY_SIMPLE_TYPE.getName()))
            return BuiltInSchema.SINGLETON.ANY_SIMPLE_TYPE;
        if (name.equals(BuiltInSchema.SINGLETON.ANY_COMPLEX_TYPE.getName()))
            return BuiltInSchema.SINGLETON.ANY_COMPLEX_TYPE;
        return null;
    }

    @Override
    public Type getTypeDefinition(NativeType nativeType)
    {
        switch (nativeType)
        {
            case BOOLEAN:
            {
                return BuiltInSchema.SINGLETON.BOOLEAN;
            }
            case STRING:
            {
                return BuiltInSchema.SINGLETON.STRING;
            }
            case DOUBLE:
            {
                return BuiltInSchema.SINGLETON.DOUBLE;
            }
            case FLOAT:
            {
                return BuiltInSchema.SINGLETON.FLOAT;
            }
            case DECIMAL:
            {
                return BuiltInSchema.SINGLETON.DECIMAL;
            }
            case INTEGER:
            {
                return BuiltInSchema.SINGLETON.INTEGER;
            }
            case LONG:
            {
                return BuiltInSchema.SINGLETON.LONG;
            }
            case INT:
            {
                return BuiltInSchema.SINGLETON.INT;
            }
            case SHORT:
            {
                return BuiltInSchema.SINGLETON.SHORT;
            }
            case BYTE:
            {
                return BuiltInSchema.SINGLETON.BYTE;
            }
            case NON_POSITIVE_INTEGER:
            {
                return BuiltInSchema.SINGLETON.NON_POSITIVE_INTEGER;
            }
            case NEGATIVE_INTEGER:
            {
                return BuiltInSchema.SINGLETON.NEGATIVE_INTEGER;
            }
            case NON_NEGATIVE_INTEGER:
            {
                return BuiltInSchema.SINGLETON.NON_NEGATIVE_INTEGER;
            }
            case POSITIVE_INTEGER:
            {
                return BuiltInSchema.SINGLETON.POSITIVE_INTEGER;
            }
            case UNSIGNED_LONG:
            {
                return BuiltInSchema.SINGLETON.UNSIGNED_LONG;
            }
            case UNSIGNED_INT:
            {
                return BuiltInSchema.SINGLETON.UNSIGNED_INT;
            }
            case UNSIGNED_SHORT:
            {
                return BuiltInSchema.SINGLETON.UNSIGNED_SHORT;
            }
            case UNSIGNED_BYTE:
            {
                return BuiltInSchema.SINGLETON.UNSIGNED_BYTE;
            }
            case UNTYPED_ATOMIC:
            {
                return BuiltInSchema.SINGLETON.UNTYPED_ATOMIC;
            }
            case UNTYPED:
            {
                return BuiltInSchema.SINGLETON.UNTYPED;
            }
            case DATE:
            {
                return BuiltInSchema.SINGLETON.DATE;
            }
            case DATETIME:
            {
                return BuiltInSchema.SINGLETON.DATETIME;
            }
            case TIME:
            {
                return BuiltInSchema.SINGLETON.TIME;
            }
            case GYEARMONTH:
            {
                return BuiltInSchema.SINGLETON.GYEARMONTH;
            }
            case GYEAR:
            {
                return BuiltInSchema.SINGLETON.GYEAR;
            }
            case GMONTHDAY:
            {
                return BuiltInSchema.SINGLETON.GMONTHDAY;
            }
            case GDAY:
            {
                return BuiltInSchema.SINGLETON.GDAY;
            }
            case GMONTH:
            {
                return BuiltInSchema.SINGLETON.GMONTH;
            }
            case DURATION_DAYTIME:
            {
                return BuiltInSchema.SINGLETON.DURATION_DAYTIME;
            }
            case DURATION_YEARMONTH:
            {
                return BuiltInSchema.SINGLETON.DURATION_YEARMONTH;
            }
            case DURATION:
            {
                return BuiltInSchema.SINGLETON.DURATION;
            }
            case ANY_URI:
            {
                return BuiltInSchema.SINGLETON.ANY_URI;
            }
            case BASE64_BINARY:
            {
                return BuiltInSchema.SINGLETON.BASE64_BINARY;
            }
            case HEX_BINARY:
            {
                return BuiltInSchema.SINGLETON.HEX_BINARY;
            }
            case LANGUAGE:
            {
                return BuiltInSchema.SINGLETON.LANGUAGE;
            }
            case QNAME:
            {
                return BuiltInSchema.SINGLETON.QNAME;
            }
            case ANY_TYPE:
            {
                return BuiltInSchema.SINGLETON.ANY_COMPLEX_TYPE;
            }
            case ANY_SIMPLE_TYPE:
            {
                return BuiltInSchema.SINGLETON.ANY_SIMPLE_TYPE;
            }
            case ANY_ATOMIC_TYPE:
            {
                return BuiltInSchema.SINGLETON.ANY_ATOMIC_TYPE;
            }
            case NORMALIZED_STRING:
            {
                return BuiltInSchema.SINGLETON.NORMALIZED_STRING;
            }
            case TOKEN:
            {
                return BuiltInSchema.SINGLETON.TOKEN;
            }
            case NMTOKEN:
            {
                return BuiltInSchema.SINGLETON.NMTOKEN;
            }
            case NAME:
            {
                return BuiltInSchema.SINGLETON.NAME;
            }
            case NCNAME:
            {
                return BuiltInSchema.SINGLETON.NCNAME;
            }
            case ID:
            {
                return BuiltInSchema.SINGLETON.ID;
            }
            case IDREF:
            {
                return BuiltInSchema.SINGLETON.IDREF;
            }
            case ENTITY:
            {
                return BuiltInSchema.SINGLETON.ENTITY;
            }
            case NOTATION:
            {
                return BuiltInSchema.SINGLETON.NOTATION;
            }
            case IDREFS:
            {
                return BuiltInSchema.SINGLETON.IDREFS;
            }
            case NMTOKENS:
            {
                return BuiltInSchema.SINGLETON.NMTOKENS;
            }
            case ENTITIES:
            {
                return BuiltInSchema.SINGLETON.ENTITIES;
            }
            default:
            {
                throw new AssertionError(nativeType);
            }
        }
    }

    private void builtins()
    {
        initialize(BuiltInSchema.SINGLETON);
        initialize(new XmlSchema());
        initialize(new XsiSchema());
    }
    
    private void initialize(final ComponentBag bag)
    {
        // TODO: need to check first to make sure that a definition
        // doesn't already exist?  Or is there any point in doing so?
        for (AttributeDefinition att : bag.getAttributes())
        {
            attributes.put(att.getName(), att);
        }
        for (AttributeGroupDefinition group : bag.getAttributeGroups())
        {
            attributeGroups.put(group.getName(), group);
        }
        for (ElementDefinition element : bag.getElements())
        {
            elements.put(element.getName(), element);
        }
        for (ModelGroup group : bag.getModelGroups())
        {
            modelGroups.put(group.getName(), group);
        }
        for (SimpleType type : bag.getSimpleTypes())
        {
            simpleTypes.put(type.getName(), type);
        }
        for (ComplexType type : bag.getComplexTypes())
        {
            complexTypes.put(type.getName(), type);
        }
        for (IdentityConstraint id : bag.getIdentityConstraints())
        {
            identities.put(id.getName(), id);
        }
        for (NotationDefinition not : bag.getNotations())
        {
            notations.put(not.getName(), not);
        }
        
    }
    
    private Map<QName, AttributeDefinition> attributes = new HashMap<QName, AttributeDefinition>();
    private Map<QName, AttributeGroupDefinition> attributeGroups = new HashMap<QName, AttributeGroupDefinition>();
    private Map<QName, ElementDefinition> elements = new HashMap<QName, ElementDefinition>();
    private Map<QName, ModelGroup> modelGroups = new HashMap<QName, ModelGroup>();
    private Map<QName, SimpleType> simpleTypes = new HashMap<QName, SimpleType>();
    private Map<QName, ComplexType> complexTypes = new HashMap<QName, ComplexType>();
    private Map<QName, IdentityConstraint> identities = new HashMap<QName, IdentityConstraint>();
    private Map<QName, NotationDefinition> notations = new HashMap<QName, NotationDefinition>();
}
