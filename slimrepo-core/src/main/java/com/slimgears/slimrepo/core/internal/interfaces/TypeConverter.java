package com.slimgears.slimrepo.core.internal.interfaces;

import com.slimgears.slimrepo.core.interfaces.fields.Field;

/**
 * Created by Denis on 08-May-15.
 */
public interface TypeConverter<T> {
    T toEntityType(Field<?, T> field, Object value);
    Object fromEntityType(Field<?, T> field, T value);
    Class getOutboundType(Field<?, T> field);
    Class getInboundType(Field<?, T> field);
}
