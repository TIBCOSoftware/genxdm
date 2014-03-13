/*
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
package org.genxdm.bridgekit.xs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.genxdm.bridgekit.misc.StringToURIParser;
import org.genxdm.bridgekit.xs.complex.CommentNodeTypeImpl;
import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.ComponentProvider;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ComponentKind;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.components.SchemaComponent;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.AtomicUrType;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.ComplexUrType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.NodeType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.SimpleUrType;
import org.genxdm.xs.types.Type;

final class SchemaCacheImpl implements SchemaComponentCache
{
    public SchemaCacheImpl()
    {
        assertNotLocked();

        BUILT_IN = BuiltInSchema.SINGLETON;
        register(BUILT_IN);
        register(new XmlSchema());
        register(new XsiSchema());

        COMMENT = new CommentNodeTypeImpl();
    }

    public PrimeType comment()
    {
        return COMMENT;
    }

    @Override
    public void declareAttribute(final AttributeDefinition attribute)
    {
        final QName name = checkComponent(attribute, ComponentKind.ATTRIBUTE);
        if (!m_attributes.containsKey(name))
        {
            m_attributes.put(name, attribute);
            recordNamespace(attribute);
        }
    }

    @Override
    public void declareElement(final ElementDefinition element)
    {
        final QName name = checkComponent(element, ComponentKind.ELEMENT);
        if (!m_elements.containsKey(name))
        {
            m_elements.put(name, element);
            recordNamespace(element);
        }
    }

    @Override
    public void declareNotation(final NotationDefinition notation)
    {
        final QName name = checkComponent(notation, ComponentKind.NOTATION);
        if (!m_notations.containsKey(name))
        {
            m_notations.put(name, notation);
            recordNamespace(notation);
        }
    }

    @Override
    public void defineAttributeGroup(final AttributeGroupDefinition attributeGroup)
    {
        final QName name = checkComponent(attributeGroup, ComponentKind.ATTRIBUTE_GROUP);
        if (!m_attributeGroups.containsKey(name))
        {
            m_attributeGroups.put(name, attributeGroup);
            recordNamespace(attributeGroup);
        }
    }

    @Override
    public void defineComplexType(final ComplexType complexType)
    {
        final QName name = checkComponent(complexType, ComponentKind.COMPLEX_TYPE);
        if (!m_complexTypes.containsKey(name))
        {
            m_complexTypes.put(name, complexType);
            recordNamespace(complexType);
        }
    }

    @Override
    public void defineIdentityConstraint(final IdentityConstraint identityConstraint)
    {
        final QName name = checkComponent(identityConstraint, ComponentKind.IDENTITY_CONSTRAINT);
        if (!m_identityConstraints.containsKey(name))
        {
            m_identityConstraints.put(name, identityConstraint);
            recordNamespace(identityConstraint);
        }
    }

    @Override
    public void defineModelGroup(final ModelGroup modelGroup)
    {
        final QName name = checkComponent(modelGroup, ComponentKind.MODEL_GROUP);
        if (!m_modelGroups.containsKey(name))
        {
            m_modelGroups.put(name, modelGroup);
            recordNamespace(modelGroup);
        }
    }

    @Override
    public void defineSimpleType(final SimpleType simpleType)
    {
        final QName name = checkComponent(simpleType, ComponentKind.SIMPLE_TYPE);
        if (!m_simpleTypes.containsKey(name))
        {
            m_simpleTypes.put(name, simpleType);
            recordNamespace(simpleType);
        }
    }

    @Override
    public ComponentProvider getComponentProvider()
    {
        return m_provider;
    }

    @Override
    public ComponentBag getComponents()
    {
        return m_components;
    }
    
    @Override
    public Iterable<String> getNamespaces()
    {
        return namespaces;
    }

    @Override
    public boolean isLocked()
    {
        return m_isLocked;
    }

    @Override
    public void lock()
    {
        m_isLocked = true;
    }

    @Override
    public void register(final ComponentBag components)
    {
        assertNotLocked();
        if (null != components)
        {
            for (final SimpleType simpleType : components.getSimpleTypes())
            {
                defineSimpleType(simpleType);
            }
            for (final ComplexType complexType : components.getComplexTypes())
            {
                defineComplexType(complexType);
            }
            for (final AttributeDefinition attribute : components.getAttributes())
            {
                declareAttribute(attribute);
            }
            for (final ElementDefinition element : components.getElements())
            {
                declareElement(element);
            }
            for (final AttributeGroupDefinition attributeGroup : components.getAttributeGroups())
            {
                defineAttributeGroup(attributeGroup);
            }
            for (final IdentityConstraint identityConstraint : components.getIdentityConstraints())
            {
                defineIdentityConstraint(identityConstraint);
            }
            for (final ModelGroup modelGroup : components.getModelGroups())
            {
                defineModelGroup(modelGroup);
            }
            for (final NotationDefinition notation : components.getNotations())
            {
                declareNotation(notation);
            }
        }
    }

    private void assertNotLocked()
    {
        PreCondition.assertFalse(m_isLocked, "isLocked -> true");
    }

    private QName checkComponent(final SchemaComponent component, final ComponentKind kind)
    {
        PreCondition.assertArgumentNotNull(component);
        if (!kind.canBeAnonymous)
        {
            PreCondition.assertFalse(component.isAnonymous());
        }
        return PreCondition.assertArgumentNotNull(component.getName());
    }

    private void recordNamespace(final SchemaComponent component)
    {
        if (!component.isAnonymous())
        {
            namespaces.add(component.getTargetNamespace());
        }
    }

    private class BagImpl implements ComponentBag
    {
        @Override
        public Iterable<AttributeGroupDefinition> getAttributeGroups()
        {
            return m_attributeGroups.values();
        }

        @Override
        public Iterable<AttributeDefinition> getAttributes()
        {
            return m_attributes.values();
        }

        @Override
        public Iterable<ComplexType> getComplexTypes()
        {
            return m_complexTypes.values();
        }

        @Override
        public Iterable<ElementDefinition> getElements()
        {
            return m_elements.values();
        }

        @Override
        public Iterable<IdentityConstraint> getIdentityConstraints()
        {
            return m_identityConstraints.values();
        }

        @Override
        public Iterable<ModelGroup> getModelGroups()
        {
            return m_modelGroups.values();
        }

        @Override
        public Iterable<NotationDefinition> getNotations()
        {
            return m_notations.values();
        }

        @Override
        public Iterable<SimpleType> getSimpleTypes()
        {
            return m_simpleTypes.values();
        }
    }
    
    private class ProviderImpl implements ComponentProvider
    {

        @Override
        public synchronized QName generateUniqueName()
        {
            assertNotLocked();
            return new QName("http://genxdm.org/typed/local-types", "type-".concat(Integer.toString(m_nextType++)));
        }

        @Override
        public AtomicType getAtomicType(final QName name)
        {
            SimpleType simpleType = m_simpleTypes.get(name);
            if(simpleType != null)
            {
            	if(simpleType.isAtomicType())
            	{
                    return (AtomicType)simpleType;
            	}
            }
            else
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
                    simpleType = m_simpleTypes.get(testName);
                    if(simpleType != null && simpleType.isAtomicType())
                    {
                    	return (AtomicType)simpleType;
                    }
            	}
            }
            return null;
        }

        @Override
        public AtomicType getAtomicType(final NativeType nativeType)
        {
            final Type type = getTypeDefinition(nativeType);
            if (type.isAtomicType())
            {
                return (AtomicType)type;
            }
            else
            {
                return null;
            }
        }

        @Override
        public AtomicUrType getAtomicUrType()
        {
            return BUILT_IN.ANY_ATOMIC_TYPE;
        }

        @Override
        public AttributeDefinition getAttributeDeclaration(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            AttributeDefinition retval = m_attributes.get(name);
            if(retval == null)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		return m_attributes.get(testName);
            	}
            }
            return retval;
        }

        @Override
        public AttributeGroupDefinition getAttributeGroup(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            AttributeGroupDefinition retval = m_attributeGroups.get(name);
            if(retval == null)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		return m_attributeGroups.get(testName);
            	}
            }
            return retval;
        }

        @Override
        public ComplexType getComplexType(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            ComplexType retval = m_complexTypes.get(name);
            if(retval == null)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		return m_complexTypes.get(testName);
            	}
            }
            return retval;
        }

        @Override
        public ComplexUrType getComplexUrType()
        {
            return BUILT_IN.ANY_COMPLEX_TYPE;
        }

        @Override
        public ElementDefinition getElementDeclaration(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            ElementDefinition retval = m_elements.get(name);
            if(retval == null)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		return m_elements.get(testName);
            	}
            }
            return retval;
        }

        @Override
        public IdentityConstraint getIdentityConstraint(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            IdentityConstraint retval = m_identityConstraints.get(name);
            if(retval == null)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		return m_identityConstraints.get(testName);
            	}
            }
            return retval;
        }

        @Override
        public ModelGroup getModelGroup(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            ModelGroup retval = m_modelGroups.get(name);
            if(retval == null)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		return m_modelGroups.get(testName);
            	}
            }
            return retval;
        }

        @Override
        public NotationDefinition getNotationDeclaration(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            NotationDefinition retval =  m_notations.get(name);
            if(retval == null)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		return m_notations.get(testName);
            	}
            }
            return retval;
        }

        @Override
        public SimpleType getSimpleType(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            SimpleType retval = m_simpleTypes.get(name);
            if(retval == null)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		return m_simpleTypes.get(testName);
            	}
            }
            return retval;
        }

        @Override
        public SimpleType getSimpleType(final NativeType nativeType)
        {
            final Type type = getTypeDefinition(nativeType);
            if (type instanceof SimpleType)
            {
                return (SimpleType)type;
            }
            else
            {
                return null;
            }
        }

        @Override
        public SimpleUrType getSimpleUrType()
        {
            return BUILT_IN.ANY_SIMPLE_TYPE;
        }

        @Override
        public Type getTypeDefinition(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            if (m_complexTypes.containsKey(name))
            {
                return m_complexTypes.get(name);
            }
            else if (m_simpleTypes.containsKey(name))
            {
                return m_simpleTypes.get(name);
            }
            else if (name.equals(BUILT_IN.ANY_ATOMIC_TYPE.getName()))
            {
                return BUILT_IN.ANY_ATOMIC_TYPE;
            }
            else if (name.equals(BUILT_IN.ANY_SIMPLE_TYPE.getName()))
            {
                return BUILT_IN.ANY_SIMPLE_TYPE;
            }
            else if (name.equals(BUILT_IN.ANY_COMPLEX_TYPE.getName()))
            {
                return BUILT_IN.ANY_COMPLEX_TYPE;
            }
            else
            {
                return null;
            }
        }

        @Override
        public Type getTypeDefinition(final NativeType nativeType)
        {
            switch (nativeType)
            {
                case BOOLEAN:
                {
                    return BUILT_IN.BOOLEAN;
                }
                case STRING:
                {
                    return BUILT_IN.STRING;
                }
                case DOUBLE:
                {
                    return BUILT_IN.DOUBLE;
                }
                case FLOAT:
                {
                    return BUILT_IN.FLOAT;
                }
                case DECIMAL:
                {
                    return BUILT_IN.DECIMAL;
                }
                case INTEGER:
                {
                    return BUILT_IN.INTEGER;
                }
                case LONG:
                {
                    return BUILT_IN.LONG;
                }
                case INT:
                {
                    return BUILT_IN.INT;
                }
                case SHORT:
                {
                    return BUILT_IN.SHORT;
                }
                case BYTE:
                {
                    return BUILT_IN.BYTE;
                }
                case NON_POSITIVE_INTEGER:
                {
                    return BUILT_IN.NON_POSITIVE_INTEGER;
                }
                case NEGATIVE_INTEGER:
                {
                    return BUILT_IN.NEGATIVE_INTEGER;
                }
                case NON_NEGATIVE_INTEGER:
                {
                    return BUILT_IN.NON_NEGATIVE_INTEGER;
                }
                case POSITIVE_INTEGER:
                {
                    return BUILT_IN.POSITIVE_INTEGER;
                }
                case UNSIGNED_LONG:
                {
                    return BUILT_IN.UNSIGNED_LONG;
                }
                case UNSIGNED_INT:
                {
                    return BUILT_IN.UNSIGNED_INT;
                }
                case UNSIGNED_SHORT:
                {
                    return BUILT_IN.UNSIGNED_SHORT;
                }
                case UNSIGNED_BYTE:
                {
                    return BUILT_IN.UNSIGNED_BYTE;
                }
                case UNTYPED_ATOMIC:
                {
                    return BUILT_IN.UNTYPED_ATOMIC;
                }
                case UNTYPED:
                {
                    return BUILT_IN.UNTYPED;
                }
                case DATE:
                {
                    return BUILT_IN.DATE;
                }
                case DATETIME:
                {
                    return BUILT_IN.DATETIME;
                }
                case TIME:
                {
                    return BUILT_IN.TIME;
                }
                case GYEARMONTH:
                {
                    return BUILT_IN.GYEARMONTH;
                }
                case GYEAR:
                {
                    return BUILT_IN.GYEAR;
                }
                case GMONTHDAY:
                {
                    return BUILT_IN.GMONTHDAY;
                }
                case GDAY:
                {
                    return BUILT_IN.GDAY;
                }
                case GMONTH:
                {
                    return BUILT_IN.GMONTH;
                }
                case DURATION_DAYTIME:
                {
                    return BUILT_IN.DURATION_DAYTIME;
                }
                case DURATION_YEARMONTH:
                {
                    return BUILT_IN.DURATION_YEARMONTH;
                }
                case DURATION:
                {
                    return BUILT_IN.DURATION;
                }
                case ANY_URI:
                {
                    return BUILT_IN.ANY_URI;
                }
                case BASE64_BINARY:
                {
                    return BUILT_IN.BASE64_BINARY;
                }
                case HEX_BINARY:
                {
                    return BUILT_IN.HEX_BINARY;
                }
                case LANGUAGE:
                {
                    return BUILT_IN.LANGUAGE;
                }
                case QNAME:
                {
                    return BUILT_IN.QNAME;
                }
                case ANY_TYPE:
                {
                    return BUILT_IN.ANY_COMPLEX_TYPE;
                }
                case ANY_SIMPLE_TYPE:
                {
                    return BUILT_IN.ANY_SIMPLE_TYPE;
                }
                case ANY_ATOMIC_TYPE:
                {
                    return BUILT_IN.ANY_ATOMIC_TYPE;
                }
                case NORMALIZED_STRING:
                {
                    return BUILT_IN.NORMALIZED_STRING;
                }
                case TOKEN:
                {
                    return BUILT_IN.TOKEN;
                }
                case NMTOKEN:
                {
                    return BUILT_IN.NMTOKEN;
                }
                case NAME:
                {
                    return BUILT_IN.NAME;
                }
                case NCNAME:
                {
                    return BUILT_IN.NCNAME;
                }
                case ID:
                {
                    return BUILT_IN.ID;
                }
                case IDREF:
                {
                    return BUILT_IN.IDREF;
                }
                case ENTITY:
                {
                    return BUILT_IN.ENTITY;
                }
                case NOTATION:
                {
                    return BUILT_IN.NOTATION;
                }
                case IDREFS:
                {
                    return BUILT_IN.IDREFS;
                }
                case NMTOKENS:
                {
                    return BUILT_IN.NMTOKENS;
                }
                case ENTITIES:
                {
                    return BUILT_IN.ENTITIES;
                }
                default:
                {
                    throw new AssertionError(nativeType);
                }
            }
        }

        @Override
        public boolean hasAttribute(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            boolean retval = m_attributes.containsKey(name);
            final QName testName = encodeQName(name);
        	if(testName != null)
        	{
        		if (m_attributes.containsKey(testName))
        		{
        			return true;
        		}
        	}
            return retval;
        }

        @Override
        public boolean hasAttributeGroup(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            boolean retval = m_attributeGroups.containsKey(name);
            final QName testName = encodeQName(name);
        	if(testName != null)
        	{
        		if (m_attributeGroups.containsKey(testName))
        		{
        			return true;
        		}
        	}
            return retval;
        }

        @Override
        public boolean hasComplexType(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            boolean retval = m_complexTypes.containsKey(name);
            final QName testName = encodeQName(name);
        	if(testName != null)
        	{
        		if (m_complexTypes.containsKey(testName))
        		{
        			return true;
        		}
        	}
            return retval;
        }

        @Override
        public boolean hasElement(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            boolean retval = m_elements.containsKey(name);
            if(!retval)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		if (m_elements.containsKey(testName))
            		{
            			return true;
            		}
            	}
            }
            return retval;
        }

        @Override
        public boolean hasIdentityConstraint(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            boolean retval = m_identityConstraints.containsKey(name);
            if(!retval)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		if (m_identityConstraints.containsKey(testName))
            		{
            			return true;
            		}
            	}
            }
            return retval;
        }

        @Override
        public boolean hasModelGroup(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            boolean retval = m_modelGroups.containsKey(name);
            if(!retval)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		if (m_modelGroups.containsKey(testName))
            		{
            			return true;
            		}
            	}
            }
            return retval;
        }

        @Override
        public boolean hasNotation(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            boolean retval = m_notations.containsKey(name);
            if(!retval)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
            		if (m_notations.containsKey(testName))
            		{
            			return true;
            		}
            	}
            }
            return retval;
        }

        @Override
        public boolean hasSimpleType(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            boolean retval = m_simpleTypes.containsKey(name);
            if(!retval)
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
                	if(testName != null)
                	{
                    	if (m_complexTypes.containsKey(testName))
                    	{
                    		return true;
                    	}
                    	else if (m_simpleTypes.containsKey(testName))
                    	{
                    		return true;
                    	}
                	}
            	}
            }
            return retval;
        }

        @Override
        public boolean hasType(final QName name)
        {
            PreCondition.assertArgumentNotNull(name, "name");
            if (m_complexTypes.containsKey(name))
            {
                return true;
            }
            else if (m_simpleTypes.containsKey(name))
            {
                return true;
            }
            else 
            {
            	final QName testName = encodeQName(name);
            	if(testName != null)
            	{
                	if (m_complexTypes.containsKey(testName))
                	{
                		return true;
                	}
                	else if (m_simpleTypes.containsKey(testName))
                	{
                		return true;
                	}
            	}
            }
            return false;
        }
    }
    private QName encodeQName(final QName name)
    {
    	final String ns = name.getNamespaceURI();
    	if(ns != null && ns.length() > 0)
    	{
        	// Do we already have an encoded version of this namespace?  Check the map of unencoded to encoded namespaces.
        	String encodedNs = m_unencodedNs2EncodedNsMap.get(ns);
        	if(encodedNs == null)
        	{
        		encodedNs = StringToURIParser.parse(ns).toString();
        		if(false == encodedNs.equals(ns))
        		{
        			m_unencodedNs2EncodedNsMap.put(ns, encodedNs);
        		}
        		else
        		{
        			// Note: at this point, we have a namespace from a QName which has no matching component in this cache; 
        			// otherwise, this encodeQName method would not have been called.  We're choosing to not put the 
        			// namespace into the map of unencodedNs2encodedNs because it doesn't need encoding; but, that means 
        			// repeated calls to match that QName will cause repeated (and unnecessary?) calls to 
        			// StringToURIParser.parse, above.  Someday, we may choose a different behavior.
        			return null; // incoming namespace did not require encoding.
        		}
        	}
        	if(encodedNs != null)
        	{
        		QName testName = m_unencodeQNameKeys.get(name);
        		if(testName == null)
        		{
            		testName = new QName(encodedNs, name.getLocalPart());
            		m_unencodeQNameKeys.put(name, testName);
        		}
        		return testName;
        	}
    	}
    	return null;
    }
    private final ComponentProvider m_provider = new ProviderImpl();
    private final ComponentBag m_components = new BagImpl();

    // built-ins
    private final BuiltInSchema BUILT_IN;
    private final NodeType COMMENT;

    // the components
    private final ConcurrentHashMap<QName, AttributeGroupDefinition> m_attributeGroups = new ConcurrentHashMap<QName, AttributeGroupDefinition>();
    private final ConcurrentHashMap<QName, AttributeDefinition> m_attributes = new ConcurrentHashMap<QName, AttributeDefinition>();
    private final ConcurrentHashMap<QName, ComplexType> m_complexTypes = new ConcurrentHashMap<QName, ComplexType>();
    private final ConcurrentHashMap<QName, ElementDefinition> m_elements = new ConcurrentHashMap<QName, ElementDefinition>();
    private final ConcurrentHashMap<QName, IdentityConstraint> m_identityConstraints = new ConcurrentHashMap<QName, IdentityConstraint>();
    private final ConcurrentHashMap<QName, ModelGroup> m_modelGroups = new ConcurrentHashMap<QName, ModelGroup>();
    private final ConcurrentHashMap<QName, NotationDefinition> m_notations = new ConcurrentHashMap<QName, NotationDefinition>();
    private final ConcurrentHashMap<QName, SimpleType> m_simpleTypes = new ConcurrentHashMap<QName, SimpleType>();

    private boolean m_isLocked = false;
    private int m_nextType = 0;
    /**
     * The set of namespaces of all components. We build this during registration, which acts as the gateway.
     */
    private final Set<String> namespaces = new HashSet<String>();

    /**
     * keys are uncoded namespaces; values are the encoded namespace which should match the namespaces in the cache
     */
    private final Map<String,String> m_unencodedNs2EncodedNsMap = Collections.synchronizedMap(new WeakHashMap<String, String>());    
    private final Map<QName,QName> m_unencodeQNameKeys = Collections.synchronizedMap(new WeakHashMap<QName, QName>());    
}
