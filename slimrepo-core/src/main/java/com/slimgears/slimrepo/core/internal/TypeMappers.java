// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.interfaces.TypeConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 30-Apr-15
 * <File Description>
 */
public class TypeMappers {
    static class EnumTypeConverter implements TypeConverter, FieldTypeMappingRegistrar.Matcher {
        private final Map<Class, Object[]> enumValuesCache = new HashMap<>();

        static final EnumTypeConverter INSTANCE = new EnumTypeConverter();

        @Override
        public Object toEntityType(Field field, Object value) {
            return value != null ? fromOrdinal(field.metaInfo().getValueType(), (Integer)value) : null;
        }

        @Override
        public Object fromEntityType(Field field, Object value) {
            return value != null ? ((Enum)value).ordinal() : null;
        }

        @Override
        public Class getOutboundType(Field field) {
            return Integer.class;
        }

        @Override
        public Class getInboundType(Field field) {
            return Integer.class;
        }

        @Override
        public boolean match(Field field) {
            return field.metaInfo().getValueType().isEnum();
        }

        private Object fromOrdinal(Class enumClass, int ordinal) {
            return getEnumValues(enumClass)[ordinal];
        }

        private Object[] getEnumValues(Class enumClass) {
            Object[] values = enumValuesCache.get(enumClass);
            if (values == null) {
                values = enumClass.getEnumConstants();
                enumValuesCache.put(enumClass, values);
            }
            return values;
        }
    }

    public static void install(FieldTypeMappingRegistrar registrar) {
        registrar.registerConverter(EnumTypeConverter.INSTANCE, EnumTypeConverter.INSTANCE);
    }
}
