/**
 * Copyright (c) 2010 TIBCO Software Inc.
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
package org.genxdm;

public enum DtdAttributeKind
{
    CDATA, // includes infoset "no value" and "unknown"
    ID, // NAME
    IDREF, // NAME
    IDREFS, // NAMES
    ENTITY, // NAME
    ENTITIES, // NAME
    NMTOKEN, // includes non-notation enumerations
    NMTOKENS,
    NOTATION; // NAME? or NMTOKEN?
    
    public static DtdAttributeKind get(final String saxName)
    {
        if ( (saxName == null) || (saxName.length() == 0) )
            return CDATA;
        if (saxName.equals("ID"))
            return ID;
        if (saxName.equals("IDREF"))
            return IDREF;
        if (saxName.equals("IDREFS"))
            return IDREFS;
        if (saxName.equals("ENTITY"))
            return ENTITY;
        if (saxName.equals("ENTITIES"))
            return ENTITIES;
        if (saxName.equals("NMTOKEN"))
            return NMTOKEN;
        if (saxName.equals("NMTOKENS"))
            return NMTOKENS;
        if (saxName.equals("NOTATION"))
            return NOTATION;
        return CDATA;
    }
}
