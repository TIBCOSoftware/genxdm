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
package org.genxdm.bridge.cx.typed;

import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLReporter;

import org.genxdm.ProcessingContext;
import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.xs.MetaBridgeOnSchemaTypeBridgeAdapter;
import org.genxdm.bridgekit.xs.SchemaTypeBridgeFactory;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.Resolver;
import org.genxdm.names.NameSource;
import org.genxdm.processor.io.ValidatingDocumentHandler;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.Validator;
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
        this.atoms = new XmlAtomBridge(this);
        this.cache = new SchemaTypeBridgeFactory().newMetaBridge();
        this.types = new MetaBridgeOnSchemaTypeBridgeAdapter(cache);
        this.model = new TypedXmlNodeModel(atoms);
    }
    
    public XmlAtom atom(Object item)
    {
        if (isAtom(item))
            return (XmlAtom)item;
        return null;
    }

    public void declareAttribute(AttributeDefinition attribute)
    {
        PreCondition.assertArgumentNotNull(attribute, "attribute");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.declareAttribute(attribute);
    }

    public void declareElement(ElementDefinition element)
    {
        PreCondition.assertArgumentNotNull(element, "element");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.declareElement(element);
    }

    public void declareNotation(NotationDefinition notation)
    {
        PreCondition.assertArgumentNotNull(notation, "notation");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.declareNotation(notation);
    }

    public void defineAttributeGroup(AttributeGroupDefinition attributeGroup)
    {
        PreCondition.assertArgumentNotNull(attributeGroup, "attributeGroup");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineAttributeGroup(attributeGroup);
    }

    public void defineComplexType(ComplexType complexType)
    {
        PreCondition.assertArgumentNotNull(complexType, "complexType");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineComplexType(complexType);
    }

    public void defineIdentityConstraint(IdentityConstraint identityConstraint)
    {
        PreCondition.assertArgumentNotNull(identityConstraint, "identityConstraint");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineIdentityConstraint(identityConstraint);
    }

    public void defineModelGroup(ModelGroup modelGroup)
    {
        PreCondition.assertArgumentNotNull(modelGroup, "modelGroup");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineModelGroup(modelGroup);
    }

    public void defineSimpleType(SimpleType simpleType)
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

    public AtomicType getAtomicType(QName name)
    {
        return cache.getAtomicType(name);
    }

    public AtomicType getAtomicType(NativeType name)
    {
        return cache.getAtomicType(name);
    }

    public AtomicUrType getAtomicUrType()
    {
        return cache.getAtomicUrType();
    }

    public AttributeDefinition getAttributeDeclaration(QName name)
    {
        return cache.getAttributeDeclaration(name);
    }

    public AttributeGroupDefinition getAttributeGroup(QName name)
    {
        return cache.getAttributeGroup(name);
    }

    public Iterable<AttributeGroupDefinition> getAttributeGroups()
    {
        return cache.getAttributeGroups();
    }

    public Iterable<AttributeDefinition> getAttributes()
    {
        return cache.getAttributes();
    }

    public ComplexType getComplexType(QName name)
    {
        return cache.getComplexType(name);
    }

    public Iterable<ComplexType> getComplexTypes()
    {
        return cache.getComplexTypes();
    }

    public ComplexUrType getComplexUrType()
    {
        return cache.getComplexUrType();
    }

    public ElementDefinition getElementDeclaration(QName name)
    {
        return cache.getElementDeclaration(name);
    }

    public Iterable<ElementDefinition> getElements()
    {
        return cache.getElements();
    }

    public IdentityConstraint getIdentityConstraint(QName name)
    {
        return cache.getIdentityConstraint(name);
    }

    public Iterable<IdentityConstraint> getIdentityConstraints()
    {
        return cache.getIdentityConstraints();
    }

    public MetaBridge getMetaBridge()
    {
        return types;
    }

    public TypedModel<XmlNode, XmlAtom> getModel()
    {
        return model;
    }

    public ModelGroup getModelGroup(QName name)
    {
        return cache.getModelGroup(name);
    }

    public Iterable<ModelGroup> getModelGroups()
    {
        return cache.getModelGroups();
    }

    public Iterable<String> getNamespaces()
    {
        return cache.getNamespaces();
    }

    public NotationDefinition getNotationDeclaration(QName name)
    {
        return cache.getNotationDeclaration(name);
    }

    public Iterable<NotationDefinition> getNotations()
    {
        return cache.getNotations();
    }

    public ProcessingContext<XmlNode> getProcessingContext()
    {
        return context;
    }

    public SimpleType getSimpleType(QName name)
    {
        return cache.getSimpleType(name);
    }

    public SimpleType getSimpleType(NativeType name)
    {
        return cache.getSimpleType(name);
    }

    public Iterable<SimpleType> getSimpleTypes()
    {
        return cache.getSimpleTypes();
    }

    public SimpleUrType getSimpleUrType()
    {
        return cache.getSimpleUrType();
    }

    public Type getTypeDefinition(QName name)
    {
        return cache.getTypeDefinition(name);
    }

    public Type getTypeDefinition(NativeType nativeType)
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

    @Override
    public ValidatingDocumentHandler<XmlNode, XmlAtom> newDocumentHandler(final Validator<XmlAtom> validator, final XMLReporter reporter, final Resolver resolver)
    {
        return new ValidatingDocumentHandler<XmlNode, XmlAtom>(this, validator, reporter, resolver);
    }

    @Override
    public void register(ComponentBag components)
    {
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.register(components);
    }
    
    @Override
    public XmlNode validate(XmlNode source, ValidationHandler<XmlAtom> validator, URI namespace)
    {
        SequenceBuilder<XmlNode, XmlAtom> builder = newSequenceBuilder();
        // TODO: this assumes building a new tree and returning it.
        // can we instead provide a tool that walks the existing tree and modifies it?
        validator.setSequenceHandler(builder);
        model.stream(source, true, true, validator);
//        SchemaExceptionHandler errors = validator.getSchemaExceptionHandler();
        // TODO: check the errors?
//        for (SchemaException error : errors)
//        {
//            // ???
//        }

        return builder.getNode();
    }

    private final XmlNodeContext context;
    private final TypedXmlNodeModel model;
    private final XmlAtomBridge atoms;
    private final SchemaTypeBridge cache;
    private final MetaBridgeOnSchemaTypeBridgeAdapter types;
    private boolean locked;
}
