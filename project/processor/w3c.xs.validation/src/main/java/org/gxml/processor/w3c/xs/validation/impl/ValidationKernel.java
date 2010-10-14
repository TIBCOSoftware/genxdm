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
package org.gxml.processor.w3c.xs.validation.impl;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.constraints.SmValueConstraint;
import org.genxdm.xs.enums.SmProcessContentsMode;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmDatatypeException;
import org.genxdm.xs.exceptions.SmExceptionHandler;
import org.genxdm.xs.exceptions.SmSimpleTypeException;
import org.genxdm.xs.types.SmComplexMarkerType;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmContentType;
import org.genxdm.xs.types.SmSimpleMarkerType;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmSimpleUrType;
import org.genxdm.xs.types.SmType;
import org.gxml.processor.w3c.xs.exception.CvcElementFixedValueOverriddenSimpleException;
import org.gxml.processor.w3c.xs.exception.CvcElementUnexpectedChildInNilledElementException;
import org.gxml.processor.w3c.xs.exception.CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException;
import org.gxml.processor.w3c.xs.exception.CvcUnexpectedTextInEmptyContentException;
import org.gxml.processor.w3c.xs.exception.SmExceptionSupplier;
import org.gxml.processor.w3c.xs.validation.api.VxMapping;
import org.gxml.processor.w3c.xs.validation.api.VxOutputHandler;
import org.gxml.processor.w3c.xs.validation.api.VxPSVI;
import org.gxml.processor.w3c.xs.validation.api.VxSchemaDocumentLocationStrategy;
import org.gxml.processor.w3c.xs.validation.api.VxValidationHost;
import org.gxml.processor.w3c.xs.validation.api.VxValidator;


/**
 * The workhorse of validation is this kernel. Currently there is an explicit coupling to the WXS schema model. However,
 * this class is package protected so overall, the validation API is schema model independent. In future, we may try to
 * create a more abstract kernel.
 */
final class ValidationKernel<A> implements VxValidator<A>, SmExceptionSupplier
{
	// Set by reset method. Preconditions guarantee that it is never null.
	// private final SmParticleTerm STRICT_WILDCARD = new StrictWildcard<A>();

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

	private final AtomBridge<A> m_atomBridge;
	private final AttributeManager<A> m_attributes;
	private ValidationItem<A> m_currentItem;
	private ModelPSVI<A> m_currentPSVI;

	private final ValidationItem<A> m_documentItem;
	private final ModelPSVI<A> m_documentPSVI;
	// Set by reset method. Preconditions guarantee that it is never null.
	private VxOutputHandler<A> m_downstream;
	// private Location m_location;
	private SmExceptionHandler m_errors = SmExceptionThrower.SINGLETON;
	private final VxSchemaDocumentLocationStrategy sdl;

	private final IdentityConstraintManager<A> m_icm = new IdentityConstraintManager<A>();

	private final IdManager<A> m_idm = new IdManager<A>();

	// Maintain state for each element.
	// private URI m_baseURI;
	private final ModelAnalyzerImpl<A> m_mac; // Model Analyzer Component
	private final ValidationPrefixResolver m_namespaces;

	// Index of node within document is used to determine node identity.
	private int m_nodeIndex = -1;

	// We must normalize character events into a single text node.
	private final StringBuilder m_text = new StringBuilder();
	private URI documentURI;

	public ValidationKernel(final VxValidationHost<A> host, final ValidationCache<A> cache, final VxSchemaDocumentLocationStrategy sdl)
	{
		PreCondition.assertArgumentNotNull(host, "host");
		m_atomBridge = host.getAtomBridge();
		m_namespaces = new ValidationPrefixResolver(host.getNameBridge());
		m_attributes = new AttributeManager<A>(host.getMetaBridge(), m_atomBridge, host.getNameBridge());
		m_currentItem = m_documentItem = new ValidationItem<A>();
		// A strict start is necessary to ensure that the root element has a declaration.
		// However, the specification does not seem very clear on what should be the starting mode.
		m_currentPSVI = m_documentPSVI = new ModelPSVI<A>(SmProcessContentsMode.Strict, host.getMetaBridge(), cache);

		m_mac = new ModelAnalyzerImpl<A>(host.getMetaBridge(), cache);
		this.sdl = sdl;
	}

