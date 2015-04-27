// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.fields;

import com.slimgears.slimrepo.core.interfaces.conditions.BinaryCondition;
import com.slimgears.slimrepo.core.interfaces.conditions.CollectionCondition;
import com.slimgears.slimrepo.core.interfaces.conditions.Conditions;
import com.slimgears.slimrepo.core.interfaces.conditions.TernaryCondition;
import com.slimgears.slimrepo.core.interfaces.conditions.UnaryCondition;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

import java.util.Collection;
import java.util.Date;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public class Fields {

    static class AbstractField<TEntity, T> implements Field<TEntity, T>, Field.MetaInfo<TEntity, T> {
        private final Class<TEntity> entityClass;
        private final String name;
        private final Class<T> type;
        private final boolean nullable;

        AbstractField(Class<TEntity> entityClass, String name, Class<T> type, boolean nullable) {
            this.entityClass = entityClass;
            this.name = name;
            this.type = type;
            this.nullable = nullable;
        }

        @Override
        public Class<TEntity> getEntityClass() {
            return this.entityClass;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Class<T> getType() {
            return this.type;
        }

        @Override
        public boolean isNullable() {
            return nullable;
        }

        @Override
        public MetaInfo<TEntity, T> metaInfo() {
            return this;
        }

        @Override
        public UnaryCondition<TEntity, T> isNull() {
            return Conditions.isNull(this);
        }

        @Override
        public UnaryCondition<TEntity, T> isNotNull() {
            return Conditions.isNotNull(this);
        }
    }

    static class AbstractValueField<TEntity, T> extends AbstractField<TEntity, T> implements ValueField<TEntity, T> {
        AbstractValueField(Class<TEntity> entityClass, String name, Class<T> type, boolean nullable) {
            super(entityClass, name, type, nullable);
        }

        @Override
        public BinaryCondition<TEntity, T> equal(T value) {
            return Conditions.equals(this, value);
        }

        @Override
        public BinaryCondition<TEntity, T> notEqual(T value) {
            return Conditions.notEquals(this, value);
        }

        @SafeVarargs
        @Override
        public final CollectionCondition<TEntity, T> in(T... values) {
            return Conditions.in(this, values);
        }

        @Override
        public CollectionCondition<TEntity, T> in(Collection<T> values) {
            //noinspection unchecked
            return Conditions.in(this, (T[]) values.toArray());
        }

        @SafeVarargs
        @Override
        public final CollectionCondition<TEntity, T> notIn(T... values) {
            return Conditions.notIn(this, values);
        }

        @Override
        public CollectionCondition<TEntity, T> notIn(Collection<T> values) {
            //noinspection unchecked
            return Conditions.notIn(this, (T[]) values.toArray());
        }
    }

    static class NumericFieldImplementation<TEntity, T> extends AbstractValueField<TEntity, T> implements NumericField<TEntity, T> {
        NumericFieldImplementation(Class<TEntity> entityClass, String name, Class<T> type, boolean nullable) {
            super(entityClass, name, type, nullable);
        }

        @Override
        public BinaryCondition<TEntity, T> greaterThan(T value) {
            return Conditions.greaterThan(this, value);
        }

        @Override
        public BinaryCondition<TEntity, T> lessThan(T value) {
            return Conditions.lessThan(this, value);
        }

        @Override
        public BinaryCondition<TEntity, T> greaterOrEqual(T value) {
            return Conditions.greaterOrEqual(this, value);
        }

        @Override
        public BinaryCondition<TEntity, T> lessOrEqual(T value) {
            return Conditions.lessOrEqual(this, value);
        }

        @Override
        public TernaryCondition<TEntity, T> between(T min, T max) {
            return Conditions.between(this, min, max);
        }
    }

    static class StringFieldImplementation<TEntity> extends AbstractValueField<TEntity, String> implements StringField<TEntity> {
        StringFieldImplementation(Class<TEntity> entityClass, String name, boolean nullable) {
            super(entityClass, name, String.class, nullable);
        }

        @Override
        public BinaryCondition<TEntity, String> contains(String substr) {
            return Conditions.contains(this, substr);
        }

        @Override
        public BinaryCondition<TEntity, String> notContains(String substr) {
            return Conditions.notContains(this, substr);
        }

        @Override
        public BinaryCondition<TEntity, String> startsWith(String substr) {
            return Conditions.startsWith(this, substr);
        }

        @Override
        public BinaryCondition<TEntity, String> endsWith(String substr) {
            return Conditions.endsWith(this, substr);
        }

        @Override
        public BinaryCondition<TEntity, String> notStartsWith(String substr) {
            return Conditions.notStartsWith(this, substr);
        }

        @Override
        public BinaryCondition<TEntity, String> notEndsWith(String substr) {
            return Conditions.notEndsWith(this, substr);
        }
    }

    static class BlobFieldImplementation<TEntity, T> extends AbstractField<TEntity, T> implements BlobField<TEntity, T> {
        BlobFieldImplementation(Class<TEntity> entityClass, String name, Class<T> type, boolean nullable) {
            super(entityClass, name, type, nullable);
        }
    }

    static class RelatedFieldImplementation<TEntity, TRelatedEntity extends Entity<?>>
            extends AbstractField<TEntity, TRelatedEntity>
            implements RelationalField<TEntity, TRelatedEntity>, RelationalField.MetaInfo<TEntity, TRelatedEntity> {
        private final EntityType<?, TRelatedEntity> relatedEntityType;

        RelatedFieldImplementation(Class<TEntity> entityClass, String name, EntityType<?, TRelatedEntity> relatedEntityType, boolean nullable) {
            super(entityClass, name, relatedEntityType.getEntityClass(), nullable);
            this.relatedEntityType = relatedEntityType;
        }

        @Override
        public RelationalField.MetaInfo<TEntity, TRelatedEntity> metaInfo() {
            return this;
        }

        @Override
        public EntityType<?, TRelatedEntity> relatedEntityType() {
            return relatedEntityType;
        }
    }

    public static <TEntity, T> NumericField<TEntity, T> numberField(String name, Class<TEntity> entityClass, Class<T> fieldType, boolean nullable) {
        return new NumericFieldImplementation<>(entityClass, name, fieldType, nullable);
    }

    public static <TEntity> StringField<TEntity> stringField(String name, Class<TEntity> entityClass, boolean nullable) {
        return new StringFieldImplementation<>(entityClass, name, nullable);
    }

    public static <TEntity> NumericField<TEntity, Date> dateField(String name, Class<TEntity> entityClass, boolean nullable) {
        return numberField(name, entityClass, Date.class, nullable);
    }

    public static <TEntity, T> BlobField<TEntity, T> blobField(String name, Class<TEntity> entityClass, Class<T> fieldType, boolean nullable) {
        return new BlobFieldImplementation<>(entityClass, name, fieldType, nullable);
    }

    public static <TEntity, TRelatedEntity extends Entity<?>> RelationalField<TEntity, TRelatedEntity> relationalField(String name, Class<TEntity> entityClass, EntityType<?, TRelatedEntity> relatedEntityType, boolean nullable) {
        return new RelatedFieldImplementation<>(entityClass, name, relatedEntityType, nullable);
    }
}
