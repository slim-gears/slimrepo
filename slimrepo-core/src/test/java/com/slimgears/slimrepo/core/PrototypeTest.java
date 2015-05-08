// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.EntityFieldValueMap;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryCreator;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.sqlite.AbstractSqliteOrmServiceProvider;
import com.slimgears.slimrepo.core.prototype.UserRepository;
import com.slimgears.slimrepo.core.prototype.generated.AccountStatus;
import com.slimgears.slimrepo.core.prototype.generated.GeneratedUserRepository;
import com.slimgears.slimrepo.core.prototype.generated.GeneratedUserRepositoryService;
import com.slimgears.slimrepo.core.prototype.generated.RoleEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;
import com.slimgears.slimrepo.core.utilities.Dates;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.slimgears.slimrepo.core.interfaces.conditions.Conditions.and;
import static com.slimgears.slimrepo.core.interfaces.conditions.Conditions.or;
import static com.slimgears.slimrepo.core.utilities.Dates.addDays;
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
    private List<String> sqlStatements;

    class TracingAnswer<T> implements Answer<T> {
        private final T answer;

        public TracingAnswer(T answer) {
            this.answer = answer;
        }

        @Override
        public T answer(InvocationOnMock invocation) throws Throwable {
            SqlCommand command = (SqlCommand)invocation.getArguments()[0];
            String sql = command.getStatement();
            Object[] params = command.getParameters().getValues();
            String sqlWithParams = sql + "\n{Params: [" + Joiner.on(", ").join(params) + "]}";
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

        sessionServiceProviderMock = new AbstractSqlSessionServiceProvider(ormServiceProviderMock) {
            @Override
            protected SqlCommandExecutor createCommandExecutor() {
                return executorMock;
            }

            @Override
            protected TransactionProvider createTransactionProvider() {
                return transactionProviderMock;
            }
        };

        Mockito.when(executorMock.select(Matchers.any(SqlCommand.class)))
                .thenAnswer(answer(rowsMock(10)));
        Mockito.when(executorMock.count(Matchers.any(SqlCommand.class)))
                .thenAnswer(answer(0));
        Mockito.doAnswer(answer(null)).when(executorMock).execute(Matchers.any(SqlCommand.class));
    }

    @Test
    public void queryCountWhereStringFieldContains() throws IOException {
        testQuery(new RepositoryService.QueryAction<UserRepository, Long>() {
            @Override
            public Long execute(UserRepository repository) throws IOException {
                return repository.users().query()
                        .where(UserEntity.UserFirstName.contains("John"))
                        .skip(2)
                        .limit(10)
                        .prepare()
                        .count();
            }
        });
        Mockito.verify(executorMock).count(Matchers.any(SqlCommand.class));
        assertSqlEquals("query-count-users.sql");
    }

    @Test
    public void queryWhereStringFieldContains() throws IOException {
        testQuery(new RepositoryService.QueryAction<UserRepository, UserEntity[]>() {
            @Override
            public UserEntity[] execute(UserRepository repository) throws IOException {
                return repository.users().query()
                        .where(
                                or(
                                        and(
                                                UserEntity.UserFirstName.contains("John"),
                                                UserEntity.UserId.greaterThan(20)
                                        ),
                                        UserEntity.UserLastName.startsWith("Smi")
                                ))
                        .orderAsc(UserEntity.UserLastName, UserEntity.UserFirstName, UserEntity.UserId)
                        .skip(3)
                        .limit(10)
                        .prepare()
                        .toArray();
            }
        });
        Mockito.verify(executorMock).select(Matchers.any(SqlCommand.class));
        assertSqlEquals("query-users.sql");
    }

    @Test
    public void queryCountWithRelationalCondition() throws IOException {
        testQuery(new RepositoryService.QueryAction<UserRepository, Object>() {
            @Override
            public Object execute(UserRepository repository) throws IOException {
                return repository.users().query()
                        .where(UserEntity.Role.is(RoleEntity.RoleDescription.in("Admin")))
                        .prepare()
                        .count();
            }
        });
        Mockito.verify(executorMock).count(Matchers.any(SqlCommand.class));
        assertSqlEquals("query-count-related-field.sql");
    }

    @Test
    public void repositoryCreation() throws IOException {
        RepositoryCreator creator = ormServiceProviderMock
                .createSessionServiceProvider(GeneratedUserRepository.Model.Instance)
                .getRepositoryCreator();
        creator.createRepository(GeneratedUserRepository.Model.Instance);
        assertSqlEquals("create-tables.sql");
    }

    @Test
    public void queryWithRelationalCondition() throws IOException {
        testQuery(new RepositoryService.QueryAction<UserRepository, Object>() {
            @Override
            public Object execute(UserRepository repository) throws IOException {
                return repository.users().query()
                        .where(UserEntity.Role.is(RoleEntity.RoleDescription.in("Admin")))
                        .prepare()
                        .toArray();
            }
        });
        Mockito.verify(executorMock).select(Matchers.any(SqlCommand.class));
        assertSqlEquals("query-related-field.sql");
    }

    @Test
    public void querySelectedFieldsToMap() throws IOException {
        testQuery(new RepositoryService.QueryAction<UserRepository, Object>() {
            @Override
            public Object execute(UserRepository repository) throws IOException {
                return repository.users().query()
                        .where(UserEntity.UserFirstName.in("John", "Jake"))
                        .selectToMap(UserEntity.UserFirstName, UserEntity.UserLastName);
            }
        });
        Mockito.verify(executorMock).select(Matchers.any(SqlCommand.class));
        assertSqlEquals("query-selected-to-map.sql");
    }

    @Test
    public void updateWithWhereTranslatedToSql() throws IOException {
        testUpdate(new RepositoryService.UpdateAction<UserRepository>() {
            @Override
            public void execute(UserRepository repository) throws IOException {
                repository.users().updateQuery()
                        .where(UserEntity.UserFirstName.eq("John"))
                        .set(UserEntity.UserLastName, "Doe")
                        .prepare()
                        .execute();
            }
        });
        Mockito.verify(executorMock).execute(Matchers.any(SqlCommand.class));
        assertSqlEquals("update-fields.sql");
    }

    @Test
    public void queryPredicatesTranslatedToSql() throws IOException {
        final Date fromDate = Dates.fromDate(2000, 1, 1);
        final Date toDate = addDays(fromDate, 1);
        testQuery(new RepositoryService.QueryAction<UserRepository, Object>() {
            @Override
            public Object execute(UserRepository repository) throws IOException {
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
            }
        });
        Mockito.verify(executorMock, times(19)).select(Matchers.any(SqlCommand.class));
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
                            .userId(i)
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
        List<String> actualLines = Lists.newArrayList(Splitter.on("\n").split(actualSql));
        try (InputStream stream = getClass().getResourceAsStream("/sql/" + resourceId)) {
            List<String> expectedLines = IOUtils.readLines(stream);
            Assert.assertEquals(expectedLines.size(), actualLines.size());
            for (int i = 0; i < expectedLines.size(); ++i) {
                Assert.assertEquals("Line mismatch", expectedLines.get(i), actualLines.get(i));
            }
        }
    }
}
