/*
 * Copyright (c) 2011 TIBCO Software Inc.
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
package org.genxdm.io;

import java.util.Map;
import javax.xml.namespace.QName;

public interface SerializationParams
{
    boolean getByteOrderMark();
    
    Iterable<QName> getCDataSectionElements();
    
    String getDoctypePublic();
    
    String getDoctypeSystem();
    
    String getEncoding();
    
    boolean getEscapeURIAttributes();
    
    boolean getIncludeContentType();
    
    boolean getIndent();
    
    String getMediaType();
    
    QName getMethod();

    String getNormalizationForm(); // NFC, NFD, NFKC, NFKD, fully-normalized, none (null), or implementation-defined

    boolean getOmitXMLDeclaration();
    
    Standalone getStandalone();
    
    boolean getUndeclarePrefixes();
    
    // should this be CodePoint instead?
    Map<Character, String> getUseCharacterMaps();
    
    String getVersion();
    
    public static enum Standalone { YES, NO, OMIT; }
}
