// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces.predicates;

import com.slimgears.slimorm.core.interfaces.fields.Field;
import com.slimgears.slimorm.core.interfaces.fields.NumberField;
import com.slimgears.slimorm.core.interfaces.fields.StringField;
import com.slimgears.slimorm.core.interfaces.fields.ValueField;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public class Predicates {
    static abstract class AbstractPredicate<TEntity> implements Predicate<TEntity> {
        protected final PredicateType type;

        AbstractPredicate(PredicateType type) {
            this.type = type;
        }

        @Override
        public PredicateType getType() {
            return type;
        }
    }

    static abstract class AbstractFieldPredicate<TEntity, T> extends AbstractPredicate<TEntity> implements FieldPredicate<TEntity, T> {
        private final Field<TEntity, T> field;

        AbstractFieldPredicate(PredicateType type, Field<TEntity, T> field) {
            super(type);
            this.field = field;
        }

        @Override
        public Field<TEntity, T> getField() {
            return field;
        }
    }

    static class BinaryPredicateImplementation<TEntity, T> extends AbstractFieldPredicate<TEntity, T> implements BinaryPredicate<TEntity, T> {
        private final T value;

        BinaryPredicateImplementation(PredicateType type, Field<TEntity, T> field, T value) {
            super(type, field);
            this.value = value;
        }

        @Override
        public T getValue() {
            return value;
        }
    }

    static class CollectionPredicateImplementation<TEntity, T> extends AbstractFieldPredicate<TEntity, T> implements CollectionPredicate<TEntity, T> {
        private final T[] values;

        CollectionPredicateImplementation(PredicateType type, Field<TEntity, T> field, T[] values) {
            super(type, field);
            this.values = values;
        }

        @Override
        public T[] getValues() {
            return values;
        }
    }

    static class TernaryPredicateImplementation<TEntity, T> extends AbstractFieldPredicate<TEntity, T> implements TernaryPredicate<TEntity, T> {
        private final T first;
        private final T second;

        TernaryPredicateImplementation(PredicateType type, Field<TEntity, T> field, T first, T second) {
            super(type, field);
            this.first = first;
            this.second = second;
        }

        @Override
        public T getFirst() {
            return first;
        }

        @Override
        public T getSecond() {
            return second;
        }
    }

    static class UnaryPredicateImplementation<TEntity, T> extends AbstractFieldPredicate<TEntity, T> implements UnaryPredicate<TEntity, T> {
        UnaryPredicateImplementation(PredicateType type, Field<TEntity, T> field) {
            super(type, field);
        }
    }

    static class CompositePredicateImplementation<TEntity> extends AbstractPredicate<TEntity> implements CompositePredicate<TEntity> {
        private final Predicate<TEntity>[] arguments;

        CompositePredicateImplementation(PredicateType type, Predicate<TEntity>[] arguments) {
            super(type);
            this.arguments = arguments;
        }

        @Override
        public Predicate<TEntity>[] getArguments() {
            return arguments;
        }
    }

    @SafeVarargs
    public static <TEntity> Predicate<TEntity> and(Predicate<TEntity>... predicates) {
        return new CompositePredicateImplementation<>(PredicateType.COMPOSITE_AND, predicates);
    }

    @SafeVarargs
    public static <TEntity> Predicate<TEntity> or(Predicate<TEntity>... predicates) {
        return new CompositePredicateImplementation<>(PredicateType.COMPOSITE_OR, predicates);
    }

    public static <TEntity, T> BinaryPredicate<TEntity, T> equals(ValueField<TEntity, T> field, T value) {
        return new BinaryPredicateImplementation<>(PredicateType.VALUE_FIELD_EQUALS, field, value);
    }

    public static <TEntity, T> BinaryPredicate<TEntity, T> notEquals(ValueField<TEntity, T> field, T value) {
        return new BinaryPredicateImplementation<>(PredicateType.VALUE_FIELD_NOT_EQUALS, field, value);
    }

    public static <TEntity, T> UnaryPredicate<TEntity, T> isNull(Field<TEntity, T> field) {
        return new UnaryPredicateImplementation<>(PredicateType.FIELD_IS_NULL, field);
    }

    public static <TEntity, T> UnaryPredicate<TEntity, T> isNotNull(Field<TEntity, T> field) {
        return new UnaryPredicateImplementation<>(PredicateType.FIELD_IS_NOT_NULL, field);
    }

    @SafeVarargs
    public static <TEntity, T> CollectionPredicate<TEntity, T> in(Field<TEntity, T> field, T... values) {
        return new CollectionPredicateImplementation<>(PredicateType.VALUE_FIELD_IN, field, values);
    }

    @SafeVarargs
    public static <TEntity, T> CollectionPredicate<TEntity, T> notIn(Field<TEntity, T> field, T... values) {
        return new CollectionPredicateImplementation<>(PredicateType.VALUE_FIELD_NOT_IN, field, values);
    }

    public static <TEntity, T> BinaryPredicate<TEntity, T> greaterThan(NumberField<TEntity, T> field, T value) {
        return new BinaryPredicateImplementation<>(PredicateType.NUMBER_FIELD_GREATER, field, value);
    }

    public static <TEntity, T> BinaryPredicate<TEntity, T> greaterOrEqual(NumberField<TEntity, T> field, T value) {
        return new BinaryPredicateImplementation<>(PredicateType.NUMBER_FIELD_GREATER_EQUAL, field, value);
    }

    public static <TEntity, T> BinaryPredicate<TEntity, T> lessThan(NumberField<TEntity, T> field, T value) {
        return new BinaryPredicateImplementation<>(PredicateType.NUMBER_FIELD_LESS, field, value);
    }

    public static <TEntity, T> BinaryPredicate<TEntity, T> lessOrEqual(NumberField<TEntity, T> field, T value) {
        return new BinaryPredicateImplementation<>(PredicateType.NUMBER_FIELD_LESS_EQUAL, field, value);
    }

    public static <TEntity, T> TernaryPredicate<TEntity, T> between(NumberField<TEntity, T> field, T lowest, T highest) {
        return new TernaryPredicateImplementation<>(PredicateType.NUMBER_FIELD_BETWEEN, field, lowest, highest);
    }

    public static <TEntity> BinaryPredicate<TEntity, String> contains(StringField<TEntity> field, String substr) {
        return new BinaryPredicateImplementation<>(PredicateType.STRING_FIELD_CONTAINS, field, substr);
    }

    public static <TEntity> BinaryPredicate<TEntity, String> notContains(StringField<TEntity> field, String substr) {
        return new BinaryPredicateImplementation<>(PredicateType.STRING_FIELD_NOT_CONTAINS, field, substr);
    }

    public static <TEntity> BinaryPredicate<TEntity, String> startsWith(StringField<TEntity> field, String substr) {
        return new BinaryPredicateImplementation<>(PredicateType.STRING_FIELD_STARTS_WITH, field, substr);
    }

    public static <TEntity> BinaryPredicate<TEntity, String> notStartsWith(StringField<TEntity> field, String substr) {
        return new BinaryPredicateImplementation<>(PredicateType.STRING_FIELD_NOT_STARTS_WITH, field, substr);
    }

    public static <TEntity> BinaryPredicate<TEntity, String> endsWith(StringField<TEntity> field, String substr) {
        return new BinaryPredicateImplementation<>(PredicateType.STRING_FIELD_ENDS_WITH, field, substr);
    }

    public static <TEntity> BinaryPredicate<TEntity, String> notEndsWith(StringField<TEntity> field, String substr) {
        return new BinaryPredicateImplementation<>(PredicateType.STRING_FIELD_NOT_ENDS_WITH, field, substr);
    }
}
