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
<p>This package provides implementations of the javax.xml.transform interfaces
for GenXDM.  The supplied implementations are expected to work with both current
bridges and all future bridges.</p>

<p>Only processors that understand the GenXDM implementations can use these, but
the design failures of javax.xml.transform are not ours to ameliorate.</p>

<p>{@link javax.xml.transform.Source} and {@link javax.xml.transform.Result} are 
here implemented <em>together with</em> methods that provide the basic functionality
that the names suggest.  For source, the processor is able to retrieve a Model
and a &lt;N>ode.  For result, the processor has a FragmentBuilder that it can
use to build a tree; the end user can then use its NodeSource-derived methods
to examine the actual content.</p>

<p>Special adapters are provided to allow existing code to supply and consume
DOMSource and DOMResult, while the transformational processor actually uses
XdmSource and XdmResult.</p>
*/
package org.genxdm.transform;
