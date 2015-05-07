// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import java.io.IOException;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public interface RepositoryCreator {
    void createRepository(RepositoryModel model) throws IOException;
    void upgradeRepository(RepositoryModel newModel);
}
