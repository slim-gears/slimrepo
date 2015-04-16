// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.sql;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import com.slimgears.slimorm.core.interfaces.fields.Field;
import com.slimgears.slimorm.core.interfaces.predicates.BinaryPredicate;
import com.slimgears.slimorm.core.interfaces.predicates.CollectionPredicate;
import com.slimgears.slimorm.core.interfaces.predicates.CompositePredicate;
import com.slimgears.slimorm.core.interfaces.predicates.FieldPredicate;
import com.slimgears.slimorm.core.interfaces.predicates.Predicate;
import com.slimgears.slimorm.core.interfaces.predicates.PredicateType;
import com.slimgears.slimorm.core.interfaces.predicates.TernaryPredicate;
import com.slimgears.slimorm.core.interfaces.predicates.UnaryPredicate;
import com.slimgears.slimorm.core.internal.PredicateVisitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
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

    private String operator(PredicateType predicateType, Object... args) {
        String format = OPERATOR_FORMATS.get(predicateType);
        if (format == null) throw new RuntimeException("Not support predicate type: " + predicateType);
        return String.format(format, args);
    }

    class BuilderVisitor<T> extends PredicateVisitor<T, String>  {
        private final SqlCommand.Parameters parameters;

        BuilderVisitor(SqlCommand.Parameters parameters) {
            this.parameters = parameters;
        }

        private String substituteArg(PredicateType type, Object value) {
            return SqlPredicateBuilder.this.substituteArg(parameters, type, value);
        }

        private Object[] substituteArgs(PredicateType type, Object... args) {
            Object[] substitutedArgs = new String[args.length];
            for (int i = 0; i < args.length; ++i) {
                substitutedArgs[i] = substituteArg(type, args[i]);
            }
            return substitutedArgs;
        }

        private String fieldOperator(FieldPredicate<T, ?> predicate, Object... args) {
            PredicateType type = predicate.getType();
            return fieldName(predicate.getField()) + " " + operator(type, substituteArgs(type, args));
        }

        @Override
        protected String visitBinary(BinaryPredicate<T, ?> predicate) {
            return fieldOperator(predicate, predicate.getValue());
        }

        @Override
        protected String visitTernary(TernaryPredicate<T, ?> predicate) {
            return fieldOperator(predicate, predicate.getFirst(), predicate.getSecond());
        }

        @Override
        protected String visitCollection(CollectionPredicate<T, ?> predicate) {
            return fieldOperator(predicate, joinValues(predicate.getType(), predicate.getValues()));
        }

        @Override
        protected String visitUnary(UnaryPredicate<T, ?> predicate) {
            return fieldOperator(predicate);
        }

        @Override
        protected String visitComposite(CompositePredicate<T> predicate) {
            return combine(predicate.getType(), Iterators.forArray(predicate.getArguments()));
        }

        @Override
        protected String visitUnknown(Predicate<T> predicate) {
            throw new RuntimeException("Not supported predicate class: " + predicate.getClass().getName());
        }

        private String combine(PredicateType type, Iterator<Predicate<T>> predicateIterator) {
            String first = visit(predicateIterator.next());
            if (!predicateIterator.hasNext()) return first;
            String second = combine(type, predicateIterator);
            return operator(type, first, second);
        }
    }

    @Override
    public <T> String build(Predicate<T> predicate, SqlCommand.Parameters parameters) {
        BuilderVisitor<T> visitor = new BuilderVisitor<>(parameters);
        return visitor.visit(predicate);
    }

    protected String substituteArg(SqlCommand.Parameters params, PredicateType type, Object value) {
        return syntaxProvider.parameterReference(params.getCount(), params.add(valueToString(type, value)));
    }

    protected String fieldName(Field field) {
        return syntaxProvider.fieldName(field);
    }

    protected String valueToString(PredicateType type, Object value) {
        String str = syntaxProvider.valueToString(value);
        String argFormat = ARGUMENT_FORMATS.getOrDefault(type, null);
        return argFormat != null ? String.format(argFormat, str) : str;
    }

    protected String joinValues(PredicateType type, Object... values) {
        String[] strValues = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            strValues[i] = valueToString(type, values[i]);
        }
        return Joiner
                .on(", ")
                .join(strValues);
    }
}