	public void characters(final char[] ch, final int start, final int length)
	{
		m_text.append(ch, start, length);
	}

	private void checkValueConstraintForElement(final SmElement<A> elementDeclaration, final SmSimpleType<A> simpleType, final List<? extends A> actualValue) throws SmAbortException
	{
		final SmValueConstraint<A> valueConstraint = elementDeclaration.getValueConstraint();
		if (null != valueConstraint)
		{
			switch (valueConstraint.getVariety())
			{
				case Fixed:
				{
					final List<A> initialFixed = valueConstraint.getValue();

					try
					{
						final List<A> actualFixed = simpleType.validate(initialFixed);
						if (!ValidationSupport.equalValues(actualFixed, actualValue))
						{
							final String fixedC14N = m_atomBridge.getC14NString(actualFixed);
							final String actualC14N = m_atomBridge.getC14NString(actualValue);
							m_errors.error(new CvcElementFixedValueOverriddenSimpleException(elementDeclaration, fixedC14N, actualC14N, m_currentItem.getLocation()));
						}
					}
					catch (final SmDatatypeException e)
					{
						final String lexicalValue = m_atomBridge.getC14NString(initialFixed);
						m_errors.error(new SmSimpleTypeException(lexicalValue, simpleType, e));
					}
				}
				break;
				case Default:
				{
					// No problem.
				}
				break;
				default:
				{
					throw new AssertionError(valueConstraint.getVariety());
				}
			}
		}
	}

	public void endDocument() throws IOException, SmAbortException
	{
		m_mac.endDocument();

		// Check for dangling IDREFs here.
		m_idm.reportDanglingIdRefs(m_errors);

		if (null != m_downstream)
		{
			m_downstream.endDocument();
		}
	}

	public VxPSVI<A> endElement() throws IOException, SmAbortException
	{
		if (m_text.length() > 0)
		{
			try
			{
				handleText(m_text.toString());
			}
			finally
			{
				m_text.setLength(0);
			}
		}

		try
		{
			if (!m_currentItem.m_detectedText)
			{
				handleNoTextCalls();
			}

			final VxPSVI<A> psvi = m_mac.endElement();

			m_icm.endElement(m_currentPSVI, m_currentItem);

			return psvi;
		}
		finally
		{
			m_currentItem = m_currentItem.pop();
			m_currentPSVI = m_currentPSVI.getParent();

			// Maintain prefix mapping information.
			m_namespaces.popContext();

			if (null != m_downstream)
			{
				m_downstream.endElement();
			}
		}
	}

	private void handleNoTextCalls() throws IOException, SmAbortException
	{
		final SmType<A> elementType = m_currentPSVI.getType();
		if (null != elementType)
		{
			if (elementType instanceof SmSimpleType<?>)
			{
				handleNoTextCallsForSimpleContentModel((SmSimpleType<A>)elementType);
			}
			else
			{
				final SmComplexType<A> complexType = (SmComplexType<A>)elementType;
				final SmContentType<A> contentType = complexType.getContentType();
				switch (contentType.getKind())
				{
					case Simple:
					{
						handleNoTextCallsForSimpleContentModel(contentType.getSimpleType());
					}
					break;
					case Empty:
					case ElementOnly:
					case Mixed:
					{
						// Do nothing
					}
					break;
					default:
					{
						throw new AssertionError(contentType.getKind());
					}
				}
			}
		}
	}

