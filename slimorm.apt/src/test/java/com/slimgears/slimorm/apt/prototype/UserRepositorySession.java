// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.apt.prototype;

import com.slimgears.slimorm.apt.prototype.generated.RoleEntity;
import com.slimgears.slimorm.apt.prototype.generated.UserEntity;
import com.slimgears.slimorm.core.interfaces.entities.EntitySet;
import com.slimgears.slimorm.core.interfaces.RepositorySession;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
public interface UserRepositorySession extends RepositorySession {
    EntitySet<Integer, UserEntity> users();
    EntitySet<Integer, RoleEntity> roles();
}
