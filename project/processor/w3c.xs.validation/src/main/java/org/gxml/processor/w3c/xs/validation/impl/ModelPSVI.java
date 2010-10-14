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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.genxdm.xs.components.SmElement;
import org.genxdm.xs.components.SmWildcard;
import org.genxdm.xs.constraints.SmIdentityConstraint;
import org.genxdm.xs.constraints.SmValueConstraint;
import org.genxdm.xs.enums.SmDerivationMethod;
import org.genxdm.xs.enums.SmNodeKind;
import org.genxdm.xs.enums.SmProcessContentsMode;
import org.genxdm.xs.exceptions.SmAbortException;
import org.genxdm.xs.exceptions.SmComponentConstraintException;
import org.genxdm.xs.exceptions.SmException;
import org.genxdm.xs.exceptions.SmExceptionHandler;
import org.genxdm.xs.resolve.SmLocation;
import org.genxdm.xs.types.SmComplexMarkerType;
import org.genxdm.xs.types.SmComplexType;
import org.genxdm.xs.types.SmContentTypeKind;
import org.genxdm.xs.types.SmSimpleType;
import org.genxdm.xs.types.SmType;
import org.genxdm.xs.types.SmUnionSimpleType;
import org.gxml.processor.w3c.xs.exception.CvcElementAbstractException;
import org.gxml.processor.w3c.xs.exception.CvcElementDeclarationAndTypeException;
import org.gxml.processor.w3c.xs.exception.CvcElementFixedAndNilledException;
import org.gxml.processor.w3c.xs.exception.CvcElementInEmptyContentException;
import org.gxml.processor.w3c.xs.exception.CvcElementInSimpleContentTypeException;
import org.gxml.processor.w3c.xs.exception.CvcElementInSimpleTypeException;
import org.gxml.processor.w3c.xs.exception.CvcElementLocalTypeDerivationException;
import org.gxml.processor.w3c.xs.exception.CvcElementNotNillableException;
import org.gxml.processor.w3c.xs.exception.CvcElementUnexpectedChildInNilledElementException;
import org.gxml.processor.w3c.xs.exception.CvcSubstitutionBlockedByHeadDeclarationException;
import org.gxml.processor.w3c.xs.exception.CvcSubstitutionBlockedByHeadTypeException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeBaseComplexDerivationException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeBaseSimpleDerivationException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeBaseUrTypeException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeDerivationHierarchyException;
import org.gxml.processor.w3c.xs.exception.SccComplexTypeDerivationMethodException;
import org.gxml.processor.w3c.xs.exception.SccSimpleTypeDerivationException;
import org.gxml.processor.w3c.xs.exception.SccSimpleTypeDerivationRestrictionException;
import org.gxml.processor.w3c.xs.exception.SmUnexpectedElementException;
import org.gxml.processor.w3c.xs.exception.SmUnexpectedEndException;
import org.gxml.processor.w3c.xs.exception.SrcFrozenLocation;
import org.gxml.processor.w3c.xs.validation.api.VxMetaBridge;
import org.gxml.processor.w3c.xs.validation.api.VxPSVI;


/**
 * Keeps track of state for the current element as they are pushed on and popped off the stack with startElement and
 * endElement. There is not a stack object per-se.. These are state objects that are linked together with parent and
 * child pointers and supply push and pop methods. Also note that the objects are recycled by the push method using the
 * child pointer.
 */
final class ModelPSVI<A> implements VxPSVI<A>, Locatable
{
	private ModelPSVI<A> m_parentItem;

	public ModelPSVI<A> getParent()
	{
		return m_parentItem;
	}

	private final ValidationCache<A> cache;
	private final VxMetaBridge<A> metaBridge;

	private ModelPSVI<A> m_childItem; // for recycling

	private final SmNodeKind m_nodeKind;

	// The name of the element information item.
	private QName m_elementName;

	public QName getName()
	{
		return m_elementName;
	}

	private int m_lineNumber;
	private int m_columnNumber;
	private int m_characterOffset;
	private String m_publicId;
	private String m_systemId;

