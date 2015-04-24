// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;

/**
 * Created by Denis on 24-Apr-15
 * <File Description>
 */
public abstract class AbstractOrmServiceProvider implements OrmServiceProvider {
    private FieldTypeMappingRegistrar typeMappingRegistrar;

    @Override
    public FieldTypeMapper getFieldTypeMapper() {
        return getTypeMappingRegistrar();
    }

    protected FieldTypeMappingRegistrar getTypeMappingRegistrar() {
        return (typeMappingRegistrar != null)
                ? typeMappingRegistrar
                : (typeMappingRegistrar = createTypeMappingRegistrar());
    }

    protected FieldTypeMappingRegistrar createTypeMappingRegistrar() {
        return new DefaultFieldTypeMapper();
    }

    protected <T> void registerConverter(Class<T> valueType, FieldTypeMappingRegistrar.TypeConverter<T> converter) {
        getTypeMappingRegistrar().registerConverter(valueType, converter);
    }
}
