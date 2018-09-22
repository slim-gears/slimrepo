// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.conditions.*;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.PredicateVisitor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;
import com.slimgears.slimrepo.core.utilities.Iterators;
import com.slimgears.slimrepo.core.utilities.Joiner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Denis on 11-Apr-15
 *
 */
public class SqlPredicateBuilder implements SqlStatementBuilder.PredicateBuilder {
    private static final Map<PredicateType, String> OPERATOR_FORMATS = new HashMap<>();
    private static final Map<PredicateType, String> ARGUMENT_FORMATS = new HashMap<>();

    static {
        OPERATOR_FORMATS.put(PredicateType.FIELD_IS_NULL, "IS NULL");
        OPERATOR_FORMATS.put(PredicateType.FIELD_IS_NOT_NULL, "IS NOT NULL");
        OPERATOR_FORMATS.put(PredicateType.VALUE_FIELD_EQUALS, "= %1$s");
        OPERATOR_FORMATS.put(PredicateType.VALUE_FIELD_NOT_EQUALS, "<> %1$s");
        OPERATOR_FORMATS.put(PredicateType.VALUE_FIELD_IN, "IN (%1$s)");
        OPERATOR_FORMATS.put(PredicateType.VALUE_FIELD_NOT_IN, "NOT IN (%1$s)");
        OPERATOR_FORMATS.put(PredicateType.NUMBER_FIELD_GREATER, "> %1$s");
        OPERATOR_FORMATS.put(PredicateType.NUMBER_FIELD_LESS, "< %1$s");
        OPERATOR_FORMATS.put(PredicateType.NUMBER_FIELD_GREATER_EQUAL, ">= %1$s");
        OPERATOR_FORMATS.put(PredicateType.NUMBER_FIELD_LESS_EQUAL, "<= %1$s");
        OPERATOR_FORMATS.put(PredicateType.NUMBER_FIELD_BETWEEN, "BETWEEN %1$s AND %2$s");
        OPERATOR_FORMATS.put(PredicateType.STRING_FIELD_CONTAINS, "LIKE %1$s");
        OPERATOR_FORMATS.put(PredicateType.STRING_FIELD_NOT_CONTAINS, "NOT LIKE %1$s");
        OPERATOR_FORMATS.put(PredicateType.STRING_FIELD_STARTS_WITH, "LIKE %1$s");
        OPERATOR_FORMATS.put(PredicateType.STRING_FIELD_ENDS_WITH, "LIKE %1$s");
        OPERATOR_FORMATS.put(PredicateType.STRING_FIELD_NOT_STARTS_WITH, "NOT LIKE %1$s");
        OPERATOR_FORMATS.put(PredicateType.STRING_FIELD_NOT_ENDS_WITH, "NOT LIKE %1$s");
        OPERATOR_FORMATS.put(PredicateType.COMPOSITE_AND, "(%1$s) AND (%2$s)");
        OPERATOR_FORMATS.put(PredicateType.COMPOSITE_OR, "(%1$s) OR (%2$s)");
        OPERATOR_FORMATS.put(PredicateType.COMPOSITE_NOT, "NOT (%1$s)");

        ARGUMENT_FORMATS.put(PredicateType.STRING_FIELD_CONTAINS, "%%%s%%");
        ARGUMENT_FORMATS.put(PredicateType.STRING_FIELD_NOT_CONTAINS, "%%%s%%");
        ARGUMENT_FORMATS.put(PredicateType.STRING_FIELD_STARTS_WITH, "%s%%");
        ARGUMENT_FORMATS.put(PredicateType.STRING_FIELD_NOT_STARTS_WITH, "%s%%");
        ARGUMENT_FORMATS.put(PredicateType.STRING_FIELD_ENDS_WITH, "%%%s");
        ARGUMENT_FORMATS.put(PredicateType.STRING_FIELD_NOT_ENDS_WITH, "%%%s");
    }

