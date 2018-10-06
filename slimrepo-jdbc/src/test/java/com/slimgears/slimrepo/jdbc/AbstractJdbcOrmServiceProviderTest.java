package com.slimgears.slimrepo.jdbc;

import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import org.junit.After;
import org.junit.Before;
import com.slimgears.slimrepo.core.prototype.*;
import com.slimgears.slimrepo.core.prototype.generated.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;


public class AbstractJdbcOrmServiceProviderTest {
    protected UserRepositoryService repositoryService;
    private Path dbPath;

    protected String getDbUrl() {
        return "jdbc:sqlite:" + dbPath;
    }

    private static Path getTempPath(Path parentDir, String prefix, String suffix) {
        String filename = prefix + new BigInteger(64, new Random()).toString(32) + suffix;
        return parentDir.resolve(filename);
    }

    @Before
    public void setUp() throws IOException {
        new File("tmp").mkdirs();
        dbPath = getTempPath(Paths.get("tmp"), "temp-", ".db");
        OrmServiceProvider orm = new JdbcOrmServiceProvider(getDbUrl());
        repositoryService = new GeneratedUserRepositoryService(orm);
    }

    @After
    public void tearDown() throws IOException {
        //Files.deleteIfExists(dbPath);
    }
}