	public SmLocation getLocation()
	{
		return new SrcFrozenLocation(m_lineNumber, m_columnNumber, m_characterOffset, m_publicId, m_systemId);
	}

	private SmType<A> m_type;
	private SmContentFiniteStateMachine<A> m_machine;

	private SmProcessContentsMode m_processContents;

	// The XML Schema specification does not specify what a validating processor should do after
	// it encounters an error. It is not obliged to report more than the first error.
	private boolean m_suspendChecking;

	private SmElement<A> m_elementDecl;

	// Did the instance use xsi:nil="true"?
	private boolean m_nilled;

	/**
	 * Identity scopes may exist for an element information item.
	 */
	public final ArrayList<IdentityScope<A>> m_identityScopes = new ArrayList<IdentityScope<A>>();
	public final HashMap<SmIdentityConstraint<A>, IdentityScope<A>> m_keyScopes = new HashMap<SmIdentityConstraint<A>, IdentityScope<A>>();

	private ModelPSVI(final ModelPSVI<A> parent, final SmNodeKind nodeKind, final SmProcessContentsMode processContents, final VxMetaBridge<A> metaBridge, final ValidationCache<A> cache)
	{
		this.m_parentItem = parent;
		this.m_nodeKind = PreCondition.assertArgumentNotNull(nodeKind, "nodeKind");
		this.metaBridge = PreCondition.assertArgumentNotNull(metaBridge, "metaBridge");
		this.cache = PreCondition.assertArgumentNotNull(cache, "cache");
		reset(processContents);
	}

	public ModelPSVI(final SmProcessContentsMode processContents, final VxMetaBridge<A> metaBridge, final ValidationCache<A> cache)
	{
		this(null, SmNodeKind.DOCUMENT, processContents, metaBridge, cache);
	}

	public ModelPSVI<A> push(final QName elementName)
	{
		if (m_childItem == null)
		{
			m_childItem = new ModelPSVI<A>(this, SmNodeKind.ELEMENT, getProcessContents(), this.metaBridge, this.cache);
		}
		else
		{
			m_childItem.reset(getProcessContents());
		}
		m_childItem.m_elementName = PreCondition.assertArgumentNotNull(elementName, "elementName");

		// TODO: Need to get the element location.
		m_childItem.m_lineNumber = -1;
		m_childItem.m_columnNumber = -1;
		m_childItem.m_characterOffset = -1;
		m_childItem.m_publicId = null;
		m_childItem.m_systemId = null;

		return m_childItem;
	}

	public boolean declExists()
	{
		return (null != m_elementDecl);
	}

	/**
	 * Computes the {nilled} property for the element information item by checking the interaction of the xsi:nil and
	 * the {nillable} property of the element declaration.
	 */
	public boolean computeNilled(final Boolean explicitNil, final SmExceptionHandler errors) throws SmAbortException
	{
		if (null != explicitNil)
		{
			if (null != m_elementDecl)
			{
				if (m_elementDecl.isNillable())
				{
					if (explicitNil)
					{
						final SmValueConstraint<A> valueConstraint = m_elementDecl.getValueConstraint();
						if (null != valueConstraint && valueConstraint.getVariety().isFixed())
						{
							errors.error(new CvcElementFixedAndNilledException(m_elementDecl, getLocation()));
						}
					}
				}
				else
				{
					errors.error(new CvcElementNotNillableException(m_elementDecl, getLocation()));
				}
			}
			return explicitNil;
		}
		else
		{
			return false;
		}
	}

