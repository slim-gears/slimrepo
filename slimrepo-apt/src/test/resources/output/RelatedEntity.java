import com.slimgears.slimrepo.core.interfaces.entities.EntityBuilder;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.ComparableField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueGetter;
import com.slimgears.slimrepo.core.interfaces.fields.ValueSetter;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;
import com.slimgears.slimrepo.core.internal.Fields;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;

class RelatedEntity extends AbstractRelatedEntity {
    public static final ComparableField<RelatedEntity, Integer> Id = Fields.comparableField(
            "id",
            Integer.class,
            new ValueGetter<RelatedEntity, Integer>() { @Override public Integer getValue(RelatedEntity entity) { return entity.getId(); }},
            new ValueSetter<RelatedEntity, Integer>() { @Override public void setValue(RelatedEntity entity, Integer value) { entity.setId(value); } },
            false);

    public static final StringField<RelatedEntity> Name = Fields.stringField(
            "name",
            new ValueGetter<RelatedEntity, String>() { @Override public String getValue(RelatedEntity entity) { return entity.getName(); }},
            new ValueSetter<RelatedEntity, String>() { @Override public void setValue(RelatedEntity entity, String value) { entity.setName(value); } },
            true);

    public static final EntityType<Integer, RelatedEntity> EntityMetaType = new MetaType();

    private RelatedEntity() {
    }

    public RelatedEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static RelatedEntity create() {
        return new RelatedEntity();
    }

    public RelatedEntity setId(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return this.id;
    }

    public RelatedEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return this.name;
    }

    private static class MetaType extends AbstractEntityType<Integer, RelatedEntity> {
        MetaType() {
            super(RelatedEntity.class, Id, Name);
        }

        @Override
        public RelatedEntity newInstance() {
            return new RelatedEntity();
        }
    }

    public static class Builder implements EntityBuilder<RelatedEntity> {
        private RelatedEntity model = new RelatedEntity();

        public RelatedEntity build() {
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
    }
}
