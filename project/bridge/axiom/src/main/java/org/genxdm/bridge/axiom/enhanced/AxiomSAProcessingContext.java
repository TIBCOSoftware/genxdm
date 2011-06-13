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
package org.genxdm.bridge.axiom.enhanced;

import java.net.URI;
import java.util.EnumSet;

import javax.xml.stream.XMLReporter;

import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.tree.CoreModelDecoration;
import org.genxdm.bridgekit.tree.CoreModelDecorator;
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
import org.genxdm.typed.io.TypedDocumentHandler;
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

public final class AxiomSAProcessingContext 
    implements TypedContext<Object, XmlAtom>
{
	public AxiomSAProcessingContext(final AxiomProcessingContext context)
	{
	    this.context = PreCondition.assertNotNull(context, "context");
		this.atomBridge = new XmlAtomBridge(this);
		final SchemaCacheFactory cacheFactory = new SchemaCacheFactory();
		cache = cacheFactory.newSchemaCache();
		this.metaBridge = new TypesBridgeImpl(cache);
		EnumSet<CoreModelDecoration> delegations = EnumSet.noneOf(CoreModelDecoration.class);
		delegations.add(CoreModelDecoration.CHILD_AXIS);
		delegations.add(CoreModelDecoration.CHILD_ELEMENTS);
		this.model = new CoreModelDecorator<Object, XmlAtom>(delegations, new AxiomSAModel(new org.genxdm.bridge.axiom.AxiomModel(), atomBridge), atomBridge);
	}
	
	public XmlAtom atom(final Object item)
	{
		return atomBridge.atom(item);
	}
	
	public void declareAttribute(final AttributeDefinition attribute)
	{
	    metaBridge.declareAttribute(attribute);
	}
	
	public void declareElement(final ElementDefinition element)
	{
	    metaBridge.declareElement(element);
	}
	
	public void declareNotation(NotationDefinition notation)
	{
	    metaBridge.declareNotation(notation);
	}
	
	public void defineAttributeGroup(AttributeGroupDefinition attributeGroup)
	{
	    metaBridge.defineAttributeGroup(attributeGroup);
	}
	
	public void defineComplexType(final ComplexType complexType)
	{
	    metaBridge.defineComplexType(complexType);
	}
	
	public void defineIdentityConstraint(IdentityConstraint identityConstraint)
	{
	    metaBridge.defineIdentityConstraint(identityConstraint);
	}

	public void defineModelGroup(ModelGroup modelGroup)
	{
	    metaBridge.defineModelGroup(modelGroup);
	}

	public void defineSimpleType(final SimpleType simpleType)
	{
	    metaBridge.defineSimpleType(simpleType);
	}

	public AtomBridge<XmlAtom> getAtomBridge()
	{
		return atomBridge;
	}

    public ComponentProvider getComponentProvider()
    {
        return metaBridge.getComponentProvider();
    }

    @Override
    public ComponentBag getComponents()
    {
        return metaBridge.getComponents();
    }

    public TypesBridge getMetaBridge()
	{
		return metaBridge;
	}

	public TypedModel<Object, XmlAtom> getModel()
	{
		return model;
	}

	public Iterable<String> getNamespaces()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public AxiomProcessingContext getProcessingContext()
	{
	    return context;
	}

	public VariantBridge<Object, XmlAtom> getVariantBridge()
	{
	    // TODO
	    return null;
	}

	public boolean isAtom(final Object item)
	{
		return item instanceof XmlAtom;
	}

	public boolean isLocked()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public void lock()
	{
		locked = true;
	}

	public TypedCursor<Object, XmlAtom> newCursor(Object node)
	{
		return new CursorOnTypedModel<Object, XmlAtom>(node, model);
	}

    public SequenceBuilder<Object, XmlAtom> newSequenceBuilder()
    {
	    return new AxiomSequenceBuilder(this, context.getOMFactory(), true);
    }
    
    @Override
    public TypedDocumentHandler<Object, XmlAtom> newDocumentHandler(Validator<XmlAtom> validator, XMLReporter reporter, Resolver resolver)
    {
        // TODO Auto-generated method stub
        return new ValidatingDocumentHandler<Object, XmlAtom>(this, validator, reporter, resolver);
    }

    public void register(final ComponentBag components)
	{
		// TODO implement
		throw new UnsupportedOperationException();
	}
    
    @Override
    public Object validate(Object source, ValidationHandler<XmlAtom> validator, URI namespace)
    {
        // TODO: implement
        throw new UnsupportedOperationException();
    }
    
    private final AxiomProcessingContext context;
	private final AtomBridge<XmlAtom> atomBridge;
	private final SchemaCache cache;
	
	@SuppressWarnings("unused")
	private boolean locked;
	private final TypesBridge metaBridge;
	private final TypedModel<Object, XmlAtom> model;
}
