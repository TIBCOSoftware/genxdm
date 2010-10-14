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
package org.gxml.processor.w3c.xs;

import static org.gxml.processor.w3c.xs.SmConstraintChecker.checkSchemaComponentConstraints;

import java.io.InputStream;
import java.net.URI;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.SmComponentBag;
import org.genxdm.xs.components.SmComponentProvider;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.exceptions.SmExceptionCatcher;
import org.genxdm.xs.exceptions.SmExceptionHandler;
import org.genxdm.xs.resolve.SmCatalog;
import org.genxdm.xs.resolve.SmResolver;

final class XMLParserImpl<A>
{
	/**
	 * Injected during initialization, already contains native and xsi schema components.
	 */
	private final SmComponentProvider<A> cache;
	private final AtomBridge<A> atomBridge;

	private SmCatalog m_catalog;
	private SmResolver m_resolver;
	private SmRegExCompiler m_regexc;
	private boolean m_processRepeatedNamespaces = true;

	public XMLParserImpl(final SmComponentProvider<A> cache, final AtomBridge<A> atomBridge)
	{
		this.cache = PreCondition.assertArgumentNotNull(cache, "cache");
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
	}

	public SmComponentBag<A> parse(final URI schemaLocation, final InputStream istream, final URI systemId) throws SmException
	{
		// This convenience routine is implemented in terms of the more general routine.
		final SmExceptionCatcher errors = new SmExceptionCatcher();
		try
		{
			final SmComponentBag<A> components = parse(schemaLocation, istream, systemId, errors);
			if (errors.size() > 0)
			{
				// Only the first error is reported.
				throw errors.getFirst();
			}
			else
			{
				return components;
			}
		}
		catch (final SmAbortException e)
		{
			// This should not happen because the errors are reported to a catcher.
			throw new AssertionError(e);
		}
	}

	public SmComponentBag<A> parse(final URI schemaLocation, final InputStream istream, final URI systemId, final SmExceptionHandler errors) throws SmAbortException
	{
		PreCondition.assertArgumentNotNull(istream, "istream");
		// PreCondition.assertArgumentNotNull(systemId, "systemId");
		// PreCondition.assertArgumentNotNull(errors, "errors");

		// The cache holds an in-memory model of the XML representation.
		final XMLSchemaCache<A> cache = new XMLSchemaCache<A>(atomBridge.getNameBridge());

		// The top-level module acts as a parent for includes, imports and redefines.
		final XMLSchemaModule<A> module = new XMLSchemaModule<A>(null, schemaLocation, systemId);

		// Catch the reported exceptions in order to maximize the amount of feedback.
		final SmExceptionCatcher caught = new SmExceptionCatcher();

		// Delegate the parsing into an XML representation (that has no coupling to the parameters A and S)
		final XMLSchemaParser<A> parser = new XMLSchemaParser<A>(this.atomBridge, this.cache, caught, getCatalog(), m_resolver, processRepeatedNamespaces());

		parser.parse(systemId, istream, cache, module);

		if (caught.size() > 0)
		{
			reportErrors(caught, errors);
			return null;
		}

		// Convert the XML representation into the compiled schema.
		final Pair<SmComponentBagImpl<A>, XMLComponentLocator<A>> converted = convert(cache, caught);

		if (caught.size() == 0)
		{
			final XMLSccExceptionAdapter<A> scc = new XMLSccExceptionAdapter<A>(caught, converted.getSecond());

			checkSchemaComponentConstraints(converted.getFirst(), this.cache, this.atomBridge, scc);

			if (caught.size() == 0)
			{
				return converted.getFirst();
			}
			else
			{
				reportErrors(caught, errors);
				return null;
			}
		}
		else
		{
			reportErrors(caught, errors);
			return null;
		}
	}

	private void reportErrors(final SmExceptionCatcher caught, final SmExceptionHandler errors) throws SmAbortException
	{
		for (final SmException error : caught)
		{
			if (null != errors)
			{
				errors.error(error);
			}
			else
			{
				throw new SmAbortException(error);
			}
		}
	}

	/**
	 * Converts the XML cache into a compiled schema.
	 */
	private Pair<SmComponentBagImpl<A>, XMLComponentLocator<A>> convert(final XMLSchemaCache<A> cache, final SmExceptionHandler errors) throws SmAbortException
	{
		try
		{
			cache.checkReferences();
		}
		catch (final SmException e)
		{
			errors.error(e);
			return null;
		}

		return XMLSchemaConverter.convert(m_regexc, this.cache, atomBridge, cache, errors);
	}

	public void setResolver(final SmResolver resolver)
	{
		m_resolver = resolver;
	}

	public SmResolver getResolver()
	{
		return m_resolver;
	}

	public void setCatalog(final SmCatalog catalog)
	{
		m_catalog = catalog;
	}

	public SmCatalog getCatalog()
	{
		return m_catalog;
	}

	public void setRegExCompiler(final SmRegExCompiler regexc)
	{
		m_regexc = regexc;
	}

	public SmRegExCompiler getRegExCompiler()
	{
		return m_regexc;
	}

	public void setProcessRepeatedNamespaces(final boolean processRepeatedNamespaces)
	{
		m_processRepeatedNamespaces = processRepeatedNamespaces;
	}

	public boolean processRepeatedNamespaces()
	{
		return m_processRepeatedNamespaces;
	}
}
