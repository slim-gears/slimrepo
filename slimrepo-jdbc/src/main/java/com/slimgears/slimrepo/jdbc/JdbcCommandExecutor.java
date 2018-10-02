// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.sqlite;

import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 10-Apr-15
 *
 */
public class JdbcCommandExecutor implements SqlCommandExecutor {
    private final Connection connection;
    private final FieldTypeMapper fieldTypeMapper;
    private final SqlStatementBuilder.SyntaxProvider syntaxProvider;


    class ResultSetAdapter<T> implements CloseableIterator<FieldValueLookup<T>> {
        private final Map<Field, Integer> fieldToIndexMap = new HashMap<>();
        private final Lookup lookup = new Lookup();
        private final ResultSet resultSet;

        ResultSetAdapter(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        @Override
        public void close() {
            JdbcHelper.execute(resultSet::close);
        }

        @Override
        public boolean hasNext() {
            return JdbcHelper.execute(resultSet::isAfterLast);
        }

        @Override
        public FieldValueLookup<T> next() {
            JdbcHelper.execute(resultSet::next);
            return lookup;
        }

        class Lookup implements FieldValueLookup<T> {
            @Override
            public <T1> T1 getValue(Field<T, T1> field) {
                int columnIndex = getColumnIndex(field);

                Class dbType = fieldTypeMapper.getInboundType(field);
                Object value = JdbcHelper.execute(() -> getValue(dbType, columnIndex));
                return fieldTypeMapper.toFieldType(field, value);
            }

            private Object getValue(Class type, int columnIndex) throws SQLException {
                if (type == Integer.class) return resultSet.getInt(columnIndex);
                if (type == String.class) return resultSet.getString(columnIndex);
                if (type == Float.class) return resultSet.getFloat(columnIndex);
                if (type == Short.class) return resultSet.getShort(columnIndex);
                if (type == Long.class) return resultSet.getLong(columnIndex);
                if (type == Double.class) return resultSet.getDouble(columnIndex);
                if (type == byte[].class) return resultSet.getBlob(columnIndex);
                if (type == FieldValueLookup.class) return this;

                throw new RuntimeException("Unsupported value type: " + type.getSimpleName());
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
    public void execute(String statement, String... params) throws Exception {
        JdbcHelper.prepareStatement(connection, statement, params).execute();
    }
}
