package org.genxdm.xs.generator;

import org.genxdm.xs.enums.WhiteSpacePolicy;

public interface SimpleTypeBuilder
{
    void minimum(String value, boolean inclusive);
    
    void maximum(String value, boolean inclusive);
    
    void digits(int total, int fraction);
    
    void length(int absolute, int min, int max); // -1 for ignore
    
    void whiteSpace(WhiteSpacePolicy policy);
    
    void enumeration(String value); // can be repeated
    
    void pattern(String regex); // can be repeated
}
