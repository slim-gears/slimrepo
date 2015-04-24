// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 24-Apr-15
 * <File Description>
 */
public class DefaultFieldTypeMapper implements FieldTypeMapper, FieldTypeMappingRegistrar {
    private static final TypeConverter DEFAULT_CONVERTER = new TypeConverter() {
        @Override
        public Object toEntityType(Object value) {
            return value;
        }

        @Override
        public Object fromEntityType(Object value) {
            return value;
        }

        @Override
        public Class getMappedType(Class fieldType) {
            return fieldType;
        }
    };

    private final Map<Class, TypeConverter> converterMap = new HashMap<>();

    @Override
    public <T> T toFieldType(Class<? extends T> fieldType, Object value) {
        return getConverter(fieldType).toEntityType(value);
    }

    @Override
    public <T> Object fromFieldType(Class<? extends T> fieldType, T value) {
        return getConverter(fieldType).fromEntityType(value);
    }

    @Override
    public Class getMappedType(Class fieldType) {
        return getConverter(fieldType).getMappedType(fieldType);
    }

    @Override
    public <T> void registerConverter(Class<? extends T> fieldType, TypeConverter<T> converter) {
        converterMap.put(fieldType, converter);
    }

    @SuppressWarnings("unchecked")
    private <T> TypeConverter<T> getConverter(Class<? extends T> fieldType) {
        TypeConverter converter = converterMap.get(fieldType);
        return converter != null
                ? (TypeConverter<T>)converter
                : (TypeConverter<T>)DEFAULT_CONVERTER;
    }
}
