// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.prototype.generated;

import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.AbstractRepositoryService;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.prototype.UserRepository;

/**
 * Created by Denis on 09-Apr-15
 *
 */
public class GeneratedUserRepositoryService extends AbstractRepositoryService<UserRepository> implements UserRepositoryService {
    public GeneratedUserRepositoryService(OrmServiceProvider ormServiceProvider) {
        super(ormServiceProvider, GeneratedUserRepository.Model.Instance);
    }

    @Override
    protected UserRepository createRepository(SessionServiceProvider sessionServiceProvider) {
        return new GeneratedUserRepository(sessionServiceProvider);
    }

    @Override
    public EntitySet<UserEntity> users() {
        return getEntitySet(UserEntity.EntityMetaType);
    }

    @Override
    public EntitySet<RoleEntity> roles() {
        return getEntitySet(RoleEntity.EntityMetaType);
    }
}
