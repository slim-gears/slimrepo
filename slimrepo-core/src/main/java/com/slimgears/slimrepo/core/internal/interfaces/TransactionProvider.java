// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import java.io.IOException;

/**
 * Created by Denis on 14-Apr-15
 *
 */
public interface TransactionProvider {
    void beginTransaction() throws IOException;
    void commitTransaction() throws IOException;
    void cancelTransaction() throws IOException;
}
