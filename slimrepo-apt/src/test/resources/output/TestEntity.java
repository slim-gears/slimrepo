import com.slimgears.slimrepo.core.interfaces.entities.EntityBuilder;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.BlobField;
import com.slimgears.slimrepo.core.interfaces.fields.ComparableField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueGetter;
import com.slimgears.slimrepo.core.interfaces.fields.ValueSetter;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;
import com.slimgears.slimrepo.core.internal.Fields;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;

class TestEntity extends AbstractTestEntity {
    public static final ComparableField<TestEntity, Integer> Id = Fields.comparableField(
            "id",
            Integer.class,
            new ValueGetter<TestEntity, Integer>() { @Override public Integer getValue(TestEntity entity) { return entity.getId(); } },
            new ValueSetter<TestEntity, Integer>() { @Override public void setValue(TestEntity entity, Integer value) { entity.setId(value); } },
            false);

    public static final StringField<TestEntity> Name = Fields.stringField(
            "name",
            new ValueGetter<TestEntity, String>() { @Override public String getValue(TestEntity entity) { return entity.getName(); } },
            new ValueSetter<TestEntity, String>() { @Override public void setValue(TestEntity entity, String value) { entity.setName(value); } },
            true);

    public static final BlobField<TestEntity, RelatedEntity> Related = Fields.blobField(
            "related",
            RelatedEntity.class,
            new ValueGetter<TestEntity, RelatedEntity>() { @Override public RelatedEntity getValue(TestEntity entity) { return entity.getRelated(); } },
            new ValueSetter<TestEntity, RelatedEntity>() { @Override public void setValue(TestEntity entity, RelatedEntity value) { entity.setRelated(value); } },
            true);

    public static final ComparableField<TestEntity, TestEnum> EnumValue = Fields.comparableField(
            "enumValue",
            TestEnum.class,
            new ValueGetter<TestEntity, TestEnum>() { @Override public TestEnum getValue(TestEntity entity) { return entity.getEnumValue(); } },
            new ValueSetter<TestEntity, TestEnum>() { @Override public void setValue(TestEntity entity, TestEnum value) { entity.setEnumValue(value); } },
            true);

    public static final ValueField<TestEntity, CustomType> CustomTypeValue = Fields.valueField(
            "customTypeValue",
            CustomType.class,
            new ValueGetter<TestEntity, CustomType>() { @Override public CustomType getValue(TestEntity entity) { return entity.getCustomTypeValue(); } },
            new ValueSetter<TestEntity, CustomType>() { @Override public void setValue(TestEntity entity, CustomType value) { entity.setCustomTypeValue(value); } },
            true);

    public static final EntityType<Integer, TestEntity> EntityMetaType = new MetaType();

    private TestEntity() {
    }

    public TestEntity(int id, String name, RelatedEntity related, TestEnum enumValue, CustomType customTypeValue) {
        this.id = id;
        this.name = name;
        this.related = related;
        this.enumValue = enumValue;
        this.customTypeValue = customTypeValue;
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

    public TestEntity setCustomTypeValue(CustomType customTypeValue) {
        this.customTypeValue = customTypeValue;
        return this;
    }

    public CustomType getCustomTypeValue() {
        return this.customTypeValue;
    }

    private static class MetaType extends AbstractEntityType<Integer, TestEntity> {
        MetaType() {
            super(TestEntity.class, Id, Name, Related, EnumValue, CustomTypeValue);
        }

        @Override
        public TestEntity newInstance() {
            return new TestEntity();
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

        public Builder customTypeValue(CustomType customTypeValue) {
            model.setCustomTypeValue(customTypeValue);
            return this;
        }
    }
}
