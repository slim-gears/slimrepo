// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.query;

import java.io.IOException;

/**
 * Created by Denis on 13-Apr-15
 *
 */
public interface PreparedQuery<T> {
    T execute() throws IOException;
}
