// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.internal.converters.TypeMappers;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingInstaller;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;

/**
 * Created by Denis on 24-Apr-15
 *
 */
public abstract class AbstractOrmServiceProvider implements OrmServiceProvider {
    private FieldTypeMappingRegistrar typeMappingRegistrar;

    @Override
    public FieldTypeMapper getFieldTypeMapper() {
        return getFieldTypeMapperRegistrar();
    }

    @Override
    public final FieldTypeMappingRegistrar getFieldTypeMapperRegistrar() {
        if (typeMappingRegistrar != null) return typeMappingRegistrar;
        typeMappingRegistrar = createTypeMappingRegistrar();
        onMapFieldTypes(typeMappingRegistrar);
        return typeMappingRegistrar;
    }

    protected FieldTypeMappingRegistrar createTypeMappingRegistrar() {
        return new com.slimgears.slimrepo.core.internal.converters.DefaultFieldTypeMapper();
    }

    protected void onMapFieldTypes(FieldTypeMappingRegistrar registrar) {
        TypeMappers.install(registrar);
    }

    protected void installTypeMappings(FieldTypeMappingRegistrar registrar, FieldTypeMappingInstaller... installers) {
        for (FieldTypeMappingInstaller installer : installers) {
            installer.install(registrar);
        }
    }
}
