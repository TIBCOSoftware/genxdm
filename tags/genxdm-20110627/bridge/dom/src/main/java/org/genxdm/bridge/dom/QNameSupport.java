/*
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
package org.genxdm.bridge.dom;

import javax.xml.XMLConstants;

public class QNameSupport
{
	private QNameSupport()
	{
		throw new AssertionError();
	}

	/**
	 * Return the local-name part of the lexical xs:QName. <br/>
	 * The input is assumed to be lexically valid.
	 * 
	 * @param qualifiedName
	 *            The lexical xs:QName.
	 */
	public static String getLocalName(final String qualifiedName)
	{
		return qualifiedName.substring(qualifiedName.indexOf(":") + 1);
	}

	/**
	 * Return the prefix part of the lexical xs:QName. If there is no colon separator, returns the empty string. <br/>
	 * The input is assumed to be lexically valid.
	 * 
	 * @param qualifiedName
	 *            The lexical xs:QName.
	 */
	public static String getPrefix(final String qualifiedName)
	{
		final int index = qualifiedName.indexOf(':');
		if (index == -1)
		{
			return XMLConstants.DEFAULT_NS_PREFIX;
		}
		else
		{
			return qualifiedName.substring(0, index);
		}
	}
}
