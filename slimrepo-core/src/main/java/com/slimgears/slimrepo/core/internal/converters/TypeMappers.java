// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.converters;

import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

/**
 * Created by Denis on 30-Apr-15
 * <File Description>
 */
public class TypeMappers {
    public static void install(FieldTypeMappingRegistrar registrar) {
        registrar.registerConverter(EnumTypeConverter.INSTANCE, EnumTypeConverter.INSTANCE);
        registrar.registerConverter(SerializableTypeConverter.INSTANCE, SerializableTypeConverter.INSTANCE);
    }
}
