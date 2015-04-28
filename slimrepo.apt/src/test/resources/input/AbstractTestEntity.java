import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.annotations.Key;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;

@GenerateEntity
public class AbstractTestEntity {
    @Key protected int id;
    protected String name;
    @Relation protected UserEntity user;
}
