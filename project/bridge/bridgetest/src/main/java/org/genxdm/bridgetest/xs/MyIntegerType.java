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
import org.genxdm.xs.Schema;
import org.genxdm.xs.components.EnumerationDefinition;
import org.genxdm.xs.enums.DerivationMethod;
import org.genxdm.xs.enums.KeeneQuantifier;
import org.genxdm.xs.enums.ScopeExtent;
import org.genxdm.xs.enums.WhiteSpacePolicy;
import org.genxdm.xs.exceptions.DatatypeException;
import org.genxdm.xs.exceptions.FacetException;
import org.genxdm.xs.facets.Facet;
import org.genxdm.xs.facets.FacetKind;
import org.genxdm.xs.facets.Limit;
import org.genxdm.xs.facets.Pattern;
import org.genxdm.xs.resolve.PrefixResolver;
import org.genxdm.xs.types.AtomicType;
import org.genxdm.xs.types.NativeType;
import org.genxdm.xs.types.PrimeType;
import org.genxdm.xs.types.PrimeTypeKind;
import org.genxdm.xs.types.SequenceTypeVisitor;
import org.genxdm.xs.types.SimpleType;
import org.genxdm.xs.types.Type;

public final class MyIntegerType<A> extends MyDerivedByRestrictionSimpleType<A> implements AtomicType<A>
{
	private static QName makeName(final String namespaceURI, final String localName, final NameSource nameBridge)
	{
		return new QName(namespaceURI, localName);
	}

	private final Schema<A> schema;
	private final AtomBridge<A> atomBridge;
	private final ArrayList<Facet<A>> facets = new ArrayList<Facet<A>>(2);
	private final Limit<A> MAX_INCLUSIVE;

	private final Limit<A> MIN_INCLUSIVE;

	private final QName name;

	public MyIntegerType(final String namespaceURI, final String localName, final Schema<A> schema, final AtomBridge<A> atomBridge)
	{
		super(makeName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer", atomBridge.getNameBridge()));
		this.schema = IllegalNullArgumentException.check(schema, "schema");
		this.atomBridge = atomBridge;
		IllegalNullArgumentException.check(namespaceURI, "namespaceURI");
		IllegalNullArgumentException.check(localName, "localName");
		this.name = new QName(namespaceURI, localName);
		this.MIN_INCLUSIVE = new MyLimit<A>(FacetKind.MinInclusive, atomBridge.createInteger(-2), atomBridge);
		this.MAX_INCLUSIVE = new MyLimit<A>(FacetKind.MaxExclusive, atomBridge.createInteger(5), atomBridge);
		facets.add(MIN_INCLUSIVE);
		facets.add(MAX_INCLUSIVE);
	}

	/**
	 * SequenceType
	 */
	public void accept(final SequenceTypeVisitor<A> visitor)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * Type
	 */
	public boolean derivedFrom(String namespace, String name, Set<DerivationMethod> derivationMethods)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * Type
	 */
	public boolean derivedFromType(Type<A> ancestorType, Set<DerivationMethod> derivationMethods)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * Type
	 */
	public SimpleType<A> getBaseType()
	{
		return (SimpleType<A>)schema.getTypeDefinition(baseName);
	}

	/**
	 * SimpleType
	 */
	public Iterable<EnumerationDefinition<A>> getEnumerations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SimpleType
	 */
	public Facet<A> getFacetOfKind(final FacetKind facetKind)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SimpleType
	 */
	public Iterable<Facet<A>> getFacets()
	{
		return facets;
	}

	/**
	 * Type
	 */
	public Set<DerivationMethod> getFinal()
	{
		return Collections.emptySet();
	}

	/**
	 * AtomicType
	 */
	public PrimeTypeKind getKind()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * Type
	 */
	public String getLocalName()
	{
		return name.getLocalPart();
	}

	/**
	 * Type
	 */
	public QName getName()
	{
		return name;
	}

	/**
	 * AtomicType
	 */
	public NativeType getNativeType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * AtomicType
	 */
	public AtomicType<A> getNativeTypeDefinition()
	{
		return this;
	}

	/**
	 * SimpleType
	 */
	public Iterable<Pattern> getPatterns()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SchemaComponent
	 */
	public ScopeExtent getScopeExtent()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * Type
	 */
	public String getTargetNamespace()
	{
		return name.getNamespaceURI();
	}

	/**
	 * SimpleType
	 */
	public WhiteSpacePolicy getWhiteSpacePolicy()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SimpleType
	 */
	public boolean hasEnumerations()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SimpleType
	 */
	public boolean hasFacetOfKind(FacetKind facetKind)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SimpleType
	 */
	public boolean hasFacets()
	{
		return facets.size() > 0;
	}

	/**
	 * SimpleType
	 */
	public boolean hasPatterns()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * Type
	 */
	public boolean isAbstract()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * Type
	 */
	public boolean isAnonymous()
	{
		return false;
	}

	/**
	 * SimpleType
	 */
	public boolean isAtomicType()
	{
		return true;
	}

	/**
	 * Type
	 */
	public boolean isAtomicUrType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * AtomicType
	 */
	public boolean isChoice()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * Type
	 */
	public boolean isComplexUrType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * Type
	 */
	public boolean isFinal(final DerivationMethod derivation)
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SimpleType
	 */
	public boolean isID()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SimpleType
	 */
	public boolean isIDREF()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SimpleType
	 */
	public boolean isIDREFS()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SimpleType
	 */
	public boolean isListType()
	{
		return false;
	}

	/**
	 * Type
	 */
	public boolean isNative()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * AtomicType
	 */
	public boolean isNone()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * Type
	 */
	public boolean isSimpleUrType()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * SimpleType
	 */
	public boolean isUnionType()
	{
		return false;
	}

	/**
	 * SimpleType
	 */
	public String normalize(final String initialValue)
	{
		return getBaseType().normalize(initialValue);
	}

	/**
	 * SequenceType
	 */
	public AtomicType<A> prime()
	{
		return this;
	}

	/**
	 * SequenceType
	 */
	public KeeneQuantifier quantifier()
	{
		// TODO Auto-generated method stub
		throw new AssertionError("TODO");
	}

	/**
	 * AtomicType
	 */
	public boolean subtype(final PrimeType<A> rhs)
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
	 * SimpleType
	 */
	public List<A> validate(final List<? extends A> value) throws DatatypeException
	{
		IllegalNullArgumentException.check(value, "value");
		if (value.size() == 1)
		{
			final A atom = value.get(0);
			final NativeType nativeType = atomBridge.getNativeType(atom);
			if (nativeType.isInteger())
			{
				for (final Facet<A> facet : facets)
				{
					try
					{
						facet.validate(value, this);
					}
					catch (final FacetException e)
					{
						throw new DatatypeException(atomBridge.getC14NString(value), this, e);
					}
				}
				// The following doesn't cost object creation on well optimized atomic values.
				return atomBridge.wrapAtom(atom);
			}
			else if (nativeType == NativeType.UNTYPED_ATOMIC)
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
			throw new DatatypeException("", this);
		}
	}

	/**
	 * SimpleType
	 */
	public List<A> validate(final String initialValue) throws DatatypeException
	{
		IllegalNullArgumentException.check(initialValue, "initialValue");
		return validate(getBaseType().validate(initialValue));
	}

	/**
	 * SimpleType
	 */
	public List<A> validate(final String initialValue, final PrefixResolver resolver) throws DatatypeException
	{
		IllegalNullArgumentException.check(resolver, "resolver");
		return validate(initialValue);
	}
}
