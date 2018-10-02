// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

/**
 * Created by Denis on 14-Apr-15
 *
 */
public interface TransactionProvider {
    void beginTransaction() throws Exception;
    void commitTransaction() throws Exception;
    void cancelTransaction() throws Exception;
}
