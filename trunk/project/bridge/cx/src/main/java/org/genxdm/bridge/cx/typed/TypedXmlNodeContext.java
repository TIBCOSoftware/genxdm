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

import javax.xml.stream.XMLReporter;

import org.genxdm.ProcessingContext;
import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.xs.SchemaCache;
import org.genxdm.bridgekit.xs.SchemaCacheFactory;
import org.genxdm.bridgekit.xs.TypesBridgeImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.Resolver;
import org.genxdm.processor.io.ValidatingDocumentHandler;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.Validator;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.TypesBridge;
import org.genxdm.typed.variant.VariantBridge;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;

public class TypedXmlNodeContext
    implements TypedContext<XmlNode, XmlAtom>
{

    public TypedXmlNodeContext(XmlNodeContext context)
    {
        this.context = PreCondition.assertNotNull(context, "context");
        this.atoms = new XmlAtomBridge(this);
        this.cache = new SchemaCacheFactory().newSchemaCache();
        this.types = new TypesBridgeImpl(cache);
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

    public AtomBridge<XmlAtom> getAtomBridge()
    {
        return atoms;
    }

    @Override
    public ComponentProvider getComponentProvider()
    {
        return types.getComponentProvider();
    }

    @Override
    public ComponentBag getComponents()
    {
        return types.getComponents();
    }

    public TypesBridge getMetaBridge()
    {
        return types;
    }

    public TypedModel<XmlNode, XmlAtom> getModel()
    {
        return model;
    }

    public Iterable<String> getNamespaces()
    {
        return cache.getNamespaces();
    }

    public ProcessingContext<XmlNode> getProcessingContext()
    {
        return context;
    }

    public VariantBridge<XmlNode, XmlAtom> getVariantBridge()
    {
        return new XmlVariantBridge();
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
    private final SchemaCache cache;
    private final TypesBridgeImpl types;
    private boolean locked;
}
