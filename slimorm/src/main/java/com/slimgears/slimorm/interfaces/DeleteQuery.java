// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces;

import java.io.IOException;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public interface DeleteQuery<TEntity> {
    DeleteQuery<TEntity> where(Predicate<TEntity> predicate);
    void execute() throws IOException;
}
