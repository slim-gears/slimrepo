package com.slimgears.slimrepo.jdbc;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.conditions.Conditions;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.prototype.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import static com.slimgears.slimrepo.core.utilities.Dates.addDays;
import static com.slimgears.slimrepo.core.utilities.Dates.fromDate;

public class JdbcOrmServiceProviderTest {
    private UserRepositoryService repositoryService;
    private Path dbPath;

    @Before
    public void setUp() throws IOException {
        new File("tmp").mkdirs();
        dbPath = getTempPath(Paths.get("tmp"), "temp-", ".db");
        String url = "jdbc:sqlite:" + dbPath;
        OrmServiceProvider orm = new JdbcOrmServiceProvider(url);
        repositoryService = new GeneratedUserRepositoryService(orm);
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(dbPath);
    }

    @Test
    public void addEntitiesThenQuery() throws Exception {
        repositoryService.update(repository -> {
            UserEntity userJohnDoe = repository.users().add(UserEntity.builder()
                    .userId("id-1")
                    .userFirstName("John")
                    .userLastName("Doe")
                    .build());
            UserEntity userJakeSmith = repository.users().add(UserEntity.builder()
                    .userId("id-2")
                    .userFirstName("Jake")
                    .userLastName("Smith")
                    .build());
            RoleEntity roleUser = repository.roles().add(RoleEntity.builder()
                    .roleDescription("User")
                    .build());
            RoleEntity roleAdmin = repository.roles().add(RoleEntity.builder()
                    .roleDescription("Administrator")
                    .build());

            repository.saveChanges();

            Assert.assertEquals("id-1", userJohnDoe.getUserId());
            Assert.assertEquals("id-2", userJakeSmith.getUserId());

            Assert.assertEquals(1, roleUser.getRoleId());
            Assert.assertEquals(2, roleAdmin.getRoleId());
        });

        repositoryService.query(repository -> {
            long userCount = repository.users().query().prepare().count();
            Assert.assertEquals(2L, userCount);

            UserEntity[] allUsers = repository.users().query().prepare().toArray();

            Assert.assertNotNull(allUsers);
            Assert.assertEquals(2, allUsers.length);
            Assert.assertEquals("John", allUsers[0].getUserFirstName());
            Assert.assertEquals("Smith", allUsers[1].getUserLastName());

            RoleEntity[] allRoles = repository.roles().query().prepare().toArray();

            Assert.assertNotNull(allRoles);
            Assert.assertEquals(2, allRoles.length);
            Assert.assertEquals("User", allRoles[0].getRoleDescription());
            Assert.assertEquals("Administrator", allRoles[1].getRoleDescription());
            return allUsers;
        });
    }

