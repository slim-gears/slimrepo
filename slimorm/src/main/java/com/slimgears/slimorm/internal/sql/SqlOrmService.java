// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.interfaces.RepositorySession;
import com.slimgears.slimorm.internal.OrmService;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
public interface SqlOrmService extends OrmService, SqlCommandExecutorFactory {
    SqlStatementBuilder getStatementBuilder();
}
