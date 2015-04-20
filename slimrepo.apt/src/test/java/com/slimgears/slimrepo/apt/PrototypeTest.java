// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.slimgears.slimrepo.apt.prototype.UserRepository;
import com.slimgears.slimrepo.apt.prototype.generated.UserEntity;
import com.slimgears.slimrepo.apt.prototype.generated.UserRepositoryServiceImpl;
import com.slimgears.slimrepo.apt.prototype.generated.UserRepositoryImpl;
import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.EntityFieldValueMap;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryCreator;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.SqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.sqlite.AbstractSqliteOrmServiceProvider;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.slimgears.slimrepo.core.interfaces.conditions.Conditions.and;
import static com.slimgears.slimrepo.core.interfaces.conditions.Conditions.or;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(executorMock.select(any(SqlCommand.class)))
                .thenAnswer(answer(rowsMock(10)));
        when(executorMock.count(any(SqlCommand.class)))
                .thenAnswer(answer(0));
        doAnswer(answer(null)).when(executorMock).execute(any(SqlCommand.class));
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
        verify(executorMock).count(any(SqlCommand.class));
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
        verify(executorMock).select(any(SqlCommand.class));
        assertSqlEquals("query-users.sql");
    }

    @Test
    public void repositoryCreation() throws IOException {
        RepositoryCreator creator = ormServiceProviderMock
                .createSessionServiceProvider(UserRepositoryImpl.Model.Instance)
                .getRepositoryCreator();
        creator.createRepository(UserRepositoryImpl.Model.Instance);
        assertSqlEquals("create-tables.sql");
    }

    private <T> T testQuery(RepositoryService.QueryAction<UserRepository, T> queryAction) throws IOException {
        RepositoryService<UserRepository> repo = new UserRepositoryServiceImpl(ormServiceProviderMock);
        T result = repo.query(queryAction);
        Assert.assertNotNull(result);
        return result;
    }

    private CloseableIterator<FieldValueLookup> rowsMock(int count) {
        FieldValueLookup[] rows = new FieldValueLookup[count];
        for (int i = 0; i < count; ++i) {
            rows[i] = new EntityFieldValueMap<>(
                    UserEntity.EntityMetaType,
                    UserEntity.create()
                            .userId(i)
                            .userFirstName("Denis")
                            .userLastName("Itsko")
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
