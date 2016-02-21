package com.slimgears.slimrepo.core.internal;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;
import com.slimgears.slimrepo.core.utilities.Maps;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Denis on 03-May-15.
 *
 */
public abstract class AbstractEntitySet<TKey, TEntity> implements EntitySet<TEntity> {
    protected final EntityType<TKey, TEntity> entityType;

    protected AbstractEntitySet(EntityType<TKey, TEntity> entityType) {
        this.entityType = entityType;
    }

    @Override
    public final long countAll() throws IOException {
        return query().prepare().count();
    }

    @Override
    public final long countAllWhere(Condition<TEntity> condition) throws IOException {
        return query().where(condition).prepare().count();
    }

    @Override
    public final <T> TEntity findFirst(ValueField<TEntity, T> field, T value) throws IOException {
        return query().where(field.eq(value)).prepare().firstOrDefault();
    }

    @Override
    public final TEntity findFirstWhere(Condition<TEntity> predicate) throws IOException {
        return query().where(predicate).prepare().firstOrDefault();
    }

    @Override
    public final <T> TEntity[] findAllBy(ValueField<TEntity, T> field, T value) throws IOException {
        return query().where(field.eq(value)).prepare().toArray();
    }

    @Override
    public final TEntity[] findAllWhere(Condition<TEntity> predicate) throws IOException {
        return query().where(predicate).prepare().toArray();
    }

    @Override
    public final TEntity[] toArray() throws IOException {
        return query().prepare().toArray();
    }

    @Override
    public final List<TEntity> toList() throws IOException {
        return query().prepare().toList();
    }

    @Override
    public final <T> Map<T, TEntity> toMap(Field<TEntity, T> keyField) throws IOException {
        return query().prepare().toMap(keyField);
    }

    @Override
    public final <K, V> Map<K, V> toMap(Field<TEntity, K> keyField, Field<TEntity, V> valueField) throws IOException {
        return query().selectToMap(keyField, valueField);
    }

    @Override
    public final <T> Collection<T> map(Transformer<TEntity, T> mapper) throws IOException {
        return Stream.of(toList()).map(mapper::transform).collect(Collectors.toList());
    }

    @Override
    @SafeVarargs
    public final TEntity[] add(TEntity... entities) throws IOException {
        addAll(Arrays.asList(entities));
        return entities;
    }

    @Override
    public final TEntity add(TEntity entity) throws IOException {
        addAll(Collections.singletonList(entity));
        return entity;
    }

    @Override
    public void remove(TEntity entity) throws IOException {
        removeAll(Collections.singletonList(entity));
    }

    @Override
    public void mergeAll(Iterable<TEntity> entities) throws IOException {
        ValueField<TEntity, TKey> keyField = entityType.getKeyField();
        Map<TKey, TEntity> index = Maps.uniqueIndex(entities, entityType::getKey);
        Map<TKey, TEntity> existingEntities = query()
                .where(keyField.in(index.keySet()))
                .prepare()
                .toMap(keyField);

        Set<TKey> existingKeys = existingEntities.keySet();
        //noinspection StaticPseudoFunctionalStyleMethod
        Collection<TEntity> newEntities = Stream
                .of(entities)
                .filter(e -> !existingKeys.contains(keyField.getValue(e)))
                .collect(Collectors.toList());

        addAll(newEntities);
        Stream
                .of(existingEntities.values())
                .forEach(entity -> entityType.copy(index.get(keyField.getValue(entity)), entity));
    }
}
