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
package org.gxml.xs.components;

import org.gxml.xs.constraints.SmHasValueConstraint;
import org.gxml.xs.types.SmNodeType;

/**
 * A data component is a named schema component that may have value constraints.
 * 
 * @param <A>
 *            The atom handle.
 */
public interface SmDataComponent<A> extends SmComponent<A>, SmHasValueConstraint<A>, SmNodeType<A>
{
}
