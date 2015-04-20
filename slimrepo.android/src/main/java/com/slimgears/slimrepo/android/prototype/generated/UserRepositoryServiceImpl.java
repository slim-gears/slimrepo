// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android.prototype.generated;

import com.slimgears.slimrepo.android.prototype.UserRepository;
import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.internal.AbstractRepositoryService;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class UserRepositoryServiceImpl extends AbstractRepositoryService<UserRepository> implements RepositoryService<UserRepository> {
    public UserRepositoryServiceImpl(OrmServiceProvider ormServiceProvider) {
        super(ormServiceProvider, UserRepositoryImpl.Model.Instance);
    }

    @Override
    protected UserRepository createRepository(SessionServiceProvider sessionServiceProvider) {
        return new UserRepositoryImpl(sessionServiceProvider);
    }
}
