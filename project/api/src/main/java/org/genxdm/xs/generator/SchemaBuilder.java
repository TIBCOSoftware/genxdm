package org.genxdm.xs.generator;

import javax.xml.namespace.QName;

import org.genxdm.xs.ComponentSource;
import org.genxdm.xs.enums.DerivationMethod;

// as a rule, an implementation ought to extend schemawriter, too.
public interface SchemaBuilder
    extends ComponentSource
{
    public enum GroupCompositor { ALL, CHOICE, SEQUENCE; }
    
    // schema element and its attributes. call only *once*
    // TODO: alternately, pass a Map<String, String> of namespace mappings.
    void startSchema(String targetNamespace, String version);
    
    void namespace(String prefix, String uri);
    
    void includeSchema(String location);
    
    void importSchema(String namespace, String location);
    
    // top level definitions. most produce a builder.
    
    void simpleTypeByUnion(String name, Iterable<QName> members);

    void simpleTypeByList(String name, QName type);
    
    SimpleTypeBuilder buildSimpleTypeByRestriction(String name, QName base);
    
    SimpleContentBuilder buildComplexTypeWithSimpleContent(String name, QName base, DerivationMethod derivation, boolean mixed);
    
    ComplexTypeBuilder buildComplexTypeWithComplexContent(String name, QName base, boolean mixed);
    
    // build by composition
    ComplexTypeBuilder buildComplexType(String name, boolean mixed);
    
    void element(String name, QName type);
    
    void element(String name, QName type, String defaultValue, boolean fixed, boolean nillable);
    
    GroupBuilder buildGroup(String name, GroupCompositor compositor);
    
    AttributeGroupBuilder buildAttributeGroup(String name);
    
// TODO: the complexity here is beyond apparent ROI; use xs:ID. if we have to, we'll reconsider
// based on feedback from users.
// identity constraints: unique, key, keyref; have name, selector, field+; selector and field are
// empty with xpath attributes. keyref has a required refer attribute
//  public enum FormDefault { QUALIFIED, UNQUALIFIED; }
//  void formDefault(FormDefault element, FormDefault attribute);
//initially, we aren't going to support blockDefault (#all, {extension, restriction, substitution})
//or finalDefault (#all, {extension, restriction} )
//void foreignAttribute(String uri, String localName, String prefix, String value);
//notation and redefine are not supported.
// the only attributes that can be defined globally (outside attribute groups) are qualified.
// that sucks. let's not permit it.
//    void attribute(String name, QName type);
}
