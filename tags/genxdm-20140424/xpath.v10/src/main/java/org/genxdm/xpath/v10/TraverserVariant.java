/*
 * Copyright (c) 2012 TIBCO Software Inc.
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
package org.genxdm.xpath.v10;

import org.genxdm.nodes.Traverser;
import org.genxdm.xpath.v10.ExprException;

/**
 * Represents a variant value where a sequence of nodes can
 * be inspected with a "Traverser" rather than as "N".
 */
public interface TraverserVariant extends VariantCore
{
    Traverser convertToTraverser();

    boolean convertToPredicate(TraverserDynamicContext context);

    // TODO - makePermanent no longer appears to be used?
    TraverserVariant makePermanentCursor() throws ExprException;
}
