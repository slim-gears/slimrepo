// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import com.slimgears.slimrepo.core.interfaces.fields.Field;

/**
 * Created by Denis on 24-Apr-15
 *
 */
public interface FieldTypeMappingRegistrar extends FieldTypeMapper {
    interface Matcher {
        boolean match(Field field);
    }

    <T> void registerConverter(Class<? extends T> valueType, TypeConverter<T> converter);
    void registerConverter(Matcher matcher, TypeConverter converter);
    void registerNotConvertibleTypes(Iterable<Class> types);
}
