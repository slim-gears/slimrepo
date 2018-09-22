// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.conditions.*;

/**
 * Created by Denis on 11-Apr-15
 *
 */
public abstract class PredicateVisitor<TEntity, T> {
    public T visit(Condition<TEntity> condition) {
        if (condition instanceof BinaryCondition) return visitBinary((BinaryCondition<TEntity, ?>) condition);
        if (condition instanceof UnaryCondition) return visitUnary((UnaryCondition<TEntity, ?>) condition);
        if (condition instanceof TernaryCondition) return visitTernary((TernaryCondition<TEntity, ?>) condition);
        if (condition instanceof CollectionCondition) return visitCollection((CollectionCondition<TEntity, ?>) condition);
        if (condition instanceof CompositeCondition) return visitComposite((CompositeCondition<TEntity>) condition);
        if (condition instanceof RelationalCondition) return visitRelational((RelationalCondition<TEntity, ?>) condition);
        return visitUnknown(condition);
    }

    protected <V> T visitBinary(BinaryCondition<TEntity, V> predicate) {
        return defaultValue();
    }

    protected <V> T visitTernary(TernaryCondition<TEntity, V> predicate) {
        return defaultValue();
    }

    protected <V> T visitCollection(CollectionCondition<TEntity, V> predicate) {
        return defaultValue();
    }

    protected <V> T visitUnary(UnaryCondition<TEntity, V> predicate) {
        return defaultValue();
    }

    protected T visitComposite(CompositeCondition<TEntity> predicate) {
        return defaultValue();
    }

    protected T visitUnknown(Condition<TEntity> condition) {
        return defaultValue();
    }

    protected <V> T visitRelational(RelationalCondition<TEntity, V> condition) {
        return defaultValue();
    }

    protected T defaultValue() {
        return null;
    }
}
