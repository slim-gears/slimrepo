import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.annotations.Key;
import com.slimgears.slimrepo.core.annotations.Relation;
import com.slimgears.slimrepo.core.prototype.AbstractUserEntity;
import com.slimgears.slimrepo.core.prototype.generated.UserEntity;

@GenerateEntity
public class AbstractTestEntity {
    protected int id;
    protected String name;
    protected AbstractUserEntity user;
}
