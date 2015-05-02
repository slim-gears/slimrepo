package com.slimgears.slimrepo.core.prototype.generated;

import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.prototype.UserRepository;

/**
 * Created by Denis on 01-May-15.
 */
public interface UserRepositoryService extends RepositoryService<UserRepository> {
    EntitySet<UserEntity> users();
    EntitySet<RoleEntity> roles();
}
