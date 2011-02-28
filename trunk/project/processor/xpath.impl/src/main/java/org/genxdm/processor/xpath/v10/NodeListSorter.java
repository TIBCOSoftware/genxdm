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
package org.genxdm.processor.xpath.v10;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genxdm.Model;
import org.genxdm.processor.xpath.v10.iterators.ListNodeIterator;
import org.genxdm.xpath.v10.expressions.ExprException;
import org.genxdm.xpath.v10.iterators.NodeIterator;

/**
 * Like the name suggests, provides a mechanism to sort the nodes in a NodeIterator based upon the test of <code>Comparator</code>
 */
public class NodeListSorter
{
	private NodeListSorter()
	{
		throw new AssertionError();
	}

	static public <N> NodeIterator<N> sort(final NodeIterator<N> iter, final Model<N> model) throws ExprException
	{
	    List<N> list = new ArrayList<N>(10);
	    
	    for (;;)
	    {
	        N item = iter.next();
	        if (item == null)
	            break;
	        list.add(item);
	    }
	    Collections.sort(list, model);
	    return new ListNodeIterator<N>(list);
	    
	}

}
