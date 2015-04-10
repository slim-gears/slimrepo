// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.apt.prototype.slimsql;

import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.interfaces.RepositorySession;
import com.slimgears.slimorm.internal.sql.AbstractSqlOrmService;
import com.slimgears.slimorm.internal.sql.SqlCommandExecutor;
import com.slimgears.slimorm.internal.sql.SqlCommandExecutorFactory;
import com.slimgears.slimorm.internal.sql.SqlOrmService;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public class SlimSqlOrm extends AbstractSqlOrmService {
    public static final SqlOrmService INSTANCE = new SlimSqlOrm();

    private static SqlCommandExecutorFactory commandExecutorFactory = null;

    public static void setCommandExecutorFactory(SqlCommandExecutorFactory commandExecutorFactory) {
        SlimSqlOrm.commandExecutorFactory = commandExecutorFactory;
    }

    @Override
    public <TSession extends RepositorySession> SqlCommandExecutor createCommandExecutor(Repository<TSession> repository, TSession session) {
        return commandExecutorFactory != null
                ? commandExecutorFactory.createCommandExecutor(repository, session)
                : null;
    }
}
