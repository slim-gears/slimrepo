package com.slimgears.slimrepo.example;

import android.os.Bundle;

import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.example.repository.CountryEntity;
import com.slimgears.slimrepo.example.repository.GeneratedUserRepositoryService;
import com.slimgears.slimrepo.example.repository.UserEntity;
import com.slimgears.slimrepo.example.repository.UserRepository;
import com.slimgears.slimrepo.example.repository.UserRepositoryService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18, manifest=Config.NONE)
public class ApplicationTest {
    @Test
    public void repositorySanityCheck() throws IOException {
        Assert.assertNotNull(UserEntity.Id);
        Assert.assertNotNull(UserEntity.FirstName);
        Assert.assertNotNull(UserEntity.LastName);
        Assert.assertNotNull(UserEntity.EntityMetaType);
        Assert.assertNotNull(UserEntity.EntityMetaType.getFields());
        Assert.assertNotEquals(0, UserEntity.EntityMetaType.getFields().size());
        Assert.assertNotNull(UserEntity.EntityMetaType.getKeyField());

        Assert.assertNotNull(RuntimeEnvironment.application);
        UserRepositoryService repoService = new GeneratedUserRepositoryService(RuntimeEnvironment.application);
        repoService.update(new RepositoryService.UpdateAction<UserRepository>() {
            @Override
            public void execute(UserRepository repository) throws IOException {
                EntitySet<CountryEntity> countries = repository.countries();
                CountryEntity countryUs = countries.add(CountryEntity.builder().name("United States").build());
                CountryEntity countryUk = countries.add(CountryEntity.builder().name("United Kingdom").build());
                CountryEntity countryFrance = countries.add(CountryEntity.builder().name("France").build());
                CountryEntity countryItaly = countries.add(CountryEntity.builder().name("Italy").build());
                repository.saveChanges();

                Bundle status = new Bundle();
                status.putInt("Integer", 1);
                status.putString("String", "test");

                EntitySet<UserEntity> users = repository.users();
                users.add(
                        UserEntity.create()
                                .setFirstName("John")
                                .setLastName("Doe")
                                .setAge(20)
                                .setCountry(countryUs)
                                .setStatus(status),
                        UserEntity.create()
                                .setFirstName("William")
                                .setLastName("Shakespeare")
                                .setAge(32)
                                .setCountry(countryUk)
                                .setStatus(status),
                        UserEntity.create()
                                .setFirstName("Mark")
                                .setLastName("Twain")
                                .setAge(40)
                                .setCountry(countryFrance)
                                .setStatus(status));
            }
        });

        CountryEntity[] countries = repoService.countries().toArray();

        Assert.assertEquals(4, countries.length);

        UserEntity[] users = repoService.users().query()
            .where(UserEntity.Country.is(CountryEntity.Name.in("France")))
            .prepare()
            .toArray();

        Assert.assertEquals(1, users.length);
        Assert.assertNotNull(users[0].getCountry());
        Assert.assertEquals("France", users[0].getCountry().getName());
        Assert.assertEquals(1, users[0].getStatus().getInt("Integer"));
        Assert.assertEquals("test", users[0].getStatus().getString("String"));
    }
}
