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

import org.genxdm.exceptions.PreCondition;
import org.genxdm.xs.components.SmAttribute;
import org.genxdm.xs.components.SmAttributeGroup;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.components.SmModelGroup;
import org.genxdm.xs.components.SmNotation;
import org.genxdm.xs.components.SmParticle;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.exceptions.SmExceptionHandler;
import org.genxdm.xs.resolve.SmLocation;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmSimpleType;
import org.gxml.processor.w3c.xs.exception.SccLocationException;


final class XMLSccExceptionAdapter<A> implements SmConstraintHandler<A>
{
	private final SmExceptionHandler m_errors;
	private final XMLComponentLocator<A> m_locations;

	public XMLSccExceptionAdapter(final SmExceptionHandler errors, final XMLComponentLocator<A> locations)
	{
		m_errors = PreCondition.assertArgumentNotNull(errors, "errors");
		m_locations = PreCondition.assertArgumentNotNull(locations, "locations");
	}

	public void error(final SmSimpleType<A> simpleType, final SmException exception) throws SmAbortException
	{
		if (m_locations.m_simpleTypeLocations.containsKey(simpleType))
		{
			final SmLocation location = m_locations.m_simpleTypeLocations.get(simpleType);
			m_errors.error(new SccLocationException(location, exception));
		}
		else
		{
			m_errors.error(exception);
		}
	}

	public void error(final SmComplexType<A> complexType, final SmException exception) throws SmAbortException
	{
		if (m_locations.m_complexTypeLocations.containsKey(complexType))
		{
			final SmLocation location = m_locations.m_complexTypeLocations.get(complexType);
			m_errors.error(new SccLocationException(location, exception));
		}
		else
		{
			m_errors.error(exception);
		}
	}

	public void error(final SmAttribute<A> attribute, final SmException exception) throws SmAbortException
	{
		if (m_locations.m_attributeLocations.containsKey(attribute))
		{
			final SmLocation location = m_locations.m_attributeLocations.get(attribute);
			m_errors.error(new SccLocationException(location, exception));
		}
		else
		{
			m_errors.error(exception);
		}
	}

	public void error(final SmElement<A> element, final SmException exception) throws SmAbortException
	{
		if (m_locations.m_elementLocations.containsKey(element))
		{
			final SmLocation location = m_locations.m_elementLocations.get(element);
			m_errors.error(new SccLocationException(location, exception));
		}
		else
		{
			m_errors.error(exception);
		}
	}

	public void error(final SmModelGroup<A> modelGroup, final SmException exception) throws SmAbortException
	{
		if (m_locations.m_modelGroupLocations.containsKey(modelGroup))
		{
			final SmLocation location = m_locations.m_modelGroupLocations.get(modelGroup);
			m_errors.error(new SccLocationException(location, exception));
		}
		else
		{
			m_errors.error(exception);
		}
	}

	public void error(final SmAttributeGroup<A> attributeGroup, final SmException exception) throws SmAbortException
	{
		if (m_locations.m_attributeGroupLocations.containsKey(attributeGroup))
		{
			final SmLocation location = m_locations.m_attributeGroupLocations.get(attributeGroup);
			m_errors.error(new SccLocationException(location, exception));
		}
		else
		{
			m_errors.error(exception);
		}
	}

	public void error(final SmIdentityConstraint<A> constraint, final SmException exception) throws SmAbortException
	{
		if (m_locations.m_constraintLocations.containsKey(constraint))
		{
			final SmLocation location = m_locations.m_constraintLocations.get(constraint);
			m_errors.error(new SccLocationException(location, exception));
		}
		else
		{
			m_errors.error(exception);
		}
	}

	public void error(final SmNotation<A> notation, final SmException exception) throws SmAbortException
	{
		if (m_locations.m_notationLocations.containsKey(notation))
		{
			final SmLocation location = m_locations.m_notationLocations.get(notation);
			m_errors.error(new SccLocationException(location, exception));
		}
		else
		{
			m_errors.error(exception);
		}
	}

	public void error(final SmParticle<A> particle, final SmException exception) throws SmAbortException
	{
		if (m_locations.m_particleLocations.containsKey(particle))
		{
			final SmLocation location = m_locations.m_particleLocations.get(particle);
			m_errors.error(new SccLocationException(location, exception));
		}
		else
		{
			m_errors.error(exception);
		}
	}
}
