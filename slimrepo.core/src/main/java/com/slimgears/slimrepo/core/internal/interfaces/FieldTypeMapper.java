// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

/**
 * Created by Denis on 24-Apr-15
 * <File Description>
 */
public interface FieldTypeMapper {
    <T> T toFieldType(Class<? extends T> fieldType, Object value);
    <T> Object fromFieldType(Class<? extends T> fieldType, T value);
    Class getMappedType(Class fieldType);
}
