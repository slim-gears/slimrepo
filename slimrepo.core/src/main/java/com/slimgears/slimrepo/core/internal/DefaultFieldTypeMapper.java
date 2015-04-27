// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 24-Apr-15
 * <File Description>
 */
public class DefaultFieldTypeMapper implements FieldTypeMapper, FieldTypeMappingRegistrar {
    private static final TypeConverter DEFAULT_CONVERTER = new TypeConverter() {
        @Override
        public Object toEntityType(Field field, Object value) {
            return value;
        }

        @Override
        public Object fromEntityType(Field field, Object value) {
            return value;
        }

        @Override
        public Class getMappedType(Class fieldType) {
            return fieldType;
        }
    };

    class MatcherConverterEntry {
        public final Matcher matcher;
        public final TypeConverter converter;

        MatcherConverterEntry(Matcher matcher, TypeConverter converter) {
            this.matcher = matcher;
            this.converter = converter;
        }
    }

    private final Map<Class, TypeConverter> converterMap = new HashMap<>();
    private final Collection<MatcherConverterEntry> matcherConverterEntries = new ArrayList<>();

    @Override
    public <T> T toFieldType(Field<?, T> field, Object value) {
        return getConverter(field).toEntityType(field, value);
    }

    @Override
    public <T> Object fromFieldType(Field<?, T> field, T value) {
        return getConverter(field).fromEntityType(field, value);
    }

    @Override
    public Class getMappedType(Field<?, ?> field) {
        return getConverter(field).getMappedType(field.metaInfo().getType());
    }

    @Override
    public <T> void registerConverter(Class<? extends T> valueType, TypeConverter<T> converter) {
        converterMap.put(valueType, converter);
    }

    @Override
    public void registerConverter(Matcher matcher, TypeConverter converter) {
        matcherConverterEntries.add(new MatcherConverterEntry(matcher, converter));
    }

    @SuppressWarnings("unchecked")
    private <T> TypeConverter<T> getConverter(Field<?, T> field) {
        TypeConverter converter = converterMap.get(field.metaInfo().getType());
        if (converter != null) return converter;

        for (MatcherConverterEntry entry : matcherConverterEntries) {
            if (entry.matcher.match(field)) return entry.converter;
        }

        return DEFAULT_CONVERTER;
    }
}
