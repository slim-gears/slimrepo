// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt.prototype.generated;

import com.slimgears.slimrepo.apt.prototype.UserRepositorySession;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.internal.AbstractRepository;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;

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
