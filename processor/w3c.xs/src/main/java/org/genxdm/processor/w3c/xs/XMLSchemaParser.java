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
package org.genxdm.processor.w3c.xs;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.processor.w3c.xs.exception.CvcUnexpectedAttributeException;
import org.genxdm.processor.w3c.xs.exception.CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException;
import org.genxdm.processor.w3c.xs.exception.SmAttributeDefaultAndUseImpliesOptionalException;
import org.genxdm.processor.w3c.xs.exception.SmAttributeRefPresentException;
import org.genxdm.processor.w3c.xs.exception.SmAttributeRefXorNameException;
import org.genxdm.processor.w3c.xs.exception.SmAttributeUseException;
import org.genxdm.processor.w3c.xs.exception.SmComplexTypeException;
import org.genxdm.processor.w3c.xs.exception.SmDuplicateIDException;
import org.genxdm.processor.w3c.xs.exception.SmElementRefPresentException;
import org.genxdm.processor.w3c.xs.exception.SmElementRefXorNameException;
import org.genxdm.processor.w3c.xs.exception.SmElementSimpleTypeXorComplexTypeException;
import org.genxdm.processor.w3c.xs.exception.SmIllegalNamespaceException;
import org.genxdm.processor.w3c.xs.exception.SmImportNotWellFormedException;
import org.genxdm.processor.w3c.xs.exception.SmInclusionNamespaceMismatchException;
import org.genxdm.processor.w3c.xs.exception.SmInclusionNotWellFormedException;
import org.genxdm.processor.w3c.xs.exception.SmMissingAttributeException;
import org.genxdm.processor.w3c.xs.exception.SmNoSchemaForNamespaceException;
import org.genxdm.processor.w3c.xs.exception.SmRedefineTypeSelfReferenceException;
import org.genxdm.processor.w3c.xs.exception.SmRedefinitionNamespaceMismatchException;
import org.genxdm.processor.w3c.xs.exception.SmRedefinitionNotWellFormedException;
import org.genxdm.processor.w3c.xs.exception.SmSimpleTypeListException;
import org.genxdm.processor.w3c.xs.exception.SmSimpleTypeRestrictionException;
import org.genxdm.processor.w3c.xs.exception.SmSimpleTypeUnionException;
import org.genxdm.processor.w3c.xs.exception.SmSourceAttributeDefaultAndFixedPresentException;
import org.genxdm.processor.w3c.xs.exception.SmTopLevelSchemaNotWellFormedException;
import org.genxdm.processor.w3c.xs.exception.SmUnexpectedElementException;
import org.genxdm.processor.w3c.xs.exception.SmUnexpectedEndException;
import org.genxdm.processor.w3c.xs.exception.SrcAttributeTypeAndSimpleTypePresentException;
import org.genxdm.processor.w3c.xs.exception.SrcPrefixNotFoundException;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.SmComponentProvider;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.constraints.SmIdentityConstraintKind;
import org.genxdm.xs.constraints.SmNamespaceConstraint;
import org.genxdm.xs.constraints.SmRestrictedXPath;
import org.genxdm.xs.constraints.SmValueConstraint;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmProcessContentsMode;
import org.genxdm.xs.enums.SmWhiteSpacePolicy;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmDatatypeException;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.exceptions.SmExceptionHandler;
import org.genxdm.xs.exceptions.SmSimpleTypeException;
import org.genxdm.xs.facets.SmFacetKind;
import org.genxdm.xs.resolve.SmCatalog;
import org.genxdm.xs.resolve.SmResolver;
import org.genxdm.xs.types.SmNativeType;
import org.genxdm.xs.types.SmSimpleType;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 * Implementation Notes:
 * <ul>
 * <li>Distinguish between global declarations and local declarations (the latter are really particles).</li>
 * <li>The processing context is used to generate anonymous type names and to retrieve references to native W3C XML
 * Schema types.</li>
 * <li>Maximise the feedback from parsing by choosing when to throw/catch
 * </ul>
 */
final class XMLSchemaParser<A> extends XMLRepresentation
{
	private enum ModuleKind
	{
		Import, Include, Redefine
	}

	private static final NameSource nameSource = new NameSource();
	// We'll use any atom bridge we can lay our hands on to help us with parsing
	// built-in types.
	// private static final GxAtomBridge<?, String> atomBridge = null;//new
	// AtomBridge<String>(StringNameBridge.getNameBridge());
	// private static final CmTable<String> annotationOptionalTable =
	// makeAnnotationOptionalTable();
	private static final CmTable<String> attributeTable = makeAttributeTable();
	private static final CmTable<String> complexContentTable = makeComplexContentTable();
	private static final CmTable<String> complexTypeTable = makeComplexTypeTable();

	private static final String EPSILON = "";
	private static final CmTable<String> extensionInComplexContentTable = makeExtensionInComplexContentTable();
	private static final CmTable<String> extensionInSimpleContentTable = makeExtensionInSimpleContentTable();
	private static final CmTable<String> restrictionInComplexContentTable = makeRestrictionInComplexContentTable();
	private static final CmTable<String> restrictionInSimpleContentTable = makeRestrictionInSimpleContentTable();
	private static final CmTable<String> simpleContentTable = makeSimpleContentTable();

	private static SrcFrozenLocation getFrozenLocation(final Location location)
	{
		PreCondition.assertArgumentNotNull(location, "location");
		// PreCondition.assertArgumentNotNull(location.getPublicId(),
		// String.class, "location.getPublicId()");
		return new SrcFrozenLocation(location);
	}

	/**
	 * Return the local-name part of the lexical xs:QName. <br/>
	 * The input is assumed to be lexically valid.
	 * 
	 * @param qualifiedName
	 *            The lexical xs:QName.
	 */
	public static String getLocalName(final String qualifiedName)
	{
		return qualifiedName.substring(qualifiedName.indexOf(":") + 1);
	}

	/**
	 * Return the prefix part of the lexical xs:QName. If there is no colon separator, returns the empty string. <br/>
	 * The input is assumed to be lexically valid.
	 * 
	 * @param qualifiedName
	 *            The lexical xs:QName.
	 */
	public static String getPrefix(final String qualifiedName)
	{
		final int index = qualifiedName.indexOf(':');
		if (index == -1)
		{
			return XMLConstants.DEFAULT_NS_PREFIX;
		}
		else
		{
			return qualifiedName.substring(0, index);
		}
	}