	public boolean step(final QName childName, final Locatable childLocatable, final SmExceptionHandler errors) throws SmAbortException
	{
		final SmType<A> elementType = getType();
		if (null == elementType)
		{
			// TODO: We should really see if locally valid wrt to type has been flagged.
			// Do nothing if there is no type annotation.
			return false;
		}

		if (elementType instanceof SmSimpleType<?>)
		{
			// TODO: Do we include the xs:anySimpleType (simple ur-type)?
			errors.error(new CvcElementInSimpleTypeException(getName(), getLocation(), childName, childLocatable.getLocation()));
			return false;
		}
		else if (elementType instanceof SmComplexMarkerType<?>)
		{
			final SmComplexMarkerType<A> complexType = (SmComplexMarkerType<A>)elementType;
			if (m_suspendChecking)
			{
				return false;
			}

			switch (getProcessContents())
			{
				case Lax:
				{
					// Fall through
				}
				break;
				case Skip:
				{
					return false;
				}
				case Strict:
				{
					// Fall through
				}
				break;
				default:
				{
					throw new AssertionError(getProcessContents().name());
				}
			}
			if (m_nilled && (null != m_elementDecl))
			{
				errors.error(new CvcElementUnexpectedChildInNilledElementException(m_elementDecl, getLocation()));
				return false;
			}
			switch (complexType.getContentType().getKind())
			{
				case Empty:
				{
					errors.error(new CvcElementInEmptyContentException(getName(), getLocation(), childName, childLocatable.getLocation()));
					return false;
				}
				case Simple:
				{
					errors.error(new CvcElementInSimpleContentTypeException(getName(), getLocation(), childName, childLocatable.getLocation()));
					return false;
				}
				case ElementOnly:
				case Mixed:
				{
					switch (m_nodeKind)
					{
						case ELEMENT:
						{
							if (null != m_machine)
							{
								if (m_machine.step(childName))
								{
									return true;
								}
								else
								{
									errors.error(new SmUnexpectedElementException(getName(), getLocation(), childName, childLocatable.getLocation()));
									m_suspendChecking = true;
									return false;
								}
							}
							else
							{
								switch (getProcessContents())
								{
									case Strict:
									{
										errors.error(new SmUnexpectedElementException(getName(), getLocation(), childName, childLocatable.getLocation()));
										m_suspendChecking = true;
									}
									default:
									{
									}
								}
								return false;
							}
						}
						case DOCUMENT:
						{
							return false;
						}
						default:
						{
							throw new AssertionError(m_nodeKind);
						}
					}
				}
				default:
				{
					throw new AssertionError(complexType.getContentType().getKind());
				}
			}
		}
		else
		{
			throw new AssertionError(elementType);
		}
	}

	public void checkForUnexpectedEndOfContent(final SmExceptionHandler errors) throws SmAbortException
	{
		if (m_suspendChecking)
		{
			return;
		}

		switch (getProcessContents())
		{
			case Lax:
			{
				// Fall through
			}
			break;
			case Skip:
			{
				return;
			}
			case Strict:
			{
				// Fall through
			}
			break;
			default:
			{
				throw new AssertionError(getProcessContents().name());
			}
		}

		if (!m_nilled)
		{
			// We don't want an exception in startElement (causing a null machine) to be masked by a NPE.
			if (null != m_machine)
			{
				if (!m_machine.end())
				{
					errors.error(new SmUnexpectedEndException(getName(), getLocation()));
				}
			}
		}
	}

	public ModelPSVI<A> pop()
	{
		return m_parentItem;
	}

	private void reset(final SmProcessContentsMode processContents)
	{
		m_elementDecl = null;
		m_type = null;
		m_machine = null;

		setProcessContents(processContents);
		m_suspendChecking = (null != m_parentItem) && m_parentItem.m_suspendChecking;

		m_nilled = false;
	}

	public void annotate(final SmType<A> type)
	{
		m_type = PreCondition.assertArgumentNotNull(type);
		if (type instanceof SmComplexType<?>)
		{
			final SmComplexType<A> complexType = (SmComplexType<A>)type;
			final SmContentTypeKind kind = complexType.getContentType().getKind();
			if (kind.isComplex())
			{
				m_machine = cache.getMachine(complexType);
			}
		}
	}

	public SmElement<A> getDeclaration()
	{
		return m_elementDecl;
	}

	public SmType<A> getType()
	{
		return m_type;
	}

	public boolean isNilled()
	{
		return m_nilled;
	}

	public void setNilled(boolean nilled)
	{
		m_nilled = nilled;
	}

	public SmProcessContentsMode getProcessContents()
	{
		return m_processContents;
	}

	public void setProcessContents(final SmProcessContentsMode processContents)
	{
		m_processContents = PreCondition.assertArgumentNotNull(processContents);
	}

