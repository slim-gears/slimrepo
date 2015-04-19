// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.conditions;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.NumericField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public class Conditions {
    static abstract class AbstractCondition<TEntity> implements Condition<TEntity> {
        protected final PredicateType type;

        AbstractCondition(PredicateType type) {
            this.type = type;
        }

        @Override
        public PredicateType getType() {
            return type;
        }

        @Override
        public Condition<TEntity> and(Condition<TEntity> other) {
            return Conditions.and(this, other);
        }

        @Override
        public Condition<TEntity> or(Condition<TEntity> other) {
            return Conditions.or(this, other);
        }
    }

    static abstract class AbstractFieldCondition<TEntity, T> extends AbstractCondition<TEntity> implements FieldCondition<TEntity, T> {
        private final Field<TEntity, T> field;

        AbstractFieldCondition(PredicateType type, Field<TEntity, T> field) {
            super(type);
            this.field = field;
        }

        @Override
        public Field<TEntity, T> getField() {
            return field;
        }
    }

    static class BinaryConditionImplementation<TEntity, T> extends AbstractFieldCondition<TEntity, T> implements BinaryCondition<TEntity, T> {
        private final T value;

        BinaryConditionImplementation(PredicateType type, Field<TEntity, T> field, T value) {
            super(type, field);
            this.value = value;
        }

        @Override
        public T getValue() {
            return value;
        }
    }

    static class CollectionConditionImplementation<TEntity, T> extends AbstractFieldCondition<TEntity, T> implements CollectionCondition<TEntity, T> {
        private final T[] values;

        CollectionConditionImplementation(PredicateType type, Field<TEntity, T> field, T[] values) {
            super(type, field);
            this.values = values;
        }

        @Override
        public T[] getValues() {
            return values;
        }
    }

    static class TernaryConditionImplementation<TEntity, T> extends AbstractFieldCondition<TEntity, T> implements TernaryCondition<TEntity, T> {
        private final T first;
        private final T second;

        TernaryConditionImplementation(PredicateType type, Field<TEntity, T> field, T first, T second) {
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

    static class UnaryConditionImplementation<TEntity, T> extends AbstractFieldCondition<TEntity, T> implements UnaryCondition<TEntity, T> {
        UnaryConditionImplementation(PredicateType type, Field<TEntity, T> field) {
            super(type, field);
        }
    }

    static class CompositeConditionImplementation<TEntity> extends AbstractCondition<TEntity> implements CompositeCondition<TEntity> {
        private final Condition<TEntity>[] arguments;

        CompositeConditionImplementation(PredicateType type, Condition<TEntity>[] arguments) {
            super(type);
            this.arguments = arguments;
        }

        @Override
        public Condition<TEntity>[] getArguments() {
            return arguments;
        }
    }

    @SafeVarargs
    public static <TEntity> Condition<TEntity> and(Condition<TEntity>... conditions) {
        return new CompositeConditionImplementation<>(PredicateType.COMPOSITE_AND, conditions);
    }

    @SafeVarargs
    public static <TEntity> Condition<TEntity> or(Condition<TEntity>... conditions) {
        return new CompositeConditionImplementation<>(PredicateType.COMPOSITE_OR, conditions);
    }

    public static <TEntity, T> BinaryCondition<TEntity, T> equals(ValueField<TEntity, T> field, T value) {
        return new BinaryConditionImplementation<>(PredicateType.VALUE_FIELD_EQUALS, field, value);
    }

    public static <TEntity, T> BinaryCondition<TEntity, T> notEquals(ValueField<TEntity, T> field, T value) {
        return new BinaryConditionImplementation<>(PredicateType.VALUE_FIELD_NOT_EQUALS, field, value);
    }

    public static <TEntity, T> UnaryCondition<TEntity, T> isNull(Field<TEntity, T> field) {
        return new UnaryConditionImplementation<>(PredicateType.FIELD_IS_NULL, field);
    }

    public static <TEntity, T> UnaryCondition<TEntity, T> isNotNull(Field<TEntity, T> field) {
        return new UnaryConditionImplementation<>(PredicateType.FIELD_IS_NOT_NULL, field);
    }

    @SafeVarargs
    public static <TEntity, T> CollectionCondition<TEntity, T> in(Field<TEntity, T> field, T... values) {
        return new CollectionConditionImplementation<>(PredicateType.VALUE_FIELD_IN, field, values);
    }

    @SafeVarargs
    public static <TEntity, T> CollectionCondition<TEntity, T> notIn(Field<TEntity, T> field, T... values) {
        return new CollectionConditionImplementation<>(PredicateType.VALUE_FIELD_NOT_IN, field, values);
    }

    public static <TEntity, T> BinaryCondition<TEntity, T> greaterThan(NumericField<TEntity, T> field, T value) {
        return new BinaryConditionImplementation<>(PredicateType.NUMBER_FIELD_GREATER, field, value);
    }

    public static <TEntity, T> BinaryCondition<TEntity, T> greaterOrEqual(NumericField<TEntity, T> field, T value) {
        return new BinaryConditionImplementation<>(PredicateType.NUMBER_FIELD_GREATER_EQUAL, field, value);
    }

    public static <TEntity, T> BinaryCondition<TEntity, T> lessThan(NumericField<TEntity, T> field, T value) {
        return new BinaryConditionImplementation<>(PredicateType.NUMBER_FIELD_LESS, field, value);
    }

    public static <TEntity, T> BinaryCondition<TEntity, T> lessOrEqual(NumericField<TEntity, T> field, T value) {
        return new BinaryConditionImplementation<>(PredicateType.NUMBER_FIELD_LESS_EQUAL, field, value);
    }

    public static <TEntity, T> TernaryCondition<TEntity, T> between(NumericField<TEntity, T> field, T lowest, T highest) {
        return new TernaryConditionImplementation<>(PredicateType.NUMBER_FIELD_BETWEEN, field, lowest, highest);
    }

    public static <TEntity> BinaryCondition<TEntity, String> contains(StringField<TEntity> field, String substr) {
        return new BinaryConditionImplementation<>(PredicateType.STRING_FIELD_CONTAINS, field, substr);
    }

    public static <TEntity> BinaryCondition<TEntity, String> notContains(StringField<TEntity> field, String substr) {
        return new BinaryConditionImplementation<>(PredicateType.STRING_FIELD_NOT_CONTAINS, field, substr);
    }

    public static <TEntity> BinaryCondition<TEntity, String> startsWith(StringField<TEntity> field, String substr) {
        return new BinaryConditionImplementation<>(PredicateType.STRING_FIELD_STARTS_WITH, field, substr);
    }

    public static <TEntity> BinaryCondition<TEntity, String> notStartsWith(StringField<TEntity> field, String substr) {
        return new BinaryConditionImplementation<>(PredicateType.STRING_FIELD_NOT_STARTS_WITH, field, substr);
    }

    public static <TEntity> BinaryCondition<TEntity, String> endsWith(StringField<TEntity> field, String substr) {
        return new BinaryConditionImplementation<>(PredicateType.STRING_FIELD_ENDS_WITH, field, substr);
    }

    public static <TEntity> BinaryCondition<TEntity, String> notEndsWith(StringField<TEntity> field, String substr) {
        return new BinaryConditionImplementation<>(PredicateType.STRING_FIELD_NOT_ENDS_WITH, field, substr);
    }
}
