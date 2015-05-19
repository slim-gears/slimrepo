package com.slimgears.slimrepo.android;

import com.google.common.base.Joiner;
import com.slimgears.slimrepo.android.core.SqliteOrmServiceProvider;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;
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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.any;

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
            return 0;
        }

        @Override
        public EntityType<?, ?>[] getEntityTypes() {
            return new EntityType<?, ?>[0];
        }
    };

    private static final SqlCommand.Parameters EMPTY_PARAMETERS = new SqlCommand.Parameters() {
        private final Map<String, String> map = new HashMap<>();
        private final String[] values = {};

        @Override
        public String add(String parameter) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Map<String, String> getMap() {
            return map;
        }

        @Override
        public String[] getValues() {
            return values;
        }
    };
    private SqliteOrmServiceProvider ormServiceProvider;

    static class ResourceCommand implements SqlCommand {
        private final String statement;

        ResourceCommand(String name) {
            try {
                statement = Joiner.on("\n").join(IOUtils.readLines(ResourceCommand.class.getResourceAsStream(name)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getStatement() {
            return statement;
        }

        @Override
        public Parameters getParameters() {
            return EMPTY_PARAMETERS;
        }
    }

    @Before
    public void initTest() {
        ormServiceProvider = new SqliteOrmServiceProvider(RuntimeEnvironment.application);
    }

    @Test
    public void fieldRemovalMigration() throws IOException {
        testMigration("field-removal-migration-db.sql", matchRepository(any(UserRepository.class)));
    }

    @Test
    public void fieldAdditionMigration() throws IOException {
        testMigration("field-addition-migration-db.sql", matchRepository(any(UserRepository.class)));
    }

    private UserRepositoryService createRepositoryService() {
        return new GeneratedUserRepositoryService(ormServiceProvider);
    }

    private void testMigration(String dbScriptName, Matcher<UserRepositoryService> repositoryServiceMatcher) throws IOException {
        createTestDb(dbScriptName);
        UserRepositoryService repositoryService = createRepositoryService();
        Assert.assertThat(repositoryService, repositoryServiceMatcher);
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
                try (UserRepository repo = repoService.open()) {
                    return matcher.matches(repo);
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

    private void createTestDb(String scriptName) throws IOException {
        SqlSessionServiceProvider sessionServiceProvider = (SqlSessionServiceProvider)ormServiceProvider.createSessionServiceProvider(EMPTY_MODEL);
        sessionServiceProvider.getExecutor().execute(new ResourceCommand(scriptName));
    }
}
