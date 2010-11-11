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
package org.genxdm.bridge.dom;

/**
 * Configuration options for processing using the DOM API under gXML.
 */
public final class DomOptions
{
    private boolean m_useEnhancedDOM = false;
    private boolean m_strictErrorChecking = false;

    public DomOptions()
    {
    }

    /**
     * Determines whether new document construction should use the
     * enhanced DOM implementation provided by gXML. This may provide
     * better XDM compatibility.
     * <br/>
     * The default value is <code>false</code>.
     */
    public boolean getUseEnhancedDOM()
    {
        return m_useEnhancedDOM;
    }

    /**
     * Sets the <code>useEnhancedDOM</code> property.
     */
    public void setUseEnhancedDOM(final boolean useEnhancedDOM)
    {
        m_useEnhancedDOM = useEnhancedDOM;
    }

    /**
     * Determines whether DOM implementations should use strict error checking.
     * <br/>
     * The default value is <code>false</code>.
     */
    public boolean getStrictErrorChecking()
    {
        return m_strictErrorChecking;
    }

    /**
     * Sets the <code>strictErrorChecking</code> property.
     */
    public void setStrictErrorChecking(final boolean strictErrorChecking)
    {
        m_strictErrorChecking = strictErrorChecking;
    }
}
