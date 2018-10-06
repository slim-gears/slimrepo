package com.slimgears.slimrepo.android;

import com.annimon.stream.function.Function;
import com.slimgears.slimrepo.android.core.SqliteOrmServiceProvider;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.queries.EntitySelectQuery;
import com.slimgears.slimrepo.core.internal.sql.SqlPredicateBuilder;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Denis on 02-Jun-15.
 */
public class RepositoryMatchers {
    private final static SqliteOrmServiceProvider SQLITE_ORM_SERVICE_PROVIDER = new SqliteOrmServiceProvider(RuntimeEnvironment.application);
    private final static SqlStatementBuilder.PredicateBuilder PREDICATE_BUILDER;
    private final static SqlCommand.Builder EMPTY_BUILDER = new SqlCommand.Builder() {
        @Override
        public SqlCommand.Builder append(String statement) {
            return this;
        }

        @Override
        public SqlCommand.Builder append(Function<SqlCommand.Builder, String> statement) {
            return this;
        }

        @Override
        public <T> int addParam(Class<T> type, T value) {
            return 0;
        }

        @Override
        public <T> int addParam(T value) {
            return 0;
        }

        @Override
        public <T> SqlCommand.Builder param(Class<T> type, T value) {
            return this;
        }

        @Override
        public <T> SqlCommand.Builder param(T value) {
            return null;
        }

        @Override
        public SqlCommand build() {
            return null;
        }
    };


    static {
        PREDICATE_BUILDER = new SqlPredicateBuilder(SQLITE_ORM_SERVICE_PROVIDER.getSyntaxProvider());
    }

    interface EntityQueryMatcher<TEntity> extends Matcher<EntitySelectQuery.Builder<TEntity>> {

    }

    abstract static class EntityQueryMatcherBase<TEntity> extends BaseMatcher<EntitySelectQuery.Builder<TEntity>> implements EntityQueryMatcher<TEntity> {
        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(Object item) {
            try {
                return matches((EntitySelectQuery.Builder<TEntity>)item);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        protected abstract boolean matches(EntitySelectQuery.Builder<TEntity> queryBuilder) throws Exception;
    }

    static class EntityQueryRepositoryMatcherAdapter<TKey, TEntity> extends BaseMatcher<Repository> {
        private final EntityType<TKey, TEntity> entityType;
        private final Matcher<EntitySelectQuery.Builder<TEntity>> matcher;

        EntityQueryRepositoryMatcherAdapter(EntityType<TKey, TEntity> entityType, Matcher<EntitySelectQuery.Builder<TEntity>> matcher) {
            this.entityType = entityType;
            this.matcher = matcher;
        }

        @Override
        public boolean matches(Object item) {
            return matcher.matches(((Repository) item).entities(entityType).query());
        }

        @Override
        public void describeTo(Description description) {
            matcher.describeTo(description);
        }
    }

    static class AllMatcher<T> extends BaseMatcher<T> {
        private final Matcher<T>[] matchers;
        private final Collection<Matcher> failedMatchers = new ArrayList<>();

        public AllMatcher(Matcher<T>[] matchers) {
            this.matchers = matchers;
        }

        @Override
        public boolean matches(Object item) {
            for (Matcher matcher : matchers) {
                if (!matcher.matches(item)) failedMatchers.add(matcher);
            }
            return failedMatchers.isEmpty();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("\n");
            for (Matcher matcher : failedMatchers) {
                matcher.describeTo(description);
                description.appendText("\n");
            }
        }
    }

    public static class NotMatcher<T> extends BaseMatcher<T> {
        private final Matcher<T> matcher;

        NotMatcher(Matcher<T> matcher) {
            this.matcher = matcher;
        }

        @Override
        public boolean matches(Object item) {
            return !matcher.matches(item);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("NOT {");
            matcher.describeTo(description);
            description.appendText("}");
        }
    }

    public static <TKey, TEntity> Matcher<Repository> matchQuery(EntityType<TKey, TEntity> entityType, Matcher<EntitySelectQuery.Builder<TEntity>> matcher) {
        return new EntityQueryRepositoryMatcherAdapter<>(entityType, matcher);
    }

    public static <TKey, TEntity> Matcher<Repository> countEquals(EntityType<TKey, TEntity> entityType, final long expectedCount) {
        return countEquals(entityType, null, expectedCount);
    }

    public static <TKey, TEntity> Matcher<Repository> countNotEquals(EntityType<TKey, TEntity> entityType, final long expectedCount) {
        return countNotEquals(entityType, null, expectedCount);
    }

    public static <TKey, TEntity> Matcher<Repository> countEquals(final EntityType<TKey, TEntity> entityType, final Condition<TEntity> condition, final long expectedCount) {
        return matchQuery(entityType, new EntityQueryMatcherBase<TEntity>() {
            private long actualCount;

            @Override
            protected boolean matches(EntitySelectQuery.Builder<TEntity> queryBuilder) throws Exception {
                if (condition != null) queryBuilder = queryBuilder.where(condition);
                actualCount = queryBuilder.prepare().count();
                return actualCount == expectedCount;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("For condition {%s} Expected count: %d, Actual count: %d", conditionString(), expectedCount, actualCount));
            }

            private String conditionString() {
                return condition != null ? PREDICATE_BUILDER.build(condition, EMPTY_BUILDER) : String.format("any %s", entityType.getName());
            }
        });
    }

    public static <TKey, TEntity> Matcher<Repository> countNotEquals(EntityType<TKey, TEntity> entityType, final Condition<TEntity> condition, final long expectedCount) {
        return not(countEquals(entityType, condition, expectedCount));
    }

    public static <TKey, TEntity> Matcher<Repository> isEmpty(EntityType<TKey, TEntity> entityType, Condition<TEntity> condition) {
        return countEquals(entityType, condition, 0);
    }

    public static <TKey, TEntity> Matcher<Repository> isNotEmpty(EntityType<TKey, TEntity> entityType, Condition<TEntity> condition) {
        return countNotEquals(entityType, condition, 0);
    }

    @SafeVarargs
    public static <T> Matcher<T> all(Matcher<T>... matchers) {
        return new AllMatcher<>(matchers);
    }

    public static <T> Matcher<T> not(Matcher<T> matcher) {
        return new NotMatcher<>(matcher);
    }
}
