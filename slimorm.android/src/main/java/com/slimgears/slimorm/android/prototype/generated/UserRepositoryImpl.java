// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype.generated;

import com.slimgears.slimorm.android.prototype.UserRepositorySession;
import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.internal.AbstractRepository;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class UserRepositoryImpl extends AbstractRepository<UserRepositorySession> implements Repository<UserRepositorySession> {
    @Override
    public UserRepositorySession open() {
        return new UserRepositorySessionImpl(this);
    }
}
