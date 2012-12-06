package org.genxdm.xs.generator;

import java.io.IOException;
import java.io.Writer;

import javax.xml.namespace.QName;

import org.genxdm.xs.ComponentBag;
import org.genxdm.xs.SchemaComponentCache;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;

public interface SchemaBuilder
{
//    public enum FormDefault { QUALIFIED, UNQUALIFIED; }
    public enum GroupCompositor { ALL, CHOICE, SEQUENCE; }
    
    // schema element and its attributes. call only *once*
    void startSchema(String targetNamespace, String version);
    
//    void formDefault(FormDefault element, FormDefault attribute);
    // TODO: initially, we aren't going to support blockDefault (#all, {extension, restriction, substitution})
    // or finalDefault (#all, {extension, restriction} )
    
    void namespace(String prefix, String uri);
    
//    void foreignAttribute(String uri, String localName, String prefix, String value);
    
    // TODO: consider returning builders for these as well--SchemaBuilder, in each case.
    // if so, we do startInclude()/endInclude(), startImport()/endImport(). We should also
    // provide a resolver of some sort if we do it that way.
    // include, redefine, import
    void includeSchema(String location);
    
    // TODO: for a start, we're not allowing redefines; this is supposed to be simple stuff.
//    void redefineSchema(String location);
    
    void importSchema(String namespace, String location);
    
    // TODO: the complexity here is beyond apparent ROI; use xs:ID. if we have to, we'll reconsider
    // based on feedback from users.
    // identity constraints: unique, key, keyref; have name, selector, field+; selector and field are
    // empty with xpath attributes. keyref has a required refer attribute
    
    // top level definitions. most produce a builder.
    
    // notations are important to the people who use them, but we're not going to support them.
//    void notation(String name, String publicId, String SystemId);
    
    // the only attributes that can be defined globally (outside attribute groups) are qualified.
    // that sucks. let's not permit it.
//    void attribute(String name, QName type);
    
    void simpleTypeByUnion(String name, Iterable<QName> members);

    void simpleTypeByList(String name, QName type);
    
    SimpleTypeBuilder buildSimpleTypeByRestriction(String name, QName base);
    
    ComplexTypeBuilder buildComplexType(String name, boolean mixed);
    
    void element(String name, QName type);
    
    void element(String name, QName type, String defaultValue, boolean fixed, boolean nillable);
    
    GroupBuilder buildGroup(String name, GroupCompositor compositor);
    
    AttributeGroupBuilder buildAttributeGroup(String name);
    
    ComponentBag resolve(SchemaComponentCache prerequisites, SchemaExceptionHandler errors)
        throws AbortException;
    
    void write(Writer writer) 
        throws IOException;
}
