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
