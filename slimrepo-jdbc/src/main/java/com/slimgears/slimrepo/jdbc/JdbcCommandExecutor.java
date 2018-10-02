// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.jdbc;

import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterators;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Denis on 10-Apr-15
 *
 */
public class JdbcCommandExecutor implements SqlCommandExecutor {
    private final static Logger log = Logger.getLogger(JdbcCommandExecutor.class.toString());
    private final Connection connection;
    private final FieldTypeMapper fieldTypeMapper;
    private final SqlStatementBuilder.SyntaxProvider syntaxProvider;

    class ResultSetAdapter<T> implements CloseableIterator<FieldValueLookup<T>> {
        private final Map<Field, Integer> fieldToIndexMap = new HashMap<>();
        private final Map<Field, Integer> fieldToTypeMap = new HashMap<>();
        private final Lookup lookup = new Lookup();
        private final ResultSet resultSet;
        private final Iterator<ResultSet> resultSetIterator;

        ResultSetAdapter(ResultSet resultSet) {
            this.resultSet = resultSet;
            this.resultSetIterator = JdbcHelper.toIterator(resultSet);
        }

        @Override
        public void close() {
            JdbcHelper.execute(resultSet::close);
        }

        @Override
        public boolean hasNext() {
            return resultSetIterator.hasNext();
        }

        @Override
        public FieldValueLookup<T> next() {
            resultSetIterator.next();
            return lookup;
        }

        class Lookup implements FieldValueLookup<T> {
            @Override
            public <T1> T1 getValue(Field<T, T1> field) {
                int columnIndex = getColumnIndex(field);
                int columnType = fieldToTypeMap.computeIfAbsent(field, f -> {
                    try {
                        return resultSet.getMetaData().getColumnType(columnIndex);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                Class dbType = fieldTypeMapper.getInboundType(field);
                Object value = JdbcHelper.execute(() -> getValue(dbType, columnType, columnIndex));
                return fieldTypeMapper.toFieldType(field, value);
            }

            private Object getValue(Class type, int columnType, int columnIndex) throws SQLException {
                if (type == FieldValueLookup.class) return this;
                Object value = JdbcHelper.getColumnValue(resultSet, columnType, columnIndex);
                return "NULL".equals(value) ? null : value;
            }
        }

        private int getColumnIndex(Field field) {
            Integer index = fieldToIndexMap.get(field);
            if (index != null) return index;

            String columnName = syntaxProvider.rawFieldAlias(field);
            try {
                index = resultSet.findColumn(columnName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            fieldToIndexMap.put(field, index);
            return index;
        }
    }

    public JdbcCommandExecutor(Connection connection, SqlSessionServiceProvider sessionServiceProvider) {
        this.connection = connection;
        SqlOrmServiceProvider serviceProvider = sessionServiceProvider.getOrmServiceProvider();
        syntaxProvider = serviceProvider.getSyntaxProvider();
        fieldTypeMapper = serviceProvider.getFieldTypeMapper();
    }

    @Override
    public long count(String statement, String... params) throws Exception {
        PreparedStatement preparedStatement = JdbcHelper.prepareStatement(connection, statement, params);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    @Override
    public <T> CloseableIterator<FieldValueLookup<T>> select(final String statement, final String... params) throws Exception {
        return new ResultSetAdapter<>(JdbcHelper.prepareStatement(connection, statement, params).executeQuery());
    }

    @Override
    public <K> CloseableIterator<K> insert(String statement, String... parameters) throws Exception {
        PreparedStatement preparedStatement = JdbcHelper.prepareStatement(() -> connection.prepareStatement(statement, new int[]{1}), parameters);
        preparedStatement.executeUpdate();
        log.log(Level.INFO, "Executing: " + statement + "\n" + Stream.of(parameters).collect(Collectors.joining(", ")));
        return CloseableIterators.fromIterator(JdbcHelper
                .toStream(preparedStatement.getGeneratedKeys())
                .map(JdbcHelper.fromFunction(rs -> JdbcHelper.<K>getColumnValue(rs, rs.getMetaData().getColumnType(1), 1)))
                .iterator());
    }

    @Override
    public void execute(String statement, String... params) throws Exception {
        log.log(Level.INFO, "Executing: " + statement + "\n" + Stream.of(params).collect(Collectors.joining(", ")));
        JdbcHelper.prepareStatement(connection, statement, params).execute();
    }
}
