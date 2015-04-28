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
    private final EntitySet.Provider<Integer, UserEntity> usersEntitySet;
    private final EntitySet.Provider<Integer, RoleEntity> rolesEntitySet;

    public GeneratedUserRepository(SessionServiceProvider sessionServiceProvider) {
        super(sessionServiceProvider);
        usersEntitySet = sessionServiceProvider.getEntitySetProvider(UserEntity.EntityMetaType);
        rolesEntitySet = sessionServiceProvider.getEntitySetProvider(RoleEntity.EntityMetaType);
    }

    public static class Model extends DefaultRepositoryModel {
        public final static RepositoryModel Instance = new Model();
        private final static int Version = 10;
        private final static String Name = "UserRepository";
        public Model() {
            super(Name, Version, UserEntity.EntityMetaType, RoleEntity.EntityMetaType);
        }
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
