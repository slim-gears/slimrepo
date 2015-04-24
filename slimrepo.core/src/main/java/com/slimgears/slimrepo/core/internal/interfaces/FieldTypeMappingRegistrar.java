// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

/**
 * Created by Denis on 24-Apr-15
 * <File Description>
 */
public interface FieldTypeMappingRegistrar extends FieldTypeMapper {
    interface TypeConverter<T> {
        T toEntityType(Object value);
        Object fromEntityType(T value);
        Class getMappedType(Class fieldType);
    }

    <T> void registerConverter(Class<? extends T> fieldType, TypeConverter<T> converter);
}
