package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 19-May-15.
 */
public class SimpleSqlCommand implements SqlCommand {
    private final String statement;
    private final Parameters parameters;

    private static class SimpleParameters implements Parameters {
        private final String[] parameters;
        private final HashMap<String, String> map;

        private SimpleParameters(String[] parameters) {
            this.parameters = parameters;
            this.map = new HashMap<>();
        }

        @Override
        public String add(String parameter) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public int getCount() {
            return parameters.length;
        }

        @Override
        public Map<String, String> getMap() {
            return map;
        }

        @Override
        public String[] getValues() {
            return parameters;
        }
    }

    public SimpleSqlCommand(String statement, String... parameters) {
        this.statement = statement;
        this.parameters = new SimpleParameters(parameters);
    }

    @Override
    public String getStatement() {
        return statement;
    }

    @Override
    public Parameters getParameters() {
        return parameters;
    }
}
