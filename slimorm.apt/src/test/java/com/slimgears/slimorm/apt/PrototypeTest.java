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

import static org.mockito.Mockito.*;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
@RunWith(JUnit4.class)
public class PrototypeTest {
    @Mock private SqlCommandExecutorFactory factoryMock;
    @Mock private SqlCommandExecutor executorMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(factoryMock.createCommandExecutor(any(Repository.class), any(RepositorySession.class)))
                .thenReturn(executorMock);
        SlimSqlOrm.setCommandExecutorFactory(factoryMock);
    }

    @Test
    public void queryCountWhereStringFieldContains() throws IOException {
        testQuery(new Repository.QueryAction<UserRepositorySession, Integer>() {
            @Override
            public Integer execute(UserRepositorySession connection) throws IOException {
                return connection.users().query()
                        .where(UserEntity.Fields.UserFirstName.contains("Denis"))
                        .skip(1)
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
                        .where(UserEntity.Fields.UserFirstName.contains("Denis"))
                        .orderAsc(UserEntity.Fields.UserLastName, UserEntity.Fields.UserFirstName, UserEntity.Fields.UserId)
                        .skip(1)
                        .limit(10)
                        .toArray();
            }
        });
        verify(executorMock).select(any(SqlCommand.class));
    }

    private void printCommand(SqlCommand command) {
        System.out.println(command.getStatement());
        System.out.println(command.getParameters().getAll());
    }

    private <T> T testQuery(Repository.QueryAction<UserRepositorySession, T> queryAction) throws IOException {
        when(executorMock.select(any(SqlCommand.class)))
                .thenAnswer(new Answer<Iterable<FieldValueLookup>>() {
                    @Override
                    public Iterable<FieldValueLookup> answer(InvocationOnMock invocation) throws Throwable {
                        printCommand((SqlCommand)invocation.getArguments()[0]);
                        return new ArrayList<>();
                    }
                });

        when(executorMock.count(any(SqlCommand.class)))
                .thenAnswer(new Answer<Integer>() {
                    @Override
                    public Integer answer(InvocationOnMock invocation) throws Throwable {
                        printCommand((SqlCommand)invocation.getArguments()[0]);
                        return 0;
                    }
                });

        Repository<UserRepositorySession> repo = new UserRepositoryImpl();
        T result = repo.query(queryAction);
        Assert.assertNotNull(result);
        return result;
    }
}