	public boolean getSuspendChecking()
	{
		return m_suspendChecking;
	}

	public static <A> void assignPSVI(final ModelPSVI<A> elementItem, final SmType<A> localType, final SmExceptionHandler errors) throws SmAbortException, SmException
	{
		final SmContentFiniteStateMachine<A> machine = elementItem.m_parentItem.m_machine;
		if (null != machine)
		{
			if (machine.isElementMatch())
			{
				elementItem.m_elementDecl = machine.getElement();
				checkDeclNotAbstract(elementItem.m_elementDecl, elementItem, errors);
				final SmType<A> dynamicType;
				if (null != localType)
				{
					dynamicType = localType;
					checkLocalTypeValidlyDerivedFromElementType(elementItem.m_elementDecl, localType, elementItem);
				}
				else
				{
					dynamicType = elementItem.m_elementDecl.getType();
				}
				final SmElement<A> substitutionGroup = elementItem.m_elementDecl.getSubstitutionGroup();
				if (null != substitutionGroup)
				{
					checkDeclSubstitutionsNotBlocked(elementItem.m_elementDecl, dynamicType, errors, elementItem);
				}
				elementItem.annotate(dynamicType);
			}
			else
			{
				// It must be a wildcard match.
				final SmWildcard<A> wildcard = machine.getWildcard();
				elementItem.setProcessContents(wildcard.getProcessContents());

				elementItem.recoverPSVI(localType, errors);
			}
		}
		else
		{
			// TODO: Why don't we annotate with the localType?
		}
	}

	public void recoverPSVI(final SmType<A> localType, final SmExceptionHandler errors) throws SmAbortException, SmException
	{
		final QName elementName = getName();

		switch (getProcessContents())
		{
			case Strict:
			{
				m_elementDecl = metaBridge.getElementDeclaration(elementName);
				if (null != m_elementDecl)
				{
					checkDeclNotAbstract(m_elementDecl, this, errors);
					if (null != localType)
					{
						checkLocalTypeValidlyDerivedFromElementType(m_elementDecl, localType, this);

						if (m_elementDecl.hasSubstitutionGroup())
						{
							checkDeclSubstitutionsNotBlocked(m_elementDecl, localType, errors, this);
						}
						annotate(localType);
					}
					else
					{
						final SmType<A> elementType = m_elementDecl.getType();

						if (m_elementDecl.hasSubstitutionGroup())
						{
							checkDeclSubstitutionsNotBlocked(m_elementDecl, elementType, errors, this);
						}
						annotate(elementType);
					}
				}
				else
				{
					if (null != localType)
					{
						annotate(localType);
					}
					else
					{
						errors.error(new CvcElementDeclarationAndTypeException(elementName, getLocation()));
					}
				}
			}
			break;
			case Lax:
			{
				m_elementDecl = metaBridge.getElementDeclaration(elementName);
				if (null != m_elementDecl)
				{
					checkDeclNotAbstract(m_elementDecl, this, errors);
					if (null != localType)
					{
						checkLocalTypeValidlyDerivedFromElementType(m_elementDecl, localType, this);

						if (m_elementDecl.hasSubstitutionGroup())
						{
							checkDeclSubstitutionsNotBlocked(m_elementDecl, localType, errors, this);
						}
						annotate(localType);
					}
					else
					{
						final SmType<A> elementType = m_elementDecl.getType();

						if (m_elementDecl.hasSubstitutionGroup())
						{
							checkDeclSubstitutionsNotBlocked(m_elementDecl, elementType, errors, this);
						}
						annotate(elementType);
					}
				}
				else
				{
					if (null != localType)
					{
						annotate(localType);
					}
				}
			}
			break;
			case Skip:
			{
			}
			break;
			default:
			{
				throw new AssertionError(getProcessContents());
			}
		}
	}

	private static <A> void checkDeclNotAbstract(final SmElement<A> elementDeclaration, final Locatable locatable, final SmExceptionHandler errors) throws SmAbortException
	{
		// Check that the declaration is not abstract.
		if (elementDeclaration.isAbstract())
		{
			errors.error(new CvcElementAbstractException(elementDeclaration, locatable.getLocation()));
		}
	}

