/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
/**

This package contains APIs to mutate existing XML trees, and is loosely based on the
<a href="http://www.w3.org/TR/xquery-update-10/">XQuery Update Facility</a>.

<h1>Overview</h1>

<p>Unlike with DOM, the creation of nodes has been separated from the Document element,
so this API may seem slightly unfamiliar to those most familiar with DOM. A node
must be created via {@link org.genxdm.mutable.NodeFactory}, and then added via a
{@link org.genxdm.mutable.MutableModel} or {@link org.genxdm.mutable.MutableCursor}.

</p>

<p>
 The mutating APIs were introduced mostly to facilitate migration of existing code
 that expects mutability. Along the way, we've tried to align with the XQUF.
 </p>
 
<h1>Getting Started</h1>
<p>
To start mutating existing trees, you must first get a {@link org.genxdm.mutable.MutableContext}
from a {@link org.genxdm.ProcessingContext}. From the <code>MutableContext</code> you can then
retrieve a {@link org.genxdm.mutable.MutableModel}, a {@link org.genxdm.mutable.NodeFactory} and
{@link org.genxdm.mutable.MutableCursor}.
</p>

<h1>Caution</h1>
<p>
The rest of GenXDM likes to work with immutable trees. When using the mutable APIs, it is
your responsibility to make sure the tree is correctly formed (namespaces properly declared,
for example). It is also your responsibility to make sure that you don't mutate the tree
at the same time the immutable APIs are expecting the tree to remain unchanged.</p>

<p>When dealing with DOM as the underlying tree, there are potentially some subtle
issues related to node ownership. Specifically, in DOM, a node is created by a {@link org.w3c.dom.Document}.
This means that when inserting a node into a tree, sometimes it is necessary to copy it,
in order to produce desirable results. From an API perspective, then everything should
work correctly, however, you may see performance issues when attempting certain kinds
of mutations.</p>

*/
package org.genxdm.mutable;
