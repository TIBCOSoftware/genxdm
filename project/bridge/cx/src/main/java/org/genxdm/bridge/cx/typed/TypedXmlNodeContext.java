/**
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.genxdm.bridge.cx.typed;

import javax.xml.namespace.QName;

import org.genxdm.ProcessingContext;
import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.xs.MetaBridgeOnSchemaTypeBridgeAdapter;
import org.genxdm.bridgekit.xs.SchemaTypeBridgeFactory;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.MetaBridge;
import org.genxdm.typed.variant.VariantBridge;
import org.genxdm.xs.SchemaTypeBridge;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ComponentBag;
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

public class TypedXmlNodeContext
    implements TypedContext<XmlNode, XmlAtom>
{

    public TypedXmlNodeContext(XmlNodeContext context)
    {
        this.context = PreCondition.assertNotNull(context, "context");
        this.atoms = new XmlAtomBridge(this, new NameSource());
        this.cache = new SchemaTypeBridgeFactory<XmlAtom>(atoms).newMetaBridge();
        this.types = new MetaBridgeOnSchemaTypeBridgeAdapter<XmlAtom>(cache, atoms);
        this.model = new TypedXmlNodeModel(atoms);
    }
    
    public XmlAtom atom(Object item)
    {
        if (isAtom(item))
            return (XmlAtom)item;
        return null;
    }

    public void declareAttribute(AttributeDefinition<XmlAtom> attribute)
    {
        PreCondition.assertArgumentNotNull(attribute, "attribute");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.declareAttribute(attribute);
    }

    public void declareElement(ElementDefinition<XmlAtom> element)
    {
        PreCondition.assertArgumentNotNull(element, "element");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.declareElement(element);
    }

    public void declareNotation(NotationDefinition<XmlAtom> notation)
    {
        PreCondition.assertArgumentNotNull(notation, "notation");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.declareNotation(notation);
    }

    public void defineAttributeGroup(AttributeGroupDefinition<XmlAtom> attributeGroup)
    {
        PreCondition.assertArgumentNotNull(attributeGroup, "attributeGroup");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineAttributeGroup(attributeGroup);
    }

    public void defineComplexType(ComplexType<XmlAtom> complexType)
    {
        PreCondition.assertArgumentNotNull(complexType, "complexType");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineComplexType(complexType);
    }

    public void defineIdentityConstraint(IdentityConstraint<XmlAtom> identityConstraint)
    {
        PreCondition.assertArgumentNotNull(identityConstraint, "identityConstraint");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineIdentityConstraint(identityConstraint);
    }

    public void defineModelGroup(ModelGroup<XmlAtom> modelGroup)
    {
        PreCondition.assertArgumentNotNull(modelGroup, "modelGroup");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineModelGroup(modelGroup);
    }

    public void defineSimpleType(SimpleType<XmlAtom> simpleType)
    {
        PreCondition.assertArgumentNotNull(simpleType, "simpleType");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineSimpleType(simpleType);
    }

    public QName generateUniqueName()
    {
        return cache.generateUniqueName();
    }

    public AtomBridge<XmlAtom> getAtomBridge()
    {
        return atoms;
    }

    public AtomicType<XmlAtom> getAtomicType(QName name)
    {
        return cache.getAtomicType(name);
    }

    public AtomicType<XmlAtom> getAtomicType(NativeType name)
    {
        return cache.getAtomicType(name);
    }

    public AtomicUrType<XmlAtom> getAtomicUrType()
    {
        return cache.getAtomicUrType();
    }

    public AttributeDefinition<XmlAtom> getAttributeDeclaration(QName name)
    {
        return cache.getAttributeDeclaration(name);
    }

    public AttributeGroupDefinition<XmlAtom> getAttributeGroup(QName name)
    {
        return cache.getAttributeGroup(name);
    }

    public Iterable<AttributeGroupDefinition<XmlAtom>> getAttributeGroups()
    {
        return cache.getAttributeGroups();
    }

    public Iterable<AttributeDefinition<XmlAtom>> getAttributes()
    {
        return cache.getAttributes();
    }

    public ComplexType<XmlAtom> getComplexType(QName name)
    {
        return cache.getComplexType(name);
    }

    public Iterable<ComplexType<XmlAtom>> getComplexTypes()
    {
        return cache.getComplexTypes();
    }

    public ComplexUrType<XmlAtom> getComplexUrType()
    {
        return cache.getComplexUrType();
    }

    public ElementDefinition<XmlAtom> getElementDeclaration(QName name)
    {
        return cache.getElementDeclaration(name);
    }

    public Iterable<ElementDefinition<XmlAtom>> getElements()
    {
        return cache.getElements();
    }

    public IdentityConstraint<XmlAtom> getIdentityConstraint(QName name)
    {
        return cache.getIdentityConstraint(name);
    }

    public Iterable<IdentityConstraint<XmlAtom>> getIdentityConstraints()
    {
        return cache.getIdentityConstraints();
    }

    public MetaBridge<XmlAtom> getMetaBridge()
    {
        return types;
    }

    public TypedModel<XmlNode, XmlAtom> getModel()
    {
        return model;
    }

    public ModelGroup<XmlAtom> getModelGroup(QName name)
    {
        return cache.getModelGroup(name);
    }

    public Iterable<ModelGroup<XmlAtom>> getModelGroups()
    {
        return cache.getModelGroups();
    }

    public Iterable<String> getNamespaces()
    {
        return cache.getNamespaces();
    }

    public NotationDefinition<XmlAtom> getNotationDeclaration(QName name)
    {
        return cache.getNotationDeclaration(name);
    }

    public Iterable<NotationDefinition<XmlAtom>> getNotations()
    {
        return cache.getNotations();
    }

    public ProcessingContext<XmlNode> getProcessingContext()
    {
        return context;
    }

    public SimpleType<XmlAtom> getSimpleType(QName name)
    {
        return cache.getSimpleType(name);
    }

    public SimpleType<XmlAtom> getSimpleType(NativeType name)
    {
        return cache.getSimpleType(name);
    }

    public Iterable<SimpleType<XmlAtom>> getSimpleTypes()
    {
        return cache.getSimpleTypes();
    }

    public SimpleUrType<XmlAtom> getSimpleUrType()
    {
        return cache.getSimpleUrType();
    }

    public Type<XmlAtom> getTypeDefinition(QName name)
    {
        return cache.getTypeDefinition(name);
    }

    public Type<XmlAtom> getTypeDefinition(NativeType nativeType)
    {
        return cache.getTypeDefinition(nativeType);
    }
    
    public VariantBridge<XmlNode, XmlAtom> getVariantBridge()
    {
        return new XmlVariantBridge();
    }

    public boolean hasAttribute(QName name)
    {
        return cache.hasAttribute(name);
    }

    public boolean hasAttributeGroup(QName name)
    {
        return cache.hasAttributeGroup(name);
    }

    public boolean hasComplexType(QName name)
    {
        return cache.hasComplexType(name);
    }

    public boolean hasElement(QName name)
    {
        return cache.hasElement(name);
    }

    public boolean hasIdentityConstraint(QName name)
    {
        return cache.hasIdentityConstraint(name);
    }

    public boolean hasModelGroup(QName name)
    {
        return cache.hasModelGroup(name);
    }

    public boolean hasNotation(QName name)
    {
        return cache.hasNotation(name);
    }

    public boolean hasSimpleType(QName name)
    {
        return cache.hasSimpleType(name);
    }

    public boolean hasType(QName name)
    {
        return cache.hasType(name);
    }

    public boolean isAtom(Object item)
    {
        return (item instanceof XmlAtom);
    }

    public boolean isLocked()
    {
        return locked;
    }

    public void lock()
    {
        locked = true;
    }

    public TypedCursor<XmlNode, XmlAtom> newCursor(final XmlNode node)
    {
        return new TypedXmlNodeCursor(this, node);
    }

    public SequenceBuilder<XmlNode, XmlAtom> newSequenceBuilder()
    {
        return new TypedXmlNodeBuilder(this);
    }

    public void register(ComponentBag<XmlAtom> components)
    {
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.register(components);
    }

    private final XmlNodeContext context;
    private final TypedXmlNodeModel model;
    private final XmlAtomBridge atoms;
    private final SchemaTypeBridge<XmlAtom> cache;
    private final MetaBridgeOnSchemaTypeBridgeAdapter<XmlAtom> types;
    private boolean locked;
}
