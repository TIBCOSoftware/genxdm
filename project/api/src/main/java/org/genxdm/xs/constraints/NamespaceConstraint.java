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
package org.genxdm.xs.constraints;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;

import org.genxdm.exceptions.PreCondition;
import org.genxdm.names.NameSource;
import org.genxdm.xs.exceptions.WildcardIntersectionException;
import org.genxdm.xs.exceptions.WildcardIntersectionNotExpressibleException;
import org.genxdm.xs.exceptions.WildcardUnionException;
import org.genxdm.xs.exceptions.WildcardUnionNotExpressibleException;

/**
 * Implementation for a {namespace constraint} property of a wildcard.
 * <p/>
 * Objects of this class are immutable and can only be created via the static "include" and "exclude" methods.
 * <p/>
 * The static constant "Any" represents the "##any" namespace constraint.
 */
public final class NamespaceConstraint
{
	public enum Mode
	{
		/**
		 * Used to indicate that the namespace constraint allows any namespaces.
		 */
		Any,

		/**
		 * Used to indicate that the namespace constraint specifies namespaces that are excluded.
		 */
		Exclude,

		/**
		 * Used to indicate that the namespace constraint specifies namespaces that are included.
		 */
		Include;

		public boolean isAny()
		{
			return this == Any;
		}

		public boolean isExclude()
		{
			return this == Exclude;
		}

		public boolean isInclude()
		{
			return this == Include;
		}
	}

	/**
	 * Singleton implementation of the namespace constraint allowing any namespaces.
	 * 
	 * @param nameBridge
	 */
	public static NamespaceConstraint Any(final NameSource nameBridge)
	{
		return new NamespaceConstraint(nameBridge);
	}

	private static boolean equals(final NamespaceConstraint one, final NamespaceConstraint two)
	{
		if (one == two)
		{
			return true;
		}
		else
		{
			// First, check the mode.
			if (one.m_mode == two.m_mode)
			{
				// Next, check the HashSet of namespaces.
				return one.m_namespaces.equals(two.m_namespaces);
			}
			return false;
		}
	}

	/**
	 * Constructs a namespace constraint equivalent to not and a namespace.
	 */
	public static NamespaceConstraint exclude(final String namespace, final NameSource nameBridge)
	{
		PreCondition.assertArgumentNotNull(namespace, "namespace");
		if (namespace.equals(XMLConstants.NULL_NS_URI))
		{
			return NotAbsent(nameBridge);
		}
		else
		{
			return new NamespaceConstraint(Mode.Exclude, namespace, nameBridge);
		}
	}

	/**
	 * Constructs a namespace constraint equivalent to allowing a set of namespaces.
	 */
	public static NamespaceConstraint include(final Set<String> namespaces, final NameSource nameBridge)
	{
		PreCondition.assertArgumentNotNull(namespaces, "namespaces");
		final HashSet<String> strings = new HashSet<String>();
		strings.addAll(namespaces);
		return new NamespaceConstraint(Mode.Include, strings, nameBridge);
	}

	private static NamespaceConstraint intersectIncludeAndExclude(final NamespaceConstraint include, final NamespaceConstraint exclude, NameSource nameBridge)
	{
		PreCondition.assertTrue(include.m_mode == Mode.Include, "include.mode == Include");
		PreCondition.assertTrue(exclude.m_mode == Mode.Exclude, "exclude.mode == Exclude");

		final HashSet<String> namespaces = new HashSet<String>();
		for (final String x : include.m_namespaces)
		{
			if (!exclude.m_namespaces.contains(x) && !x.equals(XMLConstants.NULL_NS_URI))
			{
				namespaces.add(x);
			}
		}
		return new NamespaceConstraint(Mode.Include, namespaces, nameBridge);
	}

	/**
	 * Conventional mathematical intersection.
	 */
	private static HashSet<String> intersection(final Collection<String> one, final Collection<String> two)
	{
		final HashSet<String> namespaces = new HashSet<String>();
		for (final String x : one)
		{
			if (two.contains(x))
			{
				namespaces.add(x);
			}
		}
		return namespaces;
	}

