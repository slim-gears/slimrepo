package com.slimgears.slimrepo.core.internal.sql.interfaces;

import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;

/**
 * Created by Denis on 19-May-15.
 */
public interface SqlSchemeProvider {
    SqlDatabaseScheme getDatabaseScheme();
    SqlDatabaseScheme getModelScheme(RepositoryModel model);
}
