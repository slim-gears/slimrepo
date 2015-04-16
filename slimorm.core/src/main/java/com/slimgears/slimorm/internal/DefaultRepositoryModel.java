// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.entities.EntityType;
import com.slimgears.slimorm.internal.interfaces.RepositoryModel;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public class DefaultRepositoryModel implements RepositoryModel {
    private final String name;
    private final int version;
    private final EntityType<?, ?>[] entityTypes;

    public DefaultRepositoryModel(String name, int version, EntityType<?, ?>... entityTypes) {
        this.name = name;
        this.version = version;
        this.entityTypes = entityTypes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public EntityType<?, ?>[] getEntityTypes() {
        return entityTypes;
    }
}
