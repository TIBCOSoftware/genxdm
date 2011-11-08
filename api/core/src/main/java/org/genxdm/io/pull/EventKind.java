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
package org.genxdm.io.pull;

/**
 * Enumeration representing possible types of events in the XQuery Data Model.
 * 
 * @deprecated This enumeration has never been used outside of testing; it
 * may be removed.
 */
public enum EventKind
{
    START_SEQUENCE,
    END_SEQUENCE,

    START_DOCUMENT,
    END_DOCUMENT,

    START_ELEMENT,
    END_ELEMENT,

    ATTRIBUTE,
    CHARACTERS,
    SPACE,
    NAMESPACE,
    PROCESSING_INSTRUCTION,
    COMMENT,
    ATOM
}
