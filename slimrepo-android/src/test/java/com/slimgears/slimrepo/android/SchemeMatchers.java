package com.slimgears.slimrepo.android;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Denis on 02-Jun-15.
 */
public class SchemeMatchers {
    public static Matcher<SqlDatabaseScheme> matchTableFieldNames(final String tableName, final String... fieldNames) {
        return new BaseMatcher<SqlDatabaseScheme>() {
            private Collection<String> actualFieldNames;
            private Collection<String> expectedFieldNames;

            @Override
            public boolean matches(Object item) {
                SqlDatabaseScheme scheme = (SqlDatabaseScheme)item;
                SqlDatabaseScheme.TableScheme table = scheme.getTable(tableName);
                if (table == null) {
                    return false;
                }
                actualFieldNames = table.getFields().keySet();
                expectedFieldNames = Arrays.asList(fieldNames);
                return Iterables.elementsEqual(actualFieldNames, expectedFieldNames);
            }

            @Override
            public void describeTo(Description description) {
                if (actualFieldNames == null) {
                    description.appendText(String.format("table '%s' not found", tableName));
                    return;
                }
                description.appendValueList("fields [", ", ", "]\n", expectedFieldNames);
                description.appendValueList("Actual: fields [", ", ", "]\n", actualFieldNames);
            }
        };
    }

    public static Matcher<SqlDatabaseScheme> matchTableNames(final String... tableNames) {
        return new BaseMatcher<SqlDatabaseScheme>() {
            private Set<String> notExistingTables;
            private Set<String> notExpectedTables;

            @Override
            public boolean matches(Object item) {
                Set<String> actualTableNames = ((SqlDatabaseScheme)item).getTables().keySet();
                Set<String> expectedTableNames = new HashSet<>(Arrays.asList(tableNames));
                notExistingTables = Sets.difference(expectedTableNames, actualTableNames);
                notExpectedTables = Sets.difference(actualTableNames, expectedTableNames);

                return notExpectedTables.isEmpty() && notExistingTables.isEmpty();
            }

            @Override
            public void describeTo(Description description) {
                description.appendValueList("Tables [", ", ", "]\n", Arrays.asList(tableNames));
                description.appendText("Actual:\n");

                if (!notExistingTables.isEmpty()) {
                    description.appendValueList("Tables [", ", ", "] were not found\n", notExistingTables);
                }

                if (!notExpectedTables.isEmpty()) {
                    description.appendValueList("Tables [", ", ", "] were not expected\n", notExpectedTables);
                }
            }
        };
    }
}
