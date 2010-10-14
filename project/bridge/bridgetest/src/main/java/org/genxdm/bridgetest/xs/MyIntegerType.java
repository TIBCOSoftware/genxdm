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
package org.genxdm.bridgetest.xs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.genxdm.exceptions.IllegalNullArgumentException;
import org.genxdm.names.NameSource;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.SmSchema;
import org.genxdm.xs.components.SmEnumeration;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmQuantifier;
import org.genxdm.xs.enums.SmScopeExtent;
import org.genxdm.xs.enums.SmWhiteSpacePolicy;
import org.genxdm.xs.exceptions.SmDatatypeException;
import org.genxdm.xs.exceptions.SmFacetException;
import org.genxdm.xs.facets.SmFacet;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.facets.SmLimit;
import org.genxdm.xs.facets.SmPattern;
import org.genxdm.xs.resolve.SmPrefixResolver;
import org.genxdm.xs.types.SmAtomicType;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmPrimeType;
import org.genxdm.xs.types.SmPrimeTypeKind;
import org.genxdm.xs.types.SmSequenceTypeVisitor;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmType;

public final class MyIntegerType<A> extends MyDerivedByRestrictionSimpleType<A> implements SmAtomicType<A>
{
	private static QName makeName(final String namespaceURI, final String localName, final NameSource nameBridge)
	{
		return new QName(namespaceURI, localName);
	}

	private final SmSchema<A> schema;
	private final AtomBridge<A> atomBridge;
	private final ArrayList<SmFacet<A>> facets = new ArrayList<SmFacet<A>>(2);
	private final SmLimit<A> MAX_INCLUSIVE;

	private final SmLimit<A> MIN_INCLUSIVE;

	private final QName name;

