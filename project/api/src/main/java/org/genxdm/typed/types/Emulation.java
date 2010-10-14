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
package org.genxdm.typed.types;

import java.util.Iterator;
import java.util.List;


/**
 * Controls the behavior of XPath and XQuery processors with respect to version. <br/>
 * Emulation is typically set at compile and execution time, controlling the static and dynamic processor behavior.
 */
public enum Emulation
{
	/**
	 * Canonical mode emulation is a hypothetical processor behavior. It causes a processor to behave as XPath 2.0 or XQuery 1.0, but with serialization of atomic values according to W3C Schema canonical values.
	 */
	C14N
	{
		public <A> String atomToString(final A atom, final AtomBridge<A> atomBridge)
		{
			if (atom != null)
			{
				return atomBridge.getC14NForm(atom);
			}
			else
			{
				return "";
			}
		}
	},

	/**
	 * Legacy mode emulation causes an XPath processor to behave as XPath 1.0. When used in conjunction with an XQuery processor, the behavior is implementation dependent. Implementations may support legacy mode with XQuery processors to emulate XPath 1.0. Serialization
	 * of numeric atomic values conforms to XPath 1.0 rules.
	 */
	LEGACY
	{
		public <A> String atomToString(final A atom, final AtomBridge<A> atomBridge)
		{
			if (atom != null)
			{
				return atomBridge.getXPath10Form(atom);
			}
			else
			{
				return "";
			}
		}
	},

	/**
	 * Modern mode emulation causes an XPath processor to behave as XPath 2.0. It also causes an XQuery processor to behave as XQuery 1.0. Serialization of numeric atomic values conforms to XQuery 1.0 rules.
	 */
	MODERN
	{
		public <A> String atomToString(final A atom, final AtomBridge<A> atomBridge)
		{
			if (atom != null)
			{
				return atomBridge.getXQuery10Form(atom);
			}
			else
			{
				return "";
			}
		}
	};

	private static <A> String atomsToString(final List<? extends A> value, final String separator, final Emulation emulation, final AtomBridge<A> atomBridge)
	{
		final Iterator<? extends A> atoms = value.iterator();

		if (atoms.hasNext())
		{
			final A first = atoms.next();

			if (atoms.hasNext())
			{
				final StringBuilder sb = new StringBuilder();
				sb.append(emulation.atomToString(first, atomBridge));
				while (atoms.hasNext())
				{
					sb.append(separator).append(emulation.atomToString(atoms.next(), atomBridge));
				}
				return sb.toString();
			}
			else
			{
				return emulation.atomToString(first, atomBridge);
			}
		}
		else
		{
			return "";
		}
	}

	public final <A> String atomsToString(final List<? extends A> atoms, final String separator, final AtomBridge<A> atomBridge)
	{
		return atomsToString(atoms, separator, this, atomBridge);
	}

	public final <A> String atomsToString(final List<? extends A> atoms, final AtomBridge<A> atomBridge)
	{
		return atomsToString(atoms, " ", this, atomBridge);
	}
	
	public final String listToString(final List<String> atoms)
	{
	    final Iterator<String> atom = atoms.iterator();
	    if (atom.hasNext())
	    {
	        final String first = atom.next();
	        if (atom.hasNext())
	        {
	            final StringBuilder builder = new StringBuilder();
	            String separator = " ";
	            builder.append(first);
	            while (atom.hasNext())
	            {
	                builder.append(separator).append(atom.next());
	            }
	            return builder.toString();
	        }
	        else
	        {
	            return first;
	        }
	    }
        return "";
	}

	public abstract <A> String atomToString(A atom, AtomBridge<A> atomBridge);
}
