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
package org.genxdm.processor.w3c.xs.validation.impl;

import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.regex.api.RegExMachine;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ParticleTerm;
import org.genxdm.xs.components.SchemaWildcard;



final class SmMachineImpl implements SmContentFiniteStateMachine
{
	private ElementDefinition m_element;
	private final LinkedList<ValidationExpr> m_matchers = new LinkedList<ValidationExpr>();
	private final RegExMachine<ValidationExpr, QName> m_regexm;
	private SchemaWildcard m_wildcard;

	public SmMachineImpl(final RegExMachine<ValidationExpr, QName> regexm)
	{
		m_regexm = PreCondition.assertArgumentNotNull(regexm, "regexm");
	}

	public boolean end()
	{
		return m_regexm.step(null, null);
	}

	public ElementDefinition getElement()
	{
		return m_element;
	}

	private ParticleTerm getParticleTerm()
	{
		final int size = m_matchers.size();
		if (size > 0)
		{
			final ValidationExpr expr = m_matchers.get(0);
			ParticleTerm firstMatch = expr.getParticleTerm();
			
			// The XML Schema 1.1. specification does not consider it an error if an element particle and a wildcard particle 
	        // both match.  In that case, the element particle is preferred.  See http://www.w3.org/TR/xmlschema11-1/#cos-nonambig.
			// Here's the relevant text from the spec: <br>
	        //    Note: Content models in which an ·element particle· and a ·wildcard particle· ·compete· with each other are not prohibited. 
	        //    In such cases, the Element Declaration is chosen; see the definitions of ·attribution· and ·validation-path·. 
	        //
	        // Note that this is different behavior than that specified in the 1.0 specification: http://www.w3.org/TR/xmlschema-1/#non-ambig.
			
			// If first match is a wildcard, and another non-wildcard match exists, return the non-wildcard.
			if(size > 1 && firstMatch instanceof SchemaWildcard)
			{
				ParticleTerm betterMatch = null;
				for(int icnt = 1; icnt < size; icnt++)
				{
					ValidationExpr testExpr = m_matchers.get(icnt);
					if(testExpr != null)
					{
						ParticleTerm nextMatch = testExpr.getParticleTerm();
						if (nextMatch instanceof ElementDefinition)
						{
							if(betterMatch != null)
							{
								throw new AssertionError("cos-nonambig error: http://www.w3.org/TR/xmlschema-1/#cos-nonambig");
							}
							else
							{
								betterMatch = nextMatch;
							}
						}
					}
				}
				if(betterMatch != null)
				{
					return betterMatch;
				}
			}
			return firstMatch;
		}
		return null;
	}

	public SchemaWildcard getWildcard()
	{
		return m_wildcard;
	}

	public boolean isElementMatch()
	{
		return (null != m_element);
	}

	public boolean isWildcardMatch()
	{
		return (null != m_wildcard);
	}

	public boolean step(final QName name)
	{
		m_matchers.clear();
		final boolean stepped = m_regexm.step(name, m_matchers);
		if (stepped)
		{
			final ParticleTerm term = getParticleTerm();
			if (term instanceof ElementDefinition)
			{
				m_element = (ElementDefinition)term;
				m_wildcard = null;
			}
			else if (term instanceof SchemaWildcard)
			{
				m_element = null;
				m_wildcard = (SchemaWildcard)term;
			}
			else
			{
				throw new AssertionError(term);
			}
		}
		return stepped;
	}

	@Override
	public final String toString()
	{
		if (isElementMatch())
		{
			return m_element.getName().toString();
		}
		else if (isWildcardMatch())
		{
			return m_wildcard.getProcessContents().name();
		}
		else
		{
			return "";
		}
	}
}
