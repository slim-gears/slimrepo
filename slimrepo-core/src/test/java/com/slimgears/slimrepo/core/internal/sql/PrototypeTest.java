// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.conditions.Conditions;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.EntityFieldValueMap;
import com.slimgears.slimrepo.core.internal.interfaces.*;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSchemeProvider;
import com.slimgears.slimrepo.core.internal.sql.sqlite.AbstractSqliteOrmServiceProvider;
import com.slimgears.slimrepo.core.prototype.UserRepository;
import com.slimgears.slimrepo.core.prototype.generated.*;
import com.slimgears.slimrepo.core.utilities.Dates;
import com.slimgears.slimrepo.core.utilities.Joiner;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.slimgears.slimrepo.core.utilities.Dates.addDays;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
@RunWith(JUnit4.class)
public class PrototypeTest {
    @Mock private TransactionProvider transactionProviderMock;
    @Mock private SqlCommandExecutor executorMock;

    private SessionServiceProvider sessionServiceProviderMock;
    private SqlOrmServiceProvider ormServiceProviderMock;
    private SqlSchemeProvider schemeProviderMock;
    private List<String> sqlStatements;

    private SqlDatabaseSchemeProxy databaseSchemeMock;
    private SqlDatabaseSchemeProxy repositorySchemeMock;

    private final RepositoryModel repositoryModel = GeneratedUserRepository.Model.Instance;

    class TracingAnswer<T> implements Answer<T> {
        private final T answer;

        public TracingAnswer(T answer) {
            this.answer = answer;
        }

        @Override
        public T answer(InvocationOnMock invocation) {
            String sql = (String)invocation.getArguments()[0];
            Stream<Object> params = Stream.of(invocation.getArguments()).skip(1);
            String sqlWithParams = sql + "\n{Params: [" + params.map(Object::toString).collect(Collectors.joining(", ")) + "]}";
            sqlStatements.add(sqlWithParams);
            System.out.println(sqlWithParams);
            return answer;
        }
    }

