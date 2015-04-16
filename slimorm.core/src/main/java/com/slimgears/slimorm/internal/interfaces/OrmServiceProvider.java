// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.interfaces;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public interface OrmServiceProvider {
    SessionServiceProvider createSessionServiceProvider(RepositoryModel model);
}
