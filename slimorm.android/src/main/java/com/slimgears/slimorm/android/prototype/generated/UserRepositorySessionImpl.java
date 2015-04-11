// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype.generated;

import com.slimgears.slimorm.android.prototype.UserRepositorySession;
import com.slimgears.slimorm.android.prototype.core.SqliteOrmService;
import com.slimgears.slimorm.interfaces.EntitySet;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.internal.sql.AbstractSqlRepositorySession;
import com.slimgears.slimorm.internal.sql.SqlLazyEntitySet;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class UserRepositorySessionImpl extends AbstractSqlRepositorySession implements UserRepositorySession {
    private final SqlLazyEntitySet<Integer, UserEntity> usersEntitySet;
    private final SqlLazyEntitySet<Integer, RoleEntity> rolesEntitySet;

    public static class Model {
        public final static EntityType<?, ?>[] EntityTypes = {
                UserEntity.EntityMetaType,
                RoleEntity.EntityMetaType
        };
    }

    public UserRepositorySessionImpl(Repository<UserRepositorySession> repository) {
        super(SqliteOrmService.INSTANCE, SqliteOrmService.INSTANCE.getStatementBuilder(), repository);
        usersEntitySet = new SqlLazyEntitySet<>(this, SqliteOrmService.INSTANCE, UserEntity.EntityMetaType);
        rolesEntitySet = new SqlLazyEntitySet<>(this, SqliteOrmService.INSTANCE, RoleEntity.EntityMetaType);
    }

    @Override
    public EntitySet<Integer, UserEntity> users() {
        return usersEntitySet.get();
    }

    @Override
    public EntitySet<Integer, RoleEntity> roles() {
        return rolesEntitySet.get();
    }
}
