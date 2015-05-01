// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.sqlite;

import com.slimgears.slimrepo.core.internal.AbstractTypeConverter;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

import java.util.Date;

/**
 * Created by ditskovi on 4/28/2015.
 */
public class SqliteTypeMappers {
    private final static AbstractTypeConverter<Date, Long> DATE_TYPE_MAPPER =
            new AbstractTypeConverter<Date, Long>(Date.class, Long.class) {
                @Override
                protected Long toOutbound(Date value) {
                    return value != null ? value.getTime() : null;
                }

                @Override
                protected Date fromInbound(Long value) {
                    return value != null ? new Date(value) : null;
                }
            };

    private final static AbstractTypeConverter<Boolean, Integer> BOOLEAN_TYPE_MAPPER =
            new AbstractTypeConverter<Boolean, Integer>(Boolean.class, Integer.class) {
                @Override
                protected Integer toOutbound(Boolean value) {
                    return value ? 1 : 0;
                }

                @Override
                protected Boolean fromInbound(Integer value) {
                    return value != 0;
                }
            };

    private final static AbstractTypeConverter<Byte, Short> BYTE_TYPE_MAPPER =
            new AbstractTypeConverter<Byte, Short>(Byte.class, Short.class) {
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
