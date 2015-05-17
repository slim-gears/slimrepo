package com.slimgears.slimrepo.core.internal.converters;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.TypeConverter;

/**
 * Created by Denis on 13-May-15.
 */
public abstract class AbstractTypeConverter<TSource, TDestination> implements TypeConverter<TSource> {
    protected final Class<TDestination> destinationType;

    protected abstract TSource fromInbound(TDestination value);
    protected abstract TDestination toOutbound(TSource value);

    protected AbstractTypeConverter(Class<TDestination> destinationType) {
        this.destinationType = destinationType;
    }

    @Override
    public TSource toEntityType(Field<?, TSource> field, Object value) {
        //noinspection unchecked
        return value != null ? fromInbound((TDestination)value) : null;
    }

    @Override
    public Object fromEntityType(Field<?, TSource> field, TSource value) {
        return value != null ? toOutbound(value) : null;
    }

    @Override
    public Class getOutboundType(Field<?, TSource> field) {
        return destinationType;
    }

    @Override
    public Class getInboundType(Field<?, TSource> field) {
        return getOutboundType(field);
    }
}