	private void handleNoTextCallsForSimpleContentModel(final SmSimpleType<A> simpleType) throws IOException, SmAbortException
	{
		if (m_currentPSVI.isNilled())
		{
			// OK
		}
		else
		{
			// any default or fixed values.
			final SmElement<A> declaration = m_currentPSVI.getDeclaration();
			final SmValueConstraint<A> valueConstraint = (null != declaration) ? declaration.getValueConstraint() : null;
			if (null != valueConstraint)
			{
				switch (valueConstraint.getVariety())
				{
					case Fixed:
					case Default:
					{
						final List<A> initialValue = valueConstraint.getValue();
						try
						{
							final List<A> actualValue = simpleType.validate(initialValue);

							m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
							m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

							if (null != m_downstream)
							{
								m_downstream.text(actualValue);
							}
						}
						catch (final SmDatatypeException e)
						{
							final String lexicalValue = m_atomBridge.getC14NString(initialValue);
							m_errors.error(new SmSimpleTypeException(lexicalValue, simpleType, e));

							m_idm.text(initialValue, simpleType, m_currentItem, m_errors, m_atomBridge);
							m_icm.text(initialValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

							if (null != m_downstream)
							{
								m_downstream.text(initialValue);
							}
						}
					}
					break;
					default:
					{
						throw new AssertionError(valueConstraint.getVariety());
					}
				}
			}
			else
			{
				// If no value given above then call the validator with an empty string
				// which will throw an exception if having no text is a problem.
				try
				{
					final List<A> actualValue = simpleType.validate("");

					m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
					m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

					if (null != m_downstream)
					{
						m_downstream.text(actualValue);
					}
				}
				catch (final SmDatatypeException e)
				{
					m_errors.error(new SmSimpleTypeException("", simpleType, e));
				}
			}
		}
	}

	private void handleText(final String initialValue) throws IOException, SmAbortException
	{
		m_nodeIndex++;

		// Remember that we got a text node.
		m_currentItem.m_detectedText = true;

		if (m_currentItem.getSuspendChecking())
		{
			if (null != m_downstream)
			{
				m_downstream.text(initialValue);
			}
			return;
		}

		switch (m_currentPSVI.getProcessContents())
		{
			case Strict:
			case Lax:
			{
				final SmElement<A> declaration = m_currentPSVI.getDeclaration();
				if (m_currentPSVI.isNilled() && (null != declaration))
				{
					m_errors.error(new CvcElementUnexpectedChildInNilledElementException(declaration, m_currentItem.getLocation()));
				}

				final SmType<A> elementType = m_currentPSVI.getType();
				if (null != elementType)
				{
					if (elementType instanceof SmSimpleMarkerType<?>)
					{
						if (elementType instanceof SmSimpleType<?>)
						{
							final SmSimpleType<A> simpleType = (SmSimpleType<A>)elementType;
							try
							{
								final List<A> actualValue = simpleType.validate(initialValue);

								if (null != declaration)
								{
									checkValueConstraintForElement(declaration, simpleType, actualValue);
								}

								m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
								m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

								if (null != m_downstream)
								{
									m_downstream.text(actualValue);
								}
							}
							catch (final SmDatatypeException e)
							{
								m_errors.error(new SmSimpleTypeException(initialValue, simpleType, e));
								if (null != m_downstream)
								{
									m_downstream.text(initialValue);
								}
							}
						}
						else if (elementType instanceof SmSimpleUrType<?>)
						{
							if (null != m_downstream)
							{
								m_downstream.text(initialValue);
							}
						}
						else
						{
							throw new AssertionError(elementType);
						}
					}
					else if (elementType instanceof SmComplexMarkerType<?>)
					{
						final SmComplexMarkerType<A> complexType = (SmComplexMarkerType<A>)elementType;
						final SmContentType<A> contentType = complexType.getContentType();
						switch (contentType.getKind())
						{
							case Simple:
							{
								final SmSimpleType<A> simpleType = contentType.getSimpleType();
								try
								{
									final List<A> actualValue = simpleType.validate(initialValue);

									if (null != declaration)
									{
										checkValueConstraintForElement(declaration, simpleType, actualValue);
									}

									m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
									m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

									if (null != m_downstream)
									{
										m_downstream.text(actualValue);
									}
								}
								catch (final SmDatatypeException e)
								{
									m_errors.error(new SmSimpleTypeException(initialValue, simpleType, e));
									if (null != m_downstream)
									{
										m_downstream.text(initialValue);
									}
								}
							}
							break;
							case ElementOnly:
							{
								if (!isWhiteSpace(initialValue))
								{
									m_errors.error(new CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException(m_currentPSVI.getName(), initialValue, m_currentItem.getLocation()));
								}
							}
							break;
							case Mixed:
							{
								if (null != declaration)
								{
									ValidationRules.checkValueConstraintForMixedContent(declaration, initialValue, m_currentItem, m_errors, m_atomBridge);
								}

								if (null != m_downstream)
								{
									m_downstream.text(initialValue);
								}
							}
							break;
							case Empty:
							{
								m_errors.error(new CvcUnexpectedTextInEmptyContentException(m_currentPSVI.getName(), initialValue, m_currentItem.getLocation()));
								if (null != m_downstream)
								{
									m_downstream.text(initialValue);
								}
							}
							break;
							default:
							{
								throw new AssertionError(contentType.getKind());
							}
						}
					}
					else
					{
						throw new AssertionError(elementType);
					}
				}
				else
				{
					if (null != m_downstream)
					{
						m_downstream.text(initialValue);
					}
				}
			}
			break;
			case Skip:
			{
				if (null != m_downstream)
				{
					m_downstream.text(initialValue);
				}
			}
			break;
			default:
			{
				throw new AssertionError(m_currentPSVI.getProcessContents());
			}
		}
	}

	public void reset()
	{
		m_namespaces.reset();
		m_attributes.reset();
		m_nodeIndex = -1;
		m_icm.reset();
	}

	public void setExceptionHandler(final SmExceptionHandler handler)
	{
		m_errors = PreCondition.assertArgumentNotNull(handler, "handler");
		m_mac.setExceptionHandler(handler);
	}

	public void setOutputHandler(final VxOutputHandler<A> handler)
	{
		m_downstream = PreCondition.assertArgumentNotNull(handler, "handler");
	}

	public void startDocument(final URI documentURI) throws IOException
	{
		this.documentURI = documentURI;

		m_currentPSVI = m_documentPSVI;
		m_currentItem = m_documentItem;
		m_mac.startDocument();

		m_nodeIndex = 0; // The document node gets to be the zeroth index.

		m_idm.reset();

		if (null != m_downstream)
		{
			m_downstream.startDocument();
		}
	}

	public void startElement(final QName elementName, final LinkedList<VxMapping<String, String>> namespaces, final LinkedList<VxMapping<QName, String>> attributes) throws IOException, SmAbortException
	{
		m_text.setLength(0);

		final ValidationItem<A> parentItem = m_currentItem;
		// TODO: Supply a location?
		m_currentItem = parentItem.push(++m_nodeIndex);

		// Maintain prefix mapping information.
		m_namespaces.pushContext();
		if (namespaces.size() > 0) // Optimization.
		{
			for (final VxMapping<String, String> mapping : namespaces)
			{
				m_namespaces.declarePrefix(mapping.getKey(), mapping.getValue());
			}
		}

		// Digest the attributes from the XMLSchema-instance namespace.
		m_attributes.initialize(elementName, m_currentItem, attributes, m_namespaces, documentURI, m_errors, sdl);
		final SmType<A> localType = m_attributes.getLocalType();
		final Boolean explicitNil = m_attributes.getLocalNil();

		m_currentPSVI = m_mac.startElement(elementName, localType, explicitNil);

		m_icm.startElement(m_currentPSVI, m_currentItem, m_errors);

		if (null != m_downstream)
		{
			m_downstream.startElement(elementName, m_currentPSVI.getType());

			for (final VxMapping<String, String> mapping : namespaces)
			{
				m_downstream.namespace(mapping.getKey(), mapping.getValue());
			}
		}

		// The attribute manager validates the attributes and sends them downstream, returning the index of the last
		// attribute.
		m_nodeIndex = m_attributes.attributes(m_currentPSVI, m_currentItem, attributes, m_downstream, m_errors, m_idm, m_icm);
	}

	public void text(final List<? extends A> initialValue) throws IOException, SmAbortException
	{
		m_nodeIndex++;

		// Remember that we got a text node.
		m_currentItem.m_detectedText = true;

		if (m_currentItem.getSuspendChecking())
		{
			if (null != m_downstream)
			{
				m_downstream.text(initialValue);
			}
			return;
		}

		switch (m_currentPSVI.getProcessContents())
		{
			case Strict:
			case Lax:
			{
				final SmElement<A> declaration = m_currentPSVI.getDeclaration();
				if (m_currentPSVI.isNilled())
				{
					m_errors.error(new CvcElementUnexpectedChildInNilledElementException(declaration, m_currentItem.getLocation()));
				}

				final SmType<A> elementType = m_currentPSVI.getType();
				if (null != elementType)
				{
					if (elementType instanceof SmSimpleType<?>)
					{
						final SmSimpleType<A> simpleType = (SmSimpleType<A>)elementType;
						try
						{
							final List<A> actualValue = simpleType.validate(initialValue);

							if (null != declaration)
							{
								checkValueConstraintForElement(declaration, simpleType, actualValue);
							}

							m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
							m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

							if (null != m_downstream)
							{
								m_downstream.text(actualValue);
							}
						}
						catch (final SmDatatypeException e)
						{
							m_errors.error(new SmSimpleTypeException(m_atomBridge.getC14NString(initialValue), simpleType, e));
							if (null != m_downstream)
							{
								m_downstream.text(initialValue);
							}
						}
					}
					else
					{
						final SmComplexType<A> complexType = (SmComplexType<A>)elementType;
						final SmContentType<A> contentType = complexType.getContentType();
						switch (contentType.getKind())
						{
							case Simple:
							{
								final SmSimpleType<A> simpleType = contentType.getSimpleType();
								try
								{
									final List<A> actualValue = simpleType.validate(initialValue);

									checkValueConstraintForElement(declaration, simpleType, actualValue);

									m_idm.text(actualValue, simpleType, m_currentItem, m_errors, m_atomBridge);
									m_icm.text(actualValue, simpleType, m_currentItem, m_nodeIndex, m_atomBridge);

									if (null != m_downstream)
									{
										m_downstream.text(actualValue);
									}
								}
								catch (final SmDatatypeException e)
								{
									m_errors.error(new SmSimpleTypeException(m_atomBridge.getC14NString(initialValue), simpleType, e));
									if (null != m_downstream)
									{
										m_downstream.text(initialValue);
									}
								}
							}
							break;
							case ElementOnly:
							{
								final String strval = m_atomBridge.getC14NString(initialValue);
								if (!isWhiteSpace(strval))
								{
									m_errors.error(new CvcUnexpectedNonWhiteSpaceTextInElementOnlyContentException(m_currentPSVI.getName(), strval, m_currentItem.getLocation()));
								}
							}
							break;
							case Mixed:
							{
								ValidationRules.checkValueConstraintForMixedContent(declaration, m_atomBridge.getC14NString(initialValue), m_currentItem, m_errors, m_atomBridge);

								if (null != m_downstream)
								{
									m_downstream.text(initialValue);
								}
							}
							break;
							case Empty:
							{
								m_errors.error(new CvcUnexpectedTextInEmptyContentException(m_currentPSVI.getName(), m_atomBridge.getC14NString(initialValue), m_currentItem.getLocation()));
								if (null != m_downstream)
								{
									m_downstream.text(initialValue);
								}
							}
							break;
							default:
							{
								throw new AssertionError(contentType.getKind());
							}
						}
					}
				}
				else
				{
					if (null != m_downstream)
					{
						m_downstream.text(initialValue);
					}
				}
			}
			break;
			case Skip:
			{
				if (null != m_downstream)
				{
					m_downstream.text(initialValue);
				}
			}
			break;
			default:
			{
				throw new AssertionError(m_currentPSVI.getProcessContents());
			}
		}
	}
}
