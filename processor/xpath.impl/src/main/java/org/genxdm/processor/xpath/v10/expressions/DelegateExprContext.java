/**
 * Portions copyright (c) 1998-1999, James Clark : see copyingjc.txt for
 * license details
 * Portions copyright (c) 2002, Bill Lindsey : see copying.txt for license
 * details
 * 
 * Portions copyright (c) 2009-2010 TIBCO Software Inc.
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
package org.genxdm.processor.xpath.v10.expressions;

import javax.xml.namespace.QName;

import org.genxdm.xpath.v10.ExprContextDynamic;
import org.genxdm.xpath.v10.ExprException;
import org.genxdm.xpath.v10.ExtensionContext;
import org.genxdm.xpath.v10.Variant;

/**
 * a base class for ExprContext classes that override some methods of an existing ExprContext, and delegate the rest of them to that existing ExprContest
 */
public class DelegateExprContext<N> 
    implements ExprContextDynamic<N>
{
	protected final ExprContextDynamic<N> origContext;

	/**
	 * wrap around an existing ExprContext
	 */
	protected DelegateExprContext(final ExprContextDynamic<N> context)
	{
		origContext = context;
	}

	public int getContextPosition() throws ExprException
	{
		return origContext.getContextPosition();
	}

	public int getContextSize() throws ExprException
	{
		return origContext.getContextSize();
	}

	public ExtensionContext<N> getExtensionContext(final String namespace) throws ExprException
	{
		return origContext.getExtensionContext(namespace);
	}

	public Variant<N> getVariableValue(final QName name) throws ExprException
	{
		return origContext.getVariableValue(name);
	}
}
