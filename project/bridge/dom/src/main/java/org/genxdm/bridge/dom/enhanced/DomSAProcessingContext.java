/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm.bridge.dom.enhanced;

import java.net.URI;

import javax.xml.stream.XMLReporter;

import org.genxdm.ProcessingContext;
import org.genxdm.bridge.dom.DomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.tree.CursorOnTypedModel;
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
import org.w3c.dom.Node;

public final class DomSAProcessingContext 
    implements TypedContext<Node, XmlAtom>
{
    public DomSAProcessingContext(DomProcessingContext parent)
	{
		this.atomBridge = new XmlAtomBridge(this);
		this.m_cache = new SchemaCacheFactory().newSchemaCache();
		this.m_metaBridge = new TypesBridgeImpl(m_cache);
		this.m_model = new DomSAModel(this);
		this.parent = PreCondition.assertNotNull(parent);
	}

	public XmlAtom atom(final Object item)
	{
		// Delegate to the atom bridge because we are generic wrt atoms.
		return atomBridge.atom(item);
	}
	
	public void declareAttribute(final AttributeDefinition attribute)
	{
		PreCondition.assertArgumentNotNull(attribute, "attribute");
		PreCondition.assertFalse(isLocked(), "isLocked()");
		m_cache.declareAttribute(attribute);
	}
	
	public void declareElement(final ElementDefinition element)
	{
		PreCondition.assertArgumentNotNull(element, "element");
		PreCondition.assertFalse(isLocked(), "isLocked()");
		m_cache.declareElement(element);
	}
	
	public void declareNotation(final NotationDefinition notation)
	{
		PreCondition.assertArgumentNotNull(notation, "notation");
		PreCondition.assertFalse(isLocked(), "isLocked()");
		m_cache.declareNotation(notation);
	}
	
	public void defineAttributeGroup(final AttributeGroupDefinition attributeGroup)
	{
		PreCondition.assertArgumentNotNull(attributeGroup, "attributeGroup");
		PreCondition.assertFalse(isLocked(), "isLocked()");
		m_cache.defineAttributeGroup(attributeGroup);
	}
	
	public void defineComplexType(final ComplexType complexType)
	{
		PreCondition.assertArgumentNotNull(complexType, "complexType");
		PreCondition.assertFalse(isLocked(), "isLocked()");
		m_cache.defineComplexType(complexType);
	}
	
	public void defineIdentityConstraint(final IdentityConstraint identityConstraint)
	{
		PreCondition.assertArgumentNotNull(identityConstraint, "identityConstraint");
		PreCondition.assertFalse(isLocked(), "isLocked()");
		m_cache.defineIdentityConstraint(identityConstraint);
	}

	public void defineModelGroup(final ModelGroup modelGroup)
	{
		PreCondition.assertArgumentNotNull(modelGroup, "modelGroup");
		PreCondition.assertFalse(isLocked(), "isLocked()");
		m_cache.defineModelGroup(modelGroup);
	}

	public void defineSimpleType(final SimpleType simpleType)
	{
		PreCondition.assertArgumentNotNull(simpleType, "simpleType");
		PreCondition.assertFalse(isLocked(), "isLocked()");
		m_cache.defineSimpleType(simpleType);
	}
	
	public AtomBridge<XmlAtom> getAtomBridge()
	{
		return atomBridge;
	}

    @Override
    public ComponentProvider getComponentProvider()
    {
        return m_metaBridge.getComponentProvider();
    }

    @Override
    public ComponentBag getComponents()
    {
        return m_metaBridge.getComponents();
    }

	public TypesBridge getMetaBridge()
	{
		return m_metaBridge;
	}

	public TypedModel<Node, XmlAtom> getModel()
	{
		return m_model;
	}

	public Iterable<String> getNamespaces()
	{
		return m_cache.getNamespaces();
	}

    public ProcessingContext<Node> getProcessingContext()
	{
	    return parent;
	}

	public VariantBridge<Node, XmlAtom> getVariantBridge()
	{
	    return new DomValueBridge();
	}

	public boolean isAtom(final Object item)
	{
		// Delegate to the atom bridge because we are generic wrt atoms.
		return atomBridge.isAtom(item);
	}

	public boolean isLocked()
	{
		return locked;
	}

	public void lock()
	{
		locked = true;
	}

	public TypedCursor<Node, XmlAtom> newCursor(final Node node)
	{
	    return new CursorOnTypedModel<Node, XmlAtom>(node, m_model);
	}

	public SequenceBuilder<Node, XmlAtom> newSequenceBuilder()
	{
		return new DomSequenceBuilder<XmlAtom>(parent.getDocumentBuilderFactory(), this);
	}
	
	@Override
	public ValidatingDocumentHandler<Node, XmlAtom> newDocumentHandler(final Validator<XmlAtom> validator, final XMLReporter reporter, final Resolver resolver)
	{
	    return new ValidatingDocumentHandler<Node, XmlAtom>(this, validator, reporter, resolver);
	}

	public void register(final ComponentBag components)
	{
		PreCondition.assertFalse(isLocked(), "isLocked()");
		m_cache.register(components);
	}
	
	@Override
	public Node validate(Node source, ValidationHandler<XmlAtom> validator, URI namespace)
	{
	    SequenceBuilder<Node, XmlAtom> builder = newSequenceBuilder();
	    // TODO: can we instead modify the existing tree and return it?
	    validator.setSequenceHandler(builder);
	    validator.reset();
	    m_model.stream(source, true, true, validator);
	    // TODO: check for errors?
	    
	    return builder.getNode();
	}

	private final DomProcessingContext parent;
    private final XmlAtomBridge atomBridge;
	private final SchemaCache m_cache;
	private final TypesBridge m_metaBridge;

	private final DomSAModel m_model;

    private boolean locked;

}