	private static <A, S> void checkDeclSubstitutionsNotBlocked(final SmElement<A> elementDeclaration, final SmType<A> elementType, final SmExceptionHandler errors, final Locatable locatable) throws SmAbortException
	{
		// Note: Substitution can be blocked by extension and restriction as well.
		final SmElement<A> substitutionGroup = elementDeclaration.getSubstitutionGroup();
		final Set<SmDerivationMethod> block = substitutionGroup.getDisallowedSubtitutions();
		if (block.contains(SmDerivationMethod.Substitution))
		{
			// Substitutions are blocked outright by the substitution group declaration.
			errors.error(new CvcSubstitutionBlockedByHeadDeclarationException(elementDeclaration, substitutionGroup, locatable.getLocation()));
		}

		final SmType<A> headType = substitutionGroup.getType();

		if (block.contains(SmDerivationMethod.Extension))
		{
			if (elementType.derivedFromType(headType, EnumSet.of(SmDerivationMethod.Extension)))
			{
				errors.error(new CvcSubstitutionBlockedByHeadDeclarationException(elementDeclaration, substitutionGroup, locatable.getLocation()));
			}
		}

		if (block.contains(SmDerivationMethod.Restriction))
		{
			if (elementType.derivedFromType(headType, EnumSet.of(SmDerivationMethod.Restriction)))
			{
				errors.error(new CvcSubstitutionBlockedByHeadDeclarationException(elementDeclaration, substitutionGroup, locatable.getLocation()));
			}
		}

		if (headType instanceof SmComplexType<?>)
		{
			final SmComplexType<A> complexType = (SmComplexType<A>)headType;
			final Set<SmDerivationMethod> prohibitedSubstitutions = complexType.getProhibitedSubstitutions();
			if (prohibitedSubstitutions.contains(SmDerivationMethod.Substitution))
			{
				throw new AssertionError("Isn't this dead code?");
				// Substitutions are blocked outright by the substitution group type.
				// errors.error(new CvcSubstitutionBlockedByHeadTypeException(elementDeclaration,
				// locatable.getLocation()));
			}

			if (prohibitedSubstitutions.contains(SmDerivationMethod.Extension))
			{
				if (elementType.derivedFromType(headType, EnumSet.of(SmDerivationMethod.Extension)))
				{
					errors.error(new CvcSubstitutionBlockedByHeadTypeException(elementDeclaration, locatable.getLocation()));
				}
			}

			if (prohibitedSubstitutions.contains(SmDerivationMethod.Restriction))
			{
				if (elementType.derivedFromType(headType, EnumSet.of(SmDerivationMethod.Restriction)))
				{
					errors.error(new CvcSubstitutionBlockedByHeadTypeException(elementDeclaration, locatable.getLocation()));
				}
			}
		}
	}

	private static <A, S> void checkLocalTypeValidlyDerivedFromElementType(final SmElement<A> elementDeclaration, final SmType<A> localType, final Locatable locatable) throws CvcElementLocalTypeDerivationException
	{
		final Set<SmDerivationMethod> block = elementDeclaration.getDisallowedSubtitutions();
		final SmType<A> elementType = elementDeclaration.getType();
		if (localType instanceof SmSimpleType<?>)
		{
			try
			{
				checkTypeDerivationOKSimple((SmSimpleType<A>)localType, elementType, block);
			}
			catch (final SmComponentConstraintException e)
			{
				throw new CvcElementLocalTypeDerivationException(localType, elementDeclaration, e, locatable.getLocation());
			}
		}
		else
		{
			final Set<SmDerivationMethod> union = new HashSet<SmDerivationMethod>();

			union.addAll(block);
			if (elementType instanceof SmComplexType<?>)
			{
				final SmComplexType<A> complexType = (SmComplexType<A>)elementType;
				union.addAll(complexType.getProhibitedSubstitutions());
			}

			try
			{
				checkTypeDerivationOKComplex(localType, elementType, union);
			}
			catch (final SmComponentConstraintException e)
			{
				throw new CvcElementLocalTypeDerivationException(localType, elementDeclaration, e, locatable.getLocation());
			}
		}
	}

