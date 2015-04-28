// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.sqlite;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

import java.util.Date;

/**
 * Created by ditskovi on 4/28/2015.
 */
public class SqliteTypeMappers {
    static abstract class AbstractTypeMapper<T, V> implements FieldTypeMappingRegistrar.TypeConverter<T> {
        private final Class<T> sourceType;
        private final Class<V> destinationType;

        protected AbstractTypeMapper(Class<T> sourceType, Class<V> destinationType) {
            this.sourceType = sourceType;
            this.destinationType = destinationType;
        }

        protected abstract V toOutbound(T value);
        protected abstract T fromInbound(V value);

        @Override
        public T toEntityType(Field<?, T> field, Object value) {
            //noinspection unchecked
            return fromInbound((V)value);
        }

        @Override
        public Object fromEntityType(Field<?, T> field, T value) {
            return toOutbound(value);
        }

        @Override
        public Class getOutboundType(Field<?, T> field) {
            return destinationType;
        }

        @Override
        public Class getInboundType(Field<?, T> field) {
            return destinationType;
        }

        public void install(FieldTypeMappingRegistrar registrar) {
            registrar.registerConverter(sourceType, this);
        }
    }

    private final static AbstractTypeMapper<Date, Long> DATE_TYPE_MAPPER =
            new AbstractTypeMapper<Date, Long>(Date.class, Long.class) {
                @Override
                protected Long toOutbound(Date value) {
                    return value != null ? value.getTime() : null;
                }

                @Override
                protected Date fromInbound(Long value) {
                    return value != null ? new Date(value) : null;
                }
            };

    private final static AbstractTypeMapper<Boolean, Integer> BOOLEAN_TYPE_MAPPER =
            new AbstractTypeMapper<Boolean, Integer>(Boolean.class, Integer.class) {
                @Override
                protected Integer toOutbound(Boolean value) {
                    return value ? 1 : 0;
                }

                @Override
                protected Boolean fromInbound(Integer value) {
                    return value != 0;
                }
            };

    private final static AbstractTypeMapper<Byte, Short> BYTE_TYPE_MAPPER =
            new AbstractTypeMapper<Byte, Short>(Byte.class, Short.class) {
                @Override
                protected Short toOutbound(Byte value) {
                    return (short)value;
                }

                @Override
                protected Byte fromInbound(Short value) {
                    return (byte)(short)value;
                }
            };

    public static void install(FieldTypeMappingRegistrar registrar) {
        DATE_TYPE_MAPPER.install(registrar);
        BYTE_TYPE_MAPPER.install(registrar);
        BOOLEAN_TYPE_MAPPER.install(registrar);
    }
}
