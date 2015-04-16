// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype.generated;

import com.slimgears.slimorm.android.prototype.UserRepositorySession;
import com.slimgears.slimorm.core.interfaces.entities.EntitySet;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.internal.AbstractRepositorySession;
import com.slimgears.slimorm.core.internal.DefaultRepositoryModel;
import com.slimgears.slimorm.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimorm.core.internal.interfaces.SessionServiceProvider;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class UserRepositorySessionImpl extends AbstractRepositorySession implements UserRepositorySession {
    private final EntitySet.Provider<Integer, UserEntity> usersEntitySet;
    private final EntitySet.Provider<Integer, RoleEntity> rolesEntitySet;

    public static class Model extends DefaultRepositoryModel {
        public final static RepositoryModel Instance = new Model();
        private final static int Version = 10;
        private final static String Name = "UserRepository";
        public Model() {
            super(Name, Version, UserEntity.EntityMetaType, RoleEntity.EntityMetaType);
        }
    }

    public UserRepositorySessionImpl(SessionServiceProvider sessionServiceProvider) {
        super(sessionServiceProvider);
        usersEntitySet = sessionServiceProvider.getEntitySetProvider(UserEntity.EntityMetaType);
        rolesEntitySet = sessionServiceProvider.getEntitySetProvider(RoleEntity.EntityMetaType);
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