	public MyIntegerType(final String namespaceURI, final String localName, final SmSchema<A> schema, final AtomBridge<A> atomBridge)
	{
		super(makeName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer", atomBridge.getNameBridge()));
		this.schema = IllegalNullArgumentException.check(schema, "schema");
		this.atomBridge = atomBridge;
		IllegalNullArgumentException.check(namespaceURI, "namespaceURI");
		IllegalNullArgumentException.check(localName, "localName");
		this.name = new QName(namespaceURI, localName);
		this.MIN_INCLUSIVE = new MyLimit<A>(SmFacetKind.MinInclusive, atomBridge.createInteger(-2), atomBridge);
		this.MAX_INCLUSIVE = new MyLimit<A>(SmFacetKind.MaxExclusive, atomBridge.createInteger(5), atomBridge);
		facets.add(MIN_INCLUSIVE);
		facets.add(MAX_INCLUSIVE);
	}

	/**
	 * SmSequenceType
	 */
	public void accept(final SmSequenceTypeVisitor<A> visitor)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmType
	 */
	public boolean derivedFrom(String namespace, String name, Set<SmDerivationMethod> derivationMethods)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmType
	 */
	public boolean derivedFromType(SmType<A> ancestorType, Set<SmDerivationMethod> derivationMethods)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmType
	 */
	public SmSimpleType<A> getBaseType()
	{
		return (SmSimpleType<A>)schema.getTypeDefinition(baseName);
	}

	/**
	 * SmSimpleType
	 */
	public Iterable<SmEnumeration<A>> getEnumerations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmSimpleType
	 */
	public SmFacet<A> getFacetOfKind(final SmFacetKind facetKind)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmSimpleType
	 */
	public Iterable<SmFacet<A>> getFacets()
	{
		return facets;
	}

	/**
	 * SmType
	 */
	public Set<SmDerivationMethod> getFinal()
	{
		return Collections.emptySet();
	}

	/**
	 * SmAtomicType
	 */
	public SmPrimeTypeKind getKind()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmType
	 */
	public String getLocalName()
	{
		return name.getLocalPart();
	}

	/**
	 * SmType
	 */
	public QName getName()
	{
		return name;
	}

	/**
	 * SmAtomicType
	 */
	public SmNativeType getNativeType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmAtomicType
	 */
	public SmAtomicType<A> getNativeTypeDefinition()
	{
		return this;
	}

	/**
	 * SmSimpleType
	 */
	public Iterable<SmPattern> getPatterns()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmComponent
	 */
	public SmScopeExtent getScopeExtent()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmType
	 */
	public String getTargetNamespace()
	{
		return name.getNamespaceURI();
	}

	/**
	 * SmSimpleType
	 */
	public SmWhiteSpacePolicy getWhiteSpacePolicy()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmSimpleType
	 */
	public boolean hasEnumerations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmSimpleType
	 */
	public boolean hasFacetOfKind(SmFacetKind facetKind)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmSimpleType
	 */
	public boolean hasFacets()
	{
		return facets.size() > 0;
	}

	/**
	 * SmSimpleType
	 */
	public boolean hasPatterns()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmType
	 */
	public boolean isAbstract()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmType
	 */
	public boolean isAnonymous()
	{
		return false;
	}

	/**
	 * SmSimpleType
	 */
	public boolean isAtomicType()
	{
		return true;
	}

	/**
	 * SmType
	 */
	public boolean isAtomicUrType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmAtomicType
	 */
	public boolean isChoice()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmType
	 */
	public boolean isComplexUrType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmType
	 */
	public boolean isFinal(final SmDerivationMethod derivation)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmSimpleType
	 */
	public boolean isID()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmSimpleType
	 */
	public boolean isIDREF()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmSimpleType
	 */
	public boolean isIDREFS()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmSimpleType
	 */
	public boolean isListType()
	{
		return false;
	}

	/**
	 * SmType
	 */
	public boolean isNative()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmAtomicType
	 */
	public boolean isNone()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmType
	 */
	public boolean isSimpleUrType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmSimpleType
	 */
	public boolean isUnionType()
	{
		return false;
	}

	/**
	 * SmSimpleType
	 */
	public String normalize(final String initialValue)
	{
		return getBaseType().normalize(initialValue);
	}

	/**
	 * SmSequenceType
	 */
	public SmAtomicType<A> prime()
	{
		return this;
	}

	/**
	 * SmSequenceType
	 */
	public SmQuantifier quantifier()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SmAtomicType
	 */
	public boolean subtype(final SmPrimeType<A> rhs)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	@Override
	public String toString()
	{
		return name.toString();
	}

	/**
	 * SmSimpleType
	 */
	public List<A> validate(final List<? extends A> value) throws SmDatatypeException
	{
		IllegalNullArgumentException.check(value, "value");
		if (value.size() == 1)
		{
			final A atom = value.get(0);
			final SmNativeType nativeType = atomBridge.getNativeType(atom);
			if (nativeType.isInteger())
			{
				for (final SmFacet<A> facet : facets)
				{
					try
					{
						facet.validate(value, this);
					}
					catch (final SmFacetException e)
					{
						throw new SmDatatypeException(atomBridge.getC14NString(value), this, e);
					}
				}
				// The following doesn't cost object creation on well optimized atomic values.
				return atomBridge.wrapAtom(atom);
			}
			else if (nativeType == SmNativeType.UNTYPED_ATOMIC)
			{
				return validate(atomBridge.getC14NForm(atom));
			}
			else
			{
				throw new AssertionError("TODO? validate(" + nativeType + ")");
			}
		}
		else
		{
			throw new SmDatatypeException("", this);
		}
	}

	/**
	 * SmSimpleType
	 */
	public List<A> validate(final String initialValue) throws SmDatatypeException
	{
		IllegalNullArgumentException.check(initialValue, "initialValue");
		return validate(getBaseType().validate(initialValue));
	}

	/**
	 * SmSimpleType
	 */
	public List<A> validate(final String initialValue, final SmPrefixResolver resolver) throws SmDatatypeException
	{
		IllegalNullArgumentException.check(resolver, "resolver");
		return validate(initialValue);
	}
}
