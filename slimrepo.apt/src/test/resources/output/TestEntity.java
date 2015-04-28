import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.NumericField;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;
import com.slimgears.slimrepo.core.internal.Fields;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;

public class TestEntity extends AbstractTestEntity implements Entity<Integer> {
    public static final NumericField<TestEntity, Integer> Id = Fields.numberField("id", Integer.class, false);

    public static final StringField<TestEntity> Name = Fields.stringField("name", true);

    public static final RelationalField<TestEntity, UserEntity> User = Fields.relationalField("user", UserEntity.EntityMetaType, true);

    public static final EntityType<Integer, TestEntity> EntityMetaType = new MetaType();

    private TestEntity() {
    }

    public TestEntity(int id, String name, UserEntity user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    @Override
    public Integer getEntityId() {
        return this.id;
    }

    public static Builder create() {
        return new Builder();
    }

    public TestEntity setId(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return this.id;
    }

    public TestEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public TestEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public UserEntity getUser() {
        return this.user;
    }

    private static class MetaType extends AbstractEntityType<Integer, TestEntity> {
        MetaType() {
            super("TestEntity", TestEntity.class, Id, Name, User);
        }

        @Override
        public TestEntity newInstance() {
            return new TestEntity();
        }

        @Override
        public void setKey(TestEntity entity, Integer key) {
            entity.setId(key);
        }

        @Override
        public TestEntity newInstance(FieldValueLookup<TestEntity> lookup) {
            return new TestEntity(
                    lookup.getValue(Id),
                    lookup.getValue(Name),
                    lookup.getValue(User));
        }

        @Override
        public void entityToMap(TestEntity entity, FieldValueMap<TestEntity> map) {
            map
                    .putValue(Id, entity.getId())
                    .putValue(Name, entity.getName())
                    .putValue(User, entity.getUser());
        }
    }

    public static class Builder {
        private TestEntity model = new TestEntity();

        public TestEntity build() {
            return model;
        }

        public Builder id(int id) {
            model.setId(id);
            return this;
        }

        public Builder name(String name) {
            model.setName(name);
            return this;
        }

        public Builder user(UserEntity user) {
            model.setUser(user);
            return this;
        }
    }
}
