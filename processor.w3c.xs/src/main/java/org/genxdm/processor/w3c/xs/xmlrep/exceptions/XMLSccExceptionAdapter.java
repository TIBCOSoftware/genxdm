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
package org.genxdm.processor.w3c.xs.xmlrep.exceptions;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.processor.w3c.xs.exception.scc.SccLocationException;
import org.genxdm.processor.w3c.xs.xmlrep.util.XMLComponentLocator;
import org.genxdm.xs.components.AttributeDefinition;
import org.genxdm.xs.components.AttributeGroupDefinition;
import org.genxdm.xs.components.ElementDefinition;
import org.genxdm.xs.components.ModelGroup;
import org.genxdm.xs.components.NotationDefinition;
import org.genxdm.xs.components.SchemaParticle;
import org.genxdm.xs.constraints.IdentityConstraint;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaConstraintHandler;
import org.genxdm.xs.exceptions.SchemaException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.resolve.LocationInSchema;
import org.genxdm.xs.types.ComplexType;
import org.genxdm.xs.types.SimpleType;


public final class XMLSccExceptionAdapter implements SchemaConstraintHandler
{
    private final SchemaExceptionHandler m_errors;
    private final XMLComponentLocator m_locations;

    public XMLSccExceptionAdapter(final SchemaExceptionHandler errors, final XMLComponentLocator locations)
    {
        m_errors = PreCondition.assertArgumentNotNull(errors, "errors");
        m_locations = PreCondition.assertArgumentNotNull(locations, "locations");
    }

    public void error(final SimpleType simpleType, final SchemaException exception) throws AbortException
    {
        if (m_locations.m_simpleTypeLocations.containsKey(simpleType))
        {
            final LocationInSchema location = m_locations.m_simpleTypeLocations.get(simpleType);
            m_errors.error(new SccLocationException(location, exception));
        }
        else
        {
            m_errors.error(exception);
        }
    }

    public void error(final ComplexType complexType, final SchemaException exception) throws AbortException
    {
        if (m_locations.m_complexTypeLocations.containsKey(complexType))
        {
            final LocationInSchema location = m_locations.m_complexTypeLocations.get(complexType);
            m_errors.error(new SccLocationException(location, exception));
        }
        else
        {
            m_errors.error(exception);
        }
    }

    public void error(final AttributeDefinition attribute, final SchemaException exception) throws AbortException
    {
        if (m_locations.m_attributeLocations.containsKey(attribute))
        {
            final LocationInSchema location = m_locations.m_attributeLocations.get(attribute);
            m_errors.error(new SccLocationException(location, exception));
        }
        else
        {
            m_errors.error(exception);
        }
    }

    public void error(final ElementDefinition element, final SchemaException exception) throws AbortException
    {
        if (m_locations.m_elementLocations.containsKey(element))
        {
            final LocationInSchema location = m_locations.m_elementLocations.get(element);
            m_errors.error(new SccLocationException(location, exception));
        }
        else
        {
            m_errors.error(exception);
        }
    }

    public void error(final ModelGroup modelGroup, final SchemaException exception) throws AbortException
    {
        if (m_locations.m_modelGroupLocations.containsKey(modelGroup))
        {
            final LocationInSchema location = m_locations.m_modelGroupLocations.get(modelGroup);
            m_errors.error(new SccLocationException(location, exception));
        }
        else
        {
            m_errors.error(exception);
        }
    }

    public void error(final AttributeGroupDefinition attributeGroup, final SchemaException exception) throws AbortException
    {
        if (m_locations.m_attributeGroupLocations.containsKey(attributeGroup))
        {
            final LocationInSchema location = m_locations.m_attributeGroupLocations.get(attributeGroup);
            m_errors.error(new SccLocationException(location, exception));
        }
        else
        {
            m_errors.error(exception);
        }
    }

    public void error(final IdentityConstraint constraint, final SchemaException exception) throws AbortException
    {
        if (m_locations.m_constraintLocations.containsKey(constraint))
        {
            final LocationInSchema location = m_locations.m_constraintLocations.get(constraint);
            m_errors.error(new SccLocationException(location, exception));
        }
        else
        {
            m_errors.error(exception);
        }
    }

    public void error(final NotationDefinition notation, final SchemaException exception) throws AbortException
    {
        if (m_locations.m_notationLocations.containsKey(notation))
        {
            final LocationInSchema location = m_locations.m_notationLocations.get(notation);
            m_errors.error(new SccLocationException(location, exception));
        }
        else
        {
            m_errors.error(exception);
        }
    }

    public void error(final SchemaParticle particle, final SchemaException exception) throws AbortException
    {
        if (m_locations.m_particleLocations.containsKey(particle))
        {
            final LocationInSchema location = m_locations.m_particleLocations.get(particle);
            m_errors.error(new SccLocationException(location, exception));
        }
        else
        {
            m_errors.error(exception);
        }
    }
}
