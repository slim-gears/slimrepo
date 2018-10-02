package com.slimgears.slimrepo.android;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.slimgears.slimrepo.android.core.SqliteOrmServiceProvider;
import com.slimgears.slimrepo.android.core.SqliteSchemeProvider;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.prototype.UserRepository;
import com.slimgears.slimrepo.core.prototype.generated.*;
import com.slimgears.slimrepo.core.utilities.Joiner;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.IOException;
import java.io.InputStream;

import static com.slimgears.slimrepo.android.RepositoryMatchers.*;
import static com.slimgears.slimrepo.android.SchemeMatchers.matchTableFieldNames;
import static com.slimgears.slimrepo.android.SchemeMatchers.matchTableNames;

/**
 * Created by Denis on 18-May-15.
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, manifest=Config.NONE)
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
//        ShadowLog.setupLogging();
        ShadowLog.stream = System.out;
//        ShadowLog.setLoggable(SqliteOrmServiceProvider.class.getSimpleName(), Log.INFO);
        ormServiceProvider = new SqliteOrmServiceProvider(RuntimeEnvironment.application);
    }

    @Test
    public void fieldRemovalMigration() throws IOException {
        testMigration("field-removal-migration-db.sql",
                countEquals(UserEntity.EntityMetaType, 8),
                countEquals(RoleEntity.EntityMetaType, 5),
                isEmpty(UserEntity.EntityMetaType, UserEntity.UserFirstName.isNull()),
                isEmpty(UserEntity.EntityMetaType, UserEntity.UserLastName.isNull()),
                isEmpty(UserEntity.EntityMetaType, UserEntity.LastVisitDate.isNull()),
                isEmpty(UserEntity.EntityMetaType, UserEntity.Role.isNull()),
                isEmpty(UserEntity.EntityMetaType, UserEntity.AccountStatus.isNull()));
    }

    @Test
    public void fieldAdditionMigration() throws IOException {
        testMigration("field-addition-migration-db.sql",
                countEquals(UserEntity.EntityMetaType, 8),
                countEquals(RoleEntity.EntityMetaType, 4),
                isEmpty(UserEntity.EntityMetaType, UserEntity.UserFirstName.isNull()),
                isEmpty(UserEntity.EntityMetaType, UserEntity.UserLastName.isNotNull()),
                isEmpty(UserEntity.EntityMetaType, UserEntity.LastVisitDate.isNull()),
                isEmpty(UserEntity.EntityMetaType, UserEntity.Role.isNull()),
                isEmpty(UserEntity.EntityMetaType, UserEntity.AccountStatus.isNull()));
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

    @Test
    public void addingNonNullableFieldMigration() throws IOException {
        testMigration("non-nullable-field-addition-migration-db.sql");
    }

    private UserRepositoryService createRepositoryService() {
        return new GeneratedUserRepositoryService(ormServiceProvider);
    }

    @SafeVarargs
    private final void testMigration(String dbScriptName, Matcher<Repository>... matchers) throws IOException {
        createTestDatabase(dbScriptName).close();
        UserRepositoryService repositoryService = createRepositoryService();
        UserRepository repository = repositoryService.open();
        try {
            Assert.assertThat(repository, all(matchers));
        } finally {
            repository.close();
        }
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

    @SafeVarargs
    private final void assertScheme(SQLiteDatabase database, Matcher<SqlDatabaseScheme>... sqlDatabaseSchemeMatchers) {
        SqliteSchemeProvider schemeProvider = new SqliteSchemeProvider(ormServiceProvider.getSyntaxProvider(), database);
        SqlDatabaseScheme scheme = schemeProvider.getDatabaseScheme();
        Assert.assertThat(scheme, all(sqlDatabaseSchemeMatchers));
    }

    private SQLiteDatabase createTestDatabase(final String dbScriptName) {
        SQLiteOpenHelper helper = new SQLiteOpenHelper(RuntimeEnvironment.application, EMPTY_MODEL.getName() + ".db", null, EMPTY_MODEL.getVersion()) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                try {
                    System.out.println("Creating database: " + db.getPath());
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

        return helper.getReadableDatabase();
    }


    @SuppressLint("NewApi")
    private String loadScriptFromResource(String scriptName) throws IOException {
        try (InputStream stream = ClassLoader.getSystemResourceAsStream(scriptName)) {
            return Joiner.on("\n").join(IOUtils.readLines(stream));
        }
    }
}
