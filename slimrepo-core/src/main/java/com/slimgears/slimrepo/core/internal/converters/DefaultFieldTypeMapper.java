// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.converters;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.interfaces.TypeConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 24-Apr-15
 *
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
        public Class getOutboundType(Field field) {
            return getConverter(field.metaInfo().getValueType()).getOutboundType(field);
        }

        @Override
        public Class getInboundType(Field field) {
            return getConverter(field.metaInfo().getValueType()).getInboundType(field);
        }

        private TypeConverter getConverter(Class valueType) {
            return converterMap.get(valueType);
        }
    }

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
    public <T> Class getOutboundType(Field<?, T> field) {
        return getConverter(field).getOutboundType(field);
    }

    @Override
    public <T> Class getInboundType(Field<?, T> field) {
        return getConverter(field).getInboundType(field);
    }

    @Override
    public <T> void registerConverter(Class<? extends T> valueType, TypeConverter<T> converter) {
        mappingTypeConverter.registerConverter(valueType, converter);
    }

    @Override
    public void registerConverter(Matcher matcher, TypeConverter converter) {
        matcherConverterEntries.add(new MatcherConverterEntry(matcher, converter));
    }

    @Override
    public void registerNotConvertibleTypes(Iterable<Class> types) {
        for (Class c : types) {
            registerConverter(c, EmptyTypeConverter.INSTANCE);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> TypeConverter<T> getConverter(Field<?, T> field) {
        for (MatcherConverterEntry entry : matcherConverterEntries) {
            if (entry.matcher.match(field)) return entry.converter;
        }

        throw new RuntimeException(
                "Type converter for type " +
                field.metaInfo().getValueType().getSimpleName() +
                " is not registered");
    }
}
