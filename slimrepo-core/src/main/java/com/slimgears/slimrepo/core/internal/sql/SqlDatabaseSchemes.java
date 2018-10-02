package com.slimgears.slimrepo.core.internal.sql;

import com.annimon.stream.Collectors;
import com.annimon.stream.Objects;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseSchemeDifference;
import com.slimgears.slimrepo.core.utilities.Maps;
import com.slimgears.slimrepo.core.utilities.Sets;

import java.util.Map;
import java.util.Set;

/**
 * Created by Denis on 22-May-15.
 *
 */
public class SqlDatabaseSchemes {
    private final static Predicate<SqlDatabaseSchemeDifference.TableSchemeDifference> PREDICATE_NOT_EMPTY_TABLE_DIFF = input ->
            !input.getAddedFields().isEmpty() ||
            !input.getDeletedFields().isEmpty() ||
            !input.getModifiedFields().isEmpty();

    static class DatabaseSchemeDifference implements SqlDatabaseSchemeDifference {
        private final SqlDatabaseScheme oldDatabaseScheme;
        private final SqlDatabaseScheme newDatabaseScheme;
        private final Map<String, SqlDatabaseScheme.TableScheme> addedTablesMap;
        private final Map<String, SqlDatabaseScheme.TableScheme> deletedTablesMap;
        private final Map<String, SqlDatabaseSchemeDifference.TableSchemeDifference> modifiedTablesMap;

        DatabaseSchemeDifference(
                SqlDatabaseScheme oldDatabase,
                SqlDatabaseScheme newDatabase) {
            this.oldDatabaseScheme = oldDatabase;
            this.newDatabaseScheme = newDatabase;

            Map<String, SqlDatabaseScheme.TableScheme> oldTables = oldDatabase.getTables();
            Map<String, SqlDatabaseScheme.TableScheme> newTables = newDatabase.getTables();

            Set<String> oldTableNames = oldTables.keySet();
            Set<String> newTableNames = newTables.keySet();

            Set<String> addedTableNames = Sets.difference(newTableNames, oldTableNames);
            Set<String> deletedTableNames = Sets.difference(oldTableNames, newTableNames);

            this.addedTablesMap = Maps.asMap(addedTableNames, newTables::get);
            this.deletedTablesMap = Maps.asMap(deletedTableNames, oldTables::get);

            Set<String> commonTableNames = Sets.intersection(newTableNames, oldTableNames);
            modifiedTablesMap = Stream
                    .of(commonTableNames)
                    .map(nameToTableDifference(oldTables, newTables))
                    .filter(PREDICATE_NOT_EMPTY_TABLE_DIFF)
                    .collect(Collectors.toMap(diff -> diff.getOldTableScheme().getName(), diff -> diff));
        }

        @Override
        public SqlDatabaseScheme getOldDatabaseScheme() {
            return oldDatabaseScheme;
        }

        @Override
        public SqlDatabaseScheme getNewDatabaseScheme() {
            return newDatabaseScheme;
        }

        @Override
        public Map<String, SqlDatabaseScheme.TableScheme> getAddedTables() {
            return addedTablesMap;
        }

        @Override
        public Map<String, SqlDatabaseScheme.TableScheme> getDeletedTables() {
            return deletedTablesMap;
        }

        @Override
        public Map<String, TableSchemeDifference> getModifiedTables() {
            return modifiedTablesMap;
        }

        @Override
        public boolean isEmpty() {
            return getAddedTables().isEmpty() && getDeletedTables().isEmpty() && getModifiedTables().isEmpty();
        }
    }

    static class TableSchemeDifference implements SqlDatabaseSchemeDifference.TableSchemeDifference {
        private final SqlDatabaseScheme.TableScheme oldTableScheme;
        private final SqlDatabaseScheme.TableScheme newTableScheme;
        private final Map<String, SqlDatabaseScheme.FieldScheme> addedFieldsMap;
        private final Map<String, SqlDatabaseScheme.FieldScheme> deletedFieldsMap;
        private final Map<String, SqlDatabaseScheme.FieldScheme> modifiedFieldsMap;

