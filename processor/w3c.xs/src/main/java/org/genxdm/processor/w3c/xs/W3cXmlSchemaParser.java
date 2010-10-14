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

import java.io.InputStream;
import java.net.URI;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.SmMetaLoadArgs;
import org.genxdm.xs.components.SmComponentBag;
import org.genxdm.xs.components.SmComponentProvider;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmExceptionHandler;

public final class W3cXmlSchemaParser<A>
{
	private final AtomBridge<A> atomBridge;

	// The default Regular Expression compiler is backed by the JDK.
	private static final SmRegExCompiler DEFAULT_REGEX_COMPILER = new RegExCompilerJDK();

	// The actual Regular Expression compiler may be changed.
	private SmRegExCompiler regexc;

	public W3cXmlSchemaParser(final AtomBridge<A> atomBridge)
	{
		this.atomBridge = PreCondition.assertArgumentNotNull(atomBridge, "atomBridge");
		this.regexc = DEFAULT_REGEX_COMPILER;
	}

	public SmComponentBag<A> parse(final URI schemaLocation, final InputStream istream, final URI systemId, final SmExceptionHandler errors, final SmMetaLoadArgs args, final SmComponentProvider<A> components) throws SmAbortException
	{
		PreCondition.assertArgumentNotNull(istream, "istream");
		PreCondition.assertArgumentNotNull(components, "components");
		final XMLParserImpl<A> parser = new XMLParserImpl<A>(components, atomBridge);

		parser.setCatalog(args.getCatalog());
		parser.setResolver(args.getResolver());
		parser.setRegExCompiler(regexc);

		return parser.parse(schemaLocation, istream, systemId, errors);
	}

	/**
	 * Override the default (JDK-based) Regular Expression compiler.
	 * 
	 * @param regexc
	 *            The new compiler. May be <code>null</code> to reset to default.
	 */
	public void setRegExCompiler(final SmRegExCompiler regexc)
	{
		if (null != regexc)
		{
			this.regexc = regexc;
		}
		else
		{
			this.regexc = DEFAULT_REGEX_COMPILER;
		}
	}
}
