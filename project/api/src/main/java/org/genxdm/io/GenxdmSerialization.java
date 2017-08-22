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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

public class GenxdmSerialization
    implements SerializationParams, ConfigurableSerializationParams
{

    @Override
    public boolean getByteOrderMark()
    {
        return bom;
    }

    @Override
    public Iterable<QName> getCDataSectionElements()
    {
        return cdataElements;
    }

    @Override
    public String getDoctypePublic()
    {
        return doctypePublic;
    }

    @Override
    public String getDoctypeSystem()
    {
        return doctypeSystem;
    }

    @Override
    public String getEncoding()
    {
        return (encoding == null) ? "UTF-8" : encoding;
    }

    @Override
    public boolean getEscapeURIAttributes()
    {
        return false;
    }

    @Override
    public boolean getIncludeContentType()
    {
        return false;
    }

    @Override
    public boolean getIndent()
    {
        return indent;
    }

    @Override
    public String getMediaType()
    {
        return mediaType;
    }

    @Override
    public QName getMethod()
    {
        return method;
    }
    
    @Override
    public NamespaceContext getNamespaceContextHints()
    {
        return namespaceHints;
    }

    @Override
    public String getNormalizationForm()
    {
        // TODO: enable this when we enable normalization.
        return null;
    }

    @Override
    public boolean getOmitXMLDeclaration()
    {
        return omitDeclaration;
    }

    @Override
    public Standalone getStandalone()
    {
        return standalone;
    }

    @Override
    public boolean getUndeclarePrefixes()
    {
        // TODO: for the moment, no support for 1.1
        return false;
    }

    @Override
    public Map<Character, String> getUseCharacterMaps()
    {
        // TODO: not supported at this time.
        return new HashMap<Character, String>();
    }

    @Override
    public String getVersion()
    {
        // TODO: for the moment, no support for 1.1
        return "1.0";
    }
    
    @Override
    public void setByteOrderMark(boolean flag)
    {
        bom = flag;
    }
    
    @Override
    public void setCDataSectionElements(List<QName> list)
    {
        cdataElements = list;
    }
    
    @Override
    public void setDoctypes(String pub, String sys)
    {
        doctypePublic = pub;
        doctypeSystem = sys;
    }

    @Override
    public void setEncoding(String enc)
    {
        encoding = enc;
    }
    
    @Override
    public void setIndent(boolean ind)
    {
        indent = ind;
    }
    
    @Override
    public void setMediaType(String type)
    {
        mediaType = type;
    }
    
    @Override
    public void setNamespaceContextHints(NamespaceContext hints)
    {
        namespaceHints = hints;
    }
    
    @Override
    public void setOmitXMLDeclaration(boolean flag)
    {
        omitDeclaration = flag;
    }
    
    @Override
    public void setStandalone(Standalone value)
    {
        standalone = value;
    }
    
    private boolean indent;
    private boolean omitDeclaration;
    private boolean bom;
    private String encoding;
    private String mediaType;
    private String doctypePublic;
    private String doctypeSystem;
    private Standalone standalone = Standalone.OMIT;
    private List<QName> cdataElements;
    private NamespaceContext namespaceHints;
    
    private static String URI = "http://www.genxdm.org/serialization";
    private static final QName method = new QName(URI, "basic");
}
