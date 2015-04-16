// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal;

import com.slimgears.slimorm.core.interfaces.predicates.BinaryPredicate;
import com.slimgears.slimorm.core.interfaces.predicates.CollectionPredicate;
import com.slimgears.slimorm.core.interfaces.predicates.CompositePredicate;
import com.slimgears.slimorm.core.interfaces.predicates.Predicate;
import com.slimgears.slimorm.core.interfaces.predicates.TernaryPredicate;
import com.slimgears.slimorm.core.interfaces.predicates.UnaryPredicate;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public abstract class PredicateVisitor<TEntity, T> {
    public T visit(Predicate<TEntity> predicate) {
        if (predicate instanceof BinaryPredicate) return visitBinary((BinaryPredicate<TEntity, ?>)predicate);
        if (predicate instanceof UnaryPredicate) return visitUnary((UnaryPredicate<TEntity, ?>)predicate);
        if (predicate instanceof TernaryPredicate) return visitTernary((TernaryPredicate<TEntity, ?>)predicate);
        if (predicate instanceof CollectionPredicate) return visitCollection((CollectionPredicate<TEntity, ?>)predicate);
        if (predicate instanceof CompositePredicate) return visitComposite((CompositePredicate<TEntity>)predicate);
        return visitUnknown(predicate);
    }

    protected abstract T visitBinary(BinaryPredicate<TEntity, ?> predicate);
    protected abstract T visitTernary(TernaryPredicate<TEntity, ?> predicate);
    protected abstract T visitCollection(CollectionPredicate<TEntity, ?> predicate);
    protected abstract T visitUnary(UnaryPredicate<TEntity, ?> predicate);
    protected abstract T visitComposite(CompositePredicate<TEntity> predicate);
    protected abstract T visitUnknown(Predicate<TEntity> predicate);
}
