// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.apt.prototype.generated;

import com.slimgears.slimorm.apt.prototype.UserRepositorySession;
import com.slimgears.slimorm.core.interfaces.Repository;
import com.slimgears.slimorm.core.internal.AbstractRepository;
import com.slimgears.slimorm.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimorm.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimorm.core.internal.sql.SqlOrmServiceProvider;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class UserRepositoryImpl extends AbstractRepository<UserRepositorySession> implements Repository<UserRepositorySession> {
    public UserRepositoryImpl(OrmServiceProvider ormServiceProvider) {
        super(ormServiceProvider, UserRepositorySessionImpl.Model.Instance);
    }

    @Override
    protected UserRepositorySession createSession(SessionServiceProvider sessionServiceProvider) {
        return new UserRepositorySessionImpl(sessionServiceProvider);
    }
}
