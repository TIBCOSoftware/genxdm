package org.genxdm.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

public class GenxdmSerialization
    implements SerializationParams
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
    
    // TODO: figure out how to collect mutators into another interface.
    
    public void setByteOrderMark(boolean flag)
    {
        bom = flag;
    }
    
    public void setCDataSectionElements(List<QName> list)
    {
        cdataElements = list;
    }
    
    public void setDoctypes(String pub, String sys)
    {
        doctypePublic = pub;
        doctypeSystem = sys;
    }

    public void setEncoding(String enc)
    {
        encoding = enc;
    }
    
    public void setIndent(boolean ind)
    {
        indent = ind;
    }
    
    public void setMediaType(String type)
    {
        mediaType = type;
    }
    
    public void setOmitXMLDeclaration(boolean flag)
    {
        omitDeclaration = flag;
    }
    
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
    
    private static String URI = "http://www.genxdm.org/serialization";
    private static final QName method = new QName(URI, "basic");
}
