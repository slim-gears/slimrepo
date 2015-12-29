// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.query;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.queries.EntitySelectQuery;
import com.slimgears.slimrepo.core.interfaces.queries.SelectQuery;
import com.slimgears.slimrepo.core.internal.AbstractRowIterator;
import com.slimgears.slimrepo.core.internal.OrderFieldInfo;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.interfaces.EntityCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public class DefaultEntitySelectQuery<TKey, TEntity>
        extends AbstractEntityQuery<TKey, TEntity, EntitySelectQuery<TEntity>, EntitySelectQuery.Builder<TEntity>, SelectQueryParams<TKey, TEntity>>
        implements EntitySelectQuery<TEntity>, EntitySelectQuery.Builder<TEntity> {

    class EntityIterator extends AbstractRowIterator<TEntity, TKey, TEntity> {
        public EntityIterator(CloseableIterator<FieldValueLookup<TEntity>> rowIterator) {
            super(rowIterator);
        }

        @Override
        protected TEntity toElement(final FieldValueLookup<TEntity> row) {
            return toEntity(row);
        }
    }

    class EntityFieldIterator<S> extends AbstractRowIterator<S, TKey, TEntity> {
        private final Field<TEntity, S> field;

        public EntityFieldIterator(Field<TEntity, S> field, CloseableIterator<FieldValueLookup<TEntity>> rowIterator) {
            super(rowIterator);
            this.field = field;
        }

        @Override
        protected S toElement(FieldValueLookup<TEntity> lookup) {
            return lookup.getValue(field);
        }
    }

    class SelectFieldQuery<S> implements SelectQuery<S> {
        private final SelectQueryParams<TKey, TEntity> queryParams;
        private PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> preparedQuery;
        private final Field<TEntity, S> field;

        SelectFieldQuery(Field<TEntity, S> field, SelectQueryParams<TKey, TEntity> queryParams) {
            this.queryParams = queryParams.fork();
            this.queryParams.fields = new ArrayList<>();
            this.queryParams.fields.add(field);
            this.field = field;
        }

        @Override
        public S firstOrDefault() throws IOException {
            SelectQueryParams<TKey, TEntity> queryParams = DefaultEntitySelectQuery.this.queryParams.fork();
            queryParams.pagination.limit = 1;
            try (CloseableIterator<FieldValueLookup<TEntity>> iterator = queryProvider.prepareSelect(queryParams).execute()) {
                return (iterator.hasNext()) ? iterator.next().getValue(field) : null;
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) throw (IOException)e.getCause();
                throw e;
            }
        }

        @Override
        public List<S> toList() throws IOException {
            return Arrays.asList(toArray());
        }

        @Override
        public S[] toArray() throws IOException {
            try (CloseableIterator<S> values = iterator()) {
                return Iterators.toArray(values, field.metaInfo().getValueType());
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) throw (IOException)e.getCause();
                throw e;
            }
        }

        @Override
        public long count() throws IOException {
            return DefaultEntitySelectQuery.this.count();
        }

        @Override
        public CloseableIterator<S> iterator() {
            try {
                return new EntityFieldIterator<>(field, getPreparedQuery().execute());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> getPreparedQuery() {
            if (preparedQuery != null) return preparedQuery;
            return preparedQuery = queryProvider.prepareSelect(queryParams);
        }
    }

    private PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> preparedSelectQuery;
    private PreparedQuery<Long> preparedCountQuery;
    private final EntityCache<TKey, TEntity> entityCache;
    private final EntityType<TKey, TEntity> entityType;
    private final Field<TEntity, TKey> keyField;

    public DefaultEntitySelectQuery(
            EntityType<TKey, TEntity> entityType,
            QueryProvider<TKey, TEntity> queryProvider,
            EntityCache<TKey, TEntity> entityCache) {
        this(new SelectQueryParams<>(entityType, null, null, null, null), queryProvider, entityCache, entityType);
    }


    @Override
    protected Builder<TEntity> fork(SelectQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
        return new DefaultEntitySelectQuery<>(queryParams, queryProvider, entityCache, entityType);
    }

    @Override
    protected Builder<TEntity> builder() {
        return this;
    }

    private DefaultEntitySelectQuery(
            SelectQueryParams<TKey, TEntity> queryParams,
            QueryProvider<TKey, TEntity> queryProvider,
            EntityCache<TKey, TEntity> entityCache,
            EntityType<TKey, TEntity> entityType) {
        super(queryParams, queryProvider);
        this.entityCache = entityCache;
        this.entityType = entityType;
        this.keyField = entityType.getKeyField();
    }

    @Override
    public CloseableIterator<TEntity> iterator() {
        try {
            return toEntityIterator(getPreparedSelectQuery().execute());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Builder<TEntity> orderAsc(Field<TEntity, ?>... fields) {
        addOrderFields(true, fields);
        return builder();
    }

    @Override
    public Builder<TEntity> orderDesc(Field<TEntity, ?>... fields) {
        addOrderFields(false, fields);
        return builder();
    }

    @Override
    public <S> SelectQuery<S> select(Field<TEntity, S> field) {
        return new SelectFieldQuery<>(field, queryParams);
    }

    @Override
    public <K, V> Map<K, V> selectToMap(final Field<TEntity, K> keyField, final Field<TEntity, V> valueField) throws IOException {
        final Map<K, V> map = new HashMap<>();
        SelectQueryParams<TKey, TEntity> queryParams = this.queryParams.fork();
        queryParams.fields = Arrays.asList(keyField, valueField);

        iterateRows(queryProvider.prepareSelect(queryParams), new Function<FieldValueLookup<TEntity>, Void>() {
            @Override
            public Void apply(FieldValueLookup<TEntity> input) {
                map.put(input.getValue(keyField), input.getValue(valueField));
                return null;
            }
        });
        return map;
    }

    @Override
    public TEntity firstOrDefault() throws IOException {
        SelectQueryParams<TKey, TEntity> queryParams = this.queryParams.fork();
        if (queryParams.pagination == null) queryParams.pagination = new QueryPagination();
        queryParams.pagination.limit = 1;

        try (CloseableIterator<TEntity> iterator = toEntityIterator(queryProvider.prepareSelect(queryParams).execute())) {
            return (iterator.hasNext()) ? iterator.next() : null;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) throw (IOException)e.getCause();
            throw e;
        }
    }

    @Override
    public List<TEntity> toList() throws IOException {
        return Arrays.asList(toArray());
    }

    @Override
    public <K> Map<K, TEntity> toMap(final Field<TEntity, K> keyField) throws IOException {
        final Map<K, TEntity> map = new HashMap<>();
        iterateRows(getPreparedSelectQuery(), new Function<FieldValueLookup<TEntity>, Void>() {
            @Override
            public Void apply(FieldValueLookup<TEntity> row) {
                map.put(row.getValue(keyField), toEntity(row));
                return null;
            }
        });
        return map;
    }

    @Override
    public TEntity[] toArray() throws IOException {
        try (CloseableIterator<TEntity> entities = iterator()) {
            return Iterators.toArray(entities, queryParams.entityType.getEntityClass());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) throw (IOException)e.getCause();
            throw e;
        }
    }

    @Override
    public long count() throws IOException {
        return getPreparedCountQuery().execute();
    }

    private void addOrderFields(boolean ascending, Field... fields) {
        if (queryParams.order == null) queryParams.order = new ArrayList<>();

        for (Field field : fields) {
            queryParams.order.add(new OrderFieldInfo(field, ascending));
        }
    }

    @Override
    public EntitySelectQuery<TEntity> prepare() {
        return this;
    }

    private PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> getPreparedSelectQuery() throws IOException {
        if (preparedSelectQuery != null) return preparedSelectQuery;
        return preparedSelectQuery = queryProvider.prepareSelect(queryParams);
    }

    private PreparedQuery<Long> getPreparedCountQuery() throws IOException {
        if (preparedCountQuery != null) return preparedCountQuery;
        return preparedCountQuery = queryProvider.prepareCount(queryParams);
    }

    private CloseableIterator<TEntity> toEntityIterator(CloseableIterator<FieldValueLookup<TEntity>> rowIterator) {
        return new EntityIterator(rowIterator);
    }

    private void iterateRows(PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> query, Function<FieldValueLookup<TEntity>, Void> rowCallback) throws IOException {
        try (CloseableIterator<FieldValueLookup<TEntity>> iterator = query.execute()) {
            while (iterator.hasNext()) {
                rowCallback.apply(iterator.next());
            }
        }
    }

    private TEntity toEntity(final FieldValueLookup<TEntity> lookup) {
        TKey id = lookup.getValue(keyField);
        return entityCache.get(id, new Callable<TEntity>() {
            @Override
            public TEntity call() throws Exception {
                return entityType.newInstance(lookup);
            }
        });
    }
}