	/**
	 * Type Derivation OK (Simple) (3.14.6)
	 */
	private static <A, S> void checkTypeDerivationOKSimple(final SmSimpleType<A> D, final SmType<A> B, final Set<SmDerivationMethod> subset) throws SmComponentConstraintException
	{
		if (D.getName().equals(B.getName()))
		{
			// They are the same type definition.
		}
		else
		{
			final SmType<A> deesBaseType = D.getBaseType();
			if (subset.contains(SmDerivationMethod.Restriction) || deesBaseType.getFinal().contains(SmDerivationMethod.Restriction))
			{
				throw new SccSimpleTypeDerivationRestrictionException(D.getName());
			}

			boolean isOK = false;
			if (deesBaseType.getName().equals(B.getName()))
			{
				isOK = true;
			}
			else if (!deesBaseType.isComplexUrType() && ModelPSVI.isTypeDerivationOK(deesBaseType, B, subset))
			{
				isOK = true;
			}
			else if (!D.isSimpleUrType() && (D.isListType() || D.isUnionType()) && B.isSimpleUrType())
			{
				isOK = true;
			}
			else if (B instanceof SmUnionSimpleType<?>)
			{
				final SmUnionSimpleType<A> unionType = (SmUnionSimpleType<A>)B;
				for (final SmSimpleType<A> memberType : unionType.getMemberTypes())
				{
					if (isTypeDerivationOK(D, memberType, subset))
					{
						isOK = true;
						break;
					}
				}
			}
			if (!isOK)
			{
				throw new SccSimpleTypeDerivationException(D.getName());
			}
		}
	}

	private static <A, S> void checkTypeDerivationOKComplex(final SmType<A> D, final SmType<A> B, final Set<SmDerivationMethod> subset) throws SmComponentConstraintException
	{
		if (D.getName().equals(B.getName()))
		{
			// They are the same type definition.
		}
		else if (D.isComplexUrType())
		{
			throw new SccComplexTypeDerivationHierarchyException(D, B, subset);
		}
		else
		{
			if (subset.contains(D.getDerivationMethod()))
			{
				throw new SccComplexTypeDerivationMethodException(D, B, subset);
			}

			final SmType<A> deeBaseType = D.getBaseType();
			if (deeBaseType.getName().equals(B.getName()))
			{
				// B is D's {base type definition}
			}
			else
			{
				if (deeBaseType.isComplexUrType())
				{
					throw new SccComplexTypeBaseUrTypeException(D, B, subset);
				}
				else
				{
					if (deeBaseType instanceof SmComplexType<?>)
					{
						try
						{
							checkTypeDerivationOK((SmComplexType<A>)deeBaseType, B, subset);
						}
						catch (final SmComponentConstraintException e)
						{
							throw new SccComplexTypeBaseComplexDerivationException(D, B, subset, e);
						}
					}
					else
					{
						try
						{
							checkTypeDerivationOKSimple((SmSimpleType<A>)deeBaseType, B, subset);
						}
						catch (final SmComponentConstraintException e)
						{
							throw new SccComplexTypeBaseSimpleDerivationException(D, B, subset, e);
						}
					}
				}
			}
		}
	}

	private static <A, S> void checkTypeDerivationOK(final SmType<A> D, final SmType<A> B, final Set<SmDerivationMethod> subset) throws SmComponentConstraintException
	{
		if (D instanceof SmSimpleType<?>)
		{
			checkTypeDerivationOKSimple((SmSimpleType<A>)D, B, subset);
		}
		else if (D instanceof SmComplexType<?>)
		{
			checkTypeDerivationOKComplex(D, B, subset);
		}
		else
		{
			throw new AssertionError(D);
		}
	}

	private static <A, S> boolean isTypeDerivationOK(final SmType<A> D, final SmType<A> B, final Set<SmDerivationMethod> subset)
	{
		try
		{
			checkTypeDerivationOK(D, B, subset);
		}
		catch (final SmException e)
		{
			return false;
		}
		return true;
	}
}
