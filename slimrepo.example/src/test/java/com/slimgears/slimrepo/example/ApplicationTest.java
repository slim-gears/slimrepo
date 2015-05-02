package com.slimgears.slimrepo.example;

import com.slimgears.slimrepo.android.core.SqliteOrmServiceProvider;
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
@Config(emulateSdk = 18, manifest=Config.NONE)
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
        UserRepositoryService repoService = new GeneratedUserRepositoryService(new SqliteOrmServiceProvider(RuntimeEnvironment.application));
        repoService.update(new RepositoryService.UpdateAction<UserRepository>() {
            @Override
            public void execute(UserRepository repository) throws IOException {
                EntitySet<CountryEntity> countries = repository.countries();
                CountryEntity countryUs = countries.add(CountryEntity.builder().name("United States").build());
                CountryEntity countryUk = countries.add(CountryEntity.builder().name("United Kingdom").build());
                CountryEntity countryFrance = countries.add(CountryEntity.builder().name("France").build());
                CountryEntity countryItaly = countries.add(CountryEntity.builder().name("Italy").build());
                repository.saveChanges();

                EntitySet<UserEntity> users = repository.users();
                users.add(UserEntity.builder().firstName("John").lastName("Doe").age(20).country(countryUs).build());
                users.add(UserEntity.builder().firstName("William").lastName("Shakespeare").age(32).country(countryUk).build());
                users.add(UserEntity.builder().firstName("Mark").lastName("Twain").age(40).country(countryFrance).build());
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
    }
}
