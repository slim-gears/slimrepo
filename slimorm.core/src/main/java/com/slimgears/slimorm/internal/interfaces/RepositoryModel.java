// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.interfaces;

import com.slimgears.slimorm.interfaces.entities.EntityType;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public interface RepositoryModel {
    String getName();
    int getVersion();
    EntityType<?, ?>[] getEntityTypes();
}
