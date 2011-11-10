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
<p>Provides abstractions defining navigation from one node to another, and
iteration over axes (collections of nodes standing in a particular relationship
with the origin node).  Taken together with the informational abstractions in
the <code>nodes</code> package, defines the bulk of functionality in the central
{@link Model} and {@link Cursor} abstractions.</p>

<p>There are four significant abstractions, but only three are actually implemented
and used: {@link Navigator}, {@link NodeNavigator}, {@link AxisNodeNavigator},
and (unused) {@link AxisNavigator}. In addition, <code>Navigator</code> has had
its single N-specialized method moved into a separate interface, {@link Repositioner}.
{@link Cursor} actually implements <code>Repositioner</code> rather than the
<code>Navigator</code> base interface, however.</p>

<p><code>Navigator</code> provides <code>moveTo[Relationship]</code> methods,
where the target location stands in a method-specified relationship to the
(assumed) node which represents positional state in the tree.  These methods
generally return a boolean value, <code>true</code> on success, <code>false</code> if there
is no node standing in the specified relationship to the current context node.
The actual interface extended by <code>Cursor</code> is <code>Repositioner</code>,
which adds the ability to move to an arbitrary node.</p>

<p><code>NodeNavigator</code> provides methods equivalent to <code>Navigator</code>,
but is a stateless abstraction.  Consequently, each of its <code>get[Relationship]</code>
methods takes a &lt;N>ode as its first argument, and returns one (or null, if
no node has such a relationship to the supplied node).</p>

<p><code>AxisNodeNavigator</code> provides navigation over {@link Iterable}
collections of nodes that all stand in a given relationship to the (supplied
in each method) context &lt;N>ode.  The (experimental) <code>AxisNavigator</code>
is designed to provide similar functionality for Cursor, but is untested (in
fact, unimplemented).</p>
*/
package org.genxdm.axes;
