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

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
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
                EntitySet<Integer, CountryEntity> countries = repository.countries();
                CountryEntity countryUs = countries.addNew().setName("United States");
                CountryEntity countryUk = countries.addNew().setName("United Kingdom");
                CountryEntity countryFrance = countries.addNew().setName("France");
                CountryEntity countryItaly = countries.addNew().setName("Italy");
                repository.saveChanges();

                EntitySet<Integer, UserEntity> users = repository.users();
                users.addNew().setFirstName("John").setLastName("Doe").setAge(20).setCountry(countryUs);
                users.addNew().setFirstName("William").setLastName("Shakespeare").setAge(32).setCountry(countryUk);
                users.addNew().setFirstName("Mark").setLastName("Twain").setAge(40).setCountry(countryFrance);
            }
        });

        CountryEntity[] countries = repoService.query(new RepositoryService.QueryAction<UserRepository, CountryEntity[]>() {
            @Override
            public CountryEntity[] execute(UserRepository repository) throws IOException {
                return repository.countries().toArray();
            }
        });

        Assert.assertEquals(4, countries.length);

        UserEntity[] users = repoService.query(new RepositoryService.QueryAction<UserRepository, UserEntity[]>() {
            @Override
            public UserEntity[] execute(UserRepository repository) throws IOException {
                return repository.users().query()
                        .where(UserEntity.Country.is(CountryEntity.Name.in("France")))
                        .prepare()
                        .toArray();
            }
        });

        Assert.assertEquals(1, users.length);
        Assert.assertNotNull(users[0].getCountry());
        Assert.assertEquals("France", users[0].getCountry().getName());
    }
}
