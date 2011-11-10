/*
 * Copyright (c) 2009-2011 TIBCO Software Inc.
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
package org.genxdm.typed.io;

import org.genxdm.io.pull.EventReader;
import org.genxdm.nodes.TypeInformer;

/**
 * Allows data model events to be pulled sequentially.
 * @deprecated This interface has never been implemented; it is at risk of
 * removal for lack of interest.
 */
public interface TypedReader<A> 
    extends EventReader, TypeInformer<A>
{
}
