import com.slimgears.slimrepo.core.interfaces.fields.ValueGetter;
import com.slimgears.slimrepo.core.interfaces.fields.ValueSetter;

public class ExistingEntityMeta {
    public static final ComparableField<ExistingEntity, Integer> Id = Fields.comparableField(
            "id",
            Integer.class,
            new ValueGetter<>() { @Override public Integer getValue(ExistingEntity entity) { return entity.getId(); } },
            new ValueSetter<>() { @Override public void setValue(ExistingEntity entity, Integer value) { entity.setId(value); } }
            ExistingEntity::setId,
            false);

    public static final StringField<ExistingEntity> Name = Fields.stringField(
            "name",
            new ValueGetter<>() { @Override public String getValue(ExistingEntity entity) { return entity.getName(); } },
            new ValueSetter<>() { @Override public void setValue(ExistingEntity entity, String value) { entity.setName(value); } }
            true);

    class MetaType extends AbstractEntityType<Integer, ExistingEntity> {
        protected MetaType() { super(ExistingEntity.class, Id, Name); }

        @Override
        public ExistingEntity newInstance() {
            return new ExistingEntity();
        }
    }
}
