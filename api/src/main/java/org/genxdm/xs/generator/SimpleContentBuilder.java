package org.genxdm.xs.generator;

// used for complex types with simple content, derived by extension or restriction
// the simpletypebuilder methods are only valid when derived by restriction (yes,
// for simple content, restriction both extends and restricts ... *sigh*)
public interface SimpleContentBuilder
    extends AttributeGroupBuilder, SimpleTypeBuilder
{

}
