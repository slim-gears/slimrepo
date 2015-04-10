// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.Predicate;
import com.slimgears.slimorm.interfaces.StringField;

/**
* Created by Denis on 08-Apr-15
* <File Description>
*/
public class SqlStringField<TEntity> extends AbstractSqlField<TEntity, String> implements StringField<TEntity> {
    public SqlStringField(String name) {
        super(name, String.class);
    }

    @Override
    public Predicate<TEntity> contains(String substr) {
        return like("%" + substr + "%");
    }

    @Override
    public Predicate<TEntity> startsWith(String substr) {
        return like(substr + "%");
    }

    @Override
    public Predicate<TEntity> endsWith(String substr) {
        return like("%" + substr);
    }

    private Predicate<TEntity> like(String pattern) {
        return operator("LIKE %1$s", pattern);
    }
}