        TableSchemeDifference(SqlDatabaseScheme.TableScheme oldTable, SqlDatabaseScheme.TableScheme newTable) {
            this.oldTableScheme = oldTable;
            this.newTableScheme = newTable;

            Map<String, SqlDatabaseScheme.FieldScheme> oldFields = oldTable.getFields();
            Map<String, SqlDatabaseScheme.FieldScheme> newFields = newTable.getFields();

            Set<String> oldFieldNames = oldFields.keySet();
            Set<String> newFieldNames = newFields.keySet();

            Set<String> addedFieldNames = Sets.difference(newFieldNames, oldFieldNames);
            Set<String> deletedFieldNames = Sets.difference(oldFieldNames, newFieldNames);

            this.addedFieldsMap = Maps.asMap(addedFieldNames, newFields::get);
            this.deletedFieldsMap = Maps.asMap(deletedFieldNames, oldFields::get);

            Set<String> commonFieldNames = Sets.intersection(newFieldNames, oldFieldNames);
            this.modifiedFieldsMap = Maps.asMap(Sets.filter(commonFieldNames, differentFieldsPredicate(oldFields, newFields)), newFields::get);
        }

        @Override
        public SqlDatabaseScheme.TableScheme getNewTableScheme() {
            return newTableScheme;
        }

        @Override
        public SqlDatabaseScheme.TableScheme getOldTableScheme() {
            return oldTableScheme;
        }

        @Override
        public Map<String, SqlDatabaseScheme.FieldScheme> getAddedFields() {
            return addedFieldsMap;
        }

        @Override
        public Map<String, SqlDatabaseScheme.FieldScheme> getDeletedFields() {
            return deletedFieldsMap;
        }

        @Override
        public Map<String, SqlDatabaseScheme.FieldScheme> getModifiedFields() {
            return modifiedFieldsMap;
        }
    }

    public static SqlDatabaseSchemeDifference compareDatabases(SqlDatabaseScheme oldScheme, SqlDatabaseScheme newScheme) {
        return new DatabaseSchemeDifference(oldScheme, newScheme);
    }

    public static SqlDatabaseSchemeDifference.TableSchemeDifference compareTables(SqlDatabaseScheme.TableScheme oldTable, SqlDatabaseScheme.TableScheme newTable) {
        return new TableSchemeDifference(oldTable, newTable);
    }

    public static boolean fieldsEqual(SqlDatabaseScheme.FieldScheme oldField, SqlDatabaseScheme.FieldScheme newField) {
        if (oldField == null || newField == null) return oldField == newField;

        return
                Objects.equals(oldField.getType(), newField.getType()) &&
                oldField.isNotNull() == newField.isNotNull() &&
                oldField.isForeignKey() == newField.isForeignKey() &&
                oldField.isPrimaryKey() == newField.isPrimaryKey() &&
                oldField.isAutoIncremented() == newField.isAutoIncremented() &&
                foreignFieldsEqual(oldField, newField);
    }

    private static boolean foreignFieldsEqual(SqlDatabaseScheme.FieldScheme oldField, SqlDatabaseScheme.FieldScheme newField) {
        if (!oldField.isForeignKey() || !newField.isForeignKey()) {
            return oldField.isForeignKey() == newField.isForeignKey();
        }

        SqlDatabaseScheme.FieldScheme oldRelatedField = oldField.getRelatedForeignField();
        SqlDatabaseScheme.FieldScheme newRelatedField = newField.getRelatedForeignField();

        return
                Objects.equals(oldRelatedField.getTable().getName(), newRelatedField.getTable().getName()) &&
                fieldsEqual(oldRelatedField, newRelatedField);
    }

    private static Predicate<String> differentFieldsPredicate(final Map<String, SqlDatabaseScheme.FieldScheme> oldFields, final Map<String, SqlDatabaseScheme.FieldScheme> newFields) {
        return name -> !fieldsEqual(oldFields.get(name), newFields.get(name));
    }

    private static Function<String, SqlDatabaseSchemeDifference.TableSchemeDifference> nameToTableDifference(final Map<String, SqlDatabaseScheme.TableScheme> oldTables, final Map<String, SqlDatabaseScheme.TableScheme> newTables) {
        return name -> compareTables(oldTables.get(name), newTables.get(name));
    }
}