	private static boolean isWhiteSpace(final String strval)
	{
		if (null != strval)
		{
			final int n = strval.length();

			for (int i = 0; i < n; i++)
			{
				final char ch = strval.charAt(i);

				// The follwing pattern is denormalized for speed.
				if ((ch == 0x20) || (ch == 0x09) || (ch == 0xD) || (ch == 0xA))
				{
					// Try the next one, all must be whiteSpace.
				}
				else
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * (xs:annotation?, xs:simpleType)
	 */
	private static CmTable<String> makeAttributeTable()
	{
		final CmTable<String> table = new CmTable<String>();

		final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
		ZERO.put(LN_ANNOTATION, 1);
		ZERO.put(LN_SIMPLE_TYPE, CmTable.END);
		ZERO.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
		ONE.put(LN_SIMPLE_TYPE, CmTable.END);
		ONE.put(EPSILON, CmTable.END);

		table.put(0, ZERO);
		table.put(1, ONE);

		return table;
	}

	private static CmTable<String> makeComplexContentTable()
	{
		final CmTable<String> table = new CmTable<String>();

		final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
		ZERO.put(LN_ANNOTATION, 1);
		ZERO.put(LN_RESTRICTION, CmTable.END);
		ZERO.put(LN_EXTENSION, CmTable.END);

		final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
		ONE.put(LN_RESTRICTION, CmTable.END);
		ONE.put(LN_EXTENSION, CmTable.END);

		table.put(0, ZERO);
		table.put(1, ONE);

		return table;
	}

	private static CmTable<String> makeComplexTypeTable()
	{
		final CmTable<String> table = new CmTable<String>();

		final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
		ZERO.put(LN_ANNOTATION, 1);
		ZERO.put(LN_SIMPLE_CONTENT, CmTable.END);
		ZERO.put(LN_COMPLEX_CONTENT, CmTable.END);
		ZERO.put(LN_GROUP, 2);
		ZERO.put(LN_ALL, 2);
		ZERO.put(LN_CHOICE, 2);
		ZERO.put(LN_SEQUENCE, 2);
		ZERO.put(LN_ATTRIBUTE, 2);
		ZERO.put(LN_ATTRIBUTE_GROUP, 2);
		ZERO.put(LN_ANY_ATTRIBUTE, CmTable.END);
		ZERO.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
		ONE.put(LN_SIMPLE_CONTENT, CmTable.END);
		ONE.put(LN_COMPLEX_CONTENT, CmTable.END);
		ONE.put(LN_GROUP, 2);
		ONE.put(LN_ALL, 2);
		ONE.put(LN_CHOICE, 2);
		ONE.put(LN_SEQUENCE, 2);
		ONE.put(LN_ATTRIBUTE, 2);
		ONE.put(LN_ATTRIBUTE, 2);
		ONE.put(LN_ATTRIBUTE_GROUP, 2);
		ONE.put(LN_ANY_ATTRIBUTE, CmTable.END);
		ONE.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> TWO = new HashMap<String, Integer>();
		TWO.put(LN_ATTRIBUTE, 2);
		TWO.put(LN_ATTRIBUTE_GROUP, 2);
		TWO.put(LN_ANY_ATTRIBUTE, CmTable.END);
		TWO.put(EPSILON, CmTable.END);

		table.put(0, ZERO);
		table.put(1, ONE);
		table.put(2, TWO);

		return table;
	}

	private static CmTable<String> makeExtensionInComplexContentTable()
	{
		final CmTable<String> table = new CmTable<String>();

		final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
		ZERO.put(LN_ANNOTATION, 1);
		ZERO.put(LN_GROUP, 2);
		ZERO.put(LN_ALL, 2);
		ZERO.put(LN_CHOICE, 2);
		ZERO.put(LN_SEQUENCE, 2);
		ZERO.put(LN_ATTRIBUTE, 3);
		ZERO.put(LN_ATTRIBUTE_GROUP, 3);
		ZERO.put(LN_ANY_ATTRIBUTE, CmTable.END);
		ZERO.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
		ONE.put(LN_GROUP, 2);
		ONE.put(LN_ALL, 2);
		ONE.put(LN_CHOICE, 2);
		ONE.put(LN_SEQUENCE, 2);
		ONE.put(LN_ATTRIBUTE, 3);
		ONE.put(LN_ATTRIBUTE_GROUP, 3);
		ONE.put(LN_ANY_ATTRIBUTE, CmTable.END);
		ONE.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> TWO = new HashMap<String, Integer>();
		TWO.put(LN_ATTRIBUTE, 3);
		TWO.put(LN_ATTRIBUTE_GROUP, 3);
		TWO.put(LN_ANY_ATTRIBUTE, CmTable.END);
		TWO.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> THREE = new HashMap<String, Integer>();
		THREE.put(LN_ATTRIBUTE, 3);
		THREE.put(LN_ATTRIBUTE_GROUP, 3);
		THREE.put(LN_ANY_ATTRIBUTE, CmTable.END);
		THREE.put(EPSILON, CmTable.END);

		table.put(0, ZERO);
		table.put(1, ONE);
		table.put(2, TWO);
		table.put(3, THREE);

		return table;
	}

	private static CmTable<String> makeExtensionInSimpleContentTable()
	{
		final CmTable<String> table = new CmTable<String>();

		final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
		ZERO.put(LN_ANNOTATION, 1);
		ZERO.put(LN_ATTRIBUTE, 1);
		ZERO.put(LN_ATTRIBUTE_GROUP, 1);
		ZERO.put(LN_ANY_ATTRIBUTE, CmTable.END);
		ZERO.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
		ONE.put(LN_ATTRIBUTE, 1);
		ONE.put(LN_ATTRIBUTE_GROUP, 1);
		ONE.put(LN_ANY_ATTRIBUTE, CmTable.END);
		ONE.put(EPSILON, CmTable.END);

		table.put(0, ZERO);
		table.put(1, ONE);

		return table;
	}

	private static CmTable<String> makeRestrictionInComplexContentTable()
	{
		final CmTable<String> table = new CmTable<String>();

		final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
		ZERO.put(LN_ANNOTATION, 1);
		ZERO.put(LN_GROUP, 2);
		ZERO.put(LN_ALL, 2);
		ZERO.put(LN_CHOICE, 2);
		ZERO.put(LN_SEQUENCE, 2);
		ZERO.put(LN_ATTRIBUTE, 3);
		ZERO.put(LN_ATTRIBUTE_GROUP, 3);
		ZERO.put(LN_ANY_ATTRIBUTE, CmTable.END);
		ZERO.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
		ONE.put(LN_GROUP, 2);
		ONE.put(LN_ALL, 2);
		ONE.put(LN_CHOICE, 2);
		ONE.put(LN_SEQUENCE, 2);
		ONE.put(LN_ATTRIBUTE, 3);
		ONE.put(LN_ATTRIBUTE_GROUP, 3);
		ONE.put(LN_ANY_ATTRIBUTE, CmTable.END);
		ONE.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> TWO = new HashMap<String, Integer>();
		TWO.put(LN_ATTRIBUTE, 3);
		TWO.put(LN_ATTRIBUTE_GROUP, 3);
		TWO.put(LN_ANY_ATTRIBUTE, CmTable.END);
		TWO.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> THREE = new HashMap<String, Integer>();
		THREE.put(LN_ATTRIBUTE, 3);
		THREE.put(LN_ATTRIBUTE_GROUP, 3);
		THREE.put(LN_ANY_ATTRIBUTE, CmTable.END);
		THREE.put(EPSILON, CmTable.END);

		table.put(0, ZERO);
		table.put(1, ONE);
		table.put(2, TWO);
		table.put(3, THREE);

		return table;
	}

	private static CmTable<String> makeRestrictionInSimpleContentTable()
	{
		final CmTable<String> table = new CmTable<String>();

		final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
		ZERO.put(LN_ANNOTATION, 1);
		ZERO.put(LN_SIMPLE_TYPE, 2);
		ZERO.put(LN_MIN_EXCLUSIVE, 2);
		ZERO.put(LN_MIN_INCLUSIVE, 2);
		ZERO.put(LN_MAX_EXCLUSIVE, 2);
		ZERO.put(LN_MAX_INCLUSIVE, 2);
		ZERO.put(LN_TOTAL_DIGITS, 2);
		ZERO.put(LN_FRACTION_DIGITS, 2);
		ZERO.put(LN_LENGTH, 2);
		ZERO.put(LN_MIN_LENGTH, 2);
		ZERO.put(LN_MAX_LENGTH, 2);
		ZERO.put(LN_ENUMERATION, 2);
		ZERO.put(LN_WHITE_SPACE, 2);
		ZERO.put(LN_PATTERN, 2);
		ZERO.put(LN_ATTRIBUTE, 3);
		ZERO.put(LN_ATTRIBUTE_GROUP, 3);
		ZERO.put(LN_ANY_ATTRIBUTE, CmTable.END);
		ZERO.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
		ONE.put(LN_SIMPLE_TYPE, 2);
		ONE.put(LN_MIN_EXCLUSIVE, 2);
		ONE.put(LN_MIN_INCLUSIVE, 2);
		ONE.put(LN_MAX_EXCLUSIVE, 2);
		ONE.put(LN_MAX_INCLUSIVE, 2);
		ONE.put(LN_TOTAL_DIGITS, 2);
		ONE.put(LN_FRACTION_DIGITS, 2);
		ONE.put(LN_LENGTH, 2);
		ONE.put(LN_MIN_LENGTH, 2);
		ONE.put(LN_MAX_LENGTH, 2);
		ONE.put(LN_ENUMERATION, 2);
		ONE.put(LN_WHITE_SPACE, 2);
		ONE.put(LN_PATTERN, 2);
		ONE.put(LN_ATTRIBUTE, 3);
		ONE.put(LN_ATTRIBUTE_GROUP, 3);
		ONE.put(LN_ANY_ATTRIBUTE, CmTable.END);
		ONE.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> TWO = new HashMap<String, Integer>();
		TWO.put(LN_MIN_EXCLUSIVE, 2);
		TWO.put(LN_MIN_INCLUSIVE, 2);
		TWO.put(LN_MAX_EXCLUSIVE, 2);
		TWO.put(LN_MAX_INCLUSIVE, 2);
		TWO.put(LN_TOTAL_DIGITS, 2);
		TWO.put(LN_FRACTION_DIGITS, 2);
		TWO.put(LN_LENGTH, 2);
		TWO.put(LN_MIN_LENGTH, 2);
		TWO.put(LN_MAX_LENGTH, 2);
		TWO.put(LN_ENUMERATION, 2);
		TWO.put(LN_WHITE_SPACE, 2);
		TWO.put(LN_PATTERN, 2);
		TWO.put(LN_ATTRIBUTE, 3);
		TWO.put(LN_ATTRIBUTE_GROUP, 3);
		TWO.put(LN_ANY_ATTRIBUTE, CmTable.END);
		TWO.put(EPSILON, CmTable.END);

		final HashMap<String, Integer> THREE = new HashMap<String, Integer>();
		THREE.put(LN_ATTRIBUTE, 3);
		THREE.put(LN_ATTRIBUTE_GROUP, 3);
		THREE.put(LN_ANY_ATTRIBUTE, CmTable.END);
		THREE.put(EPSILON, CmTable.END);

		table.put(0, ZERO);
		table.put(1, ONE);
		table.put(2, TWO);
		table.put(3, THREE);

		return table;
	}

	private static CmTable<String> makeSimpleContentTable()
	{
		final CmTable<String> table = new CmTable<String>();

		final HashMap<String, Integer> ZERO = new HashMap<String, Integer>();
		ZERO.put(LN_ANNOTATION, 1);
		ZERO.put(LN_RESTRICTION, CmTable.END);
		ZERO.put(LN_EXTENSION, CmTable.END);

		final HashMap<String, Integer> ONE = new HashMap<String, Integer>();
		ONE.put(LN_RESTRICTION, CmTable.END);
		ONE.put(LN_EXTENSION, CmTable.END);

		table.put(0, ZERO);
		table.put(1, ONE);

		return table;
	}

	private final XMLTypeRef<A> ANY_SIMPLE_TYPE;
	private final XMLTypeRef<A> ANY_TYPE;

	private final AtomBridge<A> atomBridge;

	private final SmComponentProvider<A> bootstrap;

	private final SmCatalog m_catalog;

	private final SmExceptionHandler m_errors;

	private final SmPrefixMappingSupport m_pms;

	private final boolean m_processRepeatedNamespaces;

	private final SmResolver m_resolver;

	/**
	 * Factory is required for
	 */
	private final SmRestrictedXPathParser m_xp;

	private final NameSource nameBridge;

	public XMLSchemaParser(final AtomBridge<A> atomBridge, final SmComponentProvider<A> bootstrap, final SmExceptionHandler errors, final SmCatalog catalog, final SmResolver resolver, boolean processRepeatedNamespaces)
	{
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
		this.nameBridge = atomBridge.getNameBridge();
		this.bootstrap = PreCondition.assertArgumentNotNull(bootstrap, "bootstrap");
		this.m_pms = new SmPrefixMappingSupport(atomBridge.getNameBridge());
		this.m_errors = PreCondition.assertArgumentNotNull(errors, "errors");
		this.m_catalog = catalog;
		this.m_resolver = resolver;
		this.m_processRepeatedNamespaces = processRepeatedNamespaces;
		this.m_xp = new RestrictedXPathParser<A>(bootstrap, atomBridge);
		ANY_SIMPLE_TYPE = new XMLTypeRef<A>(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anySimpleType"));
		ANY_TYPE = new XMLTypeRef<A>(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyType"));
	}

	private void annotationContent(final String contextName, final XMLStreamReader reader, final XMLSchemaModule<A> module) throws XMLStreamException, SmAbortException
	{
		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, contextName, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				{
					final String text = reader.getText();
					if (!isWhiteSpace(text))
					{
						reportUnexpectedNonWhiteSpaceTextInElementOnlyContent(contextName, text, reader.getLocation());
					}
				}
				break;
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	private void annotationTag(final XMLStreamReader reader, final XMLSchemaModule<A> module) throws XMLStreamException, SmAbortException
	{
		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if ("documentation".equals(localName))
							{
								documentationTag(reader);
							}
							else if ("appinfo".equals(localName))
							{
								appinfoTag(reader);
							}
							else
							{
								reportUnexpectedElementTag(LN_ANNOTATION, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	/**
	 * xs:anyAttribute.
	 */
	private XMLWildcard<A> anyAttributeTag(final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule<A> module) throws XMLStreamException, SmAbortException
	{
		SmProcessContentsMode processContents = SmProcessContentsMode.Strict;
		SmNamespaceConstraint namespaceConstraint = SmNamespaceConstraint.Any(nameSource);

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_NAMESPACE.equals(localName))
				{
					try
					{
						namespaceConstraint = namespaces(reader.getAttributeValue(i), targetNamespace);
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_PROCESS_CONTENTS.equals(localName))
				{
					try
					{
						processContents = processContents(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ANY_ATTRIBUTE, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_ANY_ATTRIBUTE, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return new XMLWildcard<A>(processContents, namespaceConstraint);
	}

	/**
	 * xs:any
	 */
	private XMLParticle<A> anyElementTag(final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule<A> module) throws XMLStreamException, SmAbortException
	{
		SmProcessContentsMode processContents = SmProcessContentsMode.Strict;
		SmNamespaceConstraint namespaceConstraint = SmNamespaceConstraint.Any(nameSource);

		BigInteger minOccurs = BigInteger.ONE;
		BigInteger maxOccurs = BigInteger.ONE;

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_MAX_OCCURS.equals(localName))
				{
					maxOccurs = maxOccurs(reader.getAttributeValue(i), true, reader.getLocation(), reader.getName());
				}
				else if (LN_MIN_OCCURS.equals(localName))
				{
					minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
				}
				else if (LN_NAMESPACE.equals(localName))
				{
					try
					{
						namespaceConstraint = namespaces(reader.getAttributeValue(i), targetNamespace);
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_PROCESS_CONTENTS.equals(localName))
				{
					try
					{
						processContents = processContents(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ANY, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_ANY, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		final XMLWildcard<A> wildcard = new XMLWildcard<A>(processContents, namespaceConstraint);
		return new XMLParticleWithWildcardTerm<A>(minOccurs, maxOccurs, wildcard, getFrozenLocation(reader.getLocation()));
	}

	private URI anyURI(final String initialValue) throws SmSimpleTypeException
	{
		final SmSimpleType<A> atomicType = bootstrap.getAtomicType(SmNativeType.ANY_URI);
		try
		{
			final List<A> value = atomicType.validate(initialValue);
			if (value.size() > 0)
			{
				return atomBridge.getURI(value.get(0));
			}
			else
			{
				return null;
			}
		}
		catch (final SmDatatypeException e)
		{
			throw new SmSimpleTypeException(initialValue, atomicType, e);
		}
	}

	private void appinfoTag(final XMLStreamReader reader) throws XMLStreamException, SmAbortException
	{
		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_SOURCE.equals(localName))
				{
					try
					{
						/* final URI source = */anyURI(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						skipTag(reader);
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	private void assertRefAbsent(final QName ref, final Location location) throws SmAbortException
	{
		if (null != ref)
		{
			m_errors.error(new SmElementRefPresentException(getFrozenLocation(location)));
		}
	}

	/**
	 * xs:attributeGroup (reference)
	 */
	private XMLAttributeGroup<A> attribGroupRefTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final String targetNamespace) throws XMLStreamException, XMLAttributeGroupException, SmAbortException
	{
		final QName ref;
		try
		{
			ref = requiredQName(LN_REF, module.isChameleon(), targetNamespace, reader);
		}
		catch (final SmComplexTypeException e)
		{
			skipTag(reader);
			throw new XMLAttributeGroupException(e);
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_REF.equals(localName))
				{
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		final XMLAttributeGroup<A> attributeGroup;
		try
		{
			attributeGroup = cache.dereferenceAttributeGroup(ref, reader.getLocation(), false);
		}
		catch (final SmException e)
		{
			skipTag(reader);
			throw new XMLAttributeGroupException(e);
		}
		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ATTRIBUTE_GROUP, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_ATTRIBUTE_GROUP, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
					// Ignore.
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}

		return attributeGroup;
	}

	/**
	 * xs:attributeGroup (global definition)
	 */
	private XMLAttributeGroup<A> attribGroupTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLAttributeGroupException, SmAbortException
	{
		final XMLAttributeGroup<A> attributeGroup;
		final LinkedList<XMLAttributeUse<A>> savedLocalAttributes;
		final LinkedList<XMLAttributeGroup<A>> savedReferencedAttributeGroups;
		final XMLWildcard<A> savedWildcard;
		final HashSet<QName> savedProhibited;
		if (!redefine)
		{
			try
			{
				attributeGroup = cache.registerAttributeGroup(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
			}
			catch (final SmException e)
			{
				skipTag(reader);
				throw new XMLAttributeGroupException(e);
			}
			savedLocalAttributes = null;
			savedReferencedAttributeGroups = null;
			savedWildcard = null;
			savedProhibited = null;
		}
		else
		{
			try
			{
				attributeGroup = cache.dereferenceAttributeGroup(requiredNCName(LN_NAME, targetNamespace, reader), reader.getLocation(), true/*
																																			 * must
																																			 * exist
																																			 */);
			}
			catch (final SmException e)
			{
				skipTag(reader);
				throw new XMLAttributeGroupException(e);
			}

			savedLocalAttributes = new LinkedList<XMLAttributeUse<A>>(attributeGroup.getAttributeUses());
			attributeGroup.getAttributeUses().clear();

			savedReferencedAttributeGroups = new LinkedList<XMLAttributeGroup<A>>(attributeGroup.getGroups());
			attributeGroup.getGroups().clear();

			savedProhibited = new HashSet<QName>(attributeGroup.prohibited);
			attributeGroup.prohibited.clear();

			savedWildcard = attributeGroup.wildcard;
			attributeGroup.wildcard = null;
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_NAME.equals(localName))
				{
					// Already known.
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ATTRIBUTE_GROUP, reader.getLocation());
								annotationTag(reader, module);
							}
							else if (LN_ATTRIBUTE.equals(localName))
							{
								attributeLocalTag(reader, cache, module, redefine, targetNamespace, attributeGroup.getAttributeUses(), attributeGroup.prohibited, new XMLScope<A>(attributeGroup));
								firstElement = false;
							}
							else if (LN_ATTRIBUTE_GROUP.equals(localName))
							{
								final XMLAttributeGroup<A> ag = attribGroupRefTag(reader, cache, module, targetNamespace);
								if (!redefine)
								{
									attributeGroup.getGroups().add(ag);
								}
								else
								{
									if (attributeGroup == ag)
									{
										for (final XMLAttributeUse<A> attributeUse : savedLocalAttributes)
										{
											attributeGroup.getAttributeUses().add(attributeUse);
										}
										for (final XMLAttributeGroup<A> reference : savedReferencedAttributeGroups)
										{
											attributeGroup.getGroups().add(reference);
										}
										for (final QName name : savedProhibited)
										{
											attributeGroup.prohibited.add(name);
										}
										attributeGroup.wildcard = savedWildcard;
									}
									else
									{
										attributeGroup.getGroups().add(ag);
									}
								}
								firstElement = false;
							}
							else if (LN_ANY_ATTRIBUTE.equals(localName))
							{
								attributeGroup.wildcard = anyAttributeTag(reader, targetNamespace, module);
								firstElement = false;
							}
							else
							{
								reportUnexpectedElementTag(LN_ATTRIBUTE_GROUP, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
					// Ignore.
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}

		if (!redefine)
		{
			return attributeGroup;
		}
		else
		{
			// This would be a copy of the original.
			return null;
		}
	}

	/**
	 * xs:attribute (reference or local definition)
	 */
	private void attributeLocalTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace, final LinkedList<XMLAttributeUse<A>> attributeUses, final HashSet<QName> prohibited, final XMLScope<A> scope) throws XMLStreamException, SmAbortException
	{
		String name = null;
		XMLTypeRef<A> type = null;
		final QName ref = referenceOptional(reader, LN_REF, module, targetNamespace);
		XMLCardinality use = XMLCardinality.OPTIONAL;
		boolean qualified = module.attributeQualified;
		XMLValueConstraint valueConstraint = null;
		A id = null;

		boolean seenForm = false;
		boolean seenDefault = false;

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_DEFAULT.equals(localName))
				{
					seenDefault = true;
					if (null == valueConstraint)
					{
						valueConstraint = new XMLValueConstraint(SmValueConstraint.Kind.Default, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
					}
					else
					{
						m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
					}
				}
				else if (LN_FIXED.equals(localName))
				{
					if (null == valueConstraint)
					{
						valueConstraint = new XMLValueConstraint(SmValueConstraint.Kind.Fixed, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
					}
					else
					{
						m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
					}
				}
				else if (LN_FORM.equals(localName))
				{
					seenForm = true;
					try
					{
						qualified = qualified(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_NAME.equals(localName))
				{
					try
					{
						name = name(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_REF.equals(localName))
				{
				}
				else if (LN_TYPE.equals(localName))
				{
					try
					{
						type = typeRef(reader.getAttributeValue(i), LN_TYPE, module.isChameleon(), targetNamespace, reader);
						ensureReferenceType(type.getName(), reader.getLocation(), false, cache);
					}
					catch (final SmComplexTypeException e)
					{
						m_errors.error(e);
					}
				}
				else if (LN_USE.equals(localName))
				{
					try
					{
						use = use(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		// If default and use are both present, use must have the actual value
		// optional.
		if (seenDefault)
		{
			if (use.getMinOccurs() != 0 || use.getMaxOccurs() != 1)
			{
				m_errors.error(new SmAttributeDefaultAndUseImpliesOptionalException(getFrozenLocation(reader.getLocation())));
			}
		}

		// The following test only applies to local attributes.
		if ((null != ref) && (seenForm || (null != type)))
		{
			m_errors.error(new SmAttributeRefPresentException(getFrozenLocation(reader.getLocation())));
		}

		final XMLAttribute<A> attribute;
		try
		{
			attribute = determineLocalAttribute(name, qualified, ref, cache, reader, targetNamespace, scope);
		}
		catch (final SmException e)
		{
			m_errors.error(e);
			skipTag(reader);
			return;
		}
		attribute.id = id;
		if (null != type)
		{
			attribute.typeRef = type;
		}

		final CmMachine<String> machine = new CmMachine<String>(attributeTable, EPSILON);
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (!machine.step(localName))
							{
								reportUnexpectedElementTag(LN_ATTRIBUTE, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
							else
							{
								if (LN_SIMPLE_TYPE.equals(localName))
								{
									if (null != ref)
									{
										m_errors.error(new SmAttributeRefPresentException(getFrozenLocation(reader.getLocation())));
									}
									if (null != type)
									{
										m_errors.error(new SrcAttributeTypeAndSimpleTypePresentException(getFrozenLocation(reader.getLocation())));
									}
									attribute.typeRef = simpleTypeLocalTag(new XMLScope<A>(attribute), cache, module, reader, redefine, targetNamespace);
								}
								else if (LN_ANNOTATION.equals(localName))
								{
									annotationTag(reader, module);
								}
								else
								{
									throw new AssertionError(reader.getName());
								}
							}
						}
						else
						{
							skipTag(reader);
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					if (!machine.end())
					{
						reportUnexpectedEnd(LN_ATTRIBUTE, reader.getLocation());
					}
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
					// Ignore.
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}

		final boolean forbidden = (use.getMaxOccurs() > 0);
		if (forbidden)
		{
			final boolean required = (use.getMinOccurs() > 0);
			final XMLAttributeUse<A> attributeUse = new XMLAttributeUse<A>(required, attribute, valueConstraint);

			attributeUses.add(attributeUse);
		}
		else
		{
			// The attribute use is forbidden.
			prohibited.add(attribute.getName());
		}
	}

	/**
	 * xs:attribute (global definition).
	 */
	private XMLAttribute<A> attributeTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final String targetNamespace) throws XMLStreamException, XMLAttributeException, SmAbortException
	{
		final XMLAttribute<A> attribute;
		try
		{
			attribute = cache.registerAttribute(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
		}
		catch (final SmException e)
		{
			skipTag(reader);
			throw new XMLAttributeException(e);
		}

		boolean missingTypeAttribute = true;
		// boolean seenDefault = false;

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_NAME.equals(localName))
				{
					// Already known.
				}
				else if (LN_TYPE.equals(localName))
				{
					missingTypeAttribute = false;
					try
					{
						attribute.typeRef = typeRef(reader.getAttributeValue(i), LN_TYPE, module.isChameleon(), targetNamespace, reader);
						ensureReferenceType(attribute.typeRef.getName(), reader.getLocation(), false, cache);
					}
					catch (final SmComplexTypeException e)
					{
						m_errors.error(e);
					}
				}
				else if (LN_DEFAULT.equals(localName))
				{
					// seenDefault = true;
					if (null == attribute.m_valueConstraint)
					{
						attribute.m_valueConstraint = new XMLValueConstraint(SmValueConstraint.Kind.Default, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
					}
					else
					{
						m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
					}
				}
				else if (LN_FIXED.equals(localName))
				{
					if (null == attribute.m_valueConstraint)
					{
						attribute.m_valueConstraint = new XMLValueConstraint(SmValueConstraint.Kind.Fixed, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
					}
					else
					{
						m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
					}
				}
				else if (LN_ID.equals(localName))
				{
					attribute.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_SIMPLE_TYPE.equals(localName))
							{
								if (missingTypeAttribute)
								{
									attribute.typeRef = simpleTypeLocalTag(new XMLScope<A>(attribute), cache, module, reader, false, targetNamespace);
								}
								else
								{
									m_errors.error(new SrcAttributeTypeAndSimpleTypePresentException(getFrozenLocation(reader.getLocation())));
								}
								firstElement = false;
							}
							else if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ATTRIBUTE, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_ATTRIBUTE, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
					// Ignore.
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}

		return attribute;
	}

	/**
	 * This function is common to extension and restriction of simple content.
	 */
	private QName baseTypeDefinitionInComplexContent(final XMLType<A> complexType, final SmDerivationMethod derivation, final XMLStreamReader reader, final boolean redefine, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final String targetNamespace) throws SmException, SmAbortException
	{
		final QName baseName = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
		ensureReferenceType(baseName, reader.getLocation(), redefine, cache);
		// The {base type definition} for the Complex Type is the type
		// definition resolved by the
		// actual value of the base attribute. This could be a forward reference
		// so we use the QName.
		complexType.setBase(new XMLTypeRef<A>(baseName), derivation);
		return baseName;
	}

	/**
	 * Used to ensure that a child xs:annotation occurs a maximumn number of once.
	 */
	private boolean checkAnnotationMaxOccursUnity(final boolean allowed, final String contextName, final Location location) throws SmAbortException
	{
		return checkWxsElementMaxOccursUnity(allowed, contextName, LN_ANNOTATION, location);
	}

	/**
	 * Validate the xs:ID attribute and return the value as a String. <br/>
	 * We assume that the name of the attribute is {@link #LN_ID} for reporting purposes.
	 * 
	 * @param attributeValue
	 *            The value of the xs:ID attribute.
	 * @param location
	 *            The stream location of the xs:ID attribute.
	 * @param elementName
	 *            The name of the element bearing the xs:ID attribute.
	 * @param module
	 *            The module being parsed - used to record the xs:ID values to ensure uniqueness.
	 * @return The xs:ID value as a String.
	 */
	private A checkID(final String attributeValue, final Location location, final QName elementName, final XMLSchemaModule<A> module) throws SmAbortException
	{
		try
		{
			return checkIDValue(attributeValue, location, module);
		}
		catch (final SmSimpleTypeException e)
		{
			reportAttributeUseError(elementName, new QName(LN_ID), location, e);
		}
		catch (final SmDuplicateIDException e)
		{
			m_errors.error(e);
		}
		// It's acceptable to return null because we aren't going to do anything
		// significant with id.
		return null;
	}

	private A checkIDValue(final String strval, final Location location, final XMLSchemaModule<A> module) throws SmSimpleTypeException, SmDuplicateIDException
	{
		PreCondition.assertArgumentNotNull(strval, LN_ID);

		final SmSimpleType<A> idType = bootstrap.getAtomicType(SmNativeType.ID);

		final List<A> value;
		try
		{
			value = idType.validate(strval);
		}
		catch (final SmDatatypeException cause)
		{
			throw new SmSimpleTypeException(strval, idType, cause);
		}
		if (value.size() > 0)
		{
			final A id = value.get(0);
			if (module.m_ids.contains(id))
			{
				throw new SmDuplicateIDException(id, new SrcFrozenLocation(location));
			}
			else
			{
				module.m_ids.add(id);
			}
			return id;
		}
		else
		{
			return null;
		}
	}

	private void checkPrefixBound(final String prefix, final String namespaceURI, final String initialValue) throws SmSimpleTypeException
	{
		if (!isBoundPrefix(prefix, namespaceURI))
		{
			final SrcPrefixNotFoundException cause = new SrcPrefixNotFoundException(prefix);
			final SmDatatypeException dte = new SmDatatypeException(initialValue, null, cause);
			throw new SmSimpleTypeException(initialValue, null, dte);
		}
	}

	/**
	 * Used to ensure that a particular child element occurs a maximum number of once.
	 */
	private boolean checkWxsElementMaxOccursUnity(final boolean missing, final String contextName, final String unexpectedName, final Location location) throws SmAbortException
	{
		if (!missing)
		{
			reportUnexpectedElementTag(contextName, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, unexpectedName), location);
		}
		return false;
	}

	/**
	 * xs:complexContent <br/>
	 * We don't return anything because this affects multiple aspects of the complex type.
	 */
	private void complexContentTag(final XMLType<A> complexType, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		// System.out.println(StripQualifiers.strip(getClass().getName()) +
		// ".complexContentTag(complexType=" + complexType + ", mixed=" + mixed
		// + ")");
		PreCondition.assertArgumentNotNull(complexType, LN_COMPLEX_TYPE);

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_MIXED.equals(localName))
				{
					try
					{
						if (trueOrFalse(reader.getAttributeValue(i)))
						{
							complexType.m_contentKind = XMLContentTypeKind.Mixed;
						}
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		final CmMachine<String> machine = new CmMachine<String>(complexContentTable, EPSILON);
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (!machine.step(localName))
							{
								reportUnexpectedElementTag(LN_COMPLEX_CONTENT, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
							else
							{
								if (LN_EXTENSION.equals(localName))
								{
									extensionInComplexContentTag(complexType, reader, cache, module, redefine, targetNamespace);
								}
								else if (LN_RESTRICTION.equals(localName))
								{
									restrictionInComplexContentTag(complexType, reader, cache, module, redefine, targetNamespace);
								}
								else if (LN_ANNOTATION.equals(localName))
								{
									annotationTag(reader, module);
								}
								else
								{
									throw new AssertionError(reader.getName());
								}
							}
						}
						else
						{
							skipTag(reader);
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					if (!machine.end())
					{
						reportUnexpectedEnd(LN_COMPLEX_CONTENT, reader.getLocation());
					}
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	/**
	 * This does not correspond to a specific tag. <br/>
	 * Used to parse the content of xs:complexType (global and local), but not the same as xs:complexContent.
	 */
	private void complexTypeContent(final XMLType<A> complexType, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final XMLStreamReader reader, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		final CmMachine<String> machine = new CmMachine<String>(complexTypeTable, EPSILON);
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (!machine.step(localName))
							{
								reportUnexpectedElementTag(LN_COMPLEX_TYPE, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
							else
							{
								if (LN_GROUP.equals(localName))
								{
									complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
									try
									{
										complexType.m_contentModel = groupParticleTag(new XMLScope<A>(complexType), reader, cache, module, redefine, targetNamespace);
									}
									catch (final XMLModelGroupUseException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_ALL.equals(localName))
								{
									complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
									try
									{
										complexType.m_contentModel = compositorOutsideGroupTag(SmModelGroup.SmCompositor.All, new XMLScope<A>(complexType), localName, reader, cache, module, redefine, targetNamespace);
									}
									catch (final XMLCompositorOutsideGroupException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_CHOICE.equals(localName))
								{
									complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
									try
									{
										complexType.m_contentModel = compositorOutsideGroupTag(SmModelGroup.SmCompositor.Choice, new XMLScope<A>(complexType), localName, reader, cache, module, redefine, targetNamespace);
									}
									catch (final XMLCompositorOutsideGroupException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_SEQUENCE.equals(localName))
								{
									complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
									try
									{
										complexType.m_contentModel = compositorOutsideGroupTag(SmModelGroup.SmCompositor.Sequence, new XMLScope<A>(complexType), localName, reader, cache, module, redefine, targetNamespace);
									}
									catch (final XMLCompositorOutsideGroupException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_ATTRIBUTE.equals(localName))
								{
									attributeLocalTag(reader, cache, module, redefine, targetNamespace, complexType.getAttributeUses(), complexType.prohibited, new XMLScope<A>(complexType));
								}
								else if (LN_ATTRIBUTE_GROUP.equals(localName))
								{
									try
									{
										complexType.getAttributeGroups().add(attribGroupRefTag(reader, cache, module, targetNamespace));
									}
									catch (final XMLAttributeGroupException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_ANY_ATTRIBUTE.equals(localName))
								{
									complexType.attributeWildcard = anyAttributeTag(reader, targetNamespace, module);
								}
								else if (LN_COMPLEX_CONTENT.equals(localName))
								{
									complexContentTag(complexType, reader, cache, module, redefine, targetNamespace);
								}
								else if (LN_SIMPLE_CONTENT.equals(localName))
								{
									simpleContentTag(complexType, reader, cache, module, redefine, targetNamespace);
								}
								else if (LN_ANNOTATION.equals(localName))
								{
									annotationTag(reader, module);
								}
								else
								{
									throw new AssertionError(reader.getName());
								}
							}
						}
						else
						{
							skipTag(reader);
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					if (!machine.end())
					{
						reportUnexpectedEnd(LN_COMPLEX_TYPE, reader.getLocation());
					}
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
					// Ignore
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	/**
	 * xs:complexType (global definition)
	 */
	private XMLType<A> complexTypeGlobalTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLComplexTypeException, SmAbortException
	{
		final XMLType<A> complexType;
		if (!redefine)
		{
			try
			{
				complexType = cache.registerType(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
			}
			catch (final SmException e)
			{
				skipTag(reader);
				throw new XMLComplexTypeException(e);
			}

			complexType.setComplexFlag();
			complexType.setBase(ANY_TYPE, SmDerivationMethod.Restriction);
			complexType.getBlock().addAll(module.blockDefault);
		}
		else
		{
			try
			{
				complexType = cache.dereferenceType(requiredNCName(LN_NAME, targetNamespace, reader), reader.getLocation(), redefine);
			}
			catch (final SmException e)
			{
				skipTag(reader);
				throw new XMLComplexTypeException(e);
			}
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_NAME.equals(localName))
				{
					// Already known.
				}
				else if (LN_MIXED.equals(localName))
				{
					try
					{
						if (trueOrFalse(reader.getAttributeValue(i)))
						{
							complexType.m_contentKind = XMLContentTypeKind.Mixed;
						}
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ABSTRACT.equals(localName))
				{
					try
					{
						complexType.setAbstractFlag(trueOrFalse(reader.getAttributeValue(i)));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_BLOCK.equals(localName))
				{
					try
					{
						control(reader.getAttributeValue(i), EnumSet.of(SmDerivationMethod.Extension, SmDerivationMethod.Restriction), complexType.getBlock());
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_FINAL.equals(localName))
				{
					try
					{
						control(reader.getAttributeValue(i), EnumSet.of(SmDerivationMethod.Extension, SmDerivationMethod.Restriction), complexType.getFinal());
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		complexTypeContent(complexType, cache, module, reader, redefine, targetNamespace);

		if (!redefine)
		{
			return complexType;
		}
		else
		{
			// In theory, we might return a copy of the original.
			return null;
		}
	}

	/**
	 * xs:complexType (local definition)
	 */
	private XMLTypeRef<A> complexTypeLocalTag(final XMLScope<A> scope, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		final XMLType<A> complexType = cache.registerAnonymousType(scope, getFrozenLocation(reader.getLocation()));
		complexType.setComplexFlag();
		complexType.setBase(ANY_TYPE, SmDerivationMethod.Restriction);

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_MIXED.equals(localName))
				{
					try
					{
						if (trueOrFalse(reader.getAttributeValue(i)))
						{
							complexType.m_contentKind = XMLContentTypeKind.Mixed;
						}
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		complexTypeContent(complexType, cache, module, reader, redefine, targetNamespace);

		return new XMLTypeRef<A>(complexType);
	}

	/**
	 * xs:sequence, xs:choice or xs:all (outside a group)
	 */
	private XMLParticleWithModelGroupTerm<A> compositorOutsideGroupTag(final SmModelGroup.SmCompositor compositor, final XMLScope<A> compositorScope, final String contextName, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLCompositorOutsideGroupException, SmAbortException
	{
		final XMLModelGroup<A> group = new XMLModelGroup<A>(compositor, compositorScope, getFrozenLocation(reader.getLocation()));

		BigInteger minOccurs = BigInteger.ONE;
		BigInteger maxOccurs = BigInteger.ONE;

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_MAX_OCCURS.equals(localName))
				{
					switch (compositor)
					{
						case All:
						{
							maxOccurs = maxOccurs(reader.getAttributeValue(i), false, reader.getLocation(), reader.getName());
						}
						break;
						case Choice:
						case Sequence:
						{
							maxOccurs = maxOccurs(reader.getAttributeValue(i), true, reader.getLocation(), reader.getName());
						}
						break;
						default:
						{
							throw new RuntimeException(compositor.name());
						}
					}
				}
				else if (LN_MIN_OCCURS.equals(localName))
				{
					switch (compositor)
					{
						case All:
						{
							minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
						}
						break;
						case Choice:
						case Sequence:
						{
							minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
						}
						break;
						default:
						{
							throw new AssertionError(compositor);
						}
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ELEMENT.equals(localName))
							{
								switch (compositor)
								{
									case All:
									{
										try
										{
											group.getParticles().add(PreCondition.assertNotNull(elementWithinAllTag(reader, cache, module, redefine, targetNamespace, new XMLScope<A>(group))));
										}
										catch (final XMLElementException e)
										{
											m_errors.error(e.getCause());
										}
									}
									break;
									case Sequence:
									case Choice:
									{
										try
										{
											group.getParticles().add(PreCondition.assertNotNull(elementLocalTag(reader, cache, module, redefine, targetNamespace, new XMLScope<A>(group))));
										}
										catch (final XMLElementException e)
										{
											m_errors.error(e.getCause());
										}
									}
									break;
									default:
									{
										throw new AssertionError(compositor);
									}
								}
								firstElement = false;
							}
							else if (LN_GROUP.equals(localName))
							{
								try
								{
									group.getParticles().add(PreCondition.assertNotNull(groupParticleTag(new XMLScope<A>(group), reader, cache, module, redefine, targetNamespace)));
								}
								catch (final XMLModelGroupUseException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_CHOICE.equals(localName))
							{
								group.getParticles().add(PreCondition.assertNotNull(compositorOutsideGroupTag(SmModelGroup.SmCompositor.Choice, new XMLScope<A>(group), contextName, reader, cache, module, redefine, targetNamespace)));
								firstElement = false;
							}
							else if (LN_SEQUENCE.equals(localName))
							{
								group.getParticles().add(PreCondition.assertNotNull(compositorOutsideGroupTag(SmModelGroup.SmCompositor.Sequence, new XMLScope<A>(group), contextName, reader, cache, module, redefine, targetNamespace)));
								firstElement = false;
							}
							else if (LN_ANY.equals(localName))
							{
								group.getParticles().add(PreCondition.assertNotNull(anyElementTag(reader, targetNamespace, module)));
								firstElement = false;
							}
							else if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, contextName, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return new XMLParticleWithModelGroupTerm<A>(minOccurs, maxOccurs, group, getFrozenLocation(reader.getLocation()));
	}

	/**
	 * xs:sequence, xs:choice or xs:all (within a group)
	 */
	private XMLModelGroup<A> compositorWithinGroupTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final XMLModelGroup<A> group, final String contextName, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		// If doing a redefine, make a copy of the original group so that we can
		// mutate the original.
		final XMLModelGroup<A> originalGroupCopy;
		if (redefine)
		{
			originalGroupCopy = new XMLModelGroup<A>(group.getName(), group.getScope(), group.getLocation());
			originalGroupCopy.setCompositor(group.getCompositor());
			if (group.getParticles().size() > 0)
			{
				for (final XMLParticle<A> particle : group.getParticles())
				{
					originalGroupCopy.getParticles().add(particle);
				}
				group.getParticles().clear();
			}
		}
		else
		{
			// This just keeps the syntax checker happy.
			originalGroupCopy = null;
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ELEMENT.equals(localName))
							{
								try
								{
									group.getParticles().add(PreCondition.assertNotNull(elementLocalTag(reader, cache, module, redefine, targetNamespace, new XMLScope<A>(group))));
								}
								catch (final XMLElementException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_GROUP.equals(localName))
							{
								switch (group.getCompositor())
								{
									case Choice:
									case Sequence:
									{
										try
										{
											final XMLParticleWithModelGroupTerm<A> groupParticle = groupParticleTag(new XMLScope<A>(group), reader, cache, module, redefine, targetNamespace);
											if (!redefine)
											{
												group.getParticles().add(PreCondition.assertNotNull(groupParticle));
											}
											else
											{
												final XMLModelGroup<A> g = groupParticle.getTerm();
												if (group == g)
												{
													if (originalGroupCopy.getParticles().size() > 0)
													{
														for (final XMLParticle<A> particle : originalGroupCopy.getParticles())
														{
															group.getParticles().add(PreCondition.assertNotNull(particle));
														}
													}
												}
												else
												{
													group.getParticles().add(PreCondition.assertNotNull(groupParticle));
												}
											}
										}
										catch (final XMLModelGroupUseException e)
										{
											m_errors.error(e.getCause());
										}
									}
									break;
									case All:
									{
										reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
										skipTag(reader);
									}
									break;
									default:
									{
										throw new AssertionError(group.getCompositor());
									}
								}
								firstElement = false;
							}
							else if (LN_CHOICE.equals(localName))
							{
								switch (group.getCompositor())
								{
									case Choice:
									case Sequence:
									{
										try
										{
											group.getParticles().add(PreCondition.assertNotNull(compositorOutsideGroupTag(SmModelGroup.SmCompositor.Choice, new XMLScope<A>(group), localName, reader, cache, module, redefine, targetNamespace)));
										}
										catch (final XMLCompositorOutsideGroupException e)
										{
											m_errors.error(e.getCause());
										}
									}
									break;
									case All:
									{
										reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
										skipTag(reader);
									}
									break;
									default:
									{
										throw new AssertionError(group.getCompositor());
									}
								}
								firstElement = false;
							}
							else if (LN_SEQUENCE.equals(localName))
							{
								switch (group.getCompositor())
								{
									case Choice:
									case Sequence:
									{
										try
										{
											group.getParticles().add(PreCondition.assertNotNull(compositorOutsideGroupTag(SmModelGroup.SmCompositor.Sequence, new XMLScope<A>(group), localName, reader, cache, module, redefine, targetNamespace)));
										}
										catch (final XMLCompositorOutsideGroupException e)
										{
											m_errors.error(e.getCause());
										}
									}
									break;
									case All:
									{
										reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
										skipTag(reader);
									}
									break;
									default:
									{
										throw new AssertionError(group.getCompositor());
									}
								}
								firstElement = false;
							}
							else if (LN_ANY.equals(localName))
							{
								switch (group.getCompositor())
								{
									case Choice:
									case Sequence:
									{
										group.getParticles().add(PreCondition.assertNotNull(anyElementTag(reader, targetNamespace, module)));
									}
									break;
									case All:
									{
										reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
										skipTag(reader);
									}
									break;
									default:
									{
										throw new AssertionError(group.getCompositor());
									}
								}
								firstElement = false;
							}
							else if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, contextName, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return group;
	}

	private String conditionNamespaceURI(final String namespaceURI)
	{
		return (null != namespaceURI) ? namespaceURI : XMLConstants.NULL_NS_URI;
	}

	/**
	 * Use to parse the LN_BLOCK and LN_FINAL attributes that control substitution and derivation.
	 */
	private void control(final String strval, final EnumSet<SmDerivationMethod> allValue, final EnumSet<SmDerivationMethod> resultSet) throws SmSimpleTypeException
	{
		resultSet.clear();

		if (strval.equals("#all"))
		{
			resultSet.addAll(allValue);
		}
		else
		{
			final StringTokenizer tokenizer = new StringTokenizer(strval);
			while (tokenizer.hasMoreTokens())
			{
				final String token = tokenizer.nextToken();
				if (token.equals("extension"))
				{
					if (allValue.contains(SmDerivationMethod.Extension))
					{
						resultSet.add(SmDerivationMethod.Extension);
					}
					else
					{
						final SmDatatypeException cause = new SmDatatypeException(token, null);
						throw new SmSimpleTypeException(strval, null, cause);
					}
				}
				else if (token.equals("restriction"))
				{
					if (allValue.contains(SmDerivationMethod.Restriction))
					{
						resultSet.add(SmDerivationMethod.Restriction);
					}
					else
					{
						final SmDatatypeException cause = new SmDatatypeException(token, null);
						throw new SmSimpleTypeException(strval, null, cause);
					}
				}
				else if (token.equals("substitution"))
				{
					if (allValue.contains(SmDerivationMethod.Substitution))
					{
						resultSet.add(SmDerivationMethod.Substitution);
					}
					else
					{
						final SmDatatypeException cause = new SmDatatypeException(token, null);
						throw new SmSimpleTypeException(strval, null, cause);
					}
				}
				else if (token.equals("union"))
				{
					if (allValue.contains(SmDerivationMethod.Union))
					{
						resultSet.add(SmDerivationMethod.Union);
					}
					else
					{
						final SmDatatypeException cause = new SmDatatypeException(token, null);
						throw new SmSimpleTypeException(strval, null, cause);
					}
				}
				else if (token.equals("list"))
				{
					if (allValue.contains(SmDerivationMethod.List))
					{
						resultSet.add(SmDerivationMethod.List);
					}
					else
					{
						final SmDatatypeException cause = new SmDatatypeException(token, null);
						throw new SmSimpleTypeException(strval, null, cause);
					}
				}
				else
				{
					final SmSimpleType<A> atomicType = bootstrap.getAtomicType(SmNativeType.UNTYPED_ATOMIC);
					final SmDatatypeException cause = new SmDatatypeException(token, atomicType);
					throw new SmSimpleTypeException(strval, atomicType, cause);
				}
			}
		}
	}

	/**
	 * Copies prefix mappings from the parser to the prefix mapping stack. <br/>
	 * This should be called for all elements immediately after the stack has been pushed. The stack should be popped in
	 * the
	 */
	private void copyNamespaces(final XMLStreamReader parser, final SmPrefixMappingSupport pms)
	{
		final int namespaceCount = parser.getNamespaceCount();
		for (int i = 0; i < namespaceCount; i++)
		{
			String prefix = parser.getNamespacePrefix(i);
			String uri = parser.getNamespaceURI(i);
			// Normalization required to map StAX to javax.
			prefix = (null == prefix) ? XMLConstants.DEFAULT_NS_PREFIX : prefix;
			if (null != uri)
			{
				m_pms.declarePrefix(prefix, uri);
			}
			else
			{
				m_pms.declarePrefix(prefix, nameBridge.empty());
			}
		}
	}

	private XMLAttribute<A> determineLocalAttribute(final String name, final boolean qualified, final QName ref, final XMLSchemaCache<A> cache, final XMLStreamReader parser, final String targetNamespace, final XMLScope<A> scope) throws SmException
	{
		if (null != name)
		{
			if (null == ref)
			{
				final XMLAttribute<A> attribute;
				if (qualified)
				{
					attribute = new XMLAttribute<A>(resolveUsingTargetNamespace(name, targetNamespace, parser.getNamespaceContext()), scope, ANY_SIMPLE_TYPE, getFrozenLocation(parser.getLocation()));
				}
				else
				{
					attribute = new XMLAttribute<A>(new QName(nameBridge.empty(), name), scope, ANY_SIMPLE_TYPE, getFrozenLocation(parser.getLocation()));
				}
				return attribute;
			}
			else
			{
				throw new SmAttributeRefXorNameException(getFrozenLocation(parser.getLocation()));
			}
		}
		else
		{
			if (null != ref)
			{
				return cache.dereferenceAttribute(ref, parser.getLocation());
			}
			else
			{
				throw new SmAttributeRefXorNameException(getFrozenLocation(parser.getLocation()));
			}
		}
	}

	/**
	 * Determines whether the local element is a local definition or a reference. <br/>
	 * Imposes the constraint that one of the ref and name must be present, but not both.
	 */
	private XMLElement<A> determineLocalElement(final String name, final boolean qualified, final XMLTypeRef<A> typeRef, final QName ref, final XMLSchemaCache<A> cache, final XMLStreamReader parser, final String targetNamespace, final XMLScope<A> scope) throws SmException
	{
		if ((null != name) && (null == ref))
		{
			final XMLElement<A> element;
			if (qualified)
			{
				final QName ename = resolveUsingTargetNamespace(name, targetNamespace, parser.getNamespaceContext());
				element = new XMLElement<A>(ename, scope, ANY_TYPE, getFrozenLocation(parser.getLocation()));
			}
			else
			{
				final QName ename = new QName(nameBridge.empty(), name);
				element = new XMLElement<A>(ename, scope, ANY_TYPE, getFrozenLocation(parser.getLocation()));
			}
			if (null != typeRef)
			{
				element.typeRef = typeRef;
			}
			return element;
		}
		else if ((null != ref) && (name == null))
		{
			return cache.dereferenceElement(ref, parser.getLocation());
		}
		else
		{
			throw new SmElementRefXorNameException(getFrozenLocation(parser.getLocation()));
		}
	}

	private void documentationTag(final XMLStreamReader reader) throws XMLStreamException, SmAbortException
	{
		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_SOURCE.equals(localName))
				{
					try
					{
						/* final URI source = */anyURI(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (XMLConstants.XML_NS_URI.equals(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if ("lang".equals(localName))
				{
					try
					{
						/* final String language = */lang(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInXmlNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						skipTag(reader);
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	private void elementContent(final XMLElement<A> element, final QName ref, final XMLSchemaModule<A> module, final XMLSchemaCache<A> cache, final XMLStreamReader reader, final boolean redefine, final String targetNamespace, final boolean seenType) throws XMLStreamException, SmAbortException
	{
		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_COMPLEX_TYPE.equals(localName))
							{
								assertRefAbsent(ref, reader.getLocation());
								if (seenType)
								{
									m_errors.error(new SmElementSimpleTypeXorComplexTypeException(getFrozenLocation(reader.getLocation())));
								}
								element.typeRef = complexTypeLocalTag(new XMLScope<A>(element), reader, cache, module, redefine, targetNamespace);
								firstElement = false;
							}
							else if (LN_SIMPLE_TYPE.equals(localName))
							{
								assertRefAbsent(ref, reader.getLocation());
								if (seenType)
								{
									m_errors.error(new SmElementSimpleTypeXorComplexTypeException(getFrozenLocation(reader.getLocation())));
								}
								element.typeRef = simpleTypeLocalTag(new XMLScope<A>(element), cache, module, reader, redefine, targetNamespace);
								firstElement = false;
							}
							else if (LN_UNIQUE.equals(localName))
							{
								assertRefAbsent(ref, reader.getLocation());
								try
								{
									element.getIdentityConstraints().add(uniqueTag(cache, reader, targetNamespace, module));
								}
								catch (final XMLIdentityConstraintException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_KEY.equals(localName))
							{
								assertRefAbsent(ref, reader.getLocation());
								try
								{
									element.getIdentityConstraints().add(keyTag(cache, reader, targetNamespace, module));
								}
								catch (final XMLIdentityConstraintException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_KEYREF.equals(localName))
							{
								assertRefAbsent(ref, reader.getLocation());
								try
								{
									element.getIdentityConstraints().add(keyrefTag(cache, reader, targetNamespace, module));
								}
								catch (final XMLIdentityConstraintException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ELEMENT, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_ELEMENT, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
					// Ignore.
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	/**
	 * xs:element (reference or local definition)
	 */
	private XMLParticle<A> elementLocalTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace, final XMLScope<A> scope) throws XMLStreamException, XMLElementException, SmAbortException
	{
		String name = null;
		XMLTypeRef<A> typeRef = null;
		final QName ref = referenceOptional(reader, LN_REF, module, targetNamespace);
		BigInteger minOccurs = BigInteger.ONE;
		BigInteger maxOccurs = BigInteger.ONE;
		boolean nillable = false;
		final EnumSet<SmDerivationMethod> block = EnumSet.copyOf(module.blockDefault);
		boolean qualified = module.elementQualified;
		XMLValueConstraint valueConstraint = null;

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_BLOCK.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					try
					{
						control(reader.getAttributeValue(i), EnumSet.of(SmDerivationMethod.Extension, SmDerivationMethod.Restriction, SmDerivationMethod.Substitution), block);
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_DEFAULT.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					if (null == valueConstraint)
					{
						valueConstraint = new XMLValueConstraint(SmValueConstraint.Kind.Default, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
					}
					else
					{
						m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
					}
				}
				else if (LN_FIXED.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					if (null == valueConstraint)
					{
						valueConstraint = new XMLValueConstraint(SmValueConstraint.Kind.Fixed, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
					}
					else
					{
						m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
					}
				}
				else if (LN_FORM.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					try
					{
						qualified = qualified(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_MAX_OCCURS.equals(localName))
				{
					maxOccurs = maxOccurs(reader.getAttributeValue(i), true, reader.getLocation(), reader.getName());
				}
				else if (LN_MIN_OCCURS.equals(localName))
				{
					minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
				}
				else if (LN_NAME.equals(localName))
				{
					try
					{
						name = name(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_NILLABLE.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					try
					{
						nillable = trueOrFalse(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_REF.equals(localName))
				{
					// Already got it.
				}
				else if (LN_TYPE.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					try
					{
						typeRef = typeRef(reader.getAttributeValue(i), LN_TYPE, module.isChameleon(), targetNamespace, reader);
						ensureReferenceType(typeRef.getName(), reader.getLocation(), false, cache);
					}
					catch (final SmComplexTypeException e)
					{
						m_errors.error(e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		final XMLElement<A> element;
		try
		{
			element = determineLocalElement(name, qualified, typeRef, ref, cache, reader, targetNamespace, scope);
		}
		catch (final SmException e)
		{
			skipTag(reader);
			throw new XMLElementException(e);
		}

		element.setNillableFlag(nillable);
		element.getBlock().addAll(block);
		if (null == ref)
		{
			element.m_valueConstraint = valueConstraint;
			elementContent(element, ref, module, cache, reader, redefine, targetNamespace, (null != typeRef));
			return new XMLParticleWithElementTerm<A>(minOccurs, maxOccurs, element, null, getFrozenLocation(reader.getLocation()));
		}
		else
		{
			element.m_valueConstraint = null;
			elementContent(element, null, module, cache, reader, redefine, targetNamespace, (null != typeRef));
			return new XMLParticleWithElementTerm<A>(minOccurs, maxOccurs, element, valueConstraint, getFrozenLocation(reader.getLocation()));
		}
	}

	/**
	 * xs:element (global definition)
	 */
	private XMLElement<A> elementTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final String targetNamespace) throws XMLStreamException, XMLElementException, SmAbortException
	{
		final XMLElement<A> element;
		try
		{
			element = cache.registerElement(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
		}
		catch (final SmException e)
		{
			skipTag(reader);
			throw new XMLElementException(e);
		}

		for (final SmDerivationMethod derivation : module.blockDefault)
		{
			// Note: blockDefault may contain other values than extension,
			// restriction or substitution.
			if (derivation.isExtension() || derivation.isRestriction() || derivation.isSubstitution())
			{
				element.getBlock().add(derivation);
			}
		}

		for (final SmDerivationMethod derivation : module.finalDefault)
		{
			// Note: finalDefault may contain other values than extension or
			// restriction.
			if (derivation.isExtension() || derivation.isRestriction())
			{
				element.getFinal().add(derivation);
			}
		}

		boolean seenType = false;

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ABSTRACT.equals(localName))
				{
					try
					{
						element.setAbstractFlag(trueOrFalse(reader.getAttributeValue(i)));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_BLOCK.equals(localName))
				{
					try
					{
						control(reader.getAttributeValue(i), EnumSet.of(SmDerivationMethod.Extension, SmDerivationMethod.Restriction, SmDerivationMethod.Substitution), element.getBlock());
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_DEFAULT.equals(localName))
				{
					if (null == element.m_valueConstraint)
					{
						element.m_valueConstraint = new XMLValueConstraint(SmValueConstraint.Kind.Default, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
					}
					else
					{
						m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
					}
				}
				else if (LN_FIXED.equals(localName))
				{
					if (null == element.m_valueConstraint)
					{
						element.m_valueConstraint = new XMLValueConstraint(SmValueConstraint.Kind.Fixed, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
					}
					else
					{
						m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
					}
				}
				else if (LN_FINAL.equals(localName))
				{
					try
					{
						control(reader.getAttributeValue(i), EnumSet.of(SmDerivationMethod.Extension, SmDerivationMethod.Restriction), element.getFinal());
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_NAME.equals(localName))
				{
					// Already known.
				}
				else if (LN_NILLABLE.equals(localName))
				{
					try
					{
						element.setNillableFlag(trueOrFalse(reader.getAttributeValue(i)));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_SUBSTITUTION_GROUP.equals(localName))
				{
					try
					{
						final QName elemName = resolveUsingXMLNamespaces(reader.getAttributeValue(i), reader.getNamespaceContext());
						element.substitutionGroup = cache.dereferenceElement(elemName, reader.getLocation());
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_TYPE.equals(localName))
				{
					seenType = true;
					try
					{
						element.typeRef = typeRef(reader.getAttributeValue(i), LN_TYPE, module.isChameleon(), targetNamespace, reader);
						ensureReferenceType(element.typeRef.getName(), reader.getLocation(), false, cache);
					}
					catch (final SmComplexTypeException e)
					{
						m_errors.error(e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		elementContent(element, null, module, cache, reader, false, targetNamespace, seenType);

		return element;
	}

	/**
	 * xs:element (within xs:all) <br/>
	 * Reference to a global element declaration or local definition (local definitions cannot be referenced). The
	 * number of occurrences can only be zero or one when xs:element is used within xs:all.
	 */
	private XMLParticle<A> elementWithinAllTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace, final XMLScope<A> scope) throws XMLStreamException, XMLElementException, SmAbortException
	{
		String name = null;
		XMLTypeRef<A> typeRef = null;
		final QName ref = referenceOptional(reader, LN_REF, module, targetNamespace);
		BigInteger minOccurs = BigInteger.ONE;
		BigInteger maxOccurs = BigInteger.ONE;
		boolean nillable = false;
		final EnumSet<SmDerivationMethod> block = EnumSet.copyOf(module.blockDefault);
		boolean qualified = module.elementQualified;
		XMLValueConstraint valueConstraint = null;

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_BLOCK.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					try
					{
						control(reader.getAttributeValue(i), EnumSet.of(SmDerivationMethod.Extension, SmDerivationMethod.Restriction, SmDerivationMethod.Substitution), block);
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_DEFAULT.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					if (null == valueConstraint)
					{
						valueConstraint = new XMLValueConstraint(SmValueConstraint.Kind.Default, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
					}
					else
					{
						m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
					}
				}
				else if (LN_FIXED.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					if (null == valueConstraint)
					{
						valueConstraint = new XMLValueConstraint(SmValueConstraint.Kind.Fixed, reader.getAttributeName(i), reader.getAttributeValue(i), getFrozenLocation(reader.getLocation()));
					}
					else
					{
						m_errors.error(new SmSourceAttributeDefaultAndFixedPresentException(getFrozenLocation(reader.getLocation())));
					}
				}
				else if (LN_FORM.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					try
					{
						qualified = qualified(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_NAME.equals(localName))
				{
					try
					{
						name = name(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_TYPE.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					try
					{
						typeRef = typeRef(reader.getAttributeValue(i), LN_TYPE, module.isChameleon(), targetNamespace, reader);
						ensureReferenceType(typeRef.getName(), reader.getLocation(), false, cache);
					}
					catch (final SmComplexTypeException e)
					{
						m_errors.error(e);
					}
				}
				else if (LN_REF.equals(localName))
				{
					// Already got it.
				}
				else if (LN_MAX_OCCURS.equals(localName))
				{
					maxOccurs = maxOccurs(reader.getAttributeValue(i), false, reader.getLocation(), reader.getName());
				}
				else if (LN_MIN_OCCURS.equals(localName))
				{
					minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
				}
				else if (LN_NILLABLE.equals(localName))
				{
					assertRefAbsent(ref, reader.getLocation());
					try
					{
						nillable = trueOrFalse(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		final XMLElement<A> element;
		try
		{
			element = determineLocalElement(name, qualified, typeRef, ref, cache, reader, targetNamespace, scope);
		}
		catch (final SmException e)
		{
			skipTag(reader);
			throw new XMLElementException(e);
		}

		element.setNillableFlag(nillable);
		element.getBlock().addAll(block);

		elementContent(element, ref, module, cache, reader, redefine, targetNamespace, (null != typeRef));

		return new XMLParticleWithElementTerm<A>(minOccurs, maxOccurs, element, valueConstraint, getFrozenLocation(reader.getLocation()));
	}

	private void ensureReferenceType(final QName name, final Location location, final boolean mustExist, final XMLSchemaCache<A> cache) throws SmAbortException
	{
		PreCondition.assertArgumentNotNull(name);

		if (nameBridge.isW3cXmlSchemaNamespaceURI(name.getNamespaceURI()))
		{
			// Do nothing. This will be caught later if a dangling reference
			// exists.
		}
		else
		{
			try
			{
				cache.dereferenceType(name, location, mustExist);
			}
			catch (final SmException e)
			{
				m_errors.error(e);
			}
		}
	}

	/**
	 * xs:enumeration
	 */
	private XMLEnumeration<A> enumerationTag(final XMLType<A> simpleType, final XMLStreamReader reader, final XMLSchemaModule<A> module) throws XMLStreamException, SmAbortException
	{
		final XMLEnumeration<A> enumeration = new XMLEnumeration<A>(simpleType, getFrozenLocation(reader.getLocation()));

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					enumeration.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_VALUE.equals(localName))
				{
					enumeration.setValue(reader.getAttributeValue(i), m_pms.getPrefixResolverSnapshot());
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_ENUMERATION, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_ENUMERATION, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return enumeration;
	}

	/**
	 * Mangles the particle returned by the compositor according to the specification so that it can become the
	 * effective content for a complex type.
	 */
	/*
	 * private ParticleWithModelGroupTerm<A> mangleCompositor(final boolean effectiveMixed, final
	 * ParticleWithModelGroupTerm<A> particle) { final ModelGroupImpl<A> term = particle.getTerm(); switch
	 * (term.getCompositor()) { case All: { if (term.getParticles().isEmpty()) { if (effectiveMixed) {
	 * particle.setMinOccurs(1); particle.setMaxOccurs(1); term.m_compositor = SmModelGroup.Compositor.Sequence; return
	 * particle; } else { return null; } } else { return particle; } } case Sequence: { if
	 * (term.getParticles().isEmpty()) { if (effectiveMixed) { particle.setMinOccurs(1); particle.setMaxOccurs(1);
	 * return particle; } else { return null; } } else { return particle; } } case Choice: { if
	 * (term.getParticles().isEmpty() && particle.getMinOccurs() == 0) { if (effectiveMixed) { particle.setMinOccurs(1);
	 * particle.setMaxOccurs(1); return particle; } else { return null; } } else { return particle; } } default: { throw
	 * new RuntimeException(term.getCompositor().name()); } } }
	 */

	private boolean equalStrings(final String lhs, final String rhs)
	{
		return lhs.equals(rhs);
	}

	/**
	 * xs:extension (complex content) <br/>
	 * We don't return anything because this affects multiple aspects of the complex type.
	 */
	private void extensionInComplexContentTag(final XMLType<A> complexType, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		// System.out.println(StripQualifiers.strip(getClass().getName()) +
		// ".extensionInComplexContentTag(complexType=" + complexType +
		// ", mixed=" + mixed + ")");
		try
		{
			final XMLType<A> redefineType;
			if (!redefine)
			{
				redefineType = null;
				baseTypeDefinitionInComplexContent(complexType, SmDerivationMethod.Extension, reader, redefine, cache, module, targetNamespace);
			}
			else
			{
				final QName baseName = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
				redefineType = cache.dereferenceType(baseName, reader.getLocation(), redefine);
				if (!complexType.getName().equals(baseName))
				{
					skipTag(reader);
					throw new SmRedefineTypeSelfReferenceException(complexType.getName(), baseName, getFrozenLocation(reader.getLocation()));
				}
			}

			final int attributeCount = reader.getAttributeCount();
			for (int i = 0; i < attributeCount; i++)
			{
				final String namespaceURI = reader.getAttributeNamespace(i);
				if (isGlobal(namespaceURI))
				{
					final String localName = reader.getAttributeLocalName(i);
					if (LN_ID.equals(localName))
					{
						checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
					}
					else if (LN_BASE.equals(localName))
					{
						// Already known.
					}
					else
					{
						reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
					}
				}
				else if (isWXS(namespaceURI))
				{
					reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
				else
				{
					// {any attributes with non-schema namespace}
				}
			}

			final CmMachine<String> machine = new CmMachine<String>(extensionInComplexContentTable, EPSILON);
			boolean done = false;
			while (!done)
			{
				final int event = reader.next();

				switch (event)
				{
					case XMLStreamConstants.START_ELEMENT:
					{
						m_pms.pushContext();
						try
						{
							copyNamespaces(reader, m_pms);
							if (isWXS(reader.getNamespaceURI()))
							{
								final String localName = reader.getLocalName();
								if (!machine.step(localName))
								{
									reportUnexpectedElementTag(LN_EXTENSION, reader.getName(), reader.getLocation());
									skipTag(reader);
								}
								else
								{
									if (LN_GROUP.equals(localName))
									{
										complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
										try
										{
											complexType.m_contentModel = groupParticleTag(new XMLScope<A>(complexType), reader, cache, module, redefine, targetNamespace);
										}
										catch (final XMLModelGroupUseException e)
										{
											m_errors.error(e.getCause());
										}
									}
									else if (LN_ALL.equals(localName))
									{
										complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
										try
										{
											complexType.m_contentModel = compositorOutsideGroupTag(SmModelGroup.SmCompositor.All, new XMLScope<A>(complexType), localName, reader, cache, module, redefine, targetNamespace);
										}
										catch (final XMLCompositorOutsideGroupException e)
										{
											m_errors.error(e.getCause());
										}
									}
									else if (LN_CHOICE.equals(localName))
									{
										complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
										try
										{
											complexType.m_contentModel = compositorOutsideGroupTag(SmModelGroup.SmCompositor.Choice, new XMLScope<A>(complexType), localName, reader, cache, module, redefine, targetNamespace);
										}
										catch (final XMLCompositorOutsideGroupException e)
										{
											m_errors.error(e.getCause());
										}
									}
									else if (LN_SEQUENCE.equals(localName))
									{
										try
										{
											final XMLParticleWithModelGroupTerm<A> contentModel = compositorOutsideGroupTag(SmModelGroup.SmCompositor.Sequence, new XMLScope<A>(complexType), localName, reader, cache, module, redefine, targetNamespace);
											if (!redefine)
											{
												complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
												complexType.m_contentModel = contentModel;
											}
											else
											{
												redefineType.extendContentType(complexType.m_contentKind.isMixed(), contentModel);
											}
										}
										catch (final XMLCompositorOutsideGroupException e)
										{
											m_errors.error(e.getCause());
										}
									}
									else if (LN_ATTRIBUTE.equals(localName))
									{
										attributeLocalTag(reader, cache, module, redefine, targetNamespace, complexType.getAttributeUses(), complexType.prohibited, new XMLScope<A>(complexType));
									}
									else if (LN_ATTRIBUTE_GROUP.equals(localName))
									{
										try
										{
											complexType.getAttributeGroups().add(attribGroupRefTag(reader, cache, module, targetNamespace));
										}
										catch (final XMLAttributeGroupException e)
										{
											m_errors.error(e.getCause());
										}
									}
									else if (LN_ANY_ATTRIBUTE.equals(localName))
									{
										if (null == complexType.attributeWildcard)
										{
											complexType.attributeWildcard = anyAttributeTag(reader, targetNamespace, module);
										}
										else
										{
											// xs:anyAttribute can only occur
											// zero or once.
											reportUnexpectedElementTag(LN_EXTENSION, reader.getName(), reader.getLocation());
											skipTag(reader);
										}
									}
									else if (LN_ANNOTATION.equals(localName))
									{
										annotationTag(reader, module);
									}
									else
									{
										throw new AssertionError(reader.getName());
									}
								}
							}
							else
							{
								skipTag(reader);
							}
						}
						finally
						{
							m_pms.popContext();
						}
					}
					break;
					case XMLStreamConstants.END_ELEMENT:
					{
						if (!machine.end())
						{
							reportUnexpectedEnd(LN_EXTENSION, reader.getLocation());
						}
						done = true;
					}
					break;
					case XMLStreamConstants.CHARACTERS:
					case XMLStreamConstants.COMMENT:
					case XMLStreamConstants.PROCESSING_INSTRUCTION:
					{
					}
					break;
					default:
					{
						throw new UnsupportedOperationException(Integer.toString(event));
					}
				}
			}
		}
		catch (final SmException e)
		{
			m_errors.error(e);
		}
	}

	/**
	 * xs:extension (simple content)
	 */
	private void extensionInSimpleContentTag(final XMLType<A> complexType, final XMLSchemaModule<A> module, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		// When extending simple content we're adding attributes to this complex
		// type.
		try
		{
			final QName baseName = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
			ensureReferenceType(baseName, reader.getLocation(), redefine, cache);
			complexType.setBase(new XMLTypeRef<A>(baseName), SmDerivationMethod.Extension);
		}
		catch (final SmException e)
		{
			m_errors.error(e);
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{

					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_BASE.equals(localName))
				{
					// Already known.
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		final CmMachine<String> machine = new CmMachine<String>(extensionInSimpleContentTable, EPSILON);
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (!machine.step(localName))
							{
								reportUnexpectedElementTag(LN_EXTENSION, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
							else
							{
								if (LN_ATTRIBUTE.equals(localName))
								{
									attributeLocalTag(reader, cache, module, redefine, targetNamespace, complexType.getAttributeUses(), complexType.prohibited, new XMLScope<A>(complexType));
								}
								else if (LN_ATTRIBUTE_GROUP.equals(localName))
								{
									try
									{
										complexType.getAttributeGroups().add(attribGroupRefTag(reader, cache, module, targetNamespace));
									}
									catch (final XMLAttributeGroupException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_ANY_ATTRIBUTE.equals(localName))
								{
									complexType.attributeWildcard = anyAttributeTag(reader, targetNamespace, module);
								}
								else if (LN_ANNOTATION.equals(localName))
								{
									annotationTag(reader, module);
								}
								else
								{
									throw new AssertionError(reader.getName());
								}
							}
						}
						else
						{
							skipTag(reader);
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					if (!machine.end())
					{
						reportUnexpectedEnd(LN_EXTENSION, reader.getLocation());
					}
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	private SmRestrictedXPath fieldTag(final XMLStreamReader reader, final XMLSchemaModule<A> module) throws XMLStreamException, XMLFieldException, SmAbortException
	{
		SmRestrictedXPath xpath = null;
		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_XPATH.equals(localName))
				{
					try
					{
						xpath = xpath(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
					}
					catch (final SmAttributeUseException e)
					{
						skipTag(reader);
						throw new XMLFieldException(e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_FIELD, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_FIELD, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return xpath;
	}

	private boolean fixed(final String strval, final Location location, final QName elementName) throws SmAttributeUseException
	{
		try
		{
			return trueOrFalse(strval);
		}
		catch (final SmSimpleTypeException e)
		{
			throw new SmAttributeUseException(elementName, new QName(LN_FIXED), getFrozenLocation(location), e);
		}
	}

	/**
	 * xs:fractionDigits
	 */
	private XMLFractionDigitsFacet<A> fractionDigitsTag(final XMLType<A> simpleType, final XMLStreamReader reader, final XMLSchemaModule<A> module) throws XMLStreamException, SmComplexTypeException, SmAbortException
	{
		final XMLFractionDigitsFacet<A> facet = new XMLFractionDigitsFacet<A>(simpleType, getFrozenLocation(reader.getLocation()));

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					facet.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_FIXED.equals(localName))
				{
					facet.fixed = fixed(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
				}
				else if (LN_VALUE.equals(localName))
				{
					final String strval = reader.getAttributeValue(i);
					try
					{
						facet.value = nonNegativeInteger(strval);
					}
					catch (final SmSimpleTypeException ignore)
					{
						throw new SmAttributeUseException(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()), ignore);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_FRACTION_DIGITS, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_FRACTION_DIGITS, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return facet;
	}

	/**
	 * xs:group (reference)
	 */
	private XMLParticleWithModelGroupTerm<A> groupParticleTag(final XMLScope<A> localScope, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLModelGroupUseException, SmAbortException
	{
		final QName ref;
		BigInteger minOccurs = BigInteger.ONE;
		BigInteger maxOccurs = BigInteger.ONE;

		try
		{
			ref = requiredQName(LN_REF, module.isChameleon(), targetNamespace, reader);
		}
		catch (final SmComplexTypeException e)
		{
			skipTag(reader);
			throw new XMLModelGroupUseException(e);
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_MAX_OCCURS.equals(localName))
				{
					maxOccurs = maxOccurs(reader.getAttributeValue(i), true, reader.getLocation(), reader.getName());
				}
				else if (LN_MIN_OCCURS.equals(localName))
				{
					minOccurs = minOccurs(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
				}
				else if (LN_REF.equals(localName))
				{
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		final XMLModelGroup<A> modelGroup;
		if (null != ref)
		{
			try
			{
				modelGroup = cache.dereferenceModelGroup(ref, reader.getLocation(), redefine);
			}
			catch (final SmException e)
			{
				skipTag(reader);
				throw new XMLModelGroupUseException(e);
			}
		}
		else
		{
			modelGroup = new XMLModelGroup<A>(SmModelGroup.SmCompositor.Sequence, localScope, getFrozenLocation(reader.getLocation()));
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_GROUP, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_GROUP, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
					// Ignore.
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}

		return new XMLParticleWithModelGroupTerm<A>(minOccurs, maxOccurs, modelGroup, getFrozenLocation(reader.getLocation()));
	}

	/**
	 * xs:group (definition)
	 */
	private XMLModelGroup<A> groupTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLModelGroupException, SmAbortException
	{
		final XMLModelGroup<A> modelGroup;
		if (!redefine)
		{
			try
			{
				modelGroup = cache.registerModelGroup(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
			}
			catch (final SmException e)
			{
				skipTag(reader);
				throw new XMLModelGroupException(e);
			}
		}
		else
		{
			try
			{
				modelGroup = cache.dereferenceModelGroup(requiredNCName(LN_NAME, targetNamespace, reader), reader.getLocation(), redefine);
			}
			catch (final SmException e)
			{
				skipTag(reader);
				throw new XMLModelGroupException(e);
			}
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_NAME.equals(localName))
				{
					// Already known.
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean missingACS = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_SEQUENCE.equals(localName))
							{
								missingACS = checkWxsElementMaxOccursUnity(missingACS, LN_GROUP, LN_SEQUENCE, reader.getLocation());
								modelGroup.setCompositor(SmModelGroup.SmCompositor.Sequence);
								compositorWithinGroupTag(reader, cache, module, modelGroup, localName, redefine, targetNamespace);
								firstElement = false;
							}
							else if (LN_CHOICE.equals(localName))
							{
								missingACS = checkWxsElementMaxOccursUnity(missingACS, LN_GROUP, LN_CHOICE, reader.getLocation());
								modelGroup.setCompositor(SmModelGroup.SmCompositor.Choice);
								compositorWithinGroupTag(reader, cache, module, modelGroup, localName, redefine, targetNamespace);
								firstElement = false;
							}
							else if (LN_ALL.equals(localName))
							{
								missingACS = checkWxsElementMaxOccursUnity(missingACS, LN_GROUP, LN_ALL, reader.getLocation());
								modelGroup.setCompositor(SmModelGroup.SmCompositor.All);
								compositorWithinGroupTag(reader, cache, module, modelGroup, localName, redefine, targetNamespace);
								firstElement = false;
							}
							else if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_GROUP, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_GROUP, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
					// Ignore.
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}

		if (null == modelGroup.getCompositor())
		{
			// Expecting (xs:all | xs:choice | xs:sequence)
			m_errors.error(new SmUnexpectedEndException(reader.getName(), getFrozenLocation(reader.getLocation())));
		}

		return modelGroup;
	}

	private void importTag(final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final XMLStreamReader reader, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		PreCondition.assertArgumentNotNull(module, "module");
		try
		{
			URI schemaLocation = null;
			URI namespace = null;
			final int attributeCount = reader.getAttributeCount();
			for (int i = 0; i < attributeCount; i++)
			{
				final String namespaceURI = reader.getAttributeNamespace(i);
				if (isGlobal(namespaceURI))
				{
					final String localName = reader.getAttributeLocalName(i);
					if (LN_SCHEMA_LOCATION.equals(localName))
					{
						try
						{
							schemaLocation = anyURI(reader.getAttributeValue(i));
						}
						catch (final SmSimpleTypeException e)
						{
							throw new SmAttributeUseException(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()), e);
						}
					}
					else if (LN_NAMESPACE.equals(localName))
					{
						try
						{
							namespace = anyURI(reader.getAttributeValue(i));
						}
						catch (final SmSimpleTypeException e)
						{
							throw new SmAttributeUseException(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()), e);
						}
						if (equalStrings(namespace.toString(), targetNamespace))
						{
							throw new SmIllegalNamespaceException(LN_IMPORT, targetNamespace, namespace.toString(), new SrcFrozenLocation(reader.getLocation()));
						}
					}
					else if (LN_ID.equals(localName))
					{
						checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
					}
					else
					{
						reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
					}
				}
				else if (isWXS(namespaceURI))
				{
					reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
				else
				{
					// {any attributes with non-schema namespace}
				}
			}

			annotationContent(LN_IMPORT, reader, module);

			// if there's no schemaLocation hint, give up. /*try using the
			// namespace.*/
			if (schemaLocation != null)
			{
				if (m_processRepeatedNamespaces || !cache.m_seenNamespaces.contains(namespace))
				{
					parseExternalModule(cache, module, reader.getLocation(), namespace, schemaLocation, ModuleKind.Import);
				}
			}
			else
			{
				if (namespace != null)
				{
					if (equalStrings(XMLConstants.XML_NS_URI, namespace.toString()))
					{
					}
					else
					{
						// Do nothing.
					}
				}
				else
				{
					// Do nothing.
				}
			}
		}
		catch (final SmAttributeUseException e)
		{
			m_errors.error(e);
		}
		catch (final SmIllegalNamespaceException e)
		{
			m_errors.error(e);
		}
	}

	private void includeTag(final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final XMLStreamReader reader) throws XMLStreamException, SmAbortException
	{
		final int attributeCount = reader.getAttributeCount();
		URI schemaLocation = null;
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_SCHEMA_LOCATION.equals(localName))
				{
					try
					{
						schemaLocation = anyURI(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		if (schemaLocation != null)
		{
			parseExternalModule(cache, module, reader.getLocation(), null, schemaLocation, ModuleKind.Include);
		}
		else
		{
			m_errors.error(new SmMissingAttributeException(reader.getName(), new QName(LN_SCHEMA_LOCATION), getFrozenLocation(reader.getLocation())));
		}

		annotationContent(LN_INCLUDE, reader, module);
	}

	/**
	 * There seems to be some inconsistency in how implementations of NamespaceContext report an unbound prefix. <br/>
	 * This routine helps in assessing the outcome. <br/>
	 * This contradicts the API specification. If this gets too troublesome we should copy the prefix mappings to our
	 * own utility and use that to do the namespace lookup.
	 */
	private boolean isBoundPrefix(final String prefix, final String namespaceURI)
	{
		if (null != namespaceURI)
		{
			if (XMLConstants.NULL_NS_URI.equals(namespaceURI))
			{
				if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix))
				{
					return true;
				}
				else
				{
					// This contradicts the API specification. If this gets too
					// troublesome
					// we should copy the prefix mappings to our own utility and
					// use that
					// to do the namespace lookup.
					return true;
				}
			}
			else
			{
				return true;
			}
		}
		else
		{
			// The return value is null. This value is not represented in the
			// API specification
			// but we assume (reasonably) that it means the prefix is not bound,
			// but only in the
			// case that the prefix is not the default namespace prefix.
			return XMLConstants.DEFAULT_NS_PREFIX.equals(prefix);
		}
	}

	/**
	 * Determines whether the namespace specified is the global (no-name) namespace. <br/>
	 * Note: StaX uses <code>null</code> for the no-name namespace, but this function also accepts the zero-length
	 * string.
	 */
	private boolean isGlobal(final String namespaceURI)
	{
		return (null == namespaceURI) || XMLConstants.NULL_NS_URI.equals(namespaceURI);
	}

	/**
	 * Determines whether the namespace specified is the W3C XML Schema namespace.
	 */
	private boolean isWXS(final String namespaceURI)
	{
		return XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(namespaceURI);
	}

	private XMLIdentityConstraint<A> keyrefTag(final XMLSchemaCache<A> cache, final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule<A> module) throws XMLStreamException, XMLIdentityConstraintException, SmAbortException
	{
		final XMLIdentityConstraint<A> keyref;
		try
		{
			final QName name = requiredNCName(LN_NAME, targetNamespace, reader);

			module.registerIdentityConstraintName(name, reader.getLocation());

			keyref = cache.registerIdentityConstraint(SmIdentityConstraintKind.KeyRef, name, getFrozenLocation(reader.getLocation()));
		}
		catch (final SmException e)
		{
			skipTag(reader);
			throw new XMLIdentityConstraintException(e);
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_NAME.equals(localName))
				{
					// Already known.
				}
				else if (LN_REFER.equals(localName))
				{
					try
					{
						/* final String name = */
						final QName reference = resolveUsingXMLNamespaces(reader.getAttributeValue(i), reader.getNamespaceContext());
						keyref.keyConstraint = cache.dereferenceIdentityConstraint(reference, reader.getLocation());
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean missingSelector = true;
		boolean missingFields = true;
		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_KEYREF, reader.getLocation());
								annotationTag(reader, module);
							}
							else if (LN_SELECTOR.equals(localName))
							{
								missingSelector = checkWxsElementMaxOccursUnity(missingSelector, LN_UNIQUE, LN_SELECTOR, reader.getLocation());
								try
								{
									keyref.selector = selectorTag(reader, module);
								}
								catch (final XMLSelectorException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_FIELD.equals(localName))
							{
								if (missingSelector)
								{
									m_errors.error(new SmUnexpectedElementException(reader.getName(), getFrozenLocation(reader.getLocation()), new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, LN_FIELD), getFrozenLocation(reader.getLocation())));
								}
								missingFields = false;
								try
								{
									keyref.fields.add(fieldTag(reader, module));
								}
								catch (final XMLFieldException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else
							{
								reportUnexpectedElementTag(LN_KEYREF, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		if (missingFields)
		{
			m_errors.error(new SmUnexpectedEndException(reader.getName(), getFrozenLocation(reader.getLocation())));
		}
		return keyref;
	}

	private XMLIdentityConstraint<A> keyTag(final XMLSchemaCache<A> cache, final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule<A> module) throws XMLStreamException, XMLIdentityConstraintException, SmAbortException
	{
		final XMLIdentityConstraint<A> constraint;
		try
		{
			final QName name = requiredNCName(LN_NAME, targetNamespace, reader);

			module.registerIdentityConstraintName(name, reader.getLocation());

			constraint = cache.registerIdentityConstraint(SmIdentityConstraintKind.Key, name, getFrozenLocation(reader.getLocation()));
		}
		catch (final SmException e)
		{
			skipTag(reader);
			throw new XMLIdentityConstraintException(e);
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_NAME.equals(localName))
				{
					// Already known.
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean missingSelector = true;
		boolean missingFields = true;
		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_KEY, reader.getLocation());
								annotationTag(reader, module);
							}
							else if (LN_SELECTOR.equals(localName))
							{
								missingSelector = checkWxsElementMaxOccursUnity(missingSelector, LN_UNIQUE, LN_SELECTOR, reader.getLocation());
								try
								{
									constraint.selector = selectorTag(reader, module);
								}
								catch (final XMLSelectorException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_FIELD.equals(localName))
							{
								if (missingSelector)
								{
									m_errors.error(new SmUnexpectedElementException(reader.getName(), getFrozenLocation(reader.getLocation()), new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, LN_FIELD), getFrozenLocation(reader.getLocation())));
								}
								missingFields = false;
								try
								{
									constraint.fields.add(fieldTag(reader, module));
								}
								catch (final XMLFieldException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else
							{
								reportUnexpectedElementTag(LN_KEY, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		if (missingFields)
		{
			m_errors.error(new SmUnexpectedEndException(reader.getName(), getFrozenLocation(reader.getLocation())));
		}
		return constraint;
	}

	private String lang(final String initialValue) throws SmSimpleTypeException
	{
		return validateString(initialValue, SmNativeType.LANGUAGE);
	}

	/**
	 * xs:length, xs:maxLength, xs:minLength
	 */
	private XMLLength<A> lengthTag(final XMLType<A> type, final boolean minimum, final boolean maximum, final String contextName, final XMLSchemaModule<A> module, final XMLStreamReader reader) throws XMLStreamException, SmComplexTypeException, SmAbortException
	{
		final XMLLength<A> length = new XMLLength<A>(type, getFrozenLocation(reader.getLocation()));
		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_VALUE.equals(localName))
				{
					if (minimum)
					{
						try
						{
							length.minLength = nonNegativeInteger(reader.getAttributeValue(i));
						}
						catch (final SmSimpleTypeException e)
						{
							reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
						}
					}
					if (maximum)
					{
						try
						{
							length.maxLength = nonNegativeInteger(reader.getAttributeValue(i));
						}
						catch (final SmSimpleTypeException e)
						{
							reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
						}
					}
				}
				else if (LN_FIXED.equals(localName))
				{
					length.fixed = fixed(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
				}
				else if (LN_ID.equals(localName))
				{
					length.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, contextName, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(contextName, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return length;
	}

	/**
	 * xs:list
	 */
	private void listTag(final XMLType<A> listType, final XMLSchemaModule<A> module, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		listType.setBase(ANY_SIMPLE_TYPE, SmDerivationMethod.List);

		// Use this to detect missing both itemType attribute and <simpleType>
		// child.
		// Note that we have to perform the "clear" rather than assert the
		// emptiness because
		// we are attempting to collect the maximum amount of feedback from this
		// parse.
		listType.itemRef = null;

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ITEM_TYPE.equals(localName))
				{
					try
					{
						listType.itemRef = typeRef(reader.getAttributeValue(i), LN_ITEM_TYPE, module.isChameleon(), targetNamespace, reader);
						ensureReferenceType(listType.itemRef.getName(), reader.getLocation(), redefine, cache);
					}
					catch (final SmComplexTypeException e)
					{
						m_errors.error(e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean missingST = true;
		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_SIMPLE_TYPE.equals(localName))
							{
								if (null == listType.itemRef)
								{
									missingST = checkWxsElementMaxOccursUnity(missingST, LN_LIST, LN_SIMPLE_TYPE, reader.getLocation());
									listType.itemRef = simpleTypeLocalTag(new XMLScope<A>(listType), cache, module, reader, false, targetNamespace);
									firstElement = false;
								}
								else
								{
									m_errors.error(new SmSimpleTypeListException(getFrozenLocation(reader.getLocation())));
								}
							}
							else if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_LIST, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_LIST, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}

		if (null == listType.itemRef)
		{
			m_errors.error(new SmSimpleTypeListException(getFrozenLocation(reader.getLocation())));
		}
	}

	/**
	 * Parse the maxOccurs attribute.
	 * 
	 * @param strval
	 *            The attribute value.
	 * @param unbounded
	 *            Determines whether "unbounded" is an acceptable value.
	 * @param location
	 *            The parser location.
	 * @param elementName
	 *            The element name containing the attribute.
	 */
	private BigInteger maxOccurs(final String strval, final boolean unbounded, final Location location, final QName elementName) throws SmAbortException
	{
		if (unbounded)
		{
			if ("unbounded".equals(strval))
			{
				return XMLParticle.UNBOUNDED;
			}
		}
		try
		{
			return nonNegativeInteger(strval);
		}
		catch (final SmSimpleTypeException e)
		{
			reportAttributeUseError(elementName, new QName(LN_MAX_OCCURS), location, e);
			return BigInteger.ONE;
		}
	}

	/**
	 * xs:maxExclusive, xs:maxInclusive, xs:minExclusive, xs:minInclusive
	 */
	private XMLMinMaxFacet<A> minmaxTag(final XMLType<A> simpleType, final SmFacetKind kind, final String elementName, final XMLStreamReader reader, final XMLSchemaModule<A> module) throws XMLStreamException, SmComplexTypeException, SmAbortException
	{
		final XMLMinMaxFacet<A> minmax = new XMLMinMaxFacet<A>(kind, elementName, simpleType, getFrozenLocation(reader.getLocation()));

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_VALUE.equals(localName))
				{
					minmax.value = reader.getAttributeValue(i);
				}
				else if (LN_FIXED.equals(localName))
				{
					minmax.fixed = fixed(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
				}
				else if (LN_ID.equals(localName))
				{
					minmax.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, elementName, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(elementName, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return minmax;
	}

	/**
	 * Parse the minOccurs attribute.
	 * 
	 * @param strval
	 *            The attribute value.
	 * @param location
	 *            The parser location.
	 * @param elementName
	 *            The element name containing the attribute.
	 */
	private BigInteger minOccurs(final String strval, final Location location, QName elementName) throws SmAbortException
	{
		try
		{
			return nonNegativeInteger(strval);
		}
		catch (final SmSimpleTypeException e)
		{
			m_errors.error(new SmAttributeUseException(elementName, new QName(LN_MIN_OCCURS), getFrozenLocation(location), e));
			return BigInteger.ONE;
		}
	}

	private String name(final String initialValue) throws SmSimpleTypeException
	{
		final SmSimpleType<A> atomicType = bootstrap.getAtomicType(SmNativeType.NCNAME);
		try
		{
			final List<A> value = atomicType.validate(initialValue);
			if (value.size() > 0)
			{
				return atomBridge.getString(value.get(0));
			}
			else
			{
				return null;
			}
		}
		catch (final SmDatatypeException cause)
		{
			throw new SmSimpleTypeException(initialValue, atomicType, cause);
		}
	}

	private SmNamespaceConstraint namespaces(final String initialValue, final String targetNamespace) throws SmSimpleTypeException
	{
		final String strval = initialValue.trim();

		if (strval.length() == 0)
		{
			// A reading of the specification might suggest an empty set
			// of namespaces that cannot be matched by any element.
			// The Microsoft interpretation of namespace="" is that
			// it is the same as not having the namespace attribute at all
			// so the default is ##any.
			// http://www.w3.org/Bugs/Public/show_bug.cgi?id=4066
			return SmNamespaceConstraint.Any(nameSource);
		}
		else if (strval.equals("##any"))
		{
			return SmNamespaceConstraint.Any(nameSource);
		}
		else if (strval.equals("##other"))
		{
			return SmNamespaceConstraint.exclude(targetNamespace, nameSource);
		}
		else
		{
			final HashSet<String> namespaces = new HashSet<String>();
			final StringTokenizer tokenizer = new StringTokenizer(strval);
			while (tokenizer.hasMoreTokens())
			{
				final String token = tokenizer.nextToken();
				if (token.equals("##targetNamespace"))
				{
					namespaces.add(targetNamespace);
				}
				else if (token.equals("##local"))
				{
					namespaces.add(XMLConstants.NULL_NS_URI);
				}
				else if (!token.startsWith("##"))
				{
					namespaces.add(token);
				}
				else
				{
					final SmDatatypeException cause = new SmDatatypeException(strval, null);
					throw new SmSimpleTypeException(strval, null, cause);
				}
			}
			return SmNamespaceConstraint.include(namespaces, nameSource);
		}
	}

	private BigInteger nonNegativeInteger(final String initialValue) throws SmSimpleTypeException
	{
		final SmSimpleType<A> atomicType = bootstrap.getAtomicType(SmNativeType.NON_NEGATIVE_INTEGER);
		try
		{
			final List<A> value = atomicType.validate(initialValue);
			if (value.size() > 0)
			{
				return atomBridge.getInteger(value.get(0));
			}
			else
			{
				return null;
			}
		}
		catch (final SmDatatypeException cause)
		{
			throw new SmSimpleTypeException(initialValue, atomicType, cause);
		}
	}

	/**
	 * xs:notation
	 */
	private XMLNotation<A> notationTag(final XMLSchemaCache<A> cache, final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule<A> module) throws XMLStreamException, XMLNotationException, SmAbortException
	{
		final XMLNotation<A> notation;
		try
		{
			notation = cache.registerNotation(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
		}
		catch (final SmException e)
		{
			skipTag(reader);
			throw new XMLNotationException(e);
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_NAME.equals(localName))
				{
					// Already have it.
				}
				else if (LN_PUBLIC.equals(localName))
				{
					try
					{
						notation.publicId = token(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_SYSTEM.equals(localName))
				{
					try
					{
						notation.systemId = anyURI(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		if (null == notation.publicId && null == notation.systemId)
		{
			// Either public or system should be defined.
			m_errors.error(new SmMissingAttributeException(reader.getName(), new QName(LN_PUBLIC), getFrozenLocation(reader.getLocation())));
		}

		annotationContent(LN_NOTATION, reader, module);

		return notation;
	}

	private QName optionalQName(final String initialValue, final String attributeName, final boolean isChameleon, final String targetNamespace, final XMLStreamReader reader) throws SmComplexTypeException
	{
		if (null != initialValue)
		{
			if (isChameleon)
			{
				try
				{
					return resolveUsingTargetNamespace(initialValue, targetNamespace, reader.getNamespaceContext());
				}
				catch (final SmSimpleTypeException e)
				{
					throw new SmAttributeUseException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()), e);
				}
			}
			else
			{
				try
				{
					return resolveUsingXMLNamespaces(initialValue, reader.getNamespaceContext());
				}
				catch (final SmSimpleTypeException e)
				{
					throw new SmAttributeUseException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()), e);
				}
			}
		}
		return null;
	}

	public void parse(final URI systemId, final InputStream istream, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module) throws SmAbortException
	{
		PreCondition.assertArgumentNotNull(cache, "cache");
		PreCondition.assertArgumentNotNull(module, "module");

		if (cache.m_seenSystemIds.contains(systemId))
		{
			return;
		}
		else
		{
			cache.m_seenSystemIds.add(systemId);
		}

		// Hardwire a dependency on the Woodstox Parser.
		// This avoids the OSGi class loading issues and provides consistency.
		// The downside is that we can't substitute a different parser.
		final XMLInputFactory factory = WstxInputFactory.newInstance();
		// final XMLInputFactory factory = XMLInputFactory.newInstance();

		final XMLStreamReader reader;
		try
		{
			if (null != systemId)
			{
				reader = factory.createXMLStreamReader(systemId.toString(), istream);
			}
			else
			{
				reader = factory.createXMLStreamReader(null, istream);
			}
		}
		catch (final XMLStreamException e)
		{
			// I'm not sure what has happened here, but it doesn't fit into the
			// category of not
			// being well formed XML. Perhaps it's not XML at all. We'll throw
			// this assertion and deal
			// with it more accurately when we know more.
			throw new AssertionError(e);
		}
		try
		{
			boolean done = false;

			while (!done)
			{
				final int event = reader.next();

				switch (event)
				{
					case XMLStreamConstants.END_DOCUMENT:
					{
						reader.close();
						done = true;
					}
					break;
					case XMLStreamConstants.START_ELEMENT:
					{
						m_pms.pushContext();
						try
						{
							copyNamespaces(reader, m_pms);
							if (isWXS(reader.getNamespaceURI()))
							{
								final String localName = reader.getLocalName();
								if (LN_SCHEMA.equals(localName))
								{
									schemaTag(reader, cache, module);
								}
								else
								{
									reportUnexpectedElementTag("document", reader.getName(), reader.getLocation());
									skipTag(reader);
								}
							}
							else
							{
								reportUnexpectedElementTag("document", reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						finally
						{
							m_pms.popContext();
						}
					}
					break;
					default:
					{
						// ignore
					}
				}
			}
		}
		catch (final XMLStreamException e)
		{
			if (module.isImport())
			{
				m_errors.error(new SmImportNotWellFormedException(getFrozenLocation(reader.getLocation())));
			}
			else if (module.isInclude())
			{
				m_errors.error(new SmInclusionNotWellFormedException(getFrozenLocation(reader.getLocation())));
			}
			else if (module.isRedefine())
			{
				m_errors.error(new SmRedefinitionNotWellFormedException(getFrozenLocation(reader.getLocation())));
			}
			else
			// must be top level schema
			{
				m_errors.error(new SmTopLevelSchemaNotWellFormedException(getFrozenLocation(reader.getLocation())));
			}
		}
	}

	private void parseExternalModule(final XMLSchemaCache<A> cache, final XMLSchemaModule<A> parent, final Location location, final URI namespace, final URI schemaLocation, final ModuleKind moduleKind) throws SmAbortException
	{
		PreCondition.assertArgumentNotNull(schemaLocation, "schemaLocation");

		if (null == m_catalog)
		{
			throw new AssertionError("catalog required for include, import or redefine.");
		}
		final URI catalogURI = m_catalog.resolveNamespaceAndSchemaLocation(parent.getSystemId(), namespace, schemaLocation);
		if (catalogURI != null)
		{
			try
			{
				if (null == m_resolver)
				{
					throw new AssertionError("resolver required for include, import or redefine.");
				}
				final InputStream source = m_resolver.resolveInputStream(catalogURI);
				final XMLSchemaModule<A> module = new XMLSchemaModule<A>(parent, schemaLocation, catalogURI);
				switch (moduleKind)
				{
					case Include:
					{
						module.setIncludeFlag();
					}
					break;
					case Import:
					{
						module.setImportFlag();
					}
					break;
					case Redefine:
					{
						module.setRedefineFlag();
					}
					break;
					default:
					{
						throw new AssertionError(moduleKind);
					}
				}
				final XMLSchemaParser<A> parser = new XMLSchemaParser<A>(atomBridge, bootstrap, m_errors, m_catalog, m_resolver, m_processRepeatedNamespaces);
				parser.parse(catalogURI, source, cache, module);
			}
			catch (final IOException e)
			{
				// Do nothing. It's not an error.
			}
		}
		else
		{
			m_errors.error(new SmNoSchemaForNamespaceException(schemaLocation, new SrcFrozenLocation(location)));
		}
	}

	/**
	 * xs:pattern
	 */
	private XMLPatternFacet<A> patternTag(final XMLType<A> simpleType, final XMLStreamReader reader, final XMLSchemaModule<A> module) throws XMLStreamException, SmAbortException
	{
		final XMLPatternFacet<A> pattern = new XMLPatternFacet<A>(simpleType, getFrozenLocation(reader.getLocation()));

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_VALUE.equals(localName))
				{
					pattern.value = reader.getAttributeValue(i);
				}
				else if (LN_ID.equals(localName))
				{
					pattern.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_PATTERN, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_PATTERN, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return pattern;
	}

	private BigInteger positiveInteger(final String initialValue) throws SmSimpleTypeException
	{
		final SmSimpleType<A> atomicType = bootstrap.getAtomicType(SmNativeType.POSITIVE_INTEGER);
		try
		{
			final List<A> value = atomicType.validate(initialValue);
			if (value.size() > 0)
			{
				return atomBridge.getInteger(value.get(0));
			}
			else
			{
				return null;
			}
		}
		catch (final SmDatatypeException cause)
		{
			throw new SmSimpleTypeException(initialValue, atomicType, cause);
		}
	}

	private SmProcessContentsMode processContents(final String strval) throws SmSimpleTypeException
	{
		if ("lax".equals(strval))
		{
			return SmProcessContentsMode.Lax;
		}
		else if ("skip".equals(strval))
		{
			return SmProcessContentsMode.Skip;
		}
		else if ("strict".equals(strval))
		{
			return SmProcessContentsMode.Strict;
		}
		else
		{
			final SmDatatypeException cause = new SmDatatypeException(strval, null);
			throw new SmSimpleTypeException(strval, null, cause);
		}
	}

	private boolean qualified(final String strval) throws SmSimpleTypeException
	{
		if ("qualified".equals(strval))
		{
			return true;
		}
		else if ("unqualified".equals(strval))
		{
			return false;
		}
		else
		{
			final SmDatatypeException cause = new SmDatatypeException(strval, null);
			throw new SmSimpleTypeException(strval, null, cause);
		}
	}

	private void redefineTag(final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final XMLStreamReader reader, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		{
			URI schemaLocation = null;
			final int attributeCount = reader.getAttributeCount();
			for (int i = 0; i < attributeCount; i++)
			{
				final String namespaceURI = reader.getAttributeNamespace(i);
				if (isGlobal(namespaceURI))
				{
					final String localName = reader.getAttributeLocalName(i);
					if (LN_SCHEMA_LOCATION.equals(localName))
					{
						try
						{
							schemaLocation = anyURI(reader.getAttributeValue(i));
						}
						catch (final SmSimpleTypeException e)
						{
							reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
						}
					}
					else if (LN_ID.equals(localName))
					{
						checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
					}
					else
					{
						reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
					}
				}
				else if (isWXS(namespaceURI))
				{
					reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
				else
				{
					// {any attributes with non-schema namespace}
				}
			}

			if (schemaLocation != null)
			{
				parseExternalModule(cache, module, reader.getLocation(), null, schemaLocation, ModuleKind.Redefine);
			}
			else
			{
				m_errors.error(new SmMissingAttributeException(reader.getName(), new QName(LN_SCHEMA_LOCATION), getFrozenLocation(reader.getLocation())));
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_REDEFINE, reader.getLocation());
								annotationTag(reader, module);
							}
							else if (LN_SIMPLE_TYPE.equals(localName))
							{
								try
								{
									simpleTypeGlobalTag(reader, cache, module, true, targetNamespace);
								}
								catch (final XMLSimpleTypeException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_COMPLEX_TYPE.equals(localName))
							{
								try
								{
									complexTypeGlobalTag(reader, cache, module, true, targetNamespace);
								}
								catch (final XMLComplexTypeException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_GROUP.equals(localName))
							{
								try
								{
									groupTag(reader, cache, module, true, targetNamespace);
								}
								catch (final XMLModelGroupException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_ATTRIBUTE_GROUP.equals(localName))
							{
								try
								{
									attribGroupTag(reader, cache, module, true, targetNamespace);
								}
								catch (final XMLAttributeGroupException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else
							{
								reportUnexpectedElementTag(LN_REDEFINE, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	private QName referenceOptional(final XMLStreamReader reader, final String localName, final XMLSchemaModule<A> module, final String targetNamespace) throws SmAbortException
	{
		final String srcval = reader.getAttributeValue(null, localName);
		if (null != srcval)
		{
			if (module.isChameleon())
			{
				try
				{
					return resolveUsingTargetNamespace(srcval, targetNamespace, reader.getNamespaceContext());
				}
				catch (final SmSimpleTypeException e)
				{
					m_errors.error(new SmAttributeUseException(reader.getName(), new QName(localName), getFrozenLocation(reader.getLocation()), e));
					return null;
				}
			}
			else
			{
				try
				{
					return resolveUsingXMLNamespaces(srcval, reader.getNamespaceContext());
				}
				catch (final SmSimpleTypeException e)
				{
					m_errors.error(new SmAttributeUseException(reader.getName(), new QName(localName), getFrozenLocation(reader.getLocation()), e));
					return null;
				}
			}
		}
		else
		{
			return null;
		}
	}

	private void reportAttributeInGlobalNamespace(final QName elementName, final QName attributeName, final SrcFrozenLocation location) throws SmAbortException
	{
		m_errors.error(new CvcUnexpectedAttributeException(elementName, attributeName, location));
	}

	private void reportAttributeInWxsNamespace(final QName elementName, final QName attributeName, final SrcFrozenLocation location) throws SmAbortException
	{
		m_errors.error(new CvcUnexpectedAttributeException(elementName, attributeName, location));
	}

	private void reportAttributeInXmlNamespace(final QName elementName, final QName attributeName, final SrcFrozenLocation location) throws SmAbortException
	{
		m_errors.error(new CvcUnexpectedAttributeException(elementName, attributeName, location));
	}

	private void reportAttributeUseError(final QName elementName, final QName attributeName, final Location location, final SmSimpleTypeException cause) throws SmAbortException
	{
		m_errors.error(new SmAttributeUseException(elementName, attributeName, getFrozenLocation(location), cause));
	}

	private void reportUnexpectedElementTag(final String contextName, final QName unexpectedName, final Location location) throws SmAbortException
	{
		m_errors.error(new SmUnexpectedElementException(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, contextName), getFrozenLocation(location), unexpectedName, getFrozenLocation(location)));
	}

	private void reportUnexpectedEnd(final String contextName, final Location location) throws SmAbortException
	{
		m_errors.error(new SmUnexpectedEndException(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, contextName), getFrozenLocation(location)));
	}

	private void reportUnexpectedNonWhiteSpaceTextInElementOnlyContent(final String contextName, final String text, final Location location) throws SmAbortException
	{
		m_errors.error(new CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, contextName), text, getFrozenLocation(location)));
	}

	/**
	 * Computes the name for a global schema component. <br/>
	 * The "name" attribute is validated to exist and to be of type xs:NCName. <br/>
	 * The name computed adopts the targetNamespace name of the module being parsed.
	 */
	private QName requiredNCName(final String attributeName, final String targetNamespace, final XMLStreamReader reader) throws SmComplexTypeException
	{
		final String name = reader.getAttributeValue(null, attributeName);
		if (null != name)
		{
			final SmSimpleType<A> atomicType = bootstrap.getAtomicType(SmNativeType.NCNAME);
			try
			{
				final List<A> value = atomicType.validate(name);
				if (value.size() > 0)
				{
					return new QName(targetNamespace, atomBridge.getString(value.get(0)));
				}
				else
				{
					throw new AssertionError();
				}
			}
			catch (final SmDatatypeException e)
			{
				final SmSimpleTypeException ste = new SmSimpleTypeException(name, atomicType, e);
				throw new SmAttributeUseException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()), ste);
			}
		}
		else
		{
			throw new SmMissingAttributeException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()));
		}
	}

	/**
	 * Obtains the required {@link #LN_BASE} or {@link #LN_REF} attribute as an expanded-QName.
	 */
	private QName requiredQName(final String attributeName, final boolean isChameleon, final String targetNamespace, final XMLStreamReader reader) throws SmComplexTypeException
	{
		final String strval = reader.getAttributeValue(null, attributeName);
		if (null != strval)
		{
			return optionalQName(strval, attributeName, isChameleon, targetNamespace, reader);
		}
		else
		{
			throw new SmMissingAttributeException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()));
		}
	}

	private QName resolveUsingTargetNamespace(final String name, final String targetNamespace, final NamespaceContext ctxt) throws SmSimpleTypeException
	{
		PreCondition.assertArgumentNotNull(name);
		final String prefix = getPrefix(name);
		if (prefix.length() > 0)
		{
			final String namespaceURI = ctxt.getNamespaceURI(prefix);
			checkPrefixBound(prefix, namespaceURI, name);
			return new QName(conditionNamespaceURI(namespaceURI), getLocalName(name), prefix);
		}
		else
		{
			return new QName(targetNamespace, name);
		}
	}

	private QName resolveUsingXMLNamespaces(final String initialValue, final NamespaceContext ctxt) throws SmSimpleTypeException
	{
		PreCondition.assertArgumentNotNull(initialValue);
		final String prefix = getPrefix(initialValue);
		if (prefix.length() > 0)
		{
			final String namespaceURI = ctxt.getNamespaceURI(prefix);
			checkPrefixBound(prefix, namespaceURI, initialValue);
			return new QName(conditionNamespaceURI(namespaceURI), getLocalName(initialValue), prefix);
		}
		else
		{
			final String namespaceURI = ctxt.getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX);
			checkPrefixBound(prefix, namespaceURI, initialValue);
			return new QName(conditionNamespaceURI(namespaceURI), initialValue);
		}
	}

	/**
	 * xs:restriction (in xs:complexContent) <br/>
	 * We don't return anything because this affects multiple aspects of the complex type.
	 */
	private void restrictionInComplexContentTag(final XMLType<A> complexType, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		if (redefine)
		{
			try
			{
				final QName baseName = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
				ensureReferenceType(baseName, reader.getLocation(), redefine, cache);
			}
			catch (final SmException e)
			{
				m_errors.error(e.getCause());
			}
		}
		else
		{
			try
			{
				baseTypeDefinitionInComplexContent(complexType, SmDerivationMethod.Restriction, reader, redefine, cache, module, targetNamespace);
			}
			catch (final SmException e)
			{
				m_errors.error(e);
			}
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_BASE.equals(localName))
				{
					// Already known.
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attribute with non-schema namespace}
			}
		}

		final CmMachine<String> machine = new CmMachine<String>(restrictionInComplexContentTable, EPSILON);
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (!machine.step(localName))
							{
								reportUnexpectedElementTag(LN_RESTRICTION, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
							else
							{
								if (LN_GROUP.equals(localName))
								{
									complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
									try
									{
										complexType.m_contentModel = groupParticleTag(new XMLScope<A>(complexType), reader, cache, module, redefine, targetNamespace);
									}
									catch (final XMLModelGroupUseException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_ALL.equals(localName))
								{
									complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
									try
									{
										complexType.m_contentModel = compositorOutsideGroupTag(SmModelGroup.SmCompositor.All, new XMLScope<A>(complexType), localName, reader, cache, module, redefine, targetNamespace);
									}
									catch (final XMLCompositorOutsideGroupException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_CHOICE.equals(localName))
								{
									complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
									try
									{
										complexType.m_contentModel = compositorOutsideGroupTag(SmModelGroup.SmCompositor.Choice, new XMLScope<A>(complexType), localName, reader, cache, module, redefine, targetNamespace);
									}
									catch (final XMLCompositorOutsideGroupException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_SEQUENCE.equals(localName))
								{
									complexType.m_contentKind = complexType.m_contentKind.isMixed() ? XMLContentTypeKind.Mixed : XMLContentTypeKind.ElementOnly;
									try
									{
										complexType.m_contentModel = compositorOutsideGroupTag(SmModelGroup.SmCompositor.Sequence, new XMLScope<A>(complexType), localName, reader, cache, module, redefine, targetNamespace);
									}
									catch (final XMLCompositorOutsideGroupException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_ATTRIBUTE.equals(localName))
								{
									attributeLocalTag(reader, cache, module, redefine, targetNamespace, complexType.getAttributeUses(), complexType.prohibited, new XMLScope<A>(complexType));
								}
								else if (LN_ATTRIBUTE_GROUP.equals(localName))
								{
									try
									{
										complexType.getAttributeGroups().add(attribGroupRefTag(reader, cache, module, targetNamespace));
									}
									catch (final XMLAttributeGroupException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_ANY_ATTRIBUTE.equals(localName))
								{
									complexType.attributeWildcard = anyAttributeTag(reader, targetNamespace, module);
								}
								else if (LN_ANNOTATION.equals(localName))
								{
									annotationTag(reader, module);
								}
								else
								{
									throw new AssertionError(reader.getName());
								}
							}
						}
						else
						{
							skipTag(reader);
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					if (!machine.end())
					{
						reportUnexpectedEnd(LN_RESTRICTION, reader.getLocation());
						skipTag(reader);
					}
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	/**
	 * xs:restriction (simple content)
	 */
	private void restrictionInSimpleContentTag(final XMLType<A> complexType, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		// We're restriction a simple type by adding facets so it makes sense
		// that we are going to need an
		// anonymous type to hang the facets from
		complexType.simpleType = cache.registerAnonymousType(new XMLScope<A>(complexType), getFrozenLocation(reader.getLocation()));
		complexType.simpleType.setSimpleFlag();

		try
		{
			final QName baseName = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
			ensureReferenceType(baseName, reader.getLocation(), redefine, cache);
			final XMLTypeRef<A> baseType = new XMLTypeRef<A>(baseName);
			complexType.setBase(baseType, SmDerivationMethod.Restriction);
		}
		catch (final SmException e)
		{
			m_errors.error(e);
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_BASE.equals(localName))
				{
					// Aleady known.
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		final CmMachine<String> machine = new CmMachine<String>(restrictionInSimpleContentTable, EPSILON);
		boolean missingMaxInclusive = true;
		boolean missingMinExclusive = true;
		boolean missingMinInclusive = true;
		boolean missingLength = true;
		boolean missingMinLength = true;
		boolean missingMaxLength = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (!machine.step(localName))
							{
								reportUnexpectedElementTag(LN_COMPLEX_CONTENT, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
							else
							{
								if (LN_SIMPLE_TYPE.equals(localName))
								{
									complexType.simpleType.setBase(simpleTypeLocalTag(new XMLScope<A>(complexType), cache, module, reader, redefine, targetNamespace), SmDerivationMethod.Restriction);
								}
								else if (LN_ENUMERATION.equals(localName))
								{
									complexType.simpleType.getEnumerations().add(enumerationTag(complexType.simpleType, reader, module));
								}
								else if (LN_MAX_EXCLUSIVE.equals(localName))
								{
									try
									{
										complexType.simpleType.getMinMaxFacets().add(minmaxTag(complexType.simpleType, SmFacetKind.MaxExclusive, localName, reader, module));
									}
									catch (final SmComplexTypeException e)
									{
										m_errors.error(e);
									}
								}
								else if (LN_MAX_INCLUSIVE.equals(localName))
								{
									missingMaxInclusive = checkWxsElementMaxOccursUnity(missingMaxInclusive, LN_RESTRICTION, LN_MAX_INCLUSIVE, reader.getLocation());
									try
									{
										complexType.simpleType.getMinMaxFacets().add(minmaxTag(complexType.simpleType, SmFacetKind.MaxInclusive, localName, reader, module));
									}
									catch (final SmComplexTypeException e)
									{
										m_errors.error(e);
									}
								}
								else if (LN_MIN_EXCLUSIVE.equals(localName))
								{
									missingMinExclusive = checkWxsElementMaxOccursUnity(missingMinExclusive, LN_RESTRICTION, LN_MIN_EXCLUSIVE, reader.getLocation());
									try
									{
										complexType.simpleType.getMinMaxFacets().add(minmaxTag(complexType.simpleType, SmFacetKind.MinExclusive, localName, reader, module));
									}
									catch (final SmComplexTypeException e)
									{
										m_errors.error(e);
									}
								}
								else if (LN_MIN_INCLUSIVE.equals(localName))
								{
									missingMinInclusive = checkWxsElementMaxOccursUnity(missingMinInclusive, LN_RESTRICTION, LN_MIN_INCLUSIVE, reader.getLocation());
									try
									{
										complexType.simpleType.getMinMaxFacets().add(minmaxTag(complexType.simpleType, SmFacetKind.MinInclusive, localName, reader, module));
									}
									catch (final SmComplexTypeException e)
									{
										m_errors.error(e);
									}
								}
								else if (LN_LENGTH.equals(localName))
								{
									missingLength = checkWxsElementMaxOccursUnity(missingLength, LN_RESTRICTION, LN_LENGTH, reader.getLocation());
									try
									{
										complexType.simpleType.getLengthFacets().add(lengthTag(complexType, true, true, localName, module, reader));
									}
									catch (final SmComplexTypeException e)
									{
										m_errors.error(e);
									}
								}
								else if (LN_MIN_LENGTH.equals(localName))
								{
									missingMinLength = checkWxsElementMaxOccursUnity(missingMinLength, LN_RESTRICTION, LN_MIN_LENGTH, reader.getLocation());
									try
									{
										complexType.simpleType.getLengthFacets().add(lengthTag(complexType, true, false, localName, module, reader));
									}
									catch (final SmComplexTypeException e)
									{
										m_errors.error(e);
									}
								}
								else if (LN_MAX_LENGTH.equals(localName))
								{
									missingMaxLength = checkWxsElementMaxOccursUnity(missingMaxLength, LN_RESTRICTION, LN_MAX_LENGTH, reader.getLocation());
									try
									{
										complexType.simpleType.getLengthFacets().add(lengthTag(complexType, false, true, localName, module, reader));
									}
									catch (final SmComplexTypeException e)
									{
										m_errors.error(e);
									}
								}
								else if (LN_WHITE_SPACE.equals(localName))
								{
									try
									{
										complexType.simpleType.setWhiteSpacePolicy(whiteSpaceTag(reader, module));
									}
									catch (final SmComplexTypeException e)
									{
										m_errors.error(e);
									}
								}
								else if (LN_PATTERN.equals(localName))
								{
									complexType.simpleType.getPatternFacets().add(patternTag(complexType.simpleType, reader, module));
								}
								else if (LN_FRACTION_DIGITS.equals(localName))
								{
									try
									{
										complexType.simpleType.getFractionDigitsFacets().add(fractionDigitsTag(complexType.simpleType, reader, module));
									}
									catch (final SmComplexTypeException e)
									{
										m_errors.error(e);
									}
								}
								else if (LN_TOTAL_DIGITS.equals(localName))
								{
									complexType.simpleType.getTotalDigitsFacets().add(totalDigitsTag(complexType.simpleType, reader, module));
								}
								else if (LN_ATTRIBUTE.equals(localName))
								{
									attributeLocalTag(reader, cache, module, redefine, targetNamespace, complexType.getAttributeUses(), complexType.prohibited, new XMLScope<A>(complexType));
								}
								else if (LN_ATTRIBUTE_GROUP.equals(localName))
								{
									try
									{
										complexType.getAttributeGroups().add(attribGroupRefTag(reader, cache, module, targetNamespace));
									}
									catch (final XMLAttributeGroupException e)
									{
										m_errors.error(e.getCause());
									}
								}
								else if (LN_ANY_ATTRIBUTE.equals(localName))
								{
									complexType.attributeWildcard = anyAttributeTag(reader, targetNamespace, module);
								}
								else if (LN_ANNOTATION.equals(localName))
								{
									annotationTag(reader, module);
								}
								else
								{
									throw new AssertionError(reader.getName());
								}
							}
						}
						else
						{
							skipTag(reader);
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					if (!machine.end())
					{
						reportUnexpectedEnd(LN_RESTRICTION, reader.getLocation());
					}
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	/**
	 * xs:restriction (simple type)
	 */
	private void restrictionTag(final XMLType<A> simpleType, final XMLSchemaModule<A> module, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		if (!redefine)
		{
		}
		else
		{
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_BASE.equals(localName))
				{
					try
					{
						final QName name = requiredQName(LN_BASE, module.isChameleon(), targetNamespace, reader);
						if (!redefine)
						{
							ensureReferenceType(name, reader.getLocation(), redefine, cache);
							simpleType.setBase(new XMLTypeRef<A>(name), SmDerivationMethod.Restriction);
						}
					}
					catch (final SmComplexTypeException e)
					{
						m_errors.error(e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean missingMaxExclusive = true;
		boolean missingMaxInclusive = true;
		boolean missingMinExclusive = true;
		boolean missingMinInclusive = true;
		boolean missingLength = true;
		boolean missingMinLength = true;
		boolean missingMaxLength = true;

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_SIMPLE_TYPE.equals(localName))
							{
								if (null == simpleType.getBaseRef())
								{
									final XMLTypeRef<A> baseType = simpleTypeLocalTag(new XMLScope<A>(simpleType), cache, module, reader, redefine, targetNamespace);
									simpleType.setBase(baseType, SmDerivationMethod.Restriction);
								}
								else
								{
									// Already set by either a "base" attribute
									// or a preceding xs:simpleType sibling.
									m_errors.error(new SmSimpleTypeRestrictionException(getFrozenLocation(reader.getLocation())));
								}
								firstElement = false;
							}
							else if (LN_ENUMERATION.equals(localName))
							{
								simpleType.getEnumerations().add(enumerationTag(simpleType, reader, module));
								firstElement = false;
							}
							else if (LN_MAX_EXCLUSIVE.equals(localName))
							{
								missingMaxExclusive = checkWxsElementMaxOccursUnity(missingMaxExclusive, LN_RESTRICTION, LN_MAX_EXCLUSIVE, reader.getLocation());
								try
								{
									simpleType.getMinMaxFacets().add(minmaxTag(simpleType, SmFacetKind.MaxExclusive, localName, reader, module));
								}
								catch (final SmComplexTypeException e)
								{
									m_errors.error(e);
								}
								firstElement = false;
							}
							else if (LN_MAX_INCLUSIVE.equals(localName))
							{
								missingMaxInclusive = checkWxsElementMaxOccursUnity(missingMaxInclusive, LN_RESTRICTION, LN_MAX_INCLUSIVE, reader.getLocation());
								try
								{
									simpleType.getMinMaxFacets().add(minmaxTag(simpleType, SmFacetKind.MaxInclusive, localName, reader, module));
								}
								catch (final SmComplexTypeException e)
								{
									m_errors.error(e);
								}
								firstElement = false;
							}
							else if (LN_MIN_EXCLUSIVE.equals(localName))
							{
								missingMinExclusive = checkWxsElementMaxOccursUnity(missingMinExclusive, LN_RESTRICTION, LN_MIN_EXCLUSIVE, reader.getLocation());
								try
								{
									simpleType.getMinMaxFacets().add(minmaxTag(simpleType, SmFacetKind.MinExclusive, localName, reader, module));
								}
								catch (final SmComplexTypeException e)
								{
									m_errors.error(e);
								}
								firstElement = false;
							}
							else if (LN_MIN_INCLUSIVE.equals(localName))
							{
								missingMinInclusive = checkWxsElementMaxOccursUnity(missingMinInclusive, LN_RESTRICTION, LN_MIN_INCLUSIVE, reader.getLocation());
								try
								{
									simpleType.getMinMaxFacets().add(minmaxTag(simpleType, SmFacetKind.MinInclusive, localName, reader, module));
								}
								catch (final SmComplexTypeException e)
								{
									m_errors.error(e);
								}
								firstElement = false;
							}
							else if (LN_LENGTH.equals(localName))
							{
								missingLength = checkWxsElementMaxOccursUnity(missingLength, LN_RESTRICTION, LN_LENGTH, reader.getLocation());
								try
								{
									simpleType.getLengthFacets().add(lengthTag(simpleType, true, true, localName, module, reader));
								}
								catch (final SmComplexTypeException e)
								{
									m_errors.error(e);
								}
								firstElement = false;
							}
							else if (LN_MIN_LENGTH.equals(localName))
							{
								missingMinLength = checkWxsElementMaxOccursUnity(missingMinLength, LN_RESTRICTION, LN_MIN_LENGTH, reader.getLocation());
								try
								{
									simpleType.getLengthFacets().add(lengthTag(simpleType, true, false, localName, module, reader));
								}
								catch (final SmComplexTypeException e)
								{
									m_errors.error(e);
								}
								firstElement = false;
							}
							else if (LN_MAX_LENGTH.equals(localName))
							{
								missingMaxLength = checkWxsElementMaxOccursUnity(missingMaxLength, LN_RESTRICTION, LN_MAX_LENGTH, reader.getLocation());
								try
								{
									simpleType.getLengthFacets().add(lengthTag(simpleType, false, true, localName, module, reader));
								}
								catch (final SmComplexTypeException e)
								{
									m_errors.error(e);
								}
								firstElement = false;
							}
							else if (LN_WHITE_SPACE.equals(localName))
							{
								try
								{
									simpleType.setWhiteSpacePolicy(whiteSpaceTag(reader, module));
								}
								catch (final SmComplexTypeException e)
								{
									m_errors.error(e);
								}
								firstElement = false;
							}
							else if (LN_PATTERN.equals(localName))
							{
								simpleType.getPatternFacets().add(patternTag(simpleType, reader, module));
								firstElement = false;
							}
							else if (LN_FRACTION_DIGITS.equals(localName))
							{
								try
								{
									simpleType.getFractionDigitsFacets().add(fractionDigitsTag(simpleType, reader, module));
								}
								catch (final SmComplexTypeException e)
								{
									m_errors.error(e);
								}
								firstElement = false;
							}
							else if (LN_TOTAL_DIGITS.equals(localName))
							{
								simpleType.getTotalDigitsFacets().add(totalDigitsTag(simpleType, reader, module));
								firstElement = false;
							}
							else if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_RESTRICTION, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_RESTRICTION, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	private void schemaTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module) throws XMLStreamException, SmAbortException
	{
		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_TARGET_NAMESPACE.equals(localName))
				{
					try
					{
						module.setTargetNamespace(anyURI(reader.getAttributeValue(i)));
						if (!m_processRepeatedNamespaces && cache.m_seenNamespaces.contains(module.getTargetNamespace()))
						{
							// Ignore this schema.
							skipTag(reader);
							return;
						}
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if ("elementFormDefault".equals(localName))
				{
					try
					{
						module.elementQualified = qualified(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if ("attributeFormDefault".equals(localName))
				{
					try
					{
						module.attributeQualified = qualified(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if ("blockDefault".equals(localName))
				{
					try
					{
						control(reader.getAttributeValue(i), EnumSet.of(SmDerivationMethod.Extension, SmDerivationMethod.Restriction, SmDerivationMethod.Substitution), module.blockDefault);
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if ("finalDefault".equals(localName))
				{
					try
					{
						control(reader.getAttributeValue(i), EnumSet.of(SmDerivationMethod.Extension, SmDerivationMethod.Restriction), module.finalDefault);
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					module.m_id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if ("version".equals(localName))
				{
					try
					{
						module.m_version = token(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (XMLConstants.XML_NS_URI.equals(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if ("lang".equals(localName))
				{
					try
					{
						module.m_lang = lang(reader.getAttributeValue(i));
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInXmlNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		if (module.isRedefine() || module.isInclude())
		{
			if (module.getTargetNamespace() != null)
			{
				if ((module.getContainingModule().getTargetNamespace() == null) || (!module.getContainingModule().getTargetNamespace().equals(module.getTargetNamespace())))
				{
					if (module.isRedefine())
					{
						m_errors.error(new SmRedefinitionNamespaceMismatchException(getFrozenLocation(reader.getLocation())));
					}
					else
					{
						m_errors.error(new SmInclusionNamespaceMismatchException(getFrozenLocation(reader.getLocation())));
					}
					skipTag(reader);
					return;
				}
			}
		}

		// Knowing the local targetNamespace, and the ancestors of the module,
		// we can compute the targetNamespace.
		final String targetNamespace = module.computeTargetNamespace();

		if (module.getTargetNamespace() != null)
		{
			cache.m_seenNamespaces.add(module.getTargetNamespace());
		}

		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_COMPLEX_TYPE.equals(localName))
							{
								try
								{
									final XMLType<A> complexType = complexTypeGlobalTag(reader, cache, module, false, targetNamespace);
									cache.m_globalTypes.put(complexType.getName(), complexType);
								}
								catch (final XMLComplexTypeException e)
								{
									m_errors.error(e.getCause());
								}
							}
							else if (LN_SIMPLE_TYPE.equals(localName))
							{
								try
								{
									final XMLType<A> simpleType = simpleTypeGlobalTag(reader, cache, module, false, targetNamespace);
									cache.m_globalTypes.put(simpleType.getName(), simpleType);
								}
								catch (final XMLSimpleTypeException e)
								{
									m_errors.error(e.getCause());
								}
							}
							else if (LN_ELEMENT.equals(localName))
							{
								try
								{
									final XMLElement<A> element = elementTag(reader, cache, module, targetNamespace);
									cache.m_elements.put(element.getName(), element);
								}
								catch (final XMLElementException e)
								{
									m_errors.error(e.getCause());
								}
							}
							else if (LN_ATTRIBUTE.equals(localName))
							{
								try
								{
									final XMLAttribute<A> attribute = attributeTag(reader, cache, module, targetNamespace);
									cache.m_attributes.put(attribute.getName(), attribute);
								}
								catch (final XMLAttributeException e)
								{
									m_errors.error(e.getCause());
								}
							}
							else if (LN_GROUP.equals(localName))
							{
								try
								{
									final XMLModelGroup<A> group = groupTag(reader, cache, module, false, targetNamespace);
									cache.m_modelGroups.put(group.getName(), group);
								}
								catch (final XMLModelGroupException e)
								{
									m_errors.error(e.getCause());
								}
							}
							else if (LN_ATTRIBUTE_GROUP.equals(localName))
							{
								try
								{
									final XMLAttributeGroup<A> attributeGroup = attribGroupTag(reader, cache, module, false, targetNamespace);
									cache.m_attributeGroups.put(attributeGroup.getName(), attributeGroup);
								}
								catch (final XMLAttributeGroupException e)
								{
									m_errors.error(e.getCause());
								}
							}
							else if (LN_INCLUDE.equals(localName))
							{
								includeTag(cache, module, reader);
							}
							else if (LN_IMPORT.equals(localName))
							{
								importTag(cache, module, reader, targetNamespace);
							}
							else if (LN_REDEFINE.equals(localName))
							{
								redefineTag(cache, module, reader, targetNamespace);
							}
							else if (LN_NOTATION.equals(localName))
							{
								try
								{
									/* final NotationImpl<A> notation = */
									notationTag(cache, reader, targetNamespace, module);
								}
								catch (final XMLNotationException e)
								{
									m_errors.error(e.getCause());
								}
							}
							else if (LN_ANNOTATION.equals(localName))
							{
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_SCHEMA, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
					// Ignore.
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	private SmRestrictedXPath selectorTag(final XMLStreamReader reader, final XMLSchemaModule<A> module) throws XMLStreamException, XMLSelectorException, SmAbortException
	{
		SmRestrictedXPath xpath = null;
		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_XPATH.equals(localName))
				{
					final String original = reader.getAttributeValue(i);
					try
					{
						xpath = xpath(original, reader.getLocation(), reader.getName());
					}
					catch (final SmAttributeUseException e)
					{
						skipTag(reader);
						throw new XMLSelectorException(e);
					}
					if (xpath.isAttribute())
					{
						final SmDatatypeException dte = new SmDatatypeException(original, null);
						final SmSimpleTypeException ste = new SmSimpleTypeException(original, null, dte);
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), ste);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_SELECTOR, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_SELECTOR, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return xpath;
	}

	/**
	 * xs:simpleContent
	 */
	private void simpleContentTag(final XMLType<A> complexType, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		complexType.m_contentKind = XMLContentTypeKind.Simple;

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		final CmMachine<String> machine = new CmMachine<String>(simpleContentTable, EPSILON);
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (!machine.step(localName))
							{
								reportUnexpectedElementTag(LN_SIMPLE_CONTENT, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
							else
							{
								if (LN_EXTENSION.equals(localName))
								{
									extensionInSimpleContentTag(complexType, module, reader, cache, redefine, targetNamespace);
								}
								else if (LN_RESTRICTION.equals(localName))
								{
									restrictionInSimpleContentTag(complexType, reader, cache, module, redefine, targetNamespace);
								}
								else if (LN_ANNOTATION.equals(localName))
								{
									annotationTag(reader, module);
								}
								else
								{
									throw new AssertionError(reader.getName());
								}
							}
						}
						else
						{
							skipTag(reader);
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					if (!machine.end())
					{
						reportUnexpectedEnd(LN_SIMPLE_CONTENT, reader.getLocation());
					}
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	/**
	 * Content for an xs:simpleType (either global or local definition). <br>
	 * Content: (xs:annotation?, (xs:restriction | xs:list | xs:union))
	 */
	private void simpleTypeContentTag(final XMLType<A> simpleType, final XMLSchemaModule<A> module, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		// Derivation property must be null so that we check that we got the
		// required child elements.
		if (!redefine)
		{
		}
		else
		{
		}

		boolean firstElement = true;
		boolean missingRLU = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_RESTRICTION.equals(localName))
							{
								missingRLU = checkWxsElementMaxOccursUnity(missingRLU, LN_SIMPLE_TYPE, LN_RESTRICTION, reader.getLocation());
								restrictionTag(simpleType, module, reader, cache, redefine, targetNamespace);
								firstElement = false;
							}
							else if (LN_LIST.equals(localName))
							{
								missingRLU = checkWxsElementMaxOccursUnity(missingRLU, LN_SIMPLE_TYPE, LN_LIST, reader.getLocation());
								listTag(simpleType, module, reader, cache, redefine, targetNamespace);
								firstElement = false;
							}
							else if (LN_UNION.equals(localName))
							{
								missingRLU = checkWxsElementMaxOccursUnity(missingRLU, LN_SIMPLE_TYPE, LN_UNION, reader.getLocation());
								unionTag(simpleType, module, reader, cache, redefine, targetNamespace);
								firstElement = false;
							}
							else if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_SIMPLE_CONTENT, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_SIMPLE_CONTENT, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
					// Ignore.
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}

		if (missingRLU)
		{
			// Expecting xs:restriction | xs:list | xs:union
			m_errors.error(new SmUnexpectedEndException(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, LN_SIMPLE_TYPE), getFrozenLocation(reader.getLocation())));
		}
	}

	/**
	 * xs:simpleType (global definition)
	 */
	private XMLType<A> simpleTypeGlobalTag(final XMLStreamReader reader, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final boolean redefine, final String targetNamespace) throws XMLStreamException, XMLSimpleTypeException, SmAbortException
	{
		final XMLType<A> simpleType;
		if (!redefine)
		{
			try
			{
				simpleType = cache.registerType(requiredNCName(LN_NAME, targetNamespace, reader), getFrozenLocation(reader.getLocation()));
			}
			catch (final SmException e)
			{
				skipTag(reader);
				throw new XMLSimpleTypeException(e);
			}
		}
		else
		{
			try
			{
				simpleType = cache.dereferenceType(requiredNCName(LN_NAME, targetNamespace, reader), reader.getLocation(), redefine);
			}
			catch (final SmException e)
			{
				skipTag(reader);
				throw new XMLSimpleTypeException(e);
			}
		}
		simpleType.setSimpleFlag();

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_FINAL.equals(localName))
				{
					try
					{
						control(reader.getAttributeValue(i), EnumSet.of(SmDerivationMethod.List, SmDerivationMethod.Union, SmDerivationMethod.Restriction), simpleType.getFinal());
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_NAME.equals(localName))
				{
					// Already known.
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		if (!redefine)
		{
		}
		else
		{
		}
		simpleTypeContentTag(simpleType, module, reader, cache, redefine, targetNamespace);
		return simpleType;
	}

	/**
	 * xs:simpleType (local definition)
	 */
	private XMLTypeRef<A> simpleTypeLocalTag(final XMLScope<A> scope, final XMLSchemaCache<A> cache, final XMLSchemaModule<A> module, final XMLStreamReader reader, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		final XMLType<A> simpleType = cache.registerAnonymousType(scope, getFrozenLocation(reader.getLocation()));
		simpleType.setSimpleFlag();

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		simpleTypeContentTag(simpleType, module, reader, cache, redefine, targetNamespace);

		return new XMLTypeRef<A>(simpleType);
	}

	/**
	 * Skips the remaining content and end element (used during development).
	 */
	private void skipTag(final XMLStreamReader reader) throws XMLStreamException
	{
		boolean done = false;

		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					skipTag(reader);
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				{
				}
				break;
				case XMLStreamConstants.COMMENT:
				{
				}
				break;
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
	}

	private String token(final String initialValue) throws SmSimpleTypeException
	{
		final SmSimpleType<A> atomicType = bootstrap.getAtomicType(SmNativeType.TOKEN);
		try
		{
			final List<A> atoms = atomicType.validate(initialValue);
			final A atom = atoms.get(0);
			return atomBridge.getString(atom);
		}
		catch (final SmDatatypeException cause)
		{
			throw new SmSimpleTypeException(initialValue, atomicType, cause);
		}
	}

	/**
	 * xs:totalDigits
	 */
	private XMLTotalDigitsFacet<A> totalDigitsTag(final XMLType<A> simpleType, final XMLStreamReader reader, final XMLSchemaModule<A> module) throws XMLStreamException, SmAbortException
	{
		final XMLTotalDigitsFacet<A> facet = new XMLTotalDigitsFacet<A>(simpleType, getFrozenLocation(reader.getLocation()));

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_ID.equals(localName))
				{
					facet.id = checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else if (LN_FIXED.equals(localName))
				{
					try
					{
						facet.fixed = fixed(reader.getAttributeValue(i), reader.getLocation(), reader.getName());
					}
					catch (final SmAttributeUseException e)
					{
						m_errors.error(e);
					}
				}
				else if (LN_VALUE.equals(localName))
				{
					final String strval = reader.getAttributeValue(i);
					try
					{
						facet.value = positiveInteger(strval);
					}
					catch (final SmSimpleTypeException e)
					{
						reportAttributeUseError(reader.getName(), reader.getAttributeName(i), reader.getLocation(), e);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_TOTAL_DIGITS, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_TOTAL_DIGITS, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return facet;
	}

	private boolean trueOrFalse(final String initialValue) throws SmSimpleTypeException
	{
		final SmSimpleType<A> atomicType = bootstrap.getAtomicType(SmNativeType.BOOLEAN);
		try
		{
			final List<A> value = atomicType.validate(initialValue);
			if (value.size() > 0)
			{
				return atomBridge.getBoolean(value.get(0));
			}
			else
			{
				throw new AssertionError();
			}
		}
		catch (final SmDatatypeException cause)
		{
			throw new SmSimpleTypeException(initialValue, atomicType, cause);
		}
	}

	/**
	 * Obtains a {@link #LN_TYPE}, {@link #LN_ITEM_TYPE} or tokenized {@link #LN_MEMBER_TYPES} attribute value as type
	 * reference.
	 */
	private XMLTypeRef<A> typeRef(final String initialValue, final String attributeName, final boolean isChameleon, final String targetNamespace, final XMLStreamReader reader) throws SmComplexTypeException
	{
		if (null != initialValue)
		{
			return new XMLTypeRef<A>(optionalQName(initialValue, attributeName, isChameleon, targetNamespace, reader));
		}
		else
		{
			throw new SmMissingAttributeException(reader.getName(), new QName(attributeName), getFrozenLocation(reader.getLocation()));
		}
	}

	private void unionTag(final XMLType<A> unionType, final XMLSchemaModule<A> module, final XMLStreamReader reader, final XMLSchemaCache<A> cache, final boolean redefine, final String targetNamespace) throws XMLStreamException, SmAbortException
	{
		unionType.setBase(ANY_SIMPLE_TYPE, SmDerivationMethod.Union);

		// Use this to detect missing both memberTypes attribute and
		// <simpleType> child.
		// Note that we have to perform the "clear" rather than assert the
		// emptiness because
		// we are attempting to collect the maximum amount of feedback from this
		// parse.
		unionType.memberRefs.clear();

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_MEMBER_TYPES.equals(localName))
				{
					final StringTokenizer tokenizer = new StringTokenizer(reader.getAttributeValue(i), " ");
					while (tokenizer.hasMoreTokens())
					{
						final String token = tokenizer.nextToken();
						try
						{
							final XMLTypeRef<A> memberType = typeRef(token, LN_MEMBER_TYPES, module.isChameleon(), targetNamespace, reader);
							ensureReferenceType(memberType.getName(), reader.getLocation(), redefine, cache);
							unionType.memberRefs.add(memberType);
						}
						catch (final SmComplexTypeException e)
						{
							m_errors.error(e);
						}
					}
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_SIMPLE_TYPE.equals(localName))
							{
								unionType.memberRefs.add(simpleTypeLocalTag(new XMLScope<A>(unionType), cache, module, reader, false, targetNamespace));
								firstElement = false;
							}
							else if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_UNION, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_UNION, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		if (unionType.memberRefs.isEmpty())
		{
			m_errors.error(new SmSimpleTypeUnionException(getFrozenLocation(reader.getLocation())));
		}
	}

	private XMLIdentityConstraint<A> uniqueTag(final XMLSchemaCache<A> cache, final XMLStreamReader reader, final String targetNamespace, final XMLSchemaModule<A> module) throws XMLStreamException, XMLIdentityConstraintException, SmAbortException
	{
		final XMLIdentityConstraint<A> unique;
		try
		{
			final QName name = requiredNCName(LN_NAME, targetNamespace, reader);
			module.registerIdentityConstraintName(name, reader.getLocation());
			unique = cache.registerIdentityConstraint(SmIdentityConstraintKind.Unique, name, getFrozenLocation(reader.getLocation()));
		}
		catch (final SmException e)
		{
			skipTag(reader);
			throw new XMLIdentityConstraintException(e);
		}

		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_NAME.equals(localName))
				{
					// Already known.
				}
				else if (LN_ID.equals(localName))
				{
					checkID(reader.getAttributeValue(i), reader.getLocation(), reader.getName(), module);
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean missingSelector = true;
		boolean missingFields = true;
		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_UNIQUE, reader.getLocation());
								annotationTag(reader, module);
							}
							else if (LN_SELECTOR.equals(localName))
							{
								missingSelector = checkWxsElementMaxOccursUnity(missingSelector, LN_UNIQUE, LN_SELECTOR, reader.getLocation());
								try
								{
									unique.selector = selectorTag(reader, module);
								}
								catch (final XMLSelectorException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else if (LN_FIELD.equals(localName))
							{
								if (missingSelector)
								{
									m_errors.error(new SmUnexpectedElementException(reader.getName(), getFrozenLocation(reader.getLocation()), new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, LN_FIELD), getFrozenLocation(reader.getLocation())));
								}
								missingFields = false;
								try
								{
									unique.fields.add(fieldTag(reader, module));
								}
								catch (final XMLFieldException e)
								{
									m_errors.error(e.getCause());
								}
								firstElement = false;
							}
							else
							{
								reportUnexpectedElementTag(LN_UNIQUE, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		if (missingFields)
		{
			m_errors.error(new SmUnexpectedEndException(reader.getName(), getFrozenLocation(reader.getLocation())));
		}
		return unique;
	}

	private XMLCardinality use(final String strval) throws SmSimpleTypeException
	{
		if ("optional".equals(strval))
		{
			return XMLCardinality.OPTIONAL;
		}
		else if ("prohibited".equals(strval))
		{
			return XMLCardinality.NONE;
		}
		else if ("required".equals(strval))
		{
			return XMLCardinality.EXACTLY_ONE;
		}
		else
		{
			final SmDatatypeException dte = new SmDatatypeException(strval, null);
			throw new SmSimpleTypeException(strval, null, dte);
		}
	}

	private String validateString(final String initialValue, final SmNativeType derivedType) throws SmSimpleTypeException
	{
		final SmSimpleType<A> atomicType = bootstrap.getAtomicType(derivedType);
		try
		{
			final List<A> value = atomicType.validate(initialValue);
			if (value.size() > 0)
			{
				return atomBridge.getString(value.get(0));
			}
			else
			{
				return null;
			}
		}
		catch (final SmDatatypeException cause)
		{
			throw new SmSimpleTypeException(initialValue, atomicType, cause);
		}
	}

	private SmWhiteSpacePolicy whiteSpaceTag(final XMLStreamReader reader, final XMLSchemaModule<A> module) throws XMLStreamException, SmComplexTypeException, SmAbortException
	{
		SmWhiteSpacePolicy policy = SmWhiteSpacePolicy.PRESERVE;
		final int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++)
		{
			final String namespaceURI = reader.getAttributeNamespace(i);
			if (isGlobal(namespaceURI))
			{
				final String localName = reader.getAttributeLocalName(i);
				if (LN_VALUE.equals(localName))
				{
					final String value = reader.getAttributeValue(i);
					if ("preserve".equals(value))
					{
						policy = SmWhiteSpacePolicy.PRESERVE;
					}
					else if ("replace".equals(value))
					{
						policy = SmWhiteSpacePolicy.REPLACE;
					}
					else if ("collapse".equals(value))
					{
						policy = SmWhiteSpacePolicy.COLLAPSE;
					}
					else
					{
						final SmDatatypeException dte = new SmDatatypeException(value, null);
						final SmSimpleTypeException ste = new SmSimpleTypeException(value, null, dte);
						throw new SmAttributeUseException(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()), ste);
					}
				}
				else
				{
					reportAttributeInGlobalNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
				}
			}
			else if (isWXS(namespaceURI))
			{
				reportAttributeInWxsNamespace(reader.getName(), reader.getAttributeName(i), getFrozenLocation(reader.getLocation()));
			}
			else
			{
				// {any attributes with non-schema namespace}
			}
		}

		boolean firstElement = true;
		boolean done = false;
		while (!done)
		{
			final int event = reader.next();

			switch (event)
			{
				case XMLStreamConstants.START_ELEMENT:
				{
					m_pms.pushContext();
					try
					{
						copyNamespaces(reader, m_pms);
						if (isWXS(reader.getNamespaceURI()))
						{
							final String localName = reader.getLocalName();
							if (LN_ANNOTATION.equals(localName))
							{
								firstElement = checkAnnotationMaxOccursUnity(firstElement, LN_WHITE_SPACE, reader.getLocation());
								annotationTag(reader, module);
							}
							else
							{
								reportUnexpectedElementTag(LN_WHITE_SPACE, reader.getName(), reader.getLocation());
								skipTag(reader);
							}
						}
						else
						{
							skipTag(reader);
							firstElement = false;
						}
					}
					finally
					{
						m_pms.popContext();
					}
				}
				break;
				case XMLStreamConstants.END_ELEMENT:
				{
					done = true;
				}
				break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.COMMENT:
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				{
				}
				break;
				default:
				{
					throw new UnsupportedOperationException(Integer.toString(event));
				}
			}
		}
		return policy;
	}

	private SmRestrictedXPath xpath(final String strval, final Location location, final QName elementName) throws SmAttributeUseException
	{
		try
		{
			final String token = token(strval);
			return m_xp.parseXPath(token, m_pms);
		}
		catch (final SmSimpleTypeException e)
		{
			throw new SmAttributeUseException(elementName, new QName(LN_XPATH), getFrozenLocation(location), e);
		}
	}

	/**
	 * (xs:annotation?)
	 */
	/*
	 * private static CmTable<String> makeAnnotationOptionalTable() { final CmTable<String> table = new
	 * CmTable<String>();
	 * 
	 * final HashMap<String, Integer> ZERO = new HashMap<String, Integer>(); ZERO.put(LN_ANNOTATION, CmTable.END);
	 * ZERO.put(EPSILON, CmTable.END);
	 * 
	 * table.put(0, ZERO);
	 * 
	 * return table; }
	 */
}
