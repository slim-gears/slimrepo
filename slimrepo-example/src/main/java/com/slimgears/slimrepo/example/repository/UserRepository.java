// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.example.repository;

import com.slimgears.slimrepo.core.annotations.GenerateRepository;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;

/**
 * Created by Denis on 22-Apr-15
 * <File Description>
 */
@GenerateRepository(version = 1, name = "UserDatabase")
public interface UserRepository extends Repository {
    EntitySet<UserEntity> users();
    EntitySet<CountryEntity> countries();
}
