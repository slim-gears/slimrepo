// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.fields;

import com.slimgears.slimorm.interfaces.predicates.BinaryPredicate;
import com.slimgears.slimorm.interfaces.predicates.CollectionPredicate;
import com.slimgears.slimorm.interfaces.predicates.Predicates;
import com.slimgears.slimorm.interfaces.predicates.TernaryPredicate;
import com.slimgears.slimorm.interfaces.predicates.UnaryPredicate;

import java.util.Collection;
import java.util.Date;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public class Fields {
    static class AbstractField<TEntity, T> implements Field<TEntity, T> {
        private final Class<TEntity> entityClass;
        private final String name;
        private final Class<T> type;

        AbstractField(Class<TEntity> entityClass, String name, Class<T> type) {
            this.entityClass = entityClass;
            this.name = name;
            this.type = type;
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
        public UnaryPredicate<TEntity, T> isNull() {
            return Predicates.isNull(this);
        }

        @Override
        public UnaryPredicate<TEntity, T> isNotNull() {
            return Predicates.isNotNull(this);
        }
    }

    static class AbstractValueField<TEntity, T> extends AbstractField<TEntity, T> implements ValueField<TEntity, T> {
        AbstractValueField(Class<TEntity> entityClass, String name, Class<T> type) {
            super(entityClass, name, type);
        }

        @Override
        public BinaryPredicate<TEntity, T> equal(T value) {
            return Predicates.equals(this, value);
        }

        @Override
        public BinaryPredicate<TEntity, T> notEqual(T value) {
            return Predicates.notEquals(this, value);
        }

        @SafeVarargs
        @Override
        public final CollectionPredicate<TEntity, T> in(T... values) {
            return Predicates.in(this, values);
        }

        @Override
        public CollectionPredicate<TEntity, T> in(Collection<T> values) {
            //noinspection unchecked
            return Predicates.in(this, (T[]) values.toArray());
        }

        @SafeVarargs
        @Override
        public final CollectionPredicate<TEntity, T> notIn(T... values) {
            return Predicates.notIn(this, values);
        }

        @Override
        public CollectionPredicate<TEntity, T> notIn(Collection<T> values) {
            //noinspection unchecked
            return Predicates.notIn(this, (T[])values.toArray());
        }
    }

    static class NumberFieldImplementation<TEntity, T> extends AbstractValueField<TEntity, T> implements NumberField<TEntity, T> {
        NumberFieldImplementation(Class<TEntity> entityClass, String name, Class<T> type) {
            super(entityClass, name, type);
        }

        @Override
        public BinaryPredicate<TEntity, T> greaterThan(T value) {
            return Predicates.greaterThan(this, value);
        }

        @Override
        public BinaryPredicate<TEntity, T> lessThan(T value) {
            return Predicates.lessThan(this, value);
        }

        @Override
        public BinaryPredicate<TEntity, T> greaterOrEqual(T value) {
            return Predicates.greaterOrEqual(this, value);
        }

        @Override
        public BinaryPredicate<TEntity, T> lessOrEqual(T value) {
            return Predicates.lessOrEqual(this, value);
        }

        @Override
        public TernaryPredicate<TEntity, T> between(T min, T max) {
            return Predicates.between(this, min, max);
        }
    }

    static class StringFieldImplementation<TEntity> extends AbstractValueField<TEntity, String> implements StringField<TEntity> {
        StringFieldImplementation(Class<TEntity> entityClass, String name) {
            super(entityClass, name, String.class);
        }

        @Override
        public BinaryPredicate<TEntity, String> contains(String substr) {
            return Predicates.contains(this, substr);
        }

        @Override
        public BinaryPredicate<TEntity, String> notContains(String substr) {
            return Predicates.notContains(this, substr);
        }

        @Override
        public BinaryPredicate<TEntity, String> startsWith(String substr) {
            return Predicates.startsWith(this, substr);
        }

        @Override
        public BinaryPredicate<TEntity, String> endsWith(String substr) {
            return Predicates.endsWith(this, substr);
        }

        @Override
        public BinaryPredicate<TEntity, String> notStartsWith(String substr) {
            return Predicates.notStartsWith(this, substr);
        }

        @Override
        public BinaryPredicate<TEntity, String> notEndsWith(String substr) {
            return Predicates.notEndsWith(this, substr);
        }
    }

    static class BlobFieldImplementation<TEntity, T> extends AbstractField<TEntity, T> implements BlobField<TEntity, T> {
        BlobFieldImplementation(Class<TEntity> entityClass, String name, Class<T> type) {
            super(entityClass, name, type);
        }
    }

    public static <TEntity, T> NumberField<TEntity, T> numberField(Class<TEntity> entityClass, String name, Class<T> fieldType) {
        return new NumberFieldImplementation<>(entityClass, name, fieldType);
    }

    public static <TEntity> StringField<TEntity> stringField(Class<TEntity> entityClass, String name) {
        return new StringFieldImplementation<>(entityClass, name);
    }

    public static <TEntity> NumberField<TEntity, Date> dateField(Class<TEntity> entityClass, String name) {
        return numberField(entityClass, name, Date.class);
    }
}
