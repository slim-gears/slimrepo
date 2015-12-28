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

public class ExistingEntityMeta {
    public static final ComparableField<ExistingEntity, Integer> Id = Fields.comparableField(
            "id",
            Integer.class,
            new ValueGetter<ExistingEntity, Integer>() { @Override public Integer getValue(ExistingEntity entity) { return entity.getId(); } },
            new ValueSetter<ExistingEntity, Integer>() { @Override public void setValue(ExistingEntity entity, Integer value) { entity.setId(value); } },
            false);

    public static final StringField<ExistingEntity> Name = Fields.stringField(
            "name",
            new ValueGetter<ExistingEntity, String>() { @Override public String getValue(ExistingEntity entity) { return entity.getName(); } },
            new ValueSetter<ExistingEntity, String>() { @Override public void setValue(ExistingEntity entity, String value) { entity.setName(value); } },
            true);

    public static final EntityType<Integer, ExistingEntity> EntityMetaType = new MetaType();

    private static class MetaType extends AbstractEntityType<Integer, ExistingEntity> {
        MetaType() {
            super(ExistingEntity.class, Id, Name);
        }

        @Override
        public ExistingEntity newInstance() {
            return new ExistingEntity();
        }
    }
}
