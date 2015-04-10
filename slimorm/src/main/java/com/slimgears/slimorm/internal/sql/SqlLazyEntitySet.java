// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntitySet;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.internal.QueryFactory;
import com.slimgears.slimorm.internal.sql.SqlEntitySet;
import com.slimgears.slimorm.internal.sql.SqlRepositorySession;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class SqlLazyEntitySet<TKey, TEntity extends Entity<TKey>> {
    private EntitySet<TKey, TEntity> entitySet = null;
    private final SqlRepositorySession session;
    private final QueryFactory queryFactory;
    private final EntityType<TKey, TEntity> entityType;

    public SqlLazyEntitySet(SqlRepositorySession session, QueryFactory queryFactory, EntityType<TKey, TEntity> entityType) {
        this.session = session;
        this.queryFactory = queryFactory;
        this.entityType = entityType;
    }

    public EntitySet<TKey, TEntity> get() {
        if (entitySet != null) return entitySet;
        return entitySet = new SqlEntitySet<>(session, queryFactory, entityType);
    }
}