    private final SqlStatementBuilder.SyntaxProvider syntaxProvider;

    public SqlPredicateBuilder(SqlStatementBuilder.SyntaxProvider syntaxProvider) {
        this.syntaxProvider = syntaxProvider;
    }

    private String operator(PredicateType predicateType, String... args) {
        String format = OPERATOR_FORMATS.get(predicateType);
        if (format == null) throw new RuntimeException("Not support predicate type: " + predicateType);
        return String.format(format, (Object[])args);
    }

    class BuilderVisitor<T> extends PredicateVisitor<T, String>  {
        private final SqlCommand.Parameters parameters;

        BuilderVisitor(SqlCommand.Parameters parameters) {
            this.parameters = parameters;
        }

        private <V> String substituteArg(FieldCondition<T, V> condition, V value) {
            return SqlPredicateBuilder.this.substituteArg(parameters, condition, value);
        }

        @SafeVarargs
        private final <V> String[] substituteArgs(FieldCondition<T, V> type, V... args) {
            String[] substitutedArgs = new String[args.length];
            for (int i = 0; i < args.length; ++i) {
                substitutedArgs[i] = substituteArg(type, args[i]);
            }
            return substitutedArgs;
        }

        private <V> String fieldOperator(FieldCondition<T, V> predicate, String... args) {
            PredicateType type = predicate.getType();
            return fieldName(predicate.getField()) + " " + operator(type, args);
        }

        @Override
        protected <V> String visitBinary(BinaryCondition<T, V> predicate) {
            return fieldOperator(predicate, substituteArg(predicate, predicate.getValue()));
        }

        @Override
        protected <V> String visitTernary(TernaryCondition<T, V> predicate) {
            return fieldOperator(predicate, substituteArgs(predicate, predicate.getFirst()        , predicate.getSecond()));
    }

        @Override
        protected <V> String visitCollection(CollectionCondition<T, V> predicate) {
            return fieldOperator(predicate, joinStrings(substituteArgs(predicate, predicate.getValues())));
        }

        @Override
        protected <V> String visitUnary(UnaryCondition<T, V> predicate) {
            return fieldOperator(predicate);
        }

        @Override
        protected String visitComposite(CompositeCondition<T> predicate) {
            return combine(predicate.getType(), Iterators.forArray(predicate.getArguments()));
        }

        @Override
        protected String visitUnknown(Condition<T> condition) {
            throw new RuntimeException("Not supported predicate class: " + condition.getClass().getName());
        }

        @Override
        protected <V> String visitRelational(RelationalCondition<T, V> condition) {
            return build(condition.getCondition(), parameters);
        }

        private String combine(PredicateType type, Iterator<Condition<T>> predicateIterator) {
            String first = visit(predicateIterator.next());
            if (!predicateIterator.hasNext()) return first;
            String second = combine(type, predicateIterator);
            return operator(type, first, second);
        }
    }

    @Override
    public <T> String build(Condition<T> condition, SqlCommand.Parameters parameters) {
        BuilderVisitor<T> visitor = new BuilderVisitor<>(parameters);
        return visitor.visit(condition);
    }

    protected <T, V> String substituteArg(SqlCommand.Parameters params, FieldCondition<T, V> condition, V value) {
        return syntaxProvider.parameterReference(params.getCount(), params.add(valueToString(condition, value)));
    }

    protected String fieldName(Field field) {
        return syntaxProvider.qualifiedFieldName(field);
    }

    protected <T, V> String valueToString(FieldCondition<T, V> condition, V value) {
        if (value instanceof String) {
            String argFormat = ARGUMENT_FORMATS.get(condition.getType());
            if (argFormat != null) //noinspection unchecked
                value = (V)String.format(argFormat, value);
        }
        return syntaxProvider.valueToString(condition.getField(), value);
    }

    private String joinStrings(String... strings) {
        return Joiner.on(", ").join(strings);
    }
}
