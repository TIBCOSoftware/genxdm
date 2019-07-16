package org.genxdm.io;

import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.genxdm.io.SerializationParams.Standalone;

public interface ConfigurableSerializationParams
{

    void setByteOrderMark(boolean flag);
    
    void setCDataSectionElements(List<QName> list);
    
    void setDoctypes(String pub, String sys);

    void setEncoding(String enc);
    
    // escapeURIAttributes boolean ; includeContentType boolean
    
    void setIndent(boolean ind);
    
    void setMediaType(String type);
    
    // method QName (really, don't want this to be terribly settable; constructor only)
    
    void setNamespaceContextHints(NamespaceContext hints);
    
//    void setNormalizationForm(String/enum);
    
    void setOmitXMLDeclaration(boolean flag);
    
    void setStandalone(Standalone value);
    
    // undeclarePrefixes boolean;
    
    // useCharacterMaps Map<Character [CodePoint instead?], String>
    
    // version String [implementation defined meaning; undefined by us]
}
