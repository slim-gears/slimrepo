// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

/**
 * Created by Denis on 15-Apr-15
 *
 */
public interface RepositoryCreator {
    void createRepository(RepositoryModel model) throws Exception;
    void upgradeRepository(RepositoryModel newModel) throws Exception;
    void upgradeOrCreate(RepositoryModel model) throws Exception;
}