	/**
	 * All numbered comments from Section 3.10.6: Schema Component Constraint: Attribute Wildcard Intersection
	 * 
	 * @param one
	 * @param two
	 * @param nameBridge
	 * @return the NamespaceConstraint representing the intersection of one & two
	 */
	private static NamespaceConstraint intersection(final NamespaceConstraint one, final NamespaceConstraint two, final NameSource nameBridge) throws WildcardIntersectionNotExpressibleException
	{
		PreCondition.assertArgumentNotNull(one, "one");
		PreCondition.assertArgumentNotNull(two, "two");

		if (equals(one, two))
		{
			return one;
		}
		else
		{
			switch (one.m_mode)
			{
				case Any:
				{
					return two;
				}
				case Exclude:
				{
					switch (two.m_mode)
					{
						case Any:
						{
							return one;
						}
						case Exclude:
						{
							if (one.m_namespaces.contains(nameBridge.empty()))
							{
								return two;
							}
							else if (two.m_namespaces.contains(nameBridge.empty()))
							{
								return one;
							}
							else
							{
								throw new WildcardIntersectionNotExpressibleException();
							}
						}
						case Include:
						{
							return intersectIncludeAndExclude(two, one, nameBridge);
						}
						default:
						{
							throw new AssertionError(one.m_mode);
						}
					}
				}
				case Include:
				{
					switch (two.m_mode)
					{
						case Any:
						{
							return one;
						}
						case Exclude:
						{
							return intersectIncludeAndExclude(one, two, nameBridge);
						}
						case Include:
						{
							return new NamespaceConstraint(Mode.Include, intersection(one.m_namespaces, two.m_namespaces), nameBridge);
						}
						default:
						{
							throw new AssertionError(one.m_mode.name());
						}
					}
				}
				default:
				{
					throw new AssertionError(one.m_mode);
				}
			}
		}
	}

