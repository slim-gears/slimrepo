// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

/**
* Created by Denis on 08-Apr-15
* <File Description>
*/
public class SqlDataField<TEntity, T> extends AbstractSqlField<TEntity, T> {
    public SqlDataField(String name, Class<T> type) {
        super(name, type);
    }
}
