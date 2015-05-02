import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityBuilder;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.NumericField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;
import com.slimgears.slimrepo.core.internal.Fields;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;

class RelatedEntity extends AbstractRelatedEntity implements Entity<Integer> {
    public static final NumericField<RelatedEntity, Integer> Id = Fields.numberField("id", Integer.class, false);

    public static final StringField<RelatedEntity> Name = Fields.stringField("name", true);

    public static final EntityType<Integer, RelatedEntity> EntityMetaType = new MetaType();

    private RelatedEntity() {
    }

    public RelatedEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Integer getEntityId() {
        return this.id;
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
            super("RelatedEntity", RelatedEntity.class, Id, Name);
        }

        @Override
        public RelatedEntity newInstance() {
            return new RelatedEntity();
        }

        @Override
        public void setKey(RelatedEntity entity, Integer key) {
            entity.setId(key);
        }

        @Override
        public RelatedEntity newInstance(FieldValueLookup<RelatedEntity> lookup) {
            return new RelatedEntity(
                    lookup.getValue(Id),
                    lookup.getValue(Name));
        }

        @Override
        public void entityToMap(RelatedEntity entity, FieldValueMap<RelatedEntity> map) {
            map
                    .putValue(Id, entity.getId())
                    .putValue(Name, entity.getName());
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
