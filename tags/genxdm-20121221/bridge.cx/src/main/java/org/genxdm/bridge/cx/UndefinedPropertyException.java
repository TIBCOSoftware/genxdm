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
package org.genxdm.bridge.cx;

import org.genxdm.NodeKind;

/**
 * Exception to be used when calling an method that is not defined for the node type.
 * <p/>
 * For example, the name property is not defined for a text node.
 * 
 * @author David Holmes
 */
@SuppressWarnings("serial")
final class UndefinedPropertyException extends AssertionError
{
    public UndefinedPropertyException(final String method, final NodeKind nodeKind)
    {
            super("Method [" + method + "] is not defined for the node type \"" + nodeKind + "\".");
    }
}
