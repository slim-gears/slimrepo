// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.conditions.BinaryCondition;
import com.slimgears.slimrepo.core.interfaces.conditions.CollectionCondition;
import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.conditions.Conditions;
import com.slimgears.slimrepo.core.interfaces.conditions.TernaryCondition;
import com.slimgears.slimrepo.core.interfaces.conditions.UnaryCondition;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.BlobField;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.NumericField;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;

import java.util.Collection;
import java.util.Date;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public class Fields {

    static class AbstractField<TEntity extends Entity<?>, T>
            implements
                Field<TEntity, T>,
                Field.MetaInfo<TEntity, T>,
            EntityType.Bindable<TEntity> {
        private EntityType<?, TEntity> entityType;
        private final String name;
        private final Class<T> type;
        private final boolean nullable;

        AbstractField(String name, Class<T> type, boolean nullable) {
            this.name = name;
            this.type = type;
            this.nullable = nullable;
        }

        @Override
        public EntityType<?, TEntity> getEntityType() {
            return this.entityType;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Class<T> getValueType() {
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

        @Override
        public void bind(EntityType<?, TEntity> entityType) {
            this.entityType = entityType;
        }
    }

    static class AbstractValueField<TEntity extends Entity<?>, T> extends AbstractField<TEntity, T> implements ValueField<TEntity, T> {
        AbstractValueField(String name, Class<T> type, boolean nullable) {
            super(name, type, nullable);
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

    static class NumericFieldImplementation<TEntity extends Entity<?>, T> extends AbstractValueField<TEntity, T> implements NumericField<TEntity, T> {
        NumericFieldImplementation(String name, Class<T> type, boolean nullable) {
            super(name, type, nullable);
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

    static class StringFieldImplementation<TEntity extends Entity<?>> extends AbstractValueField<TEntity, String> implements StringField<TEntity> {
        StringFieldImplementation(String name, boolean nullable) {
            super(name, String.class, nullable);
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

    static class BlobFieldImplementation<TEntity extends Entity<?>, T> extends AbstractField<TEntity, T> implements BlobField<TEntity, T> {
        BlobFieldImplementation(String name, Class<T> type, boolean nullable) {
            super(name, type, nullable);
        }
    }

    static class RelatedFieldImplementation<TEntity extends Entity<?>, TRelatedEntity extends Entity<?>>
            extends AbstractField<TEntity, TRelatedEntity>
            implements RelationalField<TEntity, TRelatedEntity>, RelationalField.MetaInfo<TEntity, TRelatedEntity> {
        private final EntityType<?, TRelatedEntity> relatedEntityType;

        RelatedFieldImplementation(String name, EntityType<?, TRelatedEntity> relatedEntityType, boolean nullable) {
            super(name, relatedEntityType.getEntityClass(), nullable);
            this.relatedEntityType = relatedEntityType;
        }

        @Override
        public RelationalField.MetaInfo<TEntity, TRelatedEntity> metaInfo() {
            return this;
        }

        @Override
        public Condition<TEntity> is(Condition<TRelatedEntity> condition) {
            return null;
        }

        @Override
        public EntityType<?, TRelatedEntity> relatedEntityType() {
            return relatedEntityType;
        }
    }

    public static <TEntity extends Entity<?>, T> NumericField<TEntity, T> numberField(String name, Class<T> fieldType, boolean nullable) {
        return new NumericFieldImplementation<>(name, fieldType, nullable);
    }

    public static <TEntity extends Entity<?>> StringField<TEntity> stringField(String name, boolean nullable) {
        return new StringFieldImplementation<>(name, nullable);
    }

    public static <TEntity extends Entity<?>> NumericField<TEntity, Date> dateField(String name, boolean nullable) {
        return numberField(name, Date.class, nullable);
    }

    public static <TEntity extends Entity<?>, T> BlobField<TEntity, T> blobField(String name, Class<T> fieldType, boolean nullable) {
        return new BlobFieldImplementation<>(name, fieldType, nullable);
    }

    public static <TEntity extends Entity<?>, TRelatedEntity extends Entity<?>> RelationalField<TEntity, TRelatedEntity> relationalField(String name, EntityType<?, TRelatedEntity> relatedEntityType, boolean nullable) {
        return new RelatedFieldImplementation<>(name, relatedEntityType, nullable);
    }
}
