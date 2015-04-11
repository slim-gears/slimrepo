// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype;

import com.slimgears.slimorm.android.prototype.generated.RoleEntity;
import com.slimgears.slimorm.android.prototype.generated.UserEntity;
import com.slimgears.slimorm.annotations.Repository;
import com.slimgears.slimorm.interfaces.EntitySet;
import com.slimgears.slimorm.interfaces.RepositorySession;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
@Repository(name = "UserRepository", version = 0)
public interface UserRepositorySession extends RepositorySession {
    EntitySet<Integer, UserEntity> users();
    EntitySet<Integer, RoleEntity> roles();
}
