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
    @SuppressWarnings("unchecked")
    class MappingTypeConverter implements Matcher, TypeConverter {
        private final Map<Class, TypeConverter> converterMap = new HashMap<>();

        <T> void registerConverter(Class<? extends T> valueType, TypeConverter<T> converter) {
            converterMap.put(valueType, converter);
        }

        @Override
        public boolean match(Field field) {
            return converterMap.containsKey(field.metaInfo().getValueType());
        }

        @Override
        public Object toEntityType(Field field, Object value) {
            return getConverter(field.metaInfo().getValueType()).toEntityType(field, value);
        }

        @Override
        public Object fromEntityType(Field field, Object value) {
            return getConverter(field.metaInfo().getValueType()).fromEntityType(field, value);
        }

        @Override
        public Class getMappedType(Field field) {
            return getConverter(field.metaInfo().getValueType()).getMappedType(field);
        }

        private TypeConverter getConverter(Class valueType) {
            return converterMap.get(valueType);
        }
    }

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
        public Class getMappedType(Field field) {
            return field.metaInfo().getValueType();
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

    private final MappingTypeConverter mappingTypeConverter = new MappingTypeConverter();
    private final Collection<MatcherConverterEntry> matcherConverterEntries = new ArrayList<>();

    public DefaultFieldTypeMapper() {
        registerConverter(mappingTypeConverter, mappingTypeConverter);
    }

    @Override
    public <T> T toFieldType(Field<?, T> field, Object value) {
        return getConverter(field).toEntityType(field, value);
    }

    @Override
    public <T> Object fromFieldType(Field<?, T> field, T value) {
        return getConverter(field).fromEntityType(field, value);
    }

    @Override
    public <T> Class getMappedType(Field<?, T> field) {
        return getConverter(field).getMappedType(field);
    }

    @Override
    public <T> void registerConverter(Class<? extends T> valueType, TypeConverter<T> converter) {
        mappingTypeConverter.registerConverter(valueType, converter);
    }

    @Override
    public void registerConverter(Matcher matcher, TypeConverter converter) {
        matcherConverterEntries.add(new MatcherConverterEntry(matcher, converter));
    }

    @SuppressWarnings("unchecked")
    private <T> TypeConverter<T> getConverter(Field<?, T> field) {
        for (MatcherConverterEntry entry : matcherConverterEntries) {
            if (entry.matcher.match(field)) return entry.converter;
        }

        return DEFAULT_CONVERTER;
    }
}
