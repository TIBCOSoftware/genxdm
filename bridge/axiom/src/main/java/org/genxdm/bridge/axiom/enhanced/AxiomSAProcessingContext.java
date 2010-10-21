/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
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

import java.util.EnumSet;

import javax.xml.namespace.QName;

import org.genxdm.bridge.axiom.AxiomProcessingContext;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.tree.CoreModelDecoration;
import org.genxdm.bridgekit.tree.CoreModelDecorator;
import org.genxdm.bridgekit.tree.CursorOnTypedModel;
import org.genxdm.bridgekit.xs.GxMetaBridgeOnSmMetaBridgeAdapter;
import org.genxdm.bridgekit.xs.SmMetaBridgeFactory;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.nodes.Bookmark;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.MetaBridge;
import org.genxdm.xs.SchemaTypeBridge;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ComponentBag;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.types.SmAtomicType;
import org.genxdm.xs.types.SmAtomicUrType;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmComplexUrType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmSimpleUrType;
import org.genxdm.xs.types.SmType;

public final class AxiomSAProcessingContext 
    implements TypedContext<Object, XmlAtom>
{
	public AxiomSAProcessingContext(final AxiomProcessingContext context)
	{
	    this.context = PreCondition.assertNotNull(context, "context");
		this.atomBridge = new XmlAtomBridge(this, context.getNameBridge());
		final SmMetaBridgeFactory<XmlAtom> cacheFactory = new SmMetaBridgeFactory<XmlAtom>(atomBridge);
		cache = cacheFactory.newMetaBridge();
		this.metaBridge = new GxMetaBridgeOnSmMetaBridgeAdapter<XmlAtom>(cache, atomBridge);
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
	
	public void declareAttribute(final AttributeDefinition<XmlAtom> attribute)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public void declareElement(final ElementDefinition<XmlAtom> element)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public void declareNotation(NotationDefinition<XmlAtom> notation)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public void defineAttributeGroup(AttributeGroupDefinition<XmlAtom> attributeGroup)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public void defineComplexType(final SmComplexType<XmlAtom> complexType)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public void defineIdentityConstraint(SmIdentityConstraint<XmlAtom> identityConstraint)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public void defineModelGroup(ModelGroup<XmlAtom> modelGroup)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public void defineSimpleType(final SmSimpleType<XmlAtom> simpleType)
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

	public SmAtomicType<XmlAtom> getAtomicType(QName name)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmAtomicType<XmlAtom> getAtomicType(SmNativeType name)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmAtomicUrType<XmlAtom> getAtomicUrType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public AttributeDefinition<XmlAtom> getAttributeDeclaration(QName attributeName)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public AttributeGroupDefinition<XmlAtom> getAttributeGroup(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<AttributeGroupDefinition<XmlAtom>> getAttributeGroups()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public Iterable<AttributeDefinition<XmlAtom>> getAttributes()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmComplexType<XmlAtom> getComplexType(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<SmComplexType<XmlAtom>> getComplexTypes()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmComplexUrType<XmlAtom> getComplexUrType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ElementDefinition<XmlAtom> getElementDeclaration(QName elementName)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public Iterable<ElementDefinition<XmlAtom>> getElements()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmIdentityConstraint<XmlAtom> getIdentityConstraint(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<SmIdentityConstraint<XmlAtom>> getIdentityConstraints()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public MetaBridge<XmlAtom> getMetaBridge()
	{
		return metaBridge;
	}

	public TypedModel<Object, XmlAtom> getModel()
	{
		return model;
	}

	public ModelGroup<XmlAtom> getModelGroup(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<ModelGroup<XmlAtom>> getModelGroups()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public Iterable<String> getNamespaces()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

    public NotationDefinition<XmlAtom> getNotationDeclaration(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public Iterable<NotationDefinition<XmlAtom>> getNotations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
	
	public AxiomProcessingContext getProcessingContext()
	{
	    return context;
	}

	public SmSimpleType<XmlAtom> getSimpleType(QName name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public SmSimpleType<XmlAtom> getSimpleType(SmNativeType name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Iterable<SmSimpleType<XmlAtom>> getSimpleTypes()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmSimpleUrType<XmlAtom> getSimpleUrType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public SmType<XmlAtom> getTypeDefinition(QName typeName)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	public SmType<XmlAtom> getTypeDefinition(final SmNativeType nativeType)
	{
		return cache.getTypeDefinition(nativeType);
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
    
    public void register(final ComponentBag<XmlAtom> components)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}
    
    private final AxiomProcessingContext context;
	private final AtomBridge<XmlAtom> atomBridge;
	private final SchemaTypeBridge<XmlAtom> cache;
	
	@SuppressWarnings("unused")
	private boolean locked;
	private final MetaBridge<XmlAtom> metaBridge;
	private final TypedModel<Object, XmlAtom> model;
}
