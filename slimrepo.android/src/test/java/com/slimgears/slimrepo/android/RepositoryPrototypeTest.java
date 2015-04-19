package com.slimgears.slimrepo.android;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.slimgears.slimrepo.android.core.SqliteOrmServiceProvider;
import com.slimgears.slimrepo.android.prototype.UserRepositorySession;
import com.slimgears.slimrepo.android.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.android.prototype.generated.UserEntity;
import com.slimgears.slimrepo.android.prototype.generated.UserRepositoryImpl;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.conditions.Conditions;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Arrays;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18, manifest=Config.NONE)
public class RepositoryPrototypeTest  {
    private Repository<UserRepositorySession> repository;

    @Before
    public void setup() {
        OrmServiceProvider orm = new SqliteOrmServiceProvider(RuntimeEnvironment.application);
        repository = new UserRepositoryImpl(orm);
    }

    @Test
    public void addEntitiesThenQuery() throws IOException {
        repository.update(new Repository.UpdateAction<UserRepositorySession>() {
            @Override
            public void execute(UserRepositorySession session) throws IOException {
                UserEntity userJohnDoe = session.users().addNew()
                        .setUserFirstName("John")
                        .setUserLastName("Doe");
                UserEntity userJakeSmith = session.users().addNew()
                        .setUserFirstName("Jake")
                        .setUserLastName("Smith");
                RoleEntity roleUser = session.roles().addNew()
                        .setRoleDescription("User");
                RoleEntity roleAdmin = session.roles().addNew()
                        .setRoleDescription("Administrator");

                session.saveChanges();

                Assert.assertEquals(1, userJohnDoe.getUserId());
                Assert.assertEquals(2, userJakeSmith.getUserId());

                Assert.assertEquals(1, roleUser.getRoleId());
                Assert.assertEquals(2, roleAdmin.getRoleId());
            }
        });

        repository.query(new Repository.QueryAction<UserRepositorySession, Object>() {
            @Override
            public Object execute(UserRepositorySession session) throws IOException {
                UserEntity[] allUsers = session.users().query().prepare().toArray();

                Assert.assertNotNull(allUsers);
                Assert.assertEquals(2, allUsers.length);
                Assert.assertEquals("John", allUsers[0].getUserFirstName());
                Assert.assertEquals("Smith", allUsers[1].getUserLastName());

                RoleEntity[] allRoles = session.roles().query().prepare().toArray();

                Assert.assertNotNull(allRoles);
                Assert.assertEquals(2, allRoles.length);
                Assert.assertEquals("User", allRoles[0].getRoleDescription());
                Assert.assertEquals("Administrator", allRoles[1].getRoleDescription());
                return allUsers;
            }
        });
    }

    @Test
    public void queryWithWhere() throws IOException {
        addUsers(
                UserEntity.create().userFirstName("John").userLastName("Doe").build(),
                UserEntity.create().userFirstName("Jake").userLastName("Smith").build(),
                UserEntity.create().userFirstName("Bill").userLastName("Doors").build(),
                UserEntity.create().userFirstName("Bred").userLastName("Beat").build());

        Assert.assertEquals(4, queryUsersCountWhere(null));

        UserEntity[] users = queryUsersWhere(UserEntity.UserFirstName.startsWith("J"));
        Assert.assertEquals(2, users.length);
        Assert.assertEquals("John", users[0].getUserFirstName());
        Assert.assertEquals("Smith", users[1].getUserLastName());

        users = queryUsersWhere(UserEntity.UserLastName.contains("e"));
        Assert.assertEquals(2, users.length);
        Assert.assertEquals("John", users[0].getUserFirstName());
        Assert.assertEquals("Beat", users[1].getUserLastName());

        long count = queryUsersCountWhere(
                Conditions.and(
                        UserEntity.UserFirstName.startsWith("J"),
                        UserEntity.UserLastName.contains("t")));

        Assert.assertEquals(1, count);
    }

    private void addUsers(final UserEntity... users) throws IOException {
        repository.update(new Repository.UpdateAction<UserRepositorySession>() {
            @Override
            public void execute(UserRepositorySession session) throws IOException {
                session.users().add(Arrays.asList(users));
            }
        });
    }

    private void addRoles(final RoleEntity... roles) throws IOException {
        repository.update(new Repository.UpdateAction<UserRepositorySession>() {
            @Override
            public void execute(UserRepositorySession session) throws IOException {
                session.roles().add(Arrays.asList(roles));
            }
        });
    }

    private long queryUsersCountWhere(final Condition<UserEntity> condition) throws IOException {
        return repository.query(new Repository.QueryAction<UserRepositorySession, Long>() {
            @Override
            public Long execute(UserRepositorySession session) throws IOException {
                return session.users()
                        .query()
                        .where(condition)
                        .prepare()
                        .count();
            }
        });
    }

    private UserEntity[] queryUsersWhere(final Condition<UserEntity> condition) throws IOException {
        return repository.query(new Repository.QueryAction<UserRepositorySession, UserEntity[]>() {
            @Override
            public UserEntity[] execute(UserRepositorySession session) throws IOException {
                return session.users()
                        .query()
                        .where(condition)
                        .prepare()
                        .toArray();
            }
        });
    }

    private void assertTableExists(SQLiteDatabase db, String tableName) {
        long tables = DatabaseUtils.longForQuery(db, "SELECT count(*) FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        Assert.assertEquals(1, tables);
    }
}