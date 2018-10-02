// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.jdbc;

import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Denis on 15-Apr-15
 *
 */
public class JdbcTransactionProvider implements TransactionProvider {
    private final Connection connection;

    public JdbcTransactionProvider(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void beginTransaction() throws Exception {
        connection.setAutoCommit(false);
    }

    @Override
    public void cancelTransaction() throws Exception {
        try {
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void commitTransaction() throws Exception {
        try {
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
        } finally {
            connection.setAutoCommit(false);
        }
    }
}
