package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.query.DeleteQueryParams;
import com.slimgears.slimrepo.core.internal.query.PreparedQuery;
import com.slimgears.slimrepo.core.internal.query.QueryProvider;
import com.slimgears.slimrepo.core.internal.query.SelectQueryParams;
import com.slimgears.slimrepo.core.internal.query.UpdateQueryParams;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Denis on 01-May-15.
 */
public class AutoSessionEntityServiceProvider<TKey, TEntity extends Entity<TKey>>
        extends AbstractSessionEntityServiceProvider<TKey, TEntity>
        implements QueryProvider<TKey, TEntity> {
    private final OrmServiceProvider ormServiceProvider;
    private final RepositoryModel repositoryModel;
    private final EntityType<TKey, TEntity> entityType;

    public AutoSessionEntityServiceProvider(OrmServiceProvider ormServiceProvider, RepositoryModel repositoryModel, EntityType<TKey, TEntity> entityType) {
        this.ormServiceProvider = ormServiceProvider;
        this.repositoryModel = repositoryModel;
        this.entityType = entityType;
    }

    abstract class QueryDelegator<T> {
        abstract PreparedQuery<T> prepare(QueryProvider<TKey, TEntity> queryProvider);
    }

    class AutoPreparedQuery<T> implements PreparedQuery<T> {
        private final QueryDelegator<T> delegator;

        AutoPreparedQuery(QueryDelegator<T> delegator) {
            this.delegator = delegator;
        }

        @Override
        public T execute() throws IOException {
            SessionServiceProvider sessionServiceProvider = ormServiceProvider.createSessionServiceProvider(repositoryModel);
            try {
                QueryProvider<TKey, TEntity> queryProvider = sessionServiceProvider.getEntityServiceProvider(entityType).getQueryProvider();
                return delegator.prepare(queryProvider).execute();
            } finally {
                sessionServiceProvider.close();
            }
        }
    }

    private <T> PreparedQuery<T> delegate(QueryDelegator<T> delegator) {
        return new AutoPreparedQuery<>(delegator);
    }

    @Override
    public QueryProvider<TKey, TEntity> getQueryProvider() {
        return this;
    }

    @Override
    public PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> prepareSelect(final SelectQueryParams<TKey, TEntity> query) {
        return new PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>>() {
            @Override
            public CloseableIterator<FieldValueLookup<TEntity>> execute() throws IOException {
                SessionServiceProvider sessionServiceProvider = ormServiceProvider.createSessionServiceProvider(repositoryModel);
                QueryProvider<TKey, TEntity> queryProvider = sessionServiceProvider.getEntityServiceProvider(entityType).getQueryProvider();
                return queryProvider.prepareSelect(query).execute();
            }
        };
    }

    @Override
    public PreparedQuery<Long> prepareCount(final SelectQueryParams<TKey, TEntity> query) {
        return delegate(new QueryDelegator<Long>() {
            @Override
            PreparedQuery<Long> prepare(QueryProvider<TKey, TEntity> queryProvider) {
                return queryProvider.prepareCount(query);
            }
        });
    }

    @Override
    public PreparedQuery<Void> prepareUpdate(final UpdateQueryParams<TKey, TEntity> query) {
        return delegate(new QueryDelegator<Void>() {
            @Override
            PreparedQuery<Void> prepare(QueryProvider<TKey, TEntity> queryProvider) {
                return queryProvider.prepareUpdate(query);
            }
        });
    }

    @Override
    public PreparedQuery<Void> prepareDelete(final DeleteQueryParams<TKey, TEntity> query) {
        return delegate(new QueryDelegator<Void>() {
            @Override
            PreparedQuery<Void> prepare(QueryProvider<TKey, TEntity> queryProvider) {
                return queryProvider.prepareDelete(query);
            }
        });
    }

    @Override
    public PreparedQuery<Void> prepareInsert(final Collection<TEntity> entities) {
        return delegate(new QueryDelegator<Void>() {
            @Override
            PreparedQuery<Void> prepare(QueryProvider<TKey, TEntity> queryProvider) {
                return queryProvider.prepareInsert(entities);
            }
        });
    }
}
