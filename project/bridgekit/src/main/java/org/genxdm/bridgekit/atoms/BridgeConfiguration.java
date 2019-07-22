/**
 * Copyright (c) 2019 TIBCO Software Inc.
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
package org.genxdm.bridgekit.atoms;

public final class BridgeConfiguration
{
    public boolean getDecodeBase64FromStringUntyped()
    {
        return m_decodeBase64;
    }
    
    public boolean getOmitDecimalPoint()
    {
        return m_omitDecimal;
    }
    
    public void setDecodeBase64FromStringUntyped(final boolean flag)
    {
        m_decodeBase64 = flag;
    }
    
    public void setOmitDecimalPoint(final boolean flag)
    {
        m_omitDecimal = flag;
    }
    
    private boolean m_decodeBase64;
    private boolean m_omitDecimal;
}