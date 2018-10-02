package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.interfaces.*;
import com.slimgears.slimrepo.core.internal.query.*;

import java.util.Collection;

/**
 * Created by Denis on 01-May-15.
 */
public class AutoSessionEntityServiceProvider<TKey, TEntity>
        extends AbstractSessionEntityServiceProvider<TKey, TEntity>
        implements QueryProvider<TKey, TEntity> {
    private final OrmServiceProvider ormServiceProvider;
    private final RepositoryModel repositoryModel;
    private final EntityType<TKey, TEntity> entityType;

    public AutoSessionEntityServiceProvider(OrmServiceProvider ormServiceProvider, RepositoryModel repositoryModel, EntityType<TKey, TEntity> entityType) {
        super(entityType);
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
        public T execute() throws Exception {
            SessionServiceProvider sessionServiceProvider = ormServiceProvider.createSessionServiceProvider(repositoryModel);
            try {
                return execute(sessionServiceProvider);
            } finally {
                sessionServiceProvider.close();
            }
        }

        protected T execute(SessionServiceProvider sessionServiceProvider) throws Exception {
            QueryProvider<TKey, TEntity> queryProvider = sessionServiceProvider.getEntityServiceProvider(entityType).getQueryProvider();
            return delegator.prepare(queryProvider).execute();
        }
    }

    class AutoIteratorPreparedQuery<T> extends AutoPreparedQuery<CloseableIterator<T>> {
        AutoIteratorPreparedQuery(QueryDelegator<CloseableIterator<T>> delegator) {
            super(delegator);
        }

        @Override
        public CloseableIterator<T> execute() throws Exception {
            SessionServiceProvider sessionServiceProvider = ormServiceProvider.createSessionServiceProvider(repositoryModel);
            return CloseableIterators.addCloseable(execute(sessionServiceProvider), sessionServiceProvider);
        }
    }

    private <T> PreparedQuery<T> delegate(QueryDelegator<T> delegator) {
        return new AutoPreparedQuery<>(delegator);
    }

    private <T> PreparedQuery<CloseableIterator<T>> delegateIterator(QueryDelegator<CloseableIterator<T>> delegator) {
        return new AutoIteratorPreparedQuery<>(delegator);
    }

    @Override
    public QueryProvider<TKey, TEntity> getQueryProvider() {
        return this;
    }

    @Override
    public PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> prepareSelect(final SelectQueryParams<TKey, TEntity> query) {
        return delegateIterator(new QueryDelegator<CloseableIterator<FieldValueLookup<TEntity>>>() {
            @Override
            PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> prepare(QueryProvider<TKey, TEntity> queryProvider) {
                return queryProvider.prepareSelect(query);
            }
        });
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
    public PreparedQuery<CloseableIterator<TKey>> prepareInsert(final Collection<TEntity> entities) {
        return delegateIterator(new QueryDelegator<CloseableIterator<TKey>>() {
            @Override
            PreparedQuery<CloseableIterator<TKey>> prepare(QueryProvider<TKey, TEntity> queryProvider) {
                return queryProvider.prepareInsert(entities);
            }
        });
    }
}
