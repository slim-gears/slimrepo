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

    final protected FieldTypeMappingRegistrar getTypeMappingRegistrar() {
        if (typeMappingRegistrar != null) return typeMappingRegistrar;
        typeMappingRegistrar = createTypeMappingRegistrar();
        onMapFieldTypes(typeMappingRegistrar);
        return typeMappingRegistrar;
    }

    protected FieldTypeMappingRegistrar createTypeMappingRegistrar() {
        return new DefaultFieldTypeMapper();
    }

    protected void onMapFieldTypes(FieldTypeMappingRegistrar registrar) {
        TypeMappers.install(registrar);
    }
}
