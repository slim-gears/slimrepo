package com.slimgears.slimrepo.android;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.slimgears.slimrepo.android.core.SqliteOrmServiceProvider;
import com.slimgears.slimrepo.android.core.SqliteSchemeProvider;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.prototype.UserRepository;
import com.slimgears.slimrepo.core.prototype.generated.GeneratedUserRepository;
import com.slimgears.slimrepo.core.prototype.generated.GeneratedUserRepositoryService;
import com.slimgears.slimrepo.core.prototype.generated.UserRepositoryService;

import org.apache.commons.io.IOUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Denis on 18-May-15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18, manifest=Config.NONE)
public class MigrationsTest {
    private static final RepositoryModel EMPTY_MODEL = new RepositoryModel() {
        @Override
        public String getName() {
            return GeneratedUserRepository.Model.Instance.getName();
        }

        @Override
        public int getVersion() {
            return 1;
        }

        @Override
        public EntityType<?, ?>[] getEntityTypes() {
            return new EntityType<?, ?>[0];
        }
    };

    private SqliteOrmServiceProvider ormServiceProvider;

    @Before
    public void initTest() {
        ormServiceProvider = new SqliteOrmServiceProvider(RuntimeEnvironment.application);
    }

    @Test
    public void fieldRemovalMigration() throws IOException {
        testMigration("field-removal-migration-db.sql");
    }

    @Test
    public void fieldAdditionMigration() throws IOException {
        testMigration("field-addition-migration-db.sql");
    }

    @Test
    public void schemeProviderCorrectScheme() throws IOException {
        testSchemeProvider("field-removal-migration-db.sql",
                matchTableNames("UserEntity", "RoleEntity"),
                matchTableFieldNames(
                    "UserEntity",
                    "userId",
                    "userFirstName",
                    "userLastName",
                    "lastVisitDate",
                    "role",
                    "accountStatus",
                    "comments"),
                matchTableFieldNames(
                        "RoleEntity",
                        "roleId",
                        "roleDescription"));
    }

    private UserRepositoryService createRepositoryService() {
        return new GeneratedUserRepositoryService(ormServiceProvider);
    }

    @SafeVarargs
    private final void testMigration(String dbScriptName, Matcher<SqlDatabaseScheme>... sqlDatabaseSchemeMatchers) throws IOException {
        createTestDatabase(dbScriptName).close();
        UserRepositoryService repositoryService = createRepositoryService();
        repositoryService.open().close();
    }

    @SafeVarargs
    private final void testSchemeProvider(final String dbScriptName, Matcher<SqlDatabaseScheme>... sqlDatabaseSchemeMatchers) throws IOException {
        SQLiteDatabase database = createTestDatabase(dbScriptName);

        try {
            assertScheme(database, sqlDatabaseSchemeMatchers);
        } finally {
            database.close();
        }
    }

    private final void assertScheme(SQLiteDatabase database, Matcher<SqlDatabaseScheme>... sqlDatabaseSchemeMatchers) {
        SqliteSchemeProvider schemeProvider = new SqliteSchemeProvider(ormServiceProvider.getSyntaxProvider(), database);
        SqlDatabaseScheme scheme = schemeProvider.getDatabaseScheme();

        for (Matcher<SqlDatabaseScheme> matcher : sqlDatabaseSchemeMatchers) {
            Assert.assertThat(scheme, matcher);
        }
    }

    private SQLiteDatabase createTestDatabase(final String dbScriptName) {
        SQLiteOpenHelper helper = new SQLiteOpenHelper(RuntimeEnvironment.application, EMPTY_MODEL.getName(), null, EMPTY_MODEL.getVersion()) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                try {
                    String script = loadScriptFromResource(dbScriptName);
                    String[] statements = script.split(";");
                    for (String sql : statements) {
                        db.execSQL(sql);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                throw new RuntimeException("Unexpected upgrade");
            }
        };

        SQLiteDatabase database = helper.getReadableDatabase();
        return database;
    }

    private Matcher<SqlDatabaseScheme> matchTableFieldNames(final String tableName, final String... fieldNames) {
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

    private Matcher<SqlDatabaseScheme> matchTableNames(final String... tableNames) {
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

    private Matcher<UserRepositoryService> matchModel(EntityType<?, ?> entityType) {
        return new BaseMatcher<UserRepositoryService>() {
            @Override
            public boolean matches(Object item) {
                return false;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    private Matcher<UserRepositoryService> matchRepository(final Matcher<UserRepository> matcher) {
        return new BaseMatcher<UserRepositoryService>() {
            @Override
            public boolean matches(Object item) {
                UserRepositoryService repoService = (UserRepositoryService)item;
                UserRepository repo = repoService.open();
                try {
                    try {
                        return matcher.matches(repo);
                    } finally {
                        repo.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void describeTo(Description description) {
                matcher.describeTo(description);
            }
        };
    }

    private String loadScriptFromResource(String scriptName) throws IOException {
        InputStream stream = ClassLoader.getSystemResourceAsStream(scriptName);
        try {
            return Joiner.on("\n").join(IOUtils.readLines(stream));
        } finally {
            stream.close();
        }
    }
}