    @Test
    public void queryWithWhere() throws Exception {
        repositoryService.users().add(
                UserEntity.builder().userId("id-1").userFirstName("John").userLastName("Doe").build(),
                UserEntity.builder().userId("id-2").userFirstName("Jake").userLastName("Smith").build(),
                UserEntity.builder().userId("id-3").userFirstName("Bill").userLastName("Doors").build(),
                UserEntity.builder().userId("id-4").userFirstName("Bred").userLastName("Beat").build());

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

    @Test
    public void queryByDate() throws Exception {
        Date date = fromDate(1999, 12, 31);
        addUsers(
                UserEntity.builder().lastVisitDate(addDays(date, -1)).userFirstName("John").userLastName("Doe").build(),
                UserEntity.builder().lastVisitDate(addDays(date, -2)).userFirstName("Jake").userLastName("Smith").build(),
                UserEntity.builder().lastVisitDate(addDays(date, -3)).userFirstName("Bill").userLastName("Doors").build(),
                UserEntity.builder().lastVisitDate(addDays(date, -4)).userFirstName("Bred").userLastName("Beat").build());

        UserRepository repo = repositoryService.open();
        try {
            long count = repo.users().query()
                    .where(UserEntity.LastVisitDate.greaterOrEq(addDays(date, -2)))
                    .prepare()
                    .count();
            Assert.assertEquals(2, count);
        } finally {
            repo.close();
        }
    }

    @Test
    public void queryReturnsRelatedEntities() throws Exception {
        RoleEntity[] roles = addRoles(
                RoleEntity.builder().roleDescription("Admin").build(),
                RoleEntity.builder().roleDescription("User").build());

        addUsers(
                UserEntity.builder().userFirstName("John").userLastName("Doe").role(roles[0]).build(),
                UserEntity.builder().userFirstName("Bob").userLastName("Smith").role(roles[1]).build(),
                UserEntity.builder().userFirstName("Ben").userLastName("Stone").role(roles[1]).build());

        UserEntity[] allUsers = queryUsersWhere(UserEntity.Role.is(RoleEntity.RoleDescription.in("User")));
        Assert.assertNotNull(allUsers);
        Assert.assertEquals(2, allUsers.length);
        Assert.assertNotNull(allUsers[0].getRole());
        Assert.assertEquals(2, allUsers[0].getRole().getRoleId());
        Assert.assertEquals("User", allUsers[1].getRole().getRoleDescription());
    }

    @Test
    public void enumStoredAndRestored() throws Exception {
        addUsers(
                UserEntity.builder().userFirstName("John").accountStatus(AccountStatus.ACTIVE).build(),
                UserEntity.builder().userFirstName("Bob").accountStatus(AccountStatus.DISABLED).build(),
                UserEntity.builder().userFirstName("Ben").accountStatus(AccountStatus.PAUSED).build());

        Assert.assertEquals(AccountStatus.ACTIVE, queryUsersWhere(UserEntity.UserFirstName.eq("John"))[0].getAccountStatus());
        Assert.assertEquals(AccountStatus.DISABLED, queryUsersWhere(UserEntity.UserFirstName.eq("Bob"))[0].getAccountStatus());
        Assert.assertEquals(AccountStatus.PAUSED, queryUsersWhere(UserEntity.UserFirstName.eq("Ben"))[0].getAccountStatus());
    }

    @Test
    public void querySelectSingleField() throws Exception {
        repositoryService.users().add(
                UserEntity.builder().userFirstName("John").userLastName("Doe").build(),
                UserEntity.builder().userFirstName("Jake").userLastName("Smith").build(),
                UserEntity.builder().userFirstName("Bill").userLastName("Doors").build(),
                UserEntity.builder().userFirstName("Bred").userLastName("Beat").build());

        String[] firstNames = repositoryService
                .users().query()
                .select(UserEntity.UserFirstName)
                .toArray();

        String[] lastNames = repositoryService
                .users().query()
                .select(UserEntity.UserLastName)
                .toArray();

        Assert.assertArrayEquals(new String[]{"John", "Jake", "Bill", "Bred"}, firstNames);
        Assert.assertArrayEquals(new String[]{"Doe", "Smith", "Doors", "Beat"}, lastNames);
    }

    @Test
    public void bulkUpdateThenQuery() throws Exception {
        repositoryService.users().add(
                UserEntity.builder().userFirstName("John").userLastName("Doe").build(),
                UserEntity.builder().userFirstName("Jake").userLastName("Smith").build(),
                UserEntity.builder().userFirstName("Bill").userLastName("Doors").build(),
                UserEntity.builder().userFirstName("Bred").userLastName("Beat").build());

        repositoryService.users().updateQuery()
                .where(UserEntity.UserFirstName.startsWith("J"))
                .set(UserEntity.AccountStatus, AccountStatus.PAUSED)
                .set(UserEntity.Role, repositoryService.roles().add(RoleEntity.builder().roleDescription("Admin").build()))
                .prepare()
                .execute();

        Assert.assertEquals(2, repositoryService
                .users().query()
                .where(UserEntity.AccountStatus.eq(AccountStatus.PAUSED))
                .prepare()
                .count());

        Assert.assertEquals(2, repositoryService
                .users().query()
                .where(UserEntity.Role.is(RoleEntity.RoleDescription.in("Admin")))
                .prepare()
                .count());
    }

    @Test
    public void relatedChangesSavedInProperOrder() throws Exception {
        UserRepository repo = repositoryService.open();
        try {
            EntitySet<UserEntity> users = repo.users();
            EntitySet<RoleEntity> roles = repo.roles();

            RoleEntity role = roles.add(RoleEntity.create().setRoleDescription("New role"));
            users.add(UserEntity.create().setUserFirstName("John").setUserLastName("Doe").setRole(role));

            repo.saveChanges();
        } finally {
            repo.close();
        }

        Assert.assertEquals(1, repositoryService.users().countAllWhere(UserEntity.Role.is(RoleEntity.RoleDescription.startsWith("New"))));
    }

    @Test
    public void querySelectToMap() throws Exception {
        repositoryService.users().add(
                UserEntity.builder().userFirstName("John").userLastName("Doe").build(),
                UserEntity.builder().userFirstName("Jake").userLastName("Smith").build(),
                UserEntity.builder().userFirstName("Bill").userLastName("Doors").build(),
                UserEntity.builder().userFirstName("Bred").userLastName("Beat").build());

        Map<String, String> firstNameToLastName = repositoryService.users().toMap(UserEntity.UserFirstName, UserEntity.UserLastName);
        Assert.assertEquals(4, firstNameToLastName.size());
        Assert.assertEquals("Doe", firstNameToLastName.get("John"));
        Assert.assertEquals("Smith", firstNameToLastName.get("Jake"));
    }

    @Test
    public void serializableStoredAndRestored() throws Exception {
        ArrayList<String> comments = new ArrayList<>();
        comments.add("one");
        comments.add("two");
        comments.add("three");
        repositoryService.users().add(UserEntity.builder().comments(comments).build());

        UserEntity user = repositoryService.users().query().prepare().firstOrDefault();
        Assert.assertEquals("one", user.getComments().get(0));
        Assert.assertEquals("two", user.getComments().get(1));
        Assert.assertEquals("three", user.getComments().get(2));
    }

    private UserEntity[] addUsers(final UserEntity... users) throws Exception {
        return repositoryService.users().add(users);
    }

    private RoleEntity[] addRoles(final RoleEntity... roles) throws Exception {
        return repositoryService.roles().add(roles);
    }

    private long queryUsersCountWhere(final Condition<UserEntity> condition) throws Exception {
        return repositoryService.query(repository -> repository.users()
                .query()
                .where(condition)
                .prepare()
                .count());
    }

    private UserEntity[] queryUsersWhere(final Condition<UserEntity> condition) throws Exception {
        return repositoryService.query(repository -> repository.users()
                .query()
                .where(condition)
                .prepare()
                .toArray());
    }

    private static Path getTempPath(Path parentDir, String prefix, String suffix) {
        String filename = prefix + new BigInteger(64, new Random()).toString(32) + suffix;
        return parentDir.resolve(filename);
    }
}