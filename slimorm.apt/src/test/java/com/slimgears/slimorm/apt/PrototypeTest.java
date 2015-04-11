// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.apt;

import com.slimgears.slimorm.apt.prototype.UserRepositorySession;
import com.slimgears.slimorm.apt.prototype.generated.UserEntity;
import com.slimgears.slimorm.apt.prototype.generated.UserRepositoryImpl;
import com.slimgears.slimorm.apt.prototype.slimsql.SlimSqlOrm;
import com.slimgears.slimorm.interfaces.FieldValueLookup;
import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.interfaces.RepositorySession;
import com.slimgears.slimorm.internal.CloseableIterator;
import com.slimgears.slimorm.internal.sql.SqlCommand;
import com.slimgears.slimorm.internal.sql.SqlCommandExecutor;
import com.slimgears.slimorm.internal.sql.SqlCommandExecutorFactory;

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
import java.util.ArrayList;

import static com.slimgears.slimorm.interfaces.predicates.Predicates.and;
import static com.slimgears.slimorm.interfaces.predicates.Predicates.or;
import static org.mockito.Mockito.*;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
@RunWith(JUnit4.class)
public class PrototypeTest {
    @Mock private SqlCommandExecutorFactory factoryMock;
    @Mock private SqlCommandExecutor executorMock;
    @Mock private CloseableIterator<FieldValueLookup> rowsMock;

    static class TracingAnswer<T> implements Answer<T> {
        private final T answer;

        public TracingAnswer(T answer) {
            this.answer = answer;
        }

        @Override
        public T answer(InvocationOnMock invocation) throws Throwable {
            SqlCommand command = (SqlCommand)invocation.getArguments()[0];
            System.out.println(command.getStatement());
            System.out.println(command.getParameters().getMap());
            return answer;
        }

        public static <T> TracingAnswer<T> create(T returnValue) {
            return new TracingAnswer<>(returnValue);
        }
    }

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(factoryMock.createCommandExecutor(any(Repository.class), any(RepositorySession.class)))
                .thenReturn(executorMock);
        SlimSqlOrm.setCommandExecutorFactory(factoryMock);

        when(executorMock.select(any(SqlCommand.class)))
                .thenAnswer(TracingAnswer.create(rowsMock));
        when(executorMock.count(any(SqlCommand.class)))
                .thenAnswer(TracingAnswer.create(0));
        doAnswer(TracingAnswer.create(null)).when(executorMock).execute(any(SqlCommand.class));
    }

    @Test
    public void queryCountWhereStringFieldContains() throws IOException {
        testQuery(new Repository.QueryAction<UserRepositorySession, Integer>() {
            @Override
            public Integer execute(UserRepositorySession connection) throws IOException {
                return connection.users().query()
                        .where(UserEntity.UserFirstName.contains("Denis"))
                        .skip(2)
                        .limit(10)
                        .count();
            }
        });
        verify(executorMock).count(any(SqlCommand.class));
    }

    @Test
    public void queryWhereStringFieldContains() throws IOException {
        testQuery(new Repository.QueryAction<UserRepositorySession, UserEntity[]>() {
            @Override
            public UserEntity[] execute(UserRepositorySession connection) throws IOException {
                return connection.users().query()
                        .where(
                                or(
                                        and(
                                                UserEntity.UserFirstName.contains("Denis"),
                                                UserEntity.UserId.greaterThan(20)
                                        ),
                                        UserEntity.UserLastName.startsWith("Itsko")
                                ))
                        .orderAsc(UserEntity.UserLastName, UserEntity.UserFirstName, UserEntity.UserId)
                        .skip(3)
                        .limit(10)
                        .toArray();
            }
        });
        verify(executorMock).select(any(SqlCommand.class));
    }

    private <T> T testQuery(Repository.QueryAction<UserRepositorySession, T> queryAction) throws IOException {

        Repository<UserRepositorySession> repo = new UserRepositoryImpl();
        T result = repo.query(queryAction);
        Assert.assertNotNull(result);
        return result;
    }
}
