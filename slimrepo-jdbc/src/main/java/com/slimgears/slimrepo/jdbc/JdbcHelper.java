package com.slimgears.slimrepo.jdbc;

import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JdbcHelper {
    private final static Map<Class, ParamSetter<?>> paramSettersByClass = new HashMap<>();
    private final static Map<Integer, ColumnGetter<?>> columnGettersByType = new HashMap<>();

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
        registerType(Types.INTEGER, PreparedStatement::setInt, ResultSet::getInt, Integer.class, int.class);
        registerType(Types.BIGINT, PreparedStatement::setLong, ResultSet::getLong, Long.class, long.class);
        registerType(Types.DOUBLE, PreparedStatement::setDouble, ResultSet::getDouble, Double.class, double.class);
        registerType(Types.FLOAT, PreparedStatement::setFloat, ResultSet::getFloat, Float.class, float.class);
        registerType(Types.REAL, PreparedStatement::setFloat, ResultSet::getFloat);
        registerType(Types.SMALLINT, PreparedStatement::setShort, ResultSet::getShort, Short.class, short.class);
        registerType(Types.TINYINT, PreparedStatement::setByte, ResultSet::getByte, Byte.class, byte.class);
        registerType(Types.NVARCHAR, PreparedStatement::setString, ResultSet::getString, String.class);
        registerType(Types.VARCHAR, PreparedStatement::setString, ResultSet::getString);
        registerType(Types.NCHAR, PreparedStatement::setString, ResultSet::getString);
        registerType(Types.CHAR, PreparedStatement::setString, ResultSet::getString);
        registerType(Types.BLOB, PreparedStatement::setBytes, ResultSet::getBytes, byte[].class);
        registerType(Types.DATE, PreparedStatement::setDate, ResultSet::getDate, Date.class);
    }

    public static PreparedStatement prepareStatement(Connection connection, SqlCommand command) {
        return prepareStatement(() -> connection.prepareStatement(command.getStatement()), command.getParameters());
    }

    public static PreparedStatement prepareStatement(SqlCallable<PreparedStatement> supplier, List<SqlCommand.Parameter<?>> params) {
        try {
            PreparedStatement preparedStatement = supplier.call();
            setParams(preparedStatement, params);
            return preparedStatement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> void registerType(int type, ParamSetter<T> setter, ColumnGetter<T> getter, Class<T>... classes) {
        columnGettersByType.put(type, getter);

        Arrays.asList(classes).forEach(cls -> {
            paramSettersByClass.put(cls, setter);
        });
    }

    private static void setParams(PreparedStatement preparedStatement, List<SqlCommand.Parameter<?>> params) throws SQLException {
        int paramCount = params.size();
        for (int i = 0; i < paramCount; ++i) {
            SqlCommand.Parameter<?> param = handleNull(params.get(i));
            Class paramClass = param.type();
            //noinspection unchecked
            ParamSetter<Object> setter = Optional
                    .ofNullable(paramSettersByClass.get(paramClass))
                    .map(ParamSetter.class::cast)
                    .orElseThrow(() -> new RuntimeException("Type is not supported: " + paramClass));
            setter.setParam(preparedStatement, i + 1, param.value());
        }
    }

    private static SqlCommand.Parameter<?> handleNull(SqlCommand.Parameter<?> param) {
        return param.value() != null
                ? param
                : new SqlCommand.Parameter<String>() {
            @Override
            public Class<String> type() {
                return String.class;
            }

            @Override
            public String value() {
                return "NULL";
            }
        };
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
        return (T)Optional.ofNullable(columnGettersByType.get(columnType))
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
