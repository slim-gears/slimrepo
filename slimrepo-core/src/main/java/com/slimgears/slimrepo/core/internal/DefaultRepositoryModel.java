// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;

/**
 * Created by Denis on 15-Apr-15
 *
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
