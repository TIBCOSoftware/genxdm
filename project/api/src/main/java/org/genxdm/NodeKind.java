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
package org.genxdm;

/**
 * Enumeration representing possible types of nodes in the XQuery Data Model.
 */
public enum NodeKind
{
	/**
	 * The <code>element</code> node kind.
	 */
	ELEMENT
	{
		public boolean isAttribute()
		{
			return false;
		}

		public boolean isChild()
		{
			return true;
		}
		
		public boolean isContainer()
		{
			return true;
		}

		public boolean isNamespace()
		{
			return false;
		}
	},

	/**
	 * The <code>text</code> node kind.
	 */
	TEXT
	{
		public boolean isAttribute()
		{
			return false;
		}

		public boolean isChild()
		{
			return true;
		}
		
		public boolean isContainer()
		{
			return false;
		}

		public boolean isNamespace()
		{
			return false;
		}
	},

	/**
	 * The <code>attribute</code> node kind.
	 */
	ATTRIBUTE
	{
		public boolean isAttribute()
		{
			return true;
		}

		public boolean isChild()
		{
			return false;
		}
		
		public boolean isContainer()
		{
			return false;
		}

		public boolean isNamespace()
		{
			return false;
		}
	},

	/**
	 * The <code>namespace</code> node kind.
	 */
	NAMESPACE
	{
		public boolean isAttribute()
		{
			return false;
		}

		public boolean isChild()
		{
			return false;
		}
		
		public boolean isContainer()
		{
			return false;
		}

		public boolean isNamespace()
		{
			return true;
		}
	},

	/**
	 * The <code>document</code> node kind.
	 */
	DOCUMENT
	{
		public boolean isAttribute()
		{
			return false;
		}

		public boolean isChild()
		{
			return false;
		}
		
		public boolean isContainer()
		{
			return true;
		}

		public boolean isNamespace()
		{
			return false;
		}
	},

	/**
	 * The <code>processing-instruction</code> node kind.
	 */
	PROCESSING_INSTRUCTION
	{
		public boolean isAttribute()
		{
			return false;
		}

		public boolean isChild()
		{
			return true;
		}
		
		public boolean isContainer()
		{
			return false;
		}

		public boolean isNamespace()
		{
			return false;
		}
	},

	/**
	 * The <code>comment</code> node kind.
	 */
	COMMENT
	{
		public boolean isAttribute()
		{
			return false;
		}

		public boolean isChild()
		{
			return true;
		}

		public boolean isContainer()
		{
			return false;
		}
		
		public boolean isNamespace()
		{
			return false;
		}
	};

	public abstract boolean isAttribute();

	public abstract boolean isChild();

	public abstract boolean isNamespace();
	
	public abstract boolean isContainer();
}
