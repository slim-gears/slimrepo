package com.slimgears.slimrepo.sqlite;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JdbcHelper {
    private final static Map<Integer, ParamSetter<String>> paramSetters = new HashMap<>();

    interface ParamSetter<T> {
        void setParam(PreparedStatement preparedStatement, int index, T val) throws SQLException;
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
        registerSetter(Types.INTEGER, Integer::valueOf, PreparedStatement::setInt);
        registerSetter(Types.BIGINT, Long::valueOf, PreparedStatement::setLong);
        registerSetter(Types.DOUBLE , Double::valueOf, PreparedStatement::setDouble);
        registerSetter(Types.FLOAT, Float::valueOf, PreparedStatement::setFloat);
        registerSetter(Types.REAL, Float::valueOf, PreparedStatement::setFloat);
        registerSetter(Types.SMALLINT, Short::valueOf, PreparedStatement::setShort);
        registerSetter(Types.TINYINT, Byte::valueOf, PreparedStatement::setByte);
        registerSetter(Types.NVARCHAR, String::valueOf, PreparedStatement::setString);
        registerSetter(Types.VARCHAR, String::valueOf, PreparedStatement::setString);
        registerSetter(Types.NCHAR, String::valueOf, PreparedStatement::setString);
        registerSetter(Types.CHAR, String::valueOf, PreparedStatement::setString);
    }

    public static PreparedStatement prepareStatement(Connection connection, String statement, String... params) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            setParams(preparedStatement, params);
            return preparedStatement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> void registerSetter(int type, Function<String, T> converter, ParamSetter<T> setter) {
        paramSetters.put(type, (stat, index, str) -> setter.setParam(stat, index, converter.apply(str)));
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
            setter.setParam(preparedStatement, i, param);
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
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new ResultSetIterator(resultSet), 0), false);
    }

    static class ResultSetIterator implements Iterator<ResultSet> {
        private final ResultSet resultSet;

        ResultSetIterator(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        @Override
        public boolean hasNext() {
            return execute(() -> !resultSet.isAfterLast());
        }

        @Override
        public ResultSet next() {
            execute(resultSet::next);
            return resultSet;
        }
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