	private static boolean isSubset(final NamespaceConstraint sub, final NamespaceConstraint sup, final NameSource nameBridge)
	{
		PreCondition.assertArgumentNotNull(sub, "sub");
		PreCondition.assertArgumentNotNull(sup, "sup");

		if (sup.m_mode == Mode.Any)
		{
			return true;
		}

		if (sub.m_mode == Mode.Exclude && sup.m_mode == Mode.Exclude)
		{
			if (sup.m_namespaces.containsAll(sub.m_namespaces))
			{
				return true;
			}
		}

		if (sub.m_mode == Mode.Include)
		{
			if (sup.m_mode == Mode.Include)
			{
				if (sup.m_namespaces.containsAll(sub.m_namespaces))
				{
					return true;
				}
			}
			else
			{
				if (!sub.m_namespaces.containsAll(sup.m_namespaces))
				{
					if (!sub.m_namespaces.contains(nameBridge.empty()))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Internal Singleton for Not and Absent
	 */
	public static NamespaceConstraint NotAbsent(final NameSource nameBridge)
	{
		return new NamespaceConstraint(Mode.Exclude, nameBridge.empty(), nameBridge);
	}

	/**
	 * Conventional mathematical union.
	 */
	private static HashSet<String> union(final Collection<String> one, final Collection<String> two)
	{
		final HashSet<String> namespaces = new HashSet<String>();
		namespaces.addAll(one);
		namespaces.addAll(two);
		return namespaces;
	}

	/**
	 * Schema Component Constraint: Attribute Wildcard Union
	 * 
	 * @param one
	 *            one the namespace constraints from which to create a union
	 * @param two
	 *            the other namespace constraint from which to create a union
	 * @param nameBridge
	 * @return an NamespaceConstraint representing the union of one & two
	 */
	private static NamespaceConstraint union(final NamespaceConstraint one, final NamespaceConstraint two, final NameSource nameBridge) throws WildcardUnionException
	{
		PreCondition.assertArgumentNotNull(one, "one");
		PreCondition.assertArgumentNotNull(two, "two");

		if (equals(one, two))
		{
			return one;
		}
		else
		{
			switch (one.m_mode)
			{
				case Any:
				{
					return Any(nameBridge);
				}
				case Exclude:
				{
					switch (two.m_mode)
					{
						case Any:
						{
							return Any(nameBridge);
						}
						case Exclude:
						{
							return NotAbsent(nameBridge);
						}
						case Include:
						{
							return unionIncludeAndExclude(two, one, nameBridge);
						}
						default:
						{
							throw new AssertionError(one.m_mode);
						}
					}
				}
				case Include:
				{
					switch (two.m_mode)
					{
						case Any:
						{
							return Any(nameBridge);
						}
						case Exclude:
						{
							return unionIncludeAndExclude(one, two, nameBridge);
						}
						case Include:
						{
							return new NamespaceConstraint(Mode.Include, union(one.m_namespaces, two.m_namespaces), nameBridge);
						}
						default:
						{
							throw new AssertionError(two.m_mode);
						}
					}
				}
				default:
				{
					throw new AssertionError(one.m_mode);
				}
			}
		}
	}

	private static NamespaceConstraint unionIncludeAndExclude(final NamespaceConstraint include, final NamespaceConstraint exclude, final NameSource nameBridge) throws WildcardUnionNotExpressibleException
	{
		PreCondition.assertTrue(include.m_mode == Mode.Include, "include.mode == Include");
		PreCondition.assertTrue(exclude.m_mode == Mode.Exclude, "exclude.mode == Exclude");

		final String NULL_NS_URI = nameBridge.empty();

		if (exclude.m_namespaces.contains(NULL_NS_URI))
		{
			if (include.m_namespaces.contains(NULL_NS_URI))
			{
				return Any(nameBridge);
			}
			else
			{
				return NotAbsent(nameBridge);
			}
		}
		else if (include.m_namespaces.containsAll(exclude.m_namespaces))
		{
			if (include.m_namespaces.contains(NULL_NS_URI))
			{
				return Any(nameBridge);
			}
			else
			{
				return NotAbsent(nameBridge);
			}
		}
		else
		{
			if (include.m_namespaces.contains(NULL_NS_URI))
			{
				throw new WildcardUnionNotExpressibleException();
			}
			else
			{
				return exclude;
			}
		}
	}

	private final Mode m_mode;

	private final NameSource m_nameBridge;

	private final HashSet<String> m_namespaces;

	/**
	 * Internal initializer for include or exclude and a set of namespaces. <br/>
	 * Note that the namespaces argument is not copied.
	 */
	private NamespaceConstraint(final Mode mode, final HashSet<String> namespaces, final NameSource nameBridge)
	{
		// Limitation imposed by current specification.
		PreCondition.assertTrue(mode.isInclude(), "mode.isInclude");

		m_mode = mode;
		m_namespaces = namespaces;
		m_nameBridge = nameBridge;
	}

	/**
	 * Internal initializer for include or exclude and one namespace.
	 */
	private NamespaceConstraint(final Mode mode, final String namespace, final NameSource nameBridge)
	{
		// Limitation imposed by current specification.
		PreCondition.assertTrue(mode.isExclude(), "mode.isExclude");
		m_nameBridge = PreCondition.assertArgumentNotNull(nameBridge, "nameBridge");

		m_mode = mode;
		m_namespaces = new HashSet<String>();
		m_namespaces.add(namespace);
	}

	/**
	 * Internal initializer for ##any.
	 */
	private NamespaceConstraint(final NameSource nameBridge)
	{
		m_mode = Mode.Any;
		m_namespaces = new HashSet<String>();
		m_nameBridge = nameBridge;
	}

	/**
	 * Validation Rule: Wildcard allows Namespace Name.
	 */
	public boolean allowsNamespaceName(final String namespaceURI)
	{
		PreCondition.assertArgumentNotNull(namespaceURI, "namespaceURI");

		switch (m_mode)
		{
			case Any:
			{
				return true;
			}
			case Exclude:
			{
				return !m_namespaces.contains(namespaceURI);
			}
			case Include:
			{
				return m_namespaces.contains(namespaceURI);
			}
			default:
			{
				throw new AssertionError(m_mode);
			}
		}
	}

	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj instanceof NamespaceConstraint)
		{
			final NamespaceConstraint other = (NamespaceConstraint)obj;

			return equals(this, other);
		}
		return false;
	}

	/**
	 * Returns the mode in which this namespace constraint is operating.
	 */
	public Mode getMode()
	{
		return m_mode;
	}

	/**
	 * Returns the set of namespaces applicable in {@link Mode#Include} and {@link Mode#Exclude}.
	 */
	public Iterable<String> getNamespaces()
	{
		PreCondition.assertTrue(m_mode == Mode.Include || m_mode == Mode.Exclude, m_mode.name());
		return m_namespaces;
	}

	/**
	 * Calculates the intersection of this namespace constraint and another namespace constraint. <br>
	 * This implements Schema Component Constraint: Wildcard Intersection.
	 * 
	 * @param other
	 *            The other namespace constraint.
	 */
	public NamespaceConstraint intersection(final NamespaceConstraint other) throws WildcardIntersectionException
	{
		PreCondition.assertArgumentNotNull(other, "other");
		return intersection(this, other, m_nameBridge);
	}

	/**
	 * Determines whether this namespace constraint is a subset of another namespace constraint. <br>
	 * This implements Schema Component Constraint: Wildcard Subset.
	 * 
	 * @param superSet
	 *            The other namespace constraint.
	 */
	public boolean isSubset(final NamespaceConstraint superSet)
	{
		return isSubset(this, superSet, m_nameBridge);
	}

	/**
	 * Calculates the union of this namespace constraint and another namespace constraint. <br>
	 * This implements Schema Component Constraint: Wildcard Union.
	 * 
	 * @param other
	 *            The other namespace constraint.
	 */
	public NamespaceConstraint union(final NamespaceConstraint other) throws WildcardUnionException
	{
		PreCondition.assertArgumentNotNull(other, "other");
		return union(this, other, m_nameBridge);
	}
}
