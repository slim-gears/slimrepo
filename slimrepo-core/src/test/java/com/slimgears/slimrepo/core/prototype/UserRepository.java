// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.prototype;

import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;

/**
 * Created by Denis on 05-Apr-15
 *
 */
public interface UserRepository extends Repository {
    EntitySet<UserEntity> users();
    EntitySet<RoleEntity> roles();
}
