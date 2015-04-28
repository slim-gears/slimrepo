// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.prototype.generated;

import com.slimgears.slimrepo.core.prototype.UserRepository;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.AbstractRepository;
import com.slimgears.slimrepo.core.internal.DefaultRepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class GeneratedUserRepository extends AbstractRepository implements UserRepository {
    private final EntitySet.Provider<Integer, RoleEntity> rolesEntitySet;
    private final EntitySet.Provider<Integer, UserEntity> usersEntitySet;

    public GeneratedUserRepository(SessionServiceProvider sessionServiceProvider) {
        super(sessionServiceProvider);
        rolesEntitySet = sessionServiceProvider.getEntitySetProvider(RoleEntity.EntityMetaType);
        usersEntitySet = sessionServiceProvider.getEntitySetProvider(UserEntity.EntityMetaType);
    }

    public static class Model extends DefaultRepositoryModel {
        public final static RepositoryModel Instance = new Model();
        private final static int Version = 10;
        private final static String Name = "UserRepository";
        public Model() {
            super(Name, Version, RoleEntity.EntityMetaType, UserEntity.EntityMetaType);
        }
    }

    @Override
    public EntitySet<Integer, RoleEntity> roles() {
        return rolesEntitySet.get();
    }

    @Override
    public EntitySet<Integer, UserEntity> users() {
        return usersEntitySet.get();
    }
}
