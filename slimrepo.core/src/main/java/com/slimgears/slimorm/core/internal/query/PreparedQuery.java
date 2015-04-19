// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.query;

import java.io.IOException;

/**
 * Created by Denis on 13-Apr-15
 * <File Description>
 */
public interface PreparedQuery<T> {
    T execute() throws IOException;
}
