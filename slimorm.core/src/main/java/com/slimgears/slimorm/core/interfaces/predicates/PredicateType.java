// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces.predicates;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public enum PredicateType {
    FIELD_IS_NULL,
    FIELD_IS_NOT_NULL,
    VALUE_FIELD_EQUALS,
    VALUE_FIELD_NOT_EQUALS,
    VALUE_FIELD_IN,
    VALUE_FIELD_NOT_IN,
    NUMBER_FIELD_GREATER,
    NUMBER_FIELD_LESS,
    NUMBER_FIELD_GREATER_EQUAL,
    NUMBER_FIELD_LESS_EQUAL,
    NUMBER_FIELD_BETWEEN,
    STRING_FIELD_CONTAINS,
    STRING_FIELD_NOT_CONTAINS,
    STRING_FIELD_STARTS_WITH,
    STRING_FIELD_ENDS_WITH,
    STRING_FIELD_NOT_STARTS_WITH,
    STRING_FIELD_NOT_ENDS_WITH,
    COMPOSITE_AND,
    COMPOSITE_OR,
    COMPOSITE_NOT
}
