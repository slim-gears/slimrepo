package com.slimgears.slimrepo.jdbc;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JdbcHelper {
    private final static Map<Integer, ParamSetter<String>> paramSetters = new HashMap<>();
    private final static Map<Integer, ColumnGetter<?>> columnGetters = new HashMap<>();

    interface ParamSetter<T> {
        void setParam(PreparedStatement preparedStatement, int index, T val) throws SQLException;
    }

    interface ColumnGetter<T> {
        T getValue(ResultSet resultSet, int columnIndex) throws SQLException;
    }

    public interface SqlFunction<T, R> {
        R apply(T arg) throws SQLException;
    }

    public interface SqlCallable<T> {
        T call() throws SQLException;
    }

    public interface SqlRunnable {
        void run() throws SQLException;
    }

    static {
        registerType(Types.INTEGER, Integer::valueOf, PreparedStatement::setInt, ResultSet::getInt);
        registerType(Types.BIGINT, Long::valueOf, PreparedStatement::setLong, ResultSet::getLong);
        registerType(Types.DOUBLE , Double::valueOf, PreparedStatement::setDouble, ResultSet::getDouble);
        registerType(Types.FLOAT, Float::valueOf, PreparedStatement::setFloat, ResultSet::getFloat);
        registerType(Types.REAL, Float::valueOf, PreparedStatement::setFloat, ResultSet::getFloat);
        registerType(Types.SMALLINT, Short::valueOf, PreparedStatement::setShort, ResultSet::getShort);
        registerType(Types.TINYINT, Byte::valueOf, PreparedStatement::setByte, ResultSet::getByte);
        registerType(Types.NVARCHAR, String::valueOf, PreparedStatement::setString, ResultSet::getString);
        registerType(Types.VARCHAR, String::valueOf, PreparedStatement::setString, ResultSet::getString);
        registerType(Types.NCHAR, String::valueOf, PreparedStatement::setString, ResultSet::getString);
        registerType(Types.CHAR, String::valueOf, PreparedStatement::setString, ResultSet::getString);
    }

    public static PreparedStatement prepareStatement(Connection connection, String statement, String... params) {
        return prepareStatement(() -> connection.prepareStatement(statement), params);
    }

    public static PreparedStatement prepareStatement(SqlCallable<PreparedStatement> supplier, String... params) {
        try {
            PreparedStatement preparedStatement = supplier.call();
            setParams(preparedStatement, params);
            return preparedStatement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> void registerType(int type, Function<String, T> converter, ParamSetter<T> setter, ColumnGetter<T> getter) {
        paramSetters.put(type, (stat, index, str) -> setter.setParam(stat, index, converter.apply(str)));
        columnGetters.put(type, getter);
    }

    private static void setParams(PreparedStatement preparedStatement, String... params) throws SQLException {
        ParameterMetaData metaData = preparedStatement.getParameterMetaData();
        int paramCount = metaData.getParameterCount();
        if (paramCount != params.length) {
            throw new IllegalArgumentException("Param count mismatch");
        }
        for (int i = 0; i < paramCount; ++i) {
            String param = params[i];
            int paramType = metaData.getParameterType(i);
            ParamSetter<String> setter = Optional
                    .ofNullable(paramSetters.get(paramType))
                    .orElseThrow(() -> new RuntimeException("Type is not supported: " + paramType));
            setter.setParam(preparedStatement, i + 1, param);
        }
    }

    public static void execute(SqlRunnable runnable) {
        JdbcHelper.<Void>execute(() -> { runnable.run(); return null; });
    }

    public static <T> T execute(SqlCallable<T> callable) {
        try {
            return callable.call();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<ResultSet> toStream(ResultSet resultSet) {
        return StreamSupport.stream(new ResultSetSpliterator(resultSet), false);
    }

    public static Iterator<ResultSet> toIterator(ResultSet resultSet) {
        return Spliterators.iterator(new ResultSetSpliterator(resultSet));
    }

    static class ResultSetSpliterator extends Spliterators.AbstractSpliterator<ResultSet> {
        private final ResultSet resultSet;

        protected ResultSetSpliterator(ResultSet resultSet) {
            super(Long.MAX_VALUE, 0);
            this.resultSet = resultSet;
        }

        @Override
        public boolean tryAdvance(Consumer<? super ResultSet> action) {
            try {
                if (resultSet.next()) {
                    action.accept(resultSet);
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static <T> T getColumnValue(ResultSet resultSet, int columnType, int columnIndex) throws SQLException {
        //noinspection unchecked
        return (T)Optional.ofNullable(columnGetters.get(columnType))
                .orElseThrow(() -> new RuntimeException("Not supported type: " + columnType))
                .getValue(resultSet, columnIndex);
    }

    public static <T> Supplier<T> fromCallable(SqlCallable<T> callable) {
        return () -> execute(callable);
    }

    public static Runnable fromRunnable(SqlRunnable runnable) {
        return () -> execute(runnable);
    }

    public static <T, R> Function<T, R> fromFunction(SqlFunction<T, R> func) {
        return arg -> execute(() -> func.apply(arg));
    }
}
