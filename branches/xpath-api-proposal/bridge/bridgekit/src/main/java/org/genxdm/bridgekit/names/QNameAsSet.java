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
package org.genxdm.bridgekit.names;

import javax.xml.namespace.QName;

public class QNameAsSet
{

    private QNameAsSet() {}
    
    private static boolean subset(final String lhs, final String rhs)
    {
        if (rhs != null)
        {
            if (lhs != null)
            {
                return lhs.equals(rhs);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * lhs <: rhs
     */
    public static boolean subset(final QName lhs, final QName rhs)
    {
        if (null != lhs)
        {
            if (null != rhs)
            {
                return subset(lhs.getNamespaceURI(), rhs.getNamespaceURI()) && subset(lhs.getLocalPart(), rhs.getLocalPart());
            }
            else
            {
                return true;
            }
        }
        else
        {
            if (null != rhs)
            {
                return subset(null, rhs.getNamespaceURI()) && subset(null, rhs.getLocalPart());
            }
            else
            {
                return true;
            }
        }
    }

}
