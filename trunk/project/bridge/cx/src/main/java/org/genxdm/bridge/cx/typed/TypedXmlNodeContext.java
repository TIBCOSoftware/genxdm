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

import org.genxdm.base.ProcessingContext;
import org.genxdm.bridge.cx.base.XmlNodeContext;
import org.genxdm.bridge.cx.tree.XmlNode;
import org.genxdm.bridgekit.atoms.XmlAtom;
import org.genxdm.bridgekit.atoms.XmlAtomBridge;
import org.genxdm.bridgekit.xs.GxMetaBridgeOnSmMetaBridgeAdapter;
import org.genxdm.bridgekit.xs.SmMetaBridgeFactory;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.typed.TypedContext;
import org.genxdm.typed.TypedCursor;
import org.genxdm.typed.TypedModel;
import org.genxdm.typed.io.SequenceBuilder;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.typed.types.MetaBridge;
import org.genxdm.xs.SmMetaBridge;
import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmAttributeGroup;
import org.genxdm.xs.components.SmComponentBag;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.components.SmNotation;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.types.SmAtomicType;
import org.genxdm.xs.types.SmAtomicUrType;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmComplexUrType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmSimpleUrType;
import org.genxdm.xs.types.SmType;

public class TypedXmlNodeContext
    implements TypedContext<XmlNode, XmlAtom>
{

    public TypedXmlNodeContext(XmlNodeContext context)
    {
        this.context = PreCondition.assertNotNull(context, "context");
        this.atoms = new XmlAtomBridge(this, new NameSource());
        this.cache = new SmMetaBridgeFactory<XmlAtom>(atoms).newMetaBridge();
        this.types = new GxMetaBridgeOnSmMetaBridgeAdapter<XmlAtom>(cache, atoms);
        this.model = new TypedXmlNodeModel(atoms);
    }
    
    public XmlAtom atom(Object item)
    {
        if (isAtom(item))
            return (XmlAtom)item;
        return null;
    }

    public void declareAttribute(SmAttribute<XmlAtom> attribute)
    {
        PreCondition.assertArgumentNotNull(attribute, "attribute");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.declareAttribute(attribute);
    }

    public void declareElement(SmElement<XmlAtom> element)
    {
        PreCondition.assertArgumentNotNull(element, "element");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.declareElement(element);
    }

    public void declareNotation(SmNotation<XmlAtom> notation)
    {
        PreCondition.assertArgumentNotNull(notation, "notation");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.declareNotation(notation);
    }

    public void defineAttributeGroup(SmAttributeGroup<XmlAtom> attributeGroup)
    {
        PreCondition.assertArgumentNotNull(attributeGroup, "attributeGroup");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineAttributeGroup(attributeGroup);
    }

    public void defineComplexType(SmComplexType<XmlAtom> complexType)
    {
        PreCondition.assertArgumentNotNull(complexType, "complexType");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineComplexType(complexType);
    }

    public void defineIdentityConstraint(SmIdentityConstraint<XmlAtom> identityConstraint)
    {
        PreCondition.assertArgumentNotNull(identityConstraint, "identityConstraint");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineIdentityConstraint(identityConstraint);
    }

    public void defineModelGroup(SmModelGroup<XmlAtom> modelGroup)
    {
        PreCondition.assertArgumentNotNull(modelGroup, "modelGroup");
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.defineModelGroup(modelGroup);
    }

    public void defineSimpleType(SmSimpleType<XmlAtom> simpleType)
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

    public SmAtomicType<XmlAtom> getAtomicType(QName name)
    {
        return cache.getAtomicType(name);
    }

    public SmAtomicType<XmlAtom> getAtomicType(SmNativeType name)
    {
        return cache.getAtomicType(name);
    }

    public SmAtomicUrType<XmlAtom> getAtomicUrType()
    {
        return cache.getAtomicUrType();
    }

    public SmAttribute<XmlAtom> getAttributeDeclaration(QName name)
    {
        return cache.getAttributeDeclaration(name);
    }

    public SmAttributeGroup<XmlAtom> getAttributeGroup(QName name)
    {
        return cache.getAttributeGroup(name);
    }

    public Iterable<SmAttributeGroup<XmlAtom>> getAttributeGroups()
    {
        return cache.getAttributeGroups();
    }

    public Iterable<SmAttribute<XmlAtom>> getAttributes()
    {
        return cache.getAttributes();
    }

    public SmComplexType<XmlAtom> getComplexType(QName name)
    {
        return cache.getComplexType(name);
    }

    public Iterable<SmComplexType<XmlAtom>> getComplexTypes()
    {
        return cache.getComplexTypes();
    }

    public SmComplexUrType<XmlAtom> getComplexUrType()
    {
        return cache.getComplexUrType();
    }

    public SmElement<XmlAtom> getElementDeclaration(QName name)
    {
        return cache.getElementDeclaration(name);
    }

    public Iterable<SmElement<XmlAtom>> getElements()
    {
        return cache.getElements();
    }

    public SmIdentityConstraint<XmlAtom> getIdentityConstraint(QName name)
    {
        return cache.getIdentityConstraint(name);
    }

    public Iterable<SmIdentityConstraint<XmlAtom>> getIdentityConstraints()
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

    public SmModelGroup<XmlAtom> getModelGroup(QName name)
    {
        return cache.getModelGroup(name);
    }

    public Iterable<SmModelGroup<XmlAtom>> getModelGroups()
    {
        return cache.getModelGroups();
    }

    public Iterable<String> getNamespaces()
    {
        return cache.getNamespaces();
    }

    public SmNotation<XmlAtom> getNotationDeclaration(QName name)
    {
        return cache.getNotationDeclaration(name);
    }

    public Iterable<SmNotation<XmlAtom>> getNotations()
    {
        return cache.getNotations();
    }

    public ProcessingContext<XmlNode> getProcessingContext()
    {
        return context;
    }

    public SmSimpleType<XmlAtom> getSimpleType(QName name)
    {
        return cache.getSimpleType(name);
    }

    public SmSimpleType<XmlAtom> getSimpleType(SmNativeType name)
    {
        return cache.getSimpleType(name);
    }

    public Iterable<SmSimpleType<XmlAtom>> getSimpleTypes()
    {
        return cache.getSimpleTypes();
    }

    public SmSimpleUrType<XmlAtom> getSimpleUrType()
    {
        return cache.getSimpleUrType();
    }

    public SmType<XmlAtom> getTypeDefinition(QName name)
    {
        return cache.getTypeDefinition(name);
    }

    public SmType<XmlAtom> getTypeDefinition(SmNativeType nativeType)
    {
        return cache.getTypeDefinition(nativeType);
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

    public void register(SmComponentBag<XmlAtom> components)
    {
        PreCondition.assertFalse(isLocked(), "isLocked()");
        cache.register(components);
    }

    private final XmlNodeContext context;
    private final TypedXmlNodeModel model;
    private final XmlAtomBridge atoms;
    private final SmMetaBridge<XmlAtom> cache;
    private final GxMetaBridgeOnSmMetaBridgeAdapter<XmlAtom> types;
    private boolean locked;
}
