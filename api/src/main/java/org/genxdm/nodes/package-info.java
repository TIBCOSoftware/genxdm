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
<p>This package contains abstractions used to investigate the properties of nodes.
Taken together with the navigation abstractions found in the <code>axes</code>
package, the bulk of the {@link org.genxdm.Model} and {@link org.genxdm.Cursor} interfaces are
defined.</p> 

<p>There are three basic interfaces: {@link org.genxdm.nodes.Informer}, {@link org.genxdm.nodes.NodeInformer},
and {@link org.genxdm.nodes.Bookmark}.</p>

<p>Informer and NodeInformer provide equivalent information about nodes, using
stateful and stateless models.  Informer presumes that it has, as its state,
a single node.  NodeInformer has a &lt;N> supplied as the first argument of
all of its methods.</p>

<p>Support for type annotations and typed values is provided by {@link org.genxdm.nodes.TypeInformer}
and {@link org.genxdm.nodes.TypedNodeInformer}, which enhance Informer and NodeInformer
respectively.</p>

<p>Bookmark provides a positionally-immutable, stateful abstraction to wrap
individual nodes. It's basically a {@link org.genxdm.Cursor} locked into position.</p>

*/
package org.genxdm.nodes;
