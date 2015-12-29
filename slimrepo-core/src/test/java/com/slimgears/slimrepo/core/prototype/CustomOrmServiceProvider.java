package com.slimgears.slimrepo.core.prototype;

import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSchemeProvider;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSchemeProvider;
import com.slimgears.slimrepo.core.internal.sql.sqlite.AbstractSqliteOrmServiceProvider;

import org.mockito.Answers;

import static org.mockito.Mockito.mock;

/**
 * Created by Denis on 08-May-15.
 */
public class CustomOrmServiceProvider extends AbstractSqliteOrmServiceProvider {

    public CustomOrmServiceProvider(String customStringParameter, int customIntParameter) {
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

        @Override
        protected SqlSchemeProvider createSchemeProvider() {
            return mock(AbstractSqlSchemeProvider.class, Answers.CALLS_REAL_METHODS);
        }
    }

    @Override
    public SessionServiceProvider createSessionServiceProvider(RepositoryModel model) {
        return new CustomSessionServiceProvider();
    }
}
