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

import javax.xml.namespace.QName;

import org.genxdm.processor.w3c.xs.exception.CvcAbstractComplexTypeException;
import org.genxdm.processor.w3c.xs.exception.CvcElementChildElementWithFixedException;
import org.genxdm.processor.w3c.xs.exception.CvcElementFixedValueOverriddenMixedException;
import org.genxdm.processor.w3c.xs.exception.SrcFrozenLocation;
import org.genxdm.typed.types.AtomBridge;
import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.constraints.SmValueConstraint;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmExceptionHandler;
import org.genxdm.xs.types.SmComplexType;


final class ValidationRules
{
	/**
	 * If there is a fixed {value constraint} the element information item must have no element information item
	 * children.
	 */
	public static <A> void checkValueConstraintAllowsElementChild(final SmElement<A> elementDeclaration, final QName childName, final Locatable childLocatable, final SmExceptionHandler errors) throws SmAbortException
	{
		final SmValueConstraint<A> valueConstraint = elementDeclaration.getValueConstraint();
		if (null != valueConstraint)
		{
			switch (valueConstraint.getVariety())
			{
				case Fixed:
				{
					errors.error(new CvcElementChildElementWithFixedException(elementDeclaration, childName, childLocatable.getLocation()));
				}
				break;
				case Default:
				{
					// No problem.
				}
				break;
				default:
				{
					throw new AssertionError(valueConstraint.getVariety().name());
				}
			}
		}
	}

	/**
	 * If the data component has a "fixed" attribute, reports an error if there is a conflict with the validated value.
	 */
	public static <A> void checkValueConstraintForMixedContent(final SmElement<A> elementDeclaration, final String initialValue, final Locatable locatable, final SmExceptionHandler errors, final AtomBridge<A> atomBridge) throws SmAbortException
	{
		final SmValueConstraint<A> valueConstraint = elementDeclaration.getValueConstraint();
		if (null != valueConstraint)
		{
			switch (valueConstraint.getVariety())
			{
				case Fixed:
				{
					final String expectValue = atomBridge.getC14NString(valueConstraint.getValue());

					if (!expectValue.equals(initialValue))
					{
						errors.error(new CvcElementFixedValueOverriddenMixedException(elementDeclaration, expectValue, initialValue, locatable.getLocation()));
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

	/**
	 * Determines whether the element {type} is {abstract}. <br/>
	 * The error is only raised if the element {type} exists, is a complex type, and is abstract.
	 * 
	 * @param elementType
	 *            The type of the element, may be <code>null</code>.
	 * @param elementName
	 *            The name of the element information item.
	 * @param errors
	 *            The exception handler.
	 */
	public static <A, T> void checkComplexTypeNotAbstract(final SmComplexType<A> elementType, final QName elementName, final SmExceptionHandler errors) throws SmAbortException
	{
		if (null != elementType && elementType.isAbstract())
		{
			errors.error(new CvcAbstractComplexTypeException(elementName, elementType, new SrcFrozenLocation(-1, -1, -1, null, null)));
		}
	}
}