    private <T> TracingAnswer<T> answer(T returnValue) {
        return new TracingAnswer<>(returnValue);
    }

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);

        sqlStatements = new ArrayList<>();

        ormServiceProviderMock = new AbstractSqliteOrmServiceProvider() {
            @Override
            public SessionServiceProvider createSessionServiceProvider(RepositoryModel model) {
                return sessionServiceProviderMock;
            }
        };

        repositorySchemeMock = new SqlDatabaseSchemeProxy(getDatabaseScheme(repositoryModel));
        databaseSchemeMock = new SqlDatabaseSchemeProxy(getDatabaseScheme(repositoryModel));

        schemeProviderMock = Mockito.mock(SqlSchemeProvider.class);

        when(schemeProviderMock.getModelScheme(repositoryModel)).thenReturn(repositorySchemeMock);
        when(schemeProviderMock.getDatabaseScheme()).thenReturn(databaseSchemeMock);

        sessionServiceProviderMock = new AbstractSqlSessionServiceProvider(ormServiceProviderMock) {
            @Override
            protected SqlCommandExecutor createCommandExecutor() {
                return executorMock;
            }

            @Override
            protected TransactionProvider createTransactionProvider() {
                return transactionProviderMock;
            }

            @Override
            protected SqlSchemeProvider createSchemeProvider() {
                return schemeProviderMock;
            }
        };

        when(executorMock.select(any(String.class), any()))
                .thenAnswer(answer(rowsMock(10)));
        when(executorMock.count(any(String.class), any()))
                .thenAnswer(answer(0L));

        doAnswer(answer(null))
                .when(executorMock)
                .execute(any(String.class), any());
    }

    @Test
    public void queryCountWhereStringFieldContains() throws IOException {
        testQuery(repository -> repository.users().query()
                .where(UserEntity.UserFirstName.contains("John"))
                .skip(2)
                .limit(10)
                .prepare()
                .count());
        Mockito.verify(executorMock).count(any(String.class), any());
        assertSqlEquals("query-count-users.sql");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWhereStringFieldContains() throws IOException {
        testQuery(repository -> repository.users().query()
                .where(
                        Conditions.or(
                                Conditions.and(
                                        UserEntity.UserFirstName.contains("John"),
                                        UserEntity.UserId.startsWith("id-2")
                                ),
                                UserEntity.UserLastName.startsWith("Smi")
                        ))
                .orderAsc(UserEntity.UserLastName, UserEntity.UserFirstName, UserEntity.UserId)
                .skip(3)
                .limit(10)
                .prepare()
                .toArray());
        Mockito.verify(executorMock).select(any(String.class), any());
        assertSqlEquals("query-users.sql");
    }

    @Test
    public void queryCountWithRelationalCondition() throws IOException {
        testQuery(repository -> repository.users().query()
                .where(UserEntity.Role.is(RoleEntity.RoleDescription.in("Admin")))
                .prepare()
                .count());
        Mockito.verify(executorMock).count(any(String.class), any());
        assertSqlEquals("query-count-related-field.sql");
    }

    @Test
    public void repositoryCreation() throws IOException {
        RepositoryCreator creator = ormServiceProviderMock
                .createSessionServiceProvider(repositoryModel)
                .getRepositoryCreator();
        creator.createRepository(repositoryModel);
        assertSqlEquals("create-tables.sql");
    }

    @Test
    public void queryWithRelationalCondition() throws IOException {
        testQuery(repository -> repository.users().query()
                .where(UserEntity.Role.is(RoleEntity.RoleDescription.in("Admin")))
                .prepare()
                .toArray());
        Mockito.verify(executorMock).select(any(String.class), any());
        assertSqlEquals("query-related-field.sql");
    }

    @Test
    public void querySelectedFieldsToMap() throws IOException {
        testQuery(repository -> repository.users().query()
                .where(UserEntity.UserFirstName.in("John", "Jake"))
                .selectToMap(UserEntity.UserFirstName, UserEntity.UserLastName));
        Mockito.verify(executorMock).select(any(String.class), any());
        assertSqlEquals("query-selected-to-map.sql");
    }

    @Test
    public void updateWithWhereTranslatedToSql() throws IOException {
        testUpdate(repository -> repository.users().updateQuery()
                .where(UserEntity.UserFirstName.eq("John"))
                .set(UserEntity.UserLastName, "Doe")
                .prepare()
                .execute());
        Mockito.verify(executorMock).execute(any(String.class), any());
        assertSqlEquals("update-fields.sql");
    }

    @Test
    public void repositoryUpgradeWhenFieldAdded() throws IOException {
        databaseSchemeMock.hideFields(UserEntity.EntityMetaType, UserEntity.Comments);
        assertUpgrade("upgrade-field-added.sql");
    }

    @Test
    public void repositoryUpgradeWhenNonNullableFieldAdded() throws IOException {
        databaseSchemeMock.hideFields(UserEntity.EntityMetaType, UserEntity.Age);
        assertUpgrade("upgrade-non-nullable-field-added.sql");
    }

    @Test
    public void repositoryUpgradeWhenFieldDeleted() throws IOException {
        repositorySchemeMock.hideFields(UserEntity.EntityMetaType, UserEntity.Comments);
        assertUpgrade("upgrade-field-deleted.sql");
    }

    @Test
    public void repositoryUpgradeWhenTableAdded() throws IOException {
        databaseSchemeMock.hideTables(UserEntity.EntityMetaType);
        assertUpgrade("upgrade-table-added.sql");
    }

    @Test
    public void repositoryUpgradeWhenTableDeleted() throws IOException {
        repositorySchemeMock.hideTables(UserEntity.EntityMetaType);
        assertUpgrade("upgrade-table-deleted.sql");
    }

    private void assertUpgrade(String sqlScriptName) throws IOException {
        RepositoryCreator creator = ormServiceProviderMock
                .createSessionServiceProvider(repositoryModel)
                .getRepositoryCreator();
        creator.upgradeRepository(repositoryModel);
        assertSqlEquals(sqlScriptName);
    }

    @Test
    public void queryPredicatesTranslatedToSql() throws IOException {
        final Date fromDate = Dates.fromDate(2000, 1, 1);
        final Date toDate = addDays(fromDate, 1);
        testQuery(repository -> {
            EntitySet<UserEntity> users = repository.users();
            users.findAllWhere(UserEntity.UserLastName.isNull());
            users.findAllWhere(UserEntity.UserFirstName.isNotNull());
            users.findAllWhere(UserEntity.AccountStatus.eq(AccountStatus.PAUSED));
            users.findAllWhere(UserEntity.AccountStatus.notEq(AccountStatus.ACTIVE));
            users.findAllWhere(UserEntity.AccountStatus.in(AccountStatus.PAUSED, AccountStatus.DISABLED));
            users.findAllWhere(UserEntity.AccountStatus.in(Arrays.asList(AccountStatus.PAUSED, AccountStatus.DISABLED)));
            users.findAllWhere(UserEntity.AccountStatus.notIn(AccountStatus.ACTIVE, AccountStatus.DISABLED));
            users.findAllWhere(UserEntity.AccountStatus.notIn(Arrays.asList(AccountStatus.ACTIVE, AccountStatus.DISABLED)));
            users.findAllWhere(UserEntity.LastVisitDate.between(fromDate, toDate));
            users.findAllWhere(UserEntity.LastVisitDate.greaterOrEq(fromDate));
            users.findAllWhere(UserEntity.LastVisitDate.lessOrEq(toDate));
            users.findAllWhere(UserEntity.LastVisitDate.greaterThan(fromDate));
            users.findAllWhere(UserEntity.LastVisitDate.lessThan(toDate));
            users.findAllWhere(UserEntity.Role.is(RoleEntity.RoleDescription.startsWith("A")));
            users.findAllWhere(UserEntity.Role.is(RoleEntity.RoleDescription.endsWith("B")));
            users.findAllWhere(UserEntity.Role.is(RoleEntity.RoleDescription.contains("C")));
            users.findAllWhere(UserEntity.UserFirstName.notStartsWith("A"));
            users.findAllWhere(UserEntity.UserFirstName.notEndsWith("B"));
            users.findAllWhere(UserEntity.UserFirstName.notContains("C"));
            return users;
        });
        Mockito.verify(executorMock, times(19)).select(any(String.class), any());
        assertSqlEquals("query-predicates.sql");
    }

    private void testUpdate(RepositoryService.UpdateAction<UserRepository> updateAction) throws IOException {
        RepositoryService<UserRepository> repo = new GeneratedUserRepositoryService(ormServiceProviderMock);
        repo.update(updateAction);
    }

    private <T> T testQuery(RepositoryService.QueryAction<UserRepository, T> queryAction) throws IOException {
        RepositoryService<UserRepository> repo = new GeneratedUserRepositoryService(ormServiceProviderMock);
        T result = repo.query(queryAction);
        Assert.assertNotNull(result);
        return result;
    }

    private CloseableIterator<FieldValueLookup> rowsMock(int count) {
        FieldValueLookup[] rows = new FieldValueLookup[count];
        for (int i = 0; i < count; ++i) {
            rows[i] = new EntityFieldValueMap<>(
                    UserEntity.EntityMetaType,
                    UserEntity.builder()
                            .userId("id-" + i)
                            .userFirstName("John")
                            .userLastName("Doe")
                            .build());
        }
        return iteratorMock(rows);
    }

    @SafeVarargs
    private final <T> CloseableIterator<T> iteratorMock(T... entries) {
        final Iterator<T> iterator = Arrays.asList(entries).iterator();
        return new CloseableIterator<T>() {
            @Override
            public void close() throws IOException {

            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new RuntimeException("Not implemented");
            }
        };
    }

    private void assertSqlEquals(String resourceId) throws IOException {
        Assert.assertNotNull(sqlStatements);
        Assert.assertNotEquals(0, sqlStatements.size());
        String actualSql = Joiner.on("\n").join(sqlStatements);
        String[] actualLines = actualSql.split("\n");
        try (InputStream stream = getClass().getResourceAsStream("/sql/" + resourceId)) {
            List<String> expectedLines = IOUtils.readLines(stream);
            Assert.assertEquals(expectedLines.size(), actualLines.length);
            for (int i = 0; i < expectedLines.size(); ++i) {
                Assert.assertEquals("Line mismatch", expectedLines.get(i), actualLines[i]);
            }
        }
    }

    private SqlDatabaseScheme getDatabaseScheme(RepositoryModel model) {
        SqlSchemeProvider provider = new AbstractSqlSchemeProvider(ormServiceProviderMock.getSyntaxProvider()) {
            @Override
            public SqlDatabaseScheme getDatabaseScheme() {
                return null;
            }
        };

        return provider.getModelScheme(model);
    }
}
