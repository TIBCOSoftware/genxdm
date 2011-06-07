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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLReporter;

import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.tree.CoreModelDecoration;
import org.genxdm.bridgekit.tree.CoreModelDecorator;
import org.genxdm.bridgekit.tree.CursorOnTypedModel;
import org.genxdm.bridgekit.xs.MetaBridgeOnSchemaTypeBridgeAdapter;
import org.genxdm.bridgekit.xs.SchemaTypeBridgeFactory;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.io.Resolver;
import org.genxdm.names.NameSource;
import org.genxdm.nodes.Bookmark;
import org.genxdm.processor.io.ValidatingDocumentHandler;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.ValidationHandler;
import org.genxdm.typed.Validator;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.io.TypedDocumentHandler;
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

public final class AxiomSAProcessingContext 
    implements TypedContext<Object, XmlAtom>
{
	public AxiomSAProcessingContext(final AxiomProcessingContext context)
	{
	    this.context = PreCondition.assertNotNull(context, "context");
		this.atomBridge = new XmlAtomBridge(this);
		final SchemaTypeBridgeFactory cacheFactory = new SchemaTypeBridgeFactory();
		cache = cacheFactory.newMetaBridge();
		this.metaBridge = new MetaBridgeOnSchemaTypeBridgeAdapter(cache);
		EnumSet<CoreModelDecoration> delegations = EnumSet.noneOf(CoreModelDecoration.class);
		delegations.add(CoreModelDecoration.CHILD_AXIS);
		delegations.add(CoreModelDecoration.CHILD_ELEMENTS);
		this.model = new CoreModelDecorator<Object, XmlAtom>(delegations, new AxiomSAModel(new org.genxdm.bridge.axiom.AxiomModel(), atomBridge), atomBridge);
	}
	
	public XmlAtom atom(final Object item)
	{
		return atomBridge.atom(item);
	}
	
	public Bookmark<Object> bookmark(Object node)
	{
	    // TODO
	    return null;
	}
	
	public void declareAttribute(final AttributeDefinition attribute)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public void declareElement(final ElementDefinition element)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public void declareNotation(NotationDefinition notation)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public void defineAttributeGroup(AttributeGroupDefinition attributeGroup)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public void defineComplexType(final ComplexType complexType)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public void defineIdentityConstraint(IdentityConstraint identityConstraint)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public void defineModelGroup(ModelGroup modelGroup)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public void defineSimpleType(final SimpleType simpleType)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public QName generateUniqueName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public AtomBridge<XmlAtom> getAtomBridge()
	{
		return atomBridge;
	}

	public AtomicType getAtomicType(QName name)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public AtomicType getAtomicType(NativeType name)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public AtomicUrType getAtomicUrType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public AttributeDefinition getAttributeDeclaration(QName attributeName)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public AttributeGroupDefinition getAttributeGroup(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<AttributeGroupDefinition> getAttributeGroups()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public Iterable<AttributeDefinition> getAttributes()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public ComplexType getComplexType(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<ComplexType> getComplexTypes()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public ComplexUrType getComplexUrType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ElementDefinition getElementDeclaration(QName elementName)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<ElementDefinition> getElements()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public IdentityConstraint getIdentityConstraint(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<IdentityConstraint> getIdentityConstraints()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public MetaBridge getMetaBridge()
	{
		return metaBridge;
	}

	public TypedModel<Object, XmlAtom> getModel()
	{
		return model;
	}

	public ModelGroup getModelGroup(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<ModelGroup> getModelGroups()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public Iterable<String> getNamespaces()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

    public NotationDefinition getNotationDeclaration(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public Iterable<NotationDefinition> getNotations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public AxiomProcessingContext getProcessingContext()
	{
	    return context;
	}

	public SimpleType getSimpleType(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public SimpleType getSimpleType(NativeType name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<SimpleType> getSimpleTypes()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SimpleUrType getSimpleUrType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Type getTypeDefinition(QName typeName)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Type getTypeDefinition(final NativeType nativeType)
	{
		return cache.getTypeDefinition(nativeType);
	}
	
	public VariantBridge<Object, XmlAtom> getVariantBridge()
	{
	    // TODO
	    return null;
	}

	public boolean hasAttribute(QName name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasAttributeGroup(QName name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasComplexType(QName name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasElement(QName name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasIdentityConstraint(QName name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasModelGroup(QName name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasNotation(QName name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasSimpleType(QName name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasType(final QName name)
	{
		// TODO Auto-generated method stub
		return false;
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
	private final SchemaTypeBridge cache;
	
	@SuppressWarnings("unused")
	private boolean locked;
	private final MetaBridge metaBridge;
	private final TypedModel<Object, XmlAtom> model;
}
