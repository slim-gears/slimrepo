// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android.prototype;

import com.slimgears.slimrepo.android.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.android.prototype.generated.UserEntity;
import com.slimgears.slimrepo.core.annotations.GenerateRepository;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.RepositorySession;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
@GenerateRepository(name = "UserRepository", version = 0)
public interface UserRepositorySession extends RepositorySession {
    EntitySet<Integer, UserEntity> users();
    EntitySet<Integer, RoleEntity> roles();
}
