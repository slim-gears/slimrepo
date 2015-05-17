package com.slimgears.slimrepo.core.internal.converters;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.TypeConverter;

/**
 * Created by Denis on 13-May-15.
 */
public class EmptyTypeConverter implements TypeConverter {
    public final static TypeConverter INSTANCE = new EmptyTypeConverter();

    @Override
    public Object toEntityType(Field field, Object value) {
        return value;
    }

    @Override
    public Object fromEntityType(Field field, Object value) {
        return value;
    }

    @Override
    public Class getOutboundType(Field field) {
        return field.metaInfo().getValueType();
    }

    @Override
    public Class getInboundType(Field field) {
        return getOutboundType(field);
    }
}
