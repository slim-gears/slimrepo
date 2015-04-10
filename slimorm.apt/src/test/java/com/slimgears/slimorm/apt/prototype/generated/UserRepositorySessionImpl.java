// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.apt.prototype.generated;

import com.slimgears.slimorm.apt.prototype.UserRepositorySession;
import com.slimgears.slimorm.apt.prototype.slimsql.SlimSqlOrm;
import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.internal.sql.SqlLazyEntitySet;
import com.slimgears.slimorm.internal.sql.AbstractSqlRepositorySession;
import com.slimgears.slimorm.interfaces.EntitySet;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class UserRepositorySessionImpl extends AbstractSqlRepositorySession implements UserRepositorySession {
    private final SqlLazyEntitySet<Integer, UserEntity> usersEntitySet;
    private final SqlLazyEntitySet<Integer, RoleEntity> rolesEntitySet;

    public UserRepositorySessionImpl(Repository<UserRepositorySession> repository) {
        super(SlimSqlOrm.INSTANCE, SlimSqlOrm.INSTANCE.getStatementBuilder(), repository);
        usersEntitySet = new SqlLazyEntitySet<>(this, SlimSqlOrm.INSTANCE, UserEntity.Fields.EntityMetaType);
        rolesEntitySet = new SqlLazyEntitySet<>(this, SlimSqlOrm.INSTANCE, RoleEntity.Fields.EntityMetaType);
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
