package com.slimgears.slimrepo.core.prototype;

import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.sqlite.AbstractSqliteOrmServiceProvider;

import static org.mockito.Mockito.mock;

/**
 * Created by Denis on 08-May-15.
 */
public class CustomOrmServiceProvider extends AbstractSqliteOrmServiceProvider {
    private final String customStringParameter;
    private final int customIntParameter;

    public CustomOrmServiceProvider(String customStringParameter, int customIntParameter) {
        this.customStringParameter = customStringParameter;
        this.customIntParameter = customIntParameter;
    }

    class CustomSessionServiceProvider extends AbstractSqlSessionServiceProvider {
        public CustomSessionServiceProvider() {
            super(CustomOrmServiceProvider.this);
        }

        @Override
        protected SqlCommandExecutor createCommandExecutor() {
            return mock(SqlCommandExecutor.class);
        }

        @Override
        protected TransactionProvider createTransactionProvider() {
            return mock(TransactionProvider.class);
        }
    }

    @Override
    public SessionServiceProvider createSessionServiceProvider(RepositoryModel model) {
        return new CustomSessionServiceProvider();
    }
}
