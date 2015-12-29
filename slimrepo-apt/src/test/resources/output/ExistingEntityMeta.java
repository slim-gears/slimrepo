import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.ComparableField;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueGetter;
import com.slimgears.slimrepo.core.interfaces.fields.ValueSetter;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;
import com.slimgears.slimrepo.core.internal.Fields;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import javax.annotation.Generated;

@Generated(value = "ExistingEntity", comments = "Entity meta data generated from ExistingEntity")
public class ExistingEntityMeta {
    public static final ComparableField<ExistingEntity, Integer> Id = Fields.comparableField(
            "id",
            Integer.class,
            new ValueGetter<ExistingEntity, Integer>() { @Override public Integer getValue(ExistingEntity entity) { return entity.getId(); } },
            new ValueSetter<ExistingEntity, Integer>() { @Override public void setValue(ExistingEntity entity, Integer value) { entity.setId(value); } },
            false);

    public static final RelationalField<ExistingEntity, RelatedEntity> Related = Fields.relationalField(
            "related",
            RelatedEntity.EntityMetaType,
            new ValueGetter<ExistingEntity, RelatedEntity>() { @Override public RelatedEntity getValue(ExistingEntity entity) { return entity.getRelated(); } },
            new ValueSetter<ExistingEntity, RelatedEntity>() { @Override public void setValue(ExistingEntity entity, RelatedEntity value) { entity.setRelated(value); } },
            true
    )

    public static final StringField<ExistingEntity> Name = Fields.stringField(
            "name",
            new ValueGetter<ExistingEntity, String>() { @Override public String getValue(ExistingEntity entity) { return entity.getName(); } },
            new ValueSetter<ExistingEntity, String>() { @Override public void setValue(ExistingEntity entity, String value) { entity.setName(value); } },
            true);

    public static final EntityType<Integer, ExistingEntity> EntityMetaType = new MetaType();

    private static class MetaType extends AbstractEntityType<Integer, ExistingEntity> {
        MetaType() {
            super(ExistingEntity.class, Id, Related, Name);
        }

        @Override
        public ExistingEntity newInstance() {
            return new ExistingEntity();
        }
    }
}
