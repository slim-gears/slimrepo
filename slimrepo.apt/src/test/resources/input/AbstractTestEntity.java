import com.slimgears.slimrepo.core.annotations.GenerateEntity;

@GenerateEntity
class AbstractTestEntity {
    protected int id;
    protected String name;
    protected AbstractRelatedEntity related;
}
