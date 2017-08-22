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
    
    void setIndent(boolean ind);
    
    void setMediaType(String type);
    
    void setNamespaceContextHints(NamespaceContext hints);
    
//    void setNormalizationForm(String/enum);
    
    void setOmitXMLDeclaration(boolean flag);
    
    void setStandalone(Standalone value);
}
