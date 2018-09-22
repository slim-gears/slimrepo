// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.example.repository;

import com.slimgears.slimrepo.android.core.SqliteOrmServiceProvider;
import com.slimgears.slimrepo.core.annotations.GenerateRepository;
import com.slimgears.slimrepo.core.annotations.OrmProvider;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.example.BuildConfig;

/**
 * Created by Denis on 22-Apr-15
 *
 */
@GenerateRepository(version = BuildConfig.VERSION_CODE, name = "UserDatabase")
@OrmProvider(SqliteOrmServiceProvider.class)
public interface UserRepository extends Repository {
    EntitySet<UserEntity> users();
    EntitySet<CountryEntity> countries();
}
