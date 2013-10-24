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
package org.genxdm.exceptions;


/**
 * A wrapper class over implementation exceptions relating to well-formedness 
 * during parsing.
 */
// TODO: this is a marker class for particular kinds of GenXDMExceptions, so
// that they can be caught independently.  Query, query, query: do we want this;
// do we even want GenXDMException?  *Both* are runtime exceptions!
@SuppressWarnings("serial")
public final class XdmMarshalException extends GenXDMException
{
    public XdmMarshalException(final Throwable cause)
    {
        super(cause);
    }
}
