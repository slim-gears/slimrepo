import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityBuilder;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.ComparableField;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;
import com.slimgears.slimrepo.core.internal.Fields;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;

class TestEntity extends AbstractTestEntity implements Entity<Integer> {
    public static final ComparableField<TestEntity, Integer> Id = Fields.comparableField("id", Integer.class, false);

    public static final StringField<TestEntity> Name = Fields.stringField("name", true);

    public static final RelationalField<TestEntity, RelatedEntity> Related = Fields.relationalField("related", RelatedEntity.EntityMetaType, true);

    public static final ComparableField<TestEntity, TestEnum> EnumValue = Fields.comparableField("enumValue", TestEnum.class, true);

    public static final EntityType<Integer, TestEntity> EntityMetaType = new MetaType();

    private TestEntity() {
    }

    public TestEntity(int id, String name, RelatedEntity related, TestEnum enumValue) {
        this.id = id;
        this.name = name;
        this.related = related;
        this.enumValue = enumValue;
    }

    @Override
    public Integer getEntityId() {
        return this.id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TestEntity create() {
        return new TestEntity();
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

    public TestEntity setRelated(RelatedEntity related) {
        this.related = related;
        return this;
    }

    public RelatedEntity getRelated() {
        return (RelatedEntity)this.related;
    }

    public TestEntity setEnumValue(TestEnum enumValue) {
        this.enumValue = enumValue;
        return this;
    }

    public TestEnum getEnumValue() {
        return this.enumValue;
    }

    private static class MetaType extends AbstractEntityType<Integer, TestEntity> {
        MetaType() {
            super("TestEntity", TestEntity.class, Id, Name, Related, EnumValue);
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
                    lookup.getValue(Related),
                    lookup.getValue(EnumValue));
        }

        @Override
        public void entityToMap(TestEntity entity, FieldValueMap<TestEntity> map) {
            map
                    .putValue(Id, entity.getId())
                    .putValue(Name, entity.getName())
                    .putValue(Related, entity.getRelated())
                    .putValue(EnumValue, entity.getEnumValue());
        }
    }

    public static class Builder implements EntityBuilder<TestEntity> {
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

        public Builder related(RelatedEntity related) {
            model.setRelated(related);
            return this;
        }

        public Builder enumValue(TestEnum enumValue) {
            model.setEnumValue(enumValue);
            return this;
        }
    }
}